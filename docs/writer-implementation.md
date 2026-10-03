# VelocyPack writer implementation

The generator retains the stage-2 shared-buffer architecture. One lazy, growing
payload byte array holds the active root container. Primitive frame arrays and
direct-child offset/key-end arrays describe open containers; completing a child
retires its metadata. Completing a root resets logical positions, while capacity
can serve the next root. Storage depends on reusable active-root capacity and
maximum live child/depth counts, not the number of roots in a sequence.

## Container assembly and output

Each opening reserves nine internal header bytes. Closing samples compact and
sorting features, computes checked final sizes, and compacts the header with one
overlapping content move. Stable object sorting reorders physical pairs and
indexes, using primitive merge-sort arrays and reusable scratch. Already ordered
pairs avoid scratch copies; other sorted objects require one content pass into
scratch and one back, independently of header compaction. Geometric arena growth
still copies prior capacity. No per-value capture streams or recursive container
byte arrays remain.

A completed root flushes preceding output bytes, then passes its arena range to
the target's synchronous `write(byte[],off,len)` call. The generator makes no
root-sized final output copy. An array-producing caller still owns its output
materialization cost: resolved Jackson 3.2.0 `ObjectMapper.writeValueAsBytes` uses
`ByteArrayBuilder`, which copies to segmented output storage and then to a fresh
returned array. Generator and output-array reuse across benchmark operations are
not enabled.

## Text and binary payloads

Container strings and char-array ranges of at most 42 UTF-16 code units reserve
one local header byte and at most 126 payload bytes, encode once, and patch the
header with the actual byte length. Every possible encoding fits that short
header; valid surrogate pairs need less space than two three-byte characters.
Short String values and names now scan the entire range for ASCII, then use the
JDK's guarded bulk copy into the payload. This avoids the generic String/char-array
encoder dispatch for ASCII; Unicode uses the existing UTF-8 encoder. The deprecated
String.getBytes range overload truncates Unicode, so the ASCII check is required.
It introduces no intermediate encoded array and preserves malformed-surrogate
replacement. Character-array writes retain the existing direct encoder.
The unused reservation is capacity, not logical payload. No string content move
is required. Longer and root strings first compute exact UTF-8 length using `long`
arithmetic, then encode into the payload or small output buffer. The short header
covers 0 through 126 **bytes**; 127 or more uses `bf` and an eight-byte
little-endian length. There is no temporary UTF-8 array, intermediate String for
char ranges, unchecked `3 * charCount` calculation, or large worst-case ASCII
reservation for long strings. Container capacity may still grow geometrically.

Valid surrogate pairs encode as four bytes. Each malformed surrogate encodes as
ASCII `?`, including pairs cut by char-array range boundaries. This matches legacy
`String.getBytes(UTF_8)` with `LENIENT_UTF_ENCODING` either on or off; the feature's
advertised strict policy remains unimplemented. Normal `SerializableString`
writes and names use `getValue()`. The resolved `SerializedString.asUnquotedUTF8`
calls `JsonStringEncoder.encodeAsUTF8`, which throws for malformed surrogates and
would change the legacy error policy. Tests verify that difference. The existing
UTF-8 byte APIs copy bytes directly without introducing validation or reencoding.

Known-length binary streams read directly into a container's reserved payload
range. Small root binaries read into available output storage; large root binaries
stream bounded chunks through the independent output buffer. A zero-progress bulk
read falls back to a single-byte read, so short reads and repeated zero-progress
bulk reads cannot spin. Premature EOF reports the declared and actually read
length; unknown lengths remain unsupported. Streams belong to their callers and
are not closed. Mutable caller arrays are copied or synchronously emitted before
the write returns, never retained as borrowed pending payloads.

Huge root strings likewise use bounded output storage rather than allocating a
document arena. Small and large successful root writes preserve legacy buffered
byte counts and visibility on return, including custom-buffer prefixes and tiny
positive buffer capacities. A root stream failure may expose a partial value:
bounded streaming cannot provide transactional output on an arbitrary target.
No rollback or general recovery contract is added for failed input reads or
malformed call sequences. BCD encoding remains the legacy temporary encoder.

## Independent lifecycles

The IOContext write-encoding buffer is separate from payload and sorting storage.
The default constructor allocates that small buffer; the custom constructor
accepts its caller-provided buffer, prefix offset and recyclable flag. Bytes before
the offset are emitted as a prefix, including when the prefix fills the buffer.
Only a recyclable output buffer is released to IOContext, exactly once via
`GeneratorBase.close`'s `finally` cleanup. A caller-owned buffer is never released.
Terminal close drops both output and payload references, sorting scratch/arrays,
and frame/offset arrays, even when output write, flush or close throws.

Pending output lengths are cleared before target writes. An IOException from
output write or flush prohibits subsequent values/names/tags and discards pending
bytes, preventing replay of a prefix that the target may already have accepted.
Close still attempts the target close authorized by `AUTO_CLOSE_TARGET` or managed
resource ownership, or flush under `FLUSH_PASSED_TO_STREAM`. If both pending output
and target close fail, the first exception stays primary and the latter is
suppressed. Repeated close performs no additional release or target close.
Explicit `flush()` retains its legacy forwarding behavior.

No document arena is returned to Jackson's small write-encoding recycler. No
custom global or ThreadLocal document pool is used, and no reuse beyond one
generator is introduced. Jackson's existing output-buffer recycling remains
unchanged. The resolved IOContext and GeneratorBase sources were inspected to
verify allocation, release and close behavior; no broader pool contract is assumed.

## Tags, raw values and limits

Tags are explicit prefixes, consuming no value context slot. Parent offsets and
lengths start before tag bytes. Wrapped scalars and empty/nonempty containers
finish exactly one item or pair, including consecutive tagged values, chained
tags, nested containers, and ordinary siblings. The stage-1 lost/misplaced tag
cases are intentional byte-identity exceptions with independent wire vectors.

Root `writeRaw(byte)` and `writeBytes` preserve exact pass-through, including None
bytes and multi-value blocks, with their legacy context updates. Nested use is
rejected before modifying context or payload because these fragment APIs do not
specify coherent item boundaries. Use `writeTaggedValuePrefix` for a prefix or
`writeRawValue(SerializableString)` for one complete pre-encoded value. The latter
continues to use `asUnquotedUTF8()` and copies its bytes without parsing, including
valid custom types and padded containers. Its caller is responsible for exactly
one valid value. Empty nested raw values and a leading None byte are rejected;
multi-value raw blocks are not newly validated. Trailing nested tags, missing
object values and tags before an object property name fail clearly. Dangling root
prefixes retain their legacy byte pass-through.

Textual raw overloads and Reader string writes remain unsupported. Numeric
property translation, parser behavior, numeric/BCD representation, UTF policy and
general API design are outside this performance change. Java-array overflow
checks bound container storage; actual allocation may still exhaust the heap.
See [writer-rewrite-progress.md](writer-rewrite-progress.md) for the explicit
bug-fix ledger, test evidence and exploratory measurements.

Final independent interoperability, three-fork acceptance, regression measurements,
JFR profiles, depth-dependent copying and reproduction commands are recorded in
[writer-performance.md](writer-performance.md).
