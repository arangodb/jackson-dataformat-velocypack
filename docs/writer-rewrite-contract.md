# Writer rewrite contract (stage 1)

The rewrite will replace per-value capture streams and recursively materialized
container byte arrays with one generator-owned growable payload `byte[]` and
primitive open-container/index stacks. The small IOContext output buffer remains
separate. Production code, public APIs, wire defaults and Bench semantics remain
unchanged in this stage. No speedup has been measured for a rewritten writer.

## Frozen reference and test scope

`src/test/java/com/arangodb/jackson/dataformat/velocypack/LegacyVPackGenerator.java`
is the original source with only `VPackGenerator` renamed, including constructor
and fluent return type occurrences. It stays in the same package, so no visibility
or import changes are necessary. Original SHA-256:
`cc6d9cc29fd2193f283fdc0fd99b6247c37cec91af44d026d68b3c7a3ff16b7b`.
`WriterReplay` dispatches the same call sequence to each writer, including both
constructors. There is no production oracle or backend selector.

`VPackWriterDifferentialTest` requires byte identity for valid output. It covers
all 16 masks of sorted keys, compact arrays, compact objects and small integer
encoding, separately tests both LENIENT states, the scalar overloads, primitive
array shortcuts, empty/equal-width/mixed/nested containers, duplicate keys,
numeric property IDs, Unicode and both string headers, multiple roots, caller
buffer mutation, output offsets, flush/close, a seed-42 benchmark fixture including
1,000 documents and 24 bounded seeded trees per layout. `WriterReferenceWire`
uses independent wire arithmetic and literal vectors, with no production helpers
or constants. The normal differential suite is bounded; no timings are asserted. Both
recyclable and caller-owned custom buffers and stream close/flush flags are tested.
The frozen source is also checked against its original hash after reversing the
class rename, so accidental oracle edits fail the suite.

`LegacyWriterCharacterizationTest` records bugs and ambiguous calls against the
frozen writer alone. These assertions must not constrain a future writer to lose
tags or produce invalid wire data. Concrete corrected tag vectors below are
independent expected bytes for the later implementation.

## Valid observable behavior to retain

Defaults: compact arrays and objects on; sorted keys off; small integer encoding
on. Arrays/objects with no entries have single-byte `01`/`0a` representations in
all layouts. Noncompact equal-width arrays omit their index. Indexed widths are
1/2/4/8 and are selected using **total encoded container length**, including header
and index table. Compact lengths use forward VByte; counts use reversed VByte.

The equation for compact total length is
`L = 1 + vbyteWidth(L) + contentLength + vbyteWidth(count)`.
With count 1, content lengths 124/125 produce totals 127/129; 16379/16380 produce
16383/16385. Totals 128 and 16384 are skipped at these crossings because enlarging
the header increases the total. Tests also cross counts 127/128 and 16383/16384
for both arrays and objects. Indexed array tests with null plus a binary value
cross totals 255/260 at binary lengths 247/248 and 65535/65544 at lengths
65522/65523. One-pair indexed objects cross 255/259 and 65535/65542. Equal-width
no-index arrays cross the same width boundaries independently. Index entries are
relative to the enclosing container start; lengths include index bytes.

Container compact/sorted features are sampled when that container **closes**.
Changing them while the container is open affects its eventual layout/order.
Integer encoding is sampled when the scalar is **written**, and already written
integers do not change when features change later. WRITE_MIN_INT_WIDTH off
suppresses the one-byte small integer codes; it does not force 8-byte integers.
Normal signed and unsigned integers use widths **1/2/4/8**, not all widths 1..8.
Signed integers use two's complement little endian; floats widen to doubles;
double raw bits (including negative zero and NaN payload) are preserved.
BigInteger beyond signed long range uses BCD. Decimal scale normalization follows
the existing API-specific paths; the rewrite must not change numeric semantics.

String encoding currently delegates to `String.getBytes(UTF_8)`. Unpaired UTF-16
surrogates become ASCII `?` (`3f`), so an isolated surrogate is `41 3f` on wire.
This holds with LENIENT_UTF_ENCODING on **and** off, including char arrays,
SerializableString values and property names. The feature is not consulted despite
its Javadoc promising failure/replacement behavior. Preserve the implementation's
observable behavior during this rewrite. UTF8/rawUTF8 APIs copy supplied bytes;
they do not validate/re-encode them. Invalid caller-provided UTF-8 is outside the
valid wire identity corpus. Short strings cover 0..126 UTF-8 bytes; longer strings
use `bf`, an 8-byte little-endian byte length and the bytes.

Sorted objects reorder physical key/value pairs as well as their index. Ordering
is unsigned byte comparison of UTF-8 names; equal names retain insertion order
(stable sort). Numeric property IDs are unsigned integer keys, and their legacy
sorting compares encoded bytes rather than a new numeric ordering. Duplicate
names are permitted with strict duplicate detection off; strict detection still
must reject them when enabled.

Generator input arrays/bytes/chars are copied before the call returns; modifying
caller buffers before container close must not alter output. Root values remain
concatenated in call order. Root `writeRaw(byte)` and `writeBytes` pass through
exact bytes, including multi-value byte blocks. These calls increment the write
context's value position. They must remain supported as root pass-through; do not
reinterpret a block as a new single-value restriction.

The custom constructor's offset is a prefilled output tail: bytes in `[0,offset)`
are emitted, not skipped. Offset equal to the buffer length flushes the full
prefix before appending. Flush emits root bytes and flushes the stream; unfinished
container contents stay buffered. Retain AUTO_CLOSE_TARGET, managed-resource and
FLUSH_PASSED_TO_STREAM behavior, idempotent close and buffer ownership. The
IOContext buffer is an independent output concern, not the new payload arena.

## Verified legacy bugs and byte-identity exceptions

`writeTaggedValuePrefix` writes `ee` + one tag byte for 0..255, otherwise `ef` +
an 8-byte little-endian tag. It is a **prefix operation**: it does not consume a
value context slot or finish a value. At root it correctly prefixes scalars and
composites; before scalar array items it also works. Tags can be lost before
object scalar **and composite** values because `_verifyValueWrite` resets their
capture. Tags before composite array elements are not included in the composite
and can be discarded or drift to a later scalar. Composite array emission does
not finish/reset the pending scalar capture.

Examples below use default compact layouts and tag 7. Spaces are cosmetic.

| Calls | Frozen bytes | Independent corrected bytes |
| --- | --- | --- |
| object: name `a`, tag 7, null | `14 06 41 61 18 01` | `14 08 41 61 ee 07 18 01` |
| object: name `a`, tag 7, empty array | `14 06 41 61 01 01` | `14 08 41 61 ee 07 01 01` |
| array: tag 7, empty array | `13 04 01 01` | `13 06 ee 07 01 01` |
| array: null, tag 7, empty array, true | `13 08 18 01 ee 07 1a 03` | `13 08 18 ee 07 01 1a 03` |

Correct placement of explicit tag prefixes is the exception to byte identity.
The frozen untagged output can be structurally valid while having lost requested
tag semantics. Tags after an array composite and before a following scalar work;
tags after object values and before the next property name are lost; this placement
is characterized without specifying a new key/prefix meaning. A dangling
tag after a root value emits incomplete bytes; a dangling tag inside a container
may be silently discarded. There is no valid following value to preserve in those
calls, so they establish no new trailing-fragment semantics.

## Ambiguities and invalid output excluded from identity

`writeRaw(byte)` and `writeBytes` each call `_verifyValueWrite`, unlike
`writeTaggedValuePrefix`. Neither calls `_valueFinished`. Inside an array, raw
bytes can be combined with a later scalar in a single captured item, yielding a
count that disagrees with the number of actual values. Raw-only array contents
are discarded (empty array `01`); raw bytes before/after a composite can also be
dropped. Raw tag bytes before a scalar happen to work in some array sequences,
but this does not establish general prefix/fragment semantics.

Inside an object, raw after a name consumes the value context slot without adding
a value. A following scalar or composite raises StreamWriteException (expecting
field name). Closing that object may raise IndexOutOfBoundsException because
keys and values have different counts. Raw immediately after a completed object
value also raises StreamWriteException. Both raw API forms are tested before and
after scalar/composite values at root, array and object positions.

Do not invent fragment semantics or require these invalid legacy bytes. The
future implementation needs a deliberate documented decision for ambiguous
container raw calls; the current contract preserves root pass-through and valid
explicit tags. `writeRawValue(SerializableString)` *does* finalize a captured
value; a single pre-encoded value is valid, a multi-value block inside a container
is not. Textual raw overloads and the Reader string overload remain unsupported. Negative property IDs, malformed
call sequences, incomplete tags, invalid UTF-8 and overflow-sized allocations are
outside the valid output corpus, not an invitation to change public APIs.

## Performance acceptance

The target is at least 5x lower average time for the unchanged
Bench.treeWriteCursor at batchSize=1000. The historical 32.904 ms/op implies
6.581 ms/op arithmetically, but acceptance is **fresh baseline / new result >= 5**
in the same environment with frozen dependencies. Use `scripts/bench-tree-write.sh`,
org.openjdk.jmh.Main, three forks, all three formats, one thread, 3 x 2 s warmups,
5 x 2 s measurements, fixed 512 MB heap and GC profiler, no JFR. Compare allocation
and small/large tree document, POJO cursor, streaming cursor and sequence results
as regression evidence. Keep fixture generation, setup validation and fresh
output-array creation identical. Environment and results are recorded in the
progress document and each unique run directory.
