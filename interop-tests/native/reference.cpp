// This adapter links unmodified upstream code. No representation is compared by byte identity.
#include <velocypack/AttributeTranslator.h>
#include <velocypack/Builder.h>
#include <velocypack/Dumper.h>
#include <velocypack/Exception.h>
#include <velocypack/Iterator.h>
#include <velocypack/Parser.h>
#include <velocypack/Validator.h>
#include <atomic>
#include <bit>
#include <cstdlib>
#include <cstring>
#include <string>
#include <vector>
using namespace arangodb::velocypack;

extern "C" {
struct vp_result { int status; int code; void* data; size_t size; void* message; size_t message_size; };
}
static std::atomic<uint64_t> allocations{0};
static void* copy(void const* p, size_t n) {
  if (!n) return nullptr;
  auto out = std::malloc(n);
  if (!out) throw std::bad_alloc();
  std::memcpy(out, p, n); ++allocations; return out;
}
static void output(vp_result* r, void const* p, size_t n) { r->data = copy(p, n); r->size = n; }
static void output(vp_result* r, std::string const& s) { output(r, s.data(), s.size()); }
static void error_message(vp_result* r, char const* message) noexcept {
  try { size_t n=std::strlen(message); r->message=copy(message,n); r->message_size=n; } catch (...) {}
}
template<class F> static void guarded(vp_result* r, F f) noexcept {
  *r = {};
  try { f(); }
  catch (Exception const& e) { r->status = 1; r->code = e.errorCode(); error_message(r,e.what()); }
  catch (std::exception const& e) { r->status = 2; r->code = 999; error_message(r,e.what()); }
  catch (...) { r->status = 3; r->code = 999; }
}
// Flag bits: compact, strict UTF-8, unique keys, binary hex, date integer,
// nonfinite string, translated numeric keys, padded, disallow custom, tags, BCD.
static Options options(uint64_t flags, uint64_t depth) {
  Options o;
  o.buildUnindexedArrays = o.buildUnindexedObjects = flags & 1;
  o.validateUtf8Strings = flags & 2;
  o.checkAttributeUniqueness = flags & 4;
  o.binaryAsHex = flags & 8;
  o.datesAsIntegers = flags & 16;
  o.unsupportedDoublesAsString = flags & 32;
  o.paddingBehavior = (flags & 128) ? Options::UsePadding : Options::NoPadding;
  o.disallowExternals = true;
  o.disallowCustom = flags & 256;
  o.disallowTags = flags & 512;
  o.disallowBCD = flags & 1024;
  o.nestingLimit = depth ? depth : 64;
  return o;
}
static std::string hex(uint8_t const* p, size_t n) {
  static char const* digits = "0123456789abcdef";
  std::string s; s.reserve(n * 2);
  for (size_t i = 0; i < n; ++i) { s += digits[p[i] >> 4]; s += digits[p[i] & 15]; }
  return s;
}
static std::vector<uint8_t> unhex(std::string const& s) {
  if (s.size() % 2) throw std::invalid_argument("odd hex length");
  std::vector<uint8_t> out;
  for (size_t i=0; i<s.size(); i+=2) out.push_back(static_cast<uint8_t>(std::stoul(s.substr(i,2),nullptr,16)));
  return out;
}
static void inspect(Builder& b, Slice s) {
  b.openObject(); b.add("type", Value(s.typeName())); b.add("head", Value(uint64_t(*s.start())));
  if (s.isTagged()) {
    b.add("tags", Value(ValueType::Array)); for (auto tag : s.getTags()) b.add(Value(std::to_string(tag))); b.close();
    b.add(Value("value")); inspect(b, s.value());
  } else if (s.isArray()) {
    b.add("value", Value(ValueType::Array)); for (auto item : ArrayIterator(s)) inspect(b, item); b.close();
  } else if (s.isObject()) {
    b.add("value", Value(ValueType::Array));
    for (ObjectIterator it(s, true); it.valid(); it.next()) {
      b.openObject(); b.add(Value("key")); inspect(b, it.key(false)); b.add(Value("value")); inspect(b, it.value()); b.close();
    } b.close();
  } else if (s.isInt() || s.isSmallInt()) b.add("value", Value(std::to_string(s.getInt())));
  else if (s.isUInt()) b.add("value", Value(std::to_string(s.getUInt())));
  else if (s.isDouble()) { auto bits=std::bit_cast<uint64_t>(s.getDouble()); b.add("bits", Value(std::to_string(bits))); }
  else if (s.isUTCDate()) b.add("value", Value(std::to_string(s.getUTCDate())));
  else if (s.isString()) b.add("value", Value(s.copyString()));
  else if (s.isBool()) b.add("value", Value(s.getBool()));
  else if (s.isBinary()) { ValueLength n; auto p = s.getBinary(n); b.add("value", Value(hex(p,n))); }
  else if (s.isCustom()) b.add("wire", Value(hex(s.start(),s.byteSize())));
  b.close();
}
extern "C" void vp_free(vp_result* r) noexcept {
  if (r->data) { std::free(r->data); --allocations; }
  if (r->message) { std::free(r->message); --allocations; }
  *r = {};
}
extern "C" uint64_t vp_outstanding() noexcept { return allocations.load(); }
extern "C" char const* vp_revision() noexcept { return VPACK_REFERENCE_REVISION; }
using Input = uint8_t const*;
extern "C" void vp_parse(Input p, size_t n, uint64_t flags, uint64_t depth, vp_result* r) noexcept {
  guarded(r,[&] {
    auto o=options(flags,depth);
    // Upstream's numeric parser calls atof. Supply its required NUL sentinel,
    // while still parsing exactly the caller's length (including embedded NULs).
    std::string owned(reinterpret_cast<char const*>(p),n);
    Parser parser(&o); parser.parse(owned.c_str(),owned.size());
    auto s=parser.builder().slice(); output(r,s.start(),s.byteSize());
  });
}
extern "C" void vp_validate(Input p, size_t n, uint64_t flags, uint64_t depth, vp_result* r) noexcept {
  guarded(r,[&] { auto o=options(flags,depth); Validator(&o).validate(p,n); });
}
extern "C" void vp_dump(Input p, size_t n, uint64_t flags, uint64_t depth, vp_result* r) noexcept {
  guarded(r,[&] {
    auto o=options(flags,depth); AttributeTranslator translator;
    if (flags & 64) { for (uint64_t i=1;i<=6;++i) translator.add("id"+std::to_string(i),i); translator.seal(); o.attributeTranslator=&translator; }
    Validator(&o).validate(p,n); AttributeTranslatorScope scope(o.attributeTranslator);
    output(r,Dumper::toString(Slice(p),&o));
  });
}
extern "C" void vp_inspect(Input p, size_t n, uint64_t flags, uint64_t depth, vp_result* r) noexcept {
  guarded(r,[&] { auto o=options(flags,depth); Validator(&o).validate(p,n); Builder b; inspect(b,Slice(p)); output(r,Dumper::toString(b.slice())); });
}
// Fixture construction deliberately does not call the JSON conversion entrypoint.
extern "C" void vp_fixture(Input p, size_t n, uint64_t flags, uint64_t depth, vp_result* r) noexcept {
  guarded(r,[&] {
    auto o=options(flags,depth); auto spec=Parser::fromJson(std::string_view(reinterpret_cast<char const*>(p),n));
    auto root=spec->slice(); auto type=root.get("type").copyString(); auto v=root.get("value"); Builder b(&o);
    if (type=="None") b.add(Value(ValueType::None));
    else if (type=="Illegal") b.add(Value(ValueType::Illegal));
    else if (type=="Null") b.add(Value(ValueType::Null));
    else if (type=="MinKey") b.add(Value(ValueType::MinKey));
    else if (type=="MaxKey") b.add(Value(ValueType::MaxKey));
    else if (type=="Bool") b.add(Value(v.getBool()));
    else if (type=="Int") b.add(Value(int64_t(std::stoll(v.copyString())),ValueType::Int));
    else if (type=="UInt") b.add(Value(uint64_t(std::stoull(v.copyString())),ValueType::UInt));
    else if (type=="UTCDate") b.add(Value(int64_t(std::stoll(v.copyString())),ValueType::UTCDate));
    else if (type=="Double") { auto bits=std::stoull(v.copyString(),nullptr,16); b.add(Value(std::bit_cast<double>(uint64_t(bits)))); }
    else if (type=="String") b.add(Value(v.copyString()));
    else if (type=="Binary") { auto bytes=unhex(v.copyString()); b.add(ValuePair(bytes.data(),bytes.size(),ValueType::Binary)); }
    else if (type=="Custom") { auto bytes=unhex(v.copyString()); b.add(ValuePair(bytes.data(),bytes.size(),ValueType::Custom)); }
    else if (type=="Tagged") { Builder child; child.add(Value(root.get("number").getInt())); b.addTagged(std::stoull(v.copyString()),child.slice()); }
    else throw std::invalid_argument("unsupported fixture kind: "+type);
    auto s=b.slice(); output(r,s.start(),s.byteSize());
  });
}
// The pointer never crosses the ABI: both builders live through validation and resolution.
extern "C" void vp_external(Input, size_t, uint64_t, uint64_t, vp_result* r) noexcept {
  guarded(r,[&] { Options o; o.disallowExternals=false; Builder referent; referent.add(Value(int64_t(42))); Builder external(&o); external.addExternal(referent.slice().start()); Validator(&o).validate(external.slice().start(),external.size()); Builder b; inspect(b,external.slice().resolveExternal()); output(r,Dumper::toString(b.slice())); });
}
