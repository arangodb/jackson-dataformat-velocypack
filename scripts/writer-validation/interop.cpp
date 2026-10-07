#include <velocypack/Validator.h>
#include <velocypack/Slice.h>
#include <velocypack/Iterator.h>
#include <velocypack/Parser.h>
#include <velocypack/Builder.h>
#include <velocypack/AttributeTranslator.h>
#include <fstream>
#include <iostream>
#include <stdexcept>
#include <vector>
#include <filesystem>
#include <algorithm>
using namespace arangodb::velocypack;
void require(bool b, const std::string& what) { if(!b) throw std::runtime_error(what); }
std::string read(const std::filesystem::path& p) { std::ifstream in(p,std::ios::binary);require(bool(in),"open "+p.string());return {std::istreambuf_iterator<char>(in),{}}; }
uint64_t arrays=0,objects=0,lookups=0,items=0,tags=0,numericKeys=0;
void check(Slice actual,Slice expected) {
    if(actual.isTagged()){tags+=actual.getTags().size();actual=actual.value();}
    if(expected.isArray()) {
        ++arrays;require(actual.isArray(),"array type");require(actual.length()==expected.length(),"array length");
        uint64_t i=0;for(ArrayIterator it(actual);it.valid();++it,++i){++items;check(it.value(),expected.at(i));require(it.value().begin()==actual.at(i).begin(),"array indexed access vs iteration");}
        require(i==expected.length(),"array iteration count");
    } else if(expected.isObject()) {
        ++objects;require(actual.isObject(),"object type");require(actual.length()==expected.length(),"object length");
        uint64_t n=0;for(ObjectIterator it(actual);it.valid();++it){
            auto raw=it.key(false);if(raw.isUInt()||raw.isSmallInt())++numericKeys;
            auto key=it.key(true).copyString();auto e=expected.get(key);require(!e.isNone(),"unexpected key "+key);
            auto found=actual.get(key);++lookups;require(!found.isNone(),"lookup "+key);check(found,e);
            require(found.begin()==it.value().begin(),"lookup vs iteration "+key);++n;
        }require(n==expected.length(),"object iteration count");
        // Sequential traversal exercises physical pair order independently of index traversal.
        n=0;for(ObjectIterator it(actual,true);it.valid();++it){check(it.value(),expected.get(it.key().copyString()));++n;}
        require(n==expected.length(),"sequential iteration count");
    } else if(actual.isBinary()) {
        ValueLength len;auto b=actual.getBinary(len);std::string hex="binary:";const char* digits="0123456789abcdef";
        for(ValueLength i=0;i<len;i++){hex+=digits[b[i]>>4];hex+=digits[b[i]&15];}require(expected.isString()&&expected.copyString()==hex,"binary content");
    } else if(expected.isString()) require(actual.isString()&&actual.copyString()==expected.copyString(),"string/Unicode");
    else if(expected.isNull())require(actual.isNull(),"null");
    else if(expected.isBool())require(actual.isBool()&&actual.getBool()==expected.getBool(),"boolean");
    else if(expected.isInteger())require(actual.isInteger()&&actual.getNumber<int64_t>()==expected.getNumber<int64_t>(),"integer");
    else if(expected.isNumber())require(actual.isNumber()&&actual.getNumber<double>()==expected.getNumber<double>(),"number");
    else throw std::runtime_error("unsupported expected type");
}
void collectTags(Slice s,std::vector<uint64_t>& found) {
    if(s.isTagged()){auto t=s.getTags();found.insert(found.end(),t.begin(),t.end());s=s.value();}
    if(s.isArray())for(ArrayIterator it(s);it.valid();++it)collectTags(it.value(),found);
    if(s.isObject())for(ObjectIterator it(s,true);it.valid();++it)collectTags(it.value(),found);
}
int main(int argc,char** argv) try {
    require(argc==2||argc==3,"fixture directory required");bool current=argc==3;std::filesystem::path dir(argv[1]);
    AttributeTranslator translator;for(int i=1;i<=6;i++)translator.add("id"+std::to_string(i),i);translator.seal();AttributeTranslatorScope scope(&translator);
    Options options;options.validateUtf8Strings=true;options.disallowTags=false;options.disallowExternals=true;options.disallowCustom=true;options.disallowBCD=false;
    options.attributeTranslator=&translator;options.nestingLimit=256;
    std::ifstream manifest(dir/"manifest.txt");std::string name;uint64_t expectedTags;int count=0;
    while(manifest>>name>>expectedTags){auto data=read(dir/(name+".vpack"));Validator validator(&options);
        if(current&&name.starts_with("indexed-unsorted")){
            bool rejected=false;try{validator.validate(data.data(),data.size());}catch(const Exception& e){rejected=e.errorCode()==Exception::ValidatorInvalidType;}
            require(rejected,"current C++ expected removed unsorted object support");std::cout<<"UNSUPPORTED "<<name<<" (0x0f..0x12 removed upstream in 2022; compatible revision checks this fixture)\n";continue;
        }
        validator.validate(data.data(),data.size());
        auto reference=Parser::fromJson(read(dir/(name+".json")));auto beforeTags=tags;
        check(Slice(reinterpret_cast<const uint8_t*>(data.data())),reference->slice());
        // Traversing objects twice means nested tag observations repeat; validate separately on the tag fixtures.
        std::vector<uint64_t> actualTags;collectTags(Slice(reinterpret_cast<const uint8_t*>(data.data())),actualTags);
        require(actualTags.size()==expectedTags,"exact tag count");
        if(expectedTags){std::sort(actualTags.begin(),actualTags.end());require(actualTags==std::vector<uint64_t>({7,42,255,123456789}),"tag values");}
        else require(tags==beforeTags,"unexpected tag");
        if(expectedTags){Options deny=options;deny.disallowTags=true;bool rejected=false;try{Validator(&deny).validate(data.data(),data.size());}catch(...){rejected=true;}require(rejected,"tag denial option");}
        std::cout<<"PASS "<<name<<" "<<data.size()<<" B\n";++count;
    }
    // The writer has always sorted encoded numeric IDs, not translated attribute names.
    // Deliberately nonmonotonic translations demonstrate the old semantic limitation.
    { AttributeTranslator other;std::vector<std::string> names={"z","a","m","b","n","c"};
      for(uint64_t i=0;i<names.size();++i)other.add(names[i],i+1);other.seal();AttributeTranslatorScope otherScope(&other);
      auto numeric=read(dir/"numeric-sorted-semantic-limit.vpack");Validator(&options).validate(numeric.data(),numeric.size());
      Slice s(reinterpret_cast<const uint8_t*>(numeric.data()));uint64_t i=1;for(ObjectIterator it(s);it.valid();++it,++i){require(it.key(false).getUInt()==i,"untranslated numeric iteration");require(it.value().getNumber<int>()==int(i),"numeric values");}
      require(i==7,"numeric iteration count");int missing=0;for(auto const& name:names)if(s.get(name).isNone())++missing;
      require(missing>0,"expected old sorted numeric-name lookup limitation");
      std::cout<<"PREEXISTING numeric sorted-name lookup limitation: "<<missing<<"/6 missing with nonmonotonic translations; raw iteration works; Java confirmed identical original-writer bytes\n";
    }
    auto bcd=read(dir/"bcd.vpack");bool unsupported=false;try{Validator(&options).validate(bcd.data(),bcd.size());}catch(const Exception& e){unsupported=e.errorCode()==Exception::NotImplemented;}
    require(unsupported,"BCD expected C++ NotImplemented");
    std::cout<<"PASS "<<count<<" fixtures; arrays="<<arrays<<" objects="<<objects<<" lookups="<<lookups<<" arrayItems="<<items<<" numericKeys="<<numericKeys<<" tagVisits="<<tags<<"; BCD independently checked by Java literal\n";
    return 0;
} catch(const std::exception& e){std::cerr<<"FAIL "<<e.what()<<"\n";return 1;}
