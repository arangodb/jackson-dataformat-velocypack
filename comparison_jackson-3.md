**`jackson-3.2` is the stronger foundation for production adoption, but it is a breaking rewrite with remaining hardening gaps.** Its main advantages are explicit resource budgets, stronger container validation, safer numeric coercions, and better Jackson lifecycle behavior. The older branch accepts more VelocyPack extensions and has some cheaper scalar-writing paths, but several advertised features and standard contracts are not implemented correctly.

This review compares exactly:

- **A — `jackson-3`:** `593e05eb2bd169c6b47ce9b180394b5776dcb869`
- **B — `jackson-3.2`:** `03d782cae2018acdf4bee1fe7be835df257af3b5`

Only repository files under `src/main` were evaluated. Source links below point to extracted, branch-specific snapshots. The checkout was unchanged, and no builds, tests, benchmarks, or runtime probes were run. Findings marked **confirmed** follow directly from static traces; deployment consequences and interoperability claims requiring execution remain qualified.

**1\. Executive assessment**

| Area | A: `jackson-3` | B: `jackson-3.2` |
|---|---|---|
| Input defenses | Bounds checks and incremental allocation, but incomplete container validation | Root framing budgets plus validation of bodies, counts, padding, and indexes |
| Resource controls | Jackson constraints, inconsistently enforced; no format-specific entry/name budgets | Explicit root-byte, entry, and cumulative name-byte budgets |
| Numeric correctness | Exact native representations available, but unsafe coercions and malformed BCD acceptance | Canonical numeric values, range checks, BCD digit validation, and scale guards |
| Output architecture | Contiguous payload with primitive metadata; nested closes move payload bytes | Paged arena with linked ranges; completed child chains transfer without payload copying |
| Format coverage | Tagged values and custom types accepted by default | Tagged values and custom types rejected |
| Jackson integration | Builder/mapper integration present, with lifecycle and capability gaps | Better close/flush behavior, binary capabilities, native-value module, and JPMS declaration |
| Main remaining concern | Correctness and contract defects | Allocation before validation, permissive Unicode behavior, and migration compatibility |

Evidence: A’s parser (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:320), numeric base (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParserBase.java:321), and generator assembly (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1034); B’s root reader (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRootReader.java:233), index validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:818), and generator assembly (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:902).

A’s practical strengths are its broader extension-reading surface, reusable primitive metadata, direct UTF-8 encoding, and bounded-buffer output for large root strings and known-length binaries. These are mechanisms that may benefit particular workloads; they do not establish measured performance superiority. A generator design and scalar paths (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:22).

B’s stronger separation of framing, storage, layout arithmetic, numeric validation, and token traversal makes defensive behavior easier to audit. Its additional complexity buys meaningful correctness checks, although the parser and generator remain large classes. B root-reader responsibilities (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRootReader.java:10), layout responsibilities (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackLayout.java:5).

**Recommendation:** use B as the production-hardening candidate when its restricted wire profile and changed APIs are acceptable. Address the allocation and Unicode findings below before exposing it to adversarial input or relying on strict data preservation. Keep A only where its extension support is necessary, with the identified defects remediated. Static review alone does not establish production readiness.

**2\. Configuration and compatibility**

The feature declarations were traced into their consumers; the matrix describes actual behavior.

| Format feature or policy | A | B |
|---|---|---|
| `FAIL_ON_TAGGED_VALUES` | Default `false`; consulted on root and nested reads | Removed; tag markers rejected |
| `FAIL_ON_CUSTOM_TYPES` | Default `false`; consulted on root and nested reads | Removed; custom markers rejected |
| Read `LENIENT_UTF_ENCODING` | Default `false`, but unused; malformed UTF-8 is replacement-decoded | Removed; replacement decoding remains |
| `WRITE_OBJECT_KEYS_SORTED` | Default `false`; when enabled, physically sorts pairs, including compact objects | Removed; ordinary objects always have sorted indexes, while physical pair order is retained |
| `WRITE_COMPACT_ARRAYS` | Default `true` | Default `false`; overrides equal-length-array selection |
| `WRITE_COMPACT_OBJECTS` | Default `true` | Default `false`; selects compact output instead of indexed output |
| `WRITE_MIN_INT_WIDTH` | Default `true`; primarily controls small-integer markers. Other signed widths remain limited to 1/2/4/8 bytes | Removed; small integers and minimal 1–8-byte scalar widths are selected automatically |
| Write `LENIENT_UTF_ENCODING` | Default `false`, but unused; malformed UTF-16 becomes `?` | Removed; `String.getBytes(UTF_8)` still replaces malformed UTF-16 |
| `USE_EQUAL_LENGTH_ARRAYS` | No separate switch; noncompact equal-size arrays use the unindexed form | Default `true`; applies when compact arrays are disabled |

Declarations: A read features (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackReadFeature.java:8), A write features (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackWriteFeature.java:8), B read features (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackReadFeature.java:5), B write features (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackWriteFeature.java:6). Actual consumers: A container selection (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1047), A integer selection (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:701), B array selection (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:955), B object selection (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1241).

B adds immutable format constraints:

| Constraint | B default | Scope and enforcement |
|---|---:|---|
| Maximum root wire bytes, read/write | 64 MiB | Per root; framing or output-arena charges |
| Maximum entries, read/write | 1,000,000 | Cumulative array elements and object pairs across a root |
| Maximum resolved name bytes, read/write | 16 MiB | Cumulative UTF-8 name bytes across a root |
| Maximum number digits, write | 1,000 | BCD mantissas and textual-number parsing |
| Maximum configurable root/name bytes | `Integer.MAX_VALUE - 8` | Implementation storage ceiling |

The write digit limit is not a universal check on every native integer call: fixed-width integers follow their native encoding paths. BCD and textual numbers explicitly consult it. B read constraints (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackReadConstraints.java:16), write constraints (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackWriteConstraints.java:9), entry/name accounting (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRootBudget.java:57), numeric writing (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:131).

Both branches inherit Jackson factory configuration and pass effective stream/format feature masks from the object read/write context into their implementations. Mapper-builder feature overrides therefore reach parser/generator construction. B’s format constraints and attribute-name codec are factory-owned, and its copy/rebuild paths preserve them. A factory construction (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackFactory.java:192), B factory construction (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactory.java:150), B builder (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactoryBuilder.java:25).

B explicitly reconciles `ObjectReadContext` constraints with the `IOContext` used by Jackson’s parser base. It treats the process-default constraint object as a sentinel, preserving a customized factory limit in that case. This is an identity-based precedence rule: passing that exact default object does not override a customized factory constraint. A performs its byte-array document-length precheck against the factory’s constraints and has no corresponding reconciliation. B precedence rule (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactory.java:195), A byte-array precheck (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackFactory.java:219).

Material public compatibility changes include:

- Package migration from `com.arangodb.jackson.dataformat.velocypack` to `tools.jackson.dataformat.velocypack`, without compatibility wrappers.
- Format identity changes from `"VelocyPack"`/`FORMAT_NAME_VPACK` to `"VPack"`/`FORMAT_NAME`.
- Removal of `VPackMapper.shared()`, `VPackCustomValue`, the public utility/base/bootstrapper types, tagged-prefix writing, and raw byte/block writing.
- A much smaller public constants surface, with renamed constants.
- Changed direct parser/generator constructors; factory copy construction and builder copy construction become protected.
- Addition of `VPackDate`, `VPackSpecialValue`, `VPackType`, `VPackModule`, constraints, and `VPackAttributeNameCodec`.
- Addition of `DataInput` parsing; A explicitly rejects it.
- Removal of factory-builder feature varargs overloads, although mapper-builder feature varargs remain.

Evidence: A factory API (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackFactory.java:42), A shared mapper (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackMapper.java:153), A generator extensions (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:208), B factory API (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactory.java:39), B constants (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackConstants.java:3), B builder API (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactoryBuilder.java:32).

B’s name codec resolves unsigned numeric attribute IDs to actual names, checks encode/decode consistency on output, and errors on unknown IDs. A exposes numeric keys as decimal strings; its uint64 key path uses signed `String.valueOf(long)`, so IDs above `Long.MAX_VALUE` become negative names. B’s `writePropertyId(long)` accepts only nonnegative IDs; the codec’s `BigInteger` mapping can cover the full uint64 domain. A key interpretation (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:778), B codec contract (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackAttributeNameCodec.java:5), B read resolution (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:925), B write resolution (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1203).

Both branches register factory and mapper providers through service resources, updated to the appropriate package. B additionally declares a JPMS module with exports, transitive Jackson dependencies, and both providers. A includes native-image service-resource and constructor-reflection configurations; B removes these resources. That is a confirmed packaging change, **not proof that B fails in native images**. Native-image viability and generated-version integration require build/deployment evidence outside this review. A native-image configuration (/tmp/vpack-static-review/jackson-3/src/main/resources/META-INF/native-image/com.arangodb/jackson-dataformat-velocypack/native-image.properties:1), B module declaration (/tmp/vpack-static-review/jackson-3.2/src/main/java/module-info.java:1), B factory service (/tmp/vpack-static-review/jackson-3.2/src/main/resources/META-INF/services/tools.jackson.core.TokenStreamFactory:1), B version template (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/PackageVersion.java.in:1).

**3\. Internal architecture**

A’s input flow is:

`Factory → Bootstrapper → refill buffer or borrowed byte array → stable root container → ParseFrame traversal`

For stream containers, A reads the declared root into a contiguous array, growing it only as bytes arrive. Nested frames share that array. Byte-array inputs are traversed in place. Root scalars have separate reading paths, and string/binary payloads are materialized. Indexed containers traverse according to index offsets. A bootstrapper (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParserBootstrapper.java:26), root buffering (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:457), frame traversal (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1313).

B’s input flow is:

`Factory → RootReader → borrowed range or owned pages → layout analysis → sequential token traversal → trailing-index validation`

`VPackRootReader` frames exactly one root from byte arrays, `InputStream`, or `DataInput`. Forward-only sources use 16 KiB pages; byte-array sources borrow the supplied slice. The parser scans physical content sequentially and validates indexes against observed boundaries. Root framing precedes token delivery, but nested validation and trailing-index validation occur during traversal. B framing (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRootReader.java:202), storage (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackByteStore.java:13), traversal (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:500).

Consequently, **neither branch offers token-level streaming of a large stream-sourced container before the complete root arrives**. B also frames complete scalar roots. Its `readBinaryValue()` can write directly from stored ranges without allocating a second payload array, but the root is already buffered. Its `readString(Writer)` writes an already-decoded string and then consumes that string value; it does not provide incremental string decoding. B binary access (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:1478), B string access (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:1266).

A’s output flow is:

`Jackson write context → contiguous payload and primitive stacks → header compaction/index construction → completed-root output`

Each nested close may move its complete content to compact the reserved header. Optional sorting may add two further content copies. Arrays of offsets, sorting scratch, and payload capacity remain at their high-water sizes until close. Large root strings and known-length binaries bypass the document payload arena. A assembly and retention (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:88), header compaction (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1068), large scalar output (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:825).

B’s output flow is:

`Jackson write context → encoded scalar/name bytes → paged arena and segment chains → selected header/index → completed-root output`

Child completion transfers linked ranges into the parent without copying child payload bytes. Object bodies retain insertion order; a separate index is sorted by resolved UTF-8 names. Adjacent ranges merge, and root output gathers small slices through an output buffer. B child completion (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:902), chain transfer (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackSegmentChain.java:90), object indexing (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1255).

The performance tradeoff is concrete but unmeasured: B avoids repeated nested payload moves, while introducing range/node/frame metadata and temporary encoded arrays. A uses fewer metadata objects in several paths and directly encodes strings, but growing contiguous arrays and nested compaction can copy substantial content. B retains one recycler-owned page and up to four additional arena pages across roots; A retains full payload/sort capacity until close. B arena retention (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackOutputArena.java:7), recycler ownership (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRecyclerPageSupplier.java:30), A cleanup (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1298).

B’s 64 MiB wire limit is **not a 64 MiB heap bound**. Decoded strings, materialized binary values, index metadata, retained resolved names, temporary arrays, and sorting arrays can coexist with root storage. Known-length binary-stream writing allocates the payload and a framed copy; unknown-length writing additionally collects through `ByteArrayOutputStream`. B binary writing (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:711), binary collection (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1321), parser metadata (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:1118).

Both parsers honor managed-resource or `AUTO_CLOSE_SOURCE` ownership. Both generators honor managed-resource or `AUTO_CLOSE_TARGET` ownership. B also supplies a `DataOutput` adapter that forwards close/flush when the underlying target supports them. B marks generator failures terminal, releases owned buffers, and preserves cleanup failures as suppressed exceptions. A makes output I/O failure terminal, but its general write-error handling is less comprehensive. A input ownership (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:215), A output failure (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1326), B close/failure handling (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:738), DataOutput adapter (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackDataOutputStream.java:47).

**4\. Code quality, complexity, and maintainability**

B improves responsibility separation. Marker classification, bounds arithmetic, varints, layouts, storage ownership, root framing, and BCD handling have dedicated classes. This makes important invariants independently inspectable. A concentrates most of these responsibilities in the parser/generator, with numeric conversions in a custom parser base. B bounds (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackBounds.java:5), B markers (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackMarker.java:3), B numbers (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackNumbers.java:8).

A duplicates scalar decoding between root-input and buffered-container paths. B centralizes scalar decoding, although array/object traversal still contains parallel state-management logic. B also maintains both canonical numeric values and inherited Jackson numeric fields; this duplication is deliberate but adds invariants that future changes must preserve. A root dispatch (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:320), A buffered dispatch (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1386), B scalar dispatch (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:237).

B’s diagnostics generally carry a context and byte offset and distinguish malformed input, constraints, and write failures. However, low-level exceptions often have no parser/generator processor or content reference, and some messages use array terminology on object paths. A’s messages are generally less structured, and nested token locations follow the outer input pointer rather than the individual frame position. B error construction (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackErrors.java:18), B child-boundary messages (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:659), A location accounting (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:258), A location accessors (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParserBase.java:165).

Documentation needs reconciliation with implementation in both branches. A’s UTF feature documentation promises behavior absent from its implementation. B’s factory/package comments say roots are validated before tokens, while its constraint/root-reader documentation correctly explains that nested structures and trailing indexes are validated during traversal. A UTF feature documentation (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackWriteFeature.java:51), B factory documentation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactory.java:32), B precise constraint documentation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackReadConstraints.java:5).

Reproducible size metrics:

| Metric | A | B |
|---|---:|---:|
| Files under `src/main` | 18 | 34 |
| Files ending in `.java` | 13 | 31 |
| Java physical lines, including comments/blanks | 4,721 | 8,040 |
| Java nonblank lines | 4,212 | 7,287 |
| Java source bytes | 179,996 | 318,692 |
| All `src/main` bytes | 180,846 | 319,275 |
| Parser lines | 1,576 | 1,642 |
| Generator lines | 1,361 | 1,665 |

B’s `.java` count includes `module-info.java`; `PackageVersion.java.in` is excluded from Java metrics and included in total file/byte metrics. The increase is approximately 70% in Java physical lines. It reflects added responsibilities and checks, not an independent quality measure.

The counts can be reproduced without checking out either branch:

```
import subprocess

for ref in ("593e05e", "03d782c"):
    files = subprocess.check_output(
        ["git", "ls-tree", "-r", "--name-only", ref, "src/main"],
        text=True,
    ).splitlines()
    blobs = {f: subprocess.check_output(["git", "show", f"{ref}:{f}"])
             for f in files}
    java = [b for f, b in blobs.items() if f.endswith(".java")]
    lines = [line for b in java for line in b.decode().splitlines()]
    print(ref, len(files), len(java), len(lines),
          sum(bool(line.strip()) for line in lines),
          sum(map(len, java)), sum(map(len, blobs.values())))
```

**5\. Correctness and Jackson alignment**

The upstream comparison used official production sources tagged **`jackson-dataformats-binary-3.0.0`** and **`jackson-dataformats-binary-3.2.0`**, including parser bases and Avro implementations where needed. These are reference versions, not an assertion about this repository’s dependency configuration.

| Behavior | A | B |
|---|---|---|
| Signed integers and full uint64 scalar values | Supported; high uint64 values become `BigInteger` | Supported; high uint64 values become `BigInteger` |
| Large integers | Written through BCD outside signed-long range | Native uint64 through its full range; BCD beyond native integer ranges |
| `BigDecimal` | BCD; trailing-zero normalization may alter scale | BCD preserves supplied unscaled value and representable scale |
| Float/double | Float promoted to double; raw double bits written | Same; VPack event copying explicitly preserves double payload bits |
| UTC date | Read as integer; physical date identity lost through generic copying | Integer token plus physical `DATE` identity; native date API/module |
| Min/max sentinels | Embedded strings `"minKey"`/`"maxKey"` | Typed embedded enum values |
| Duplicate fields | Optional Jackson duplicate detector | Optional Jackson duplicate detector using resolved names |
| Multiple roots | Supported by streaming parser/generator | Supported; format budgets reset per root |
| Unknown-length binary writing | Rejected | Supported through bounded collection |
| Raw UTF-8 string writing | Accepted as byte pass-through | `writeRawUTF8String` rejected; `writeUTF8String` accepted |
| Schema / async parsing | Neither supported | Neither supported |

Evidence: A scalar decoding (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:610), A decimal encoding (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackUtil.java:162), B scalar decoding (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:237), B native copying (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:291), B module (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackModule.java:26).

Important Jackson comparisons:

- **Capabilities and standard lifecycle.** Official CBOR, Smile, and Avro generators advertise binary write capabilities and explicitly handle nesting/close/flush policy. B follows those patterns more closely. A advertises generic write capabilities, omits write-depth checks, ignores `AUTO_CLOSE_CONTENT`, and always forwards explicit flush. These are integration gaps, not consequences of VelocyPack framing. [CBOR 3.2.0 generator (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java\#L259)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java#L259>), [Smile 3.2.0 lifecycle (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java\#L1723)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java#L1723>), [Avro 3.2.0 generator (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java\#L174)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java#L174>).
- **Exact numeric access.** B preserves a canonical number across coercion calls and advertises `EXACT_FLOATS`, matching the reference parsers’ binary-number capability pattern. A’s cached conversion flags can change textual access after a narrowing coercion. [CBOR parser (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java\#L125)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java#L125>), [Smile parser base (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileParserBase.java\#L30)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileParserBase.java#L30>), [Avro parser (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroParser.java\#L24)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroParser.java#L24>).
- **Unicode behavior.** CBOR and Smile production parsers have explicit invalid UTF-8 error paths, and their writers consult lenient-surrogate features. Both VPack branches rely on replacement decoding/encoding instead. This does not imply the reference parsers validate every possible Unicode edge, but VPack lacks an equivalent explicit validation policy. [CBOR UTF-8 errors (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java\#L4064)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java#L4064>), [Smile surrogate handling (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java\#L2044)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java#L2044>).
- **Textual-number behavior is format-specific.** CBOR writes `writeNumber(String)` as a string, Smile parses it numerically, and Avro rejects nonnull untyped numeric strings. B’s strict numeric parsing therefore need not match all three. Likewise, VPack BCD, CBOR decimal tags, Smile numeric encodings, and Avro schema-directed values are different representations. [CBOR textual numbers (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java\#L1173)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java#L1173>), [Smile textual numbers (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java\#L1631)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java#L1631>), [Avro textual numbers (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java\#L604)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java#L604>).
- **Streaming differences are partly format-driven.** CBOR supports indefinite containers and Smile uses structural end markers; their generators can emit container content before completion. VPack’s selected layouts require lengths/indexes, motivating buffering. Avro’s schema-directed generator is a different comparison: schema dependence and value staging are not missing VPack features. [CBOR container writing (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java\#L396)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORGenerator.java#L396>), [Smile container writing (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java\#L446)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileGenerator.java#L446>), [Avro schema construction (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java\#L86)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/avro/src/main/java/tools/jackson/dataformat/avro/AvroGenerator.java#L86>).

The static malformed-input traces establish these differences:

| Input/contract edge | A | B |
|---|---|---|
| Truncated payload or oversized declared length | Checks bounds; stream allocation grows with received bytes | Rejects through framing/budgets and checked ranges |
| Nested value crossing parent body | Explicit parent-content bounds | Explicit parent-body bounds |
| Duplicate/omitted/interior index offsets | In-range offsets can be accepted without coverage validation | Checks observed starts and object-index bijection |
| Unequal child sizes in unindexed arrays | Does not verify equal sizes | Verifies sizes during traversal |
| Padding | Skips zeros within header region | Checks permitted padding positions |
| Invalid BCD nibbles | Converted into decimal text | Rejected |
| Extreme BCD exponent / integer conversion scale | Unguarded negation/conversion paths | Explicit exponent and conversion-scale guards |
| Invalid UTF-8 / UTF-16 | Replacement behavior | Replacement behavior |
| Trailing roots | Exposed through further parser calls | Exposed through further parser calls |

Evidence: A container boundaries (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:527), A indexed traversal (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1298), B layout validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackLayout.java:130), B equal-size validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:535), B BCD validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackNumbers.java:168).

Both support duplicate detection when enabled; neither imposes unconditional uniqueness. Multiple-root support also means accepting a first value does not establish that the entire source contains exactly one value. Whole-document policies must include full traversal or an appropriate databind trailing-token policy.

**6\. Weaknesses and production readiness**

Severity below reflects plausible production impact, not an executed exploit or measured failure rate.

**A — confirmed findings**

| Severity | Finding and consequence | Recommended remediation |
|---|---|---|
| High | **Incomplete indexed-container validation.** Offsets need only fall within content; repeated offsets, omitted body values, and offsets into another value can escape structural validation. Unindexed arrays also lack equal-size checks. | Traverse physical content and validate indexes against observed boundaries, as B does. |
| High | **Malformed BCD can become a different number.** Nibbles above 9 are appended as integers: `0xAF` becomes decimal text `"1015"`. Exponent negation and decimal-to-integer conversion lack scale guards. | Validate nibbles/nonempty mantissas; guard exponent/scale extremes before conversion. |
| High | **Unchecked double-to-integer coercion.** Java casts silently saturate overflowing values and turn NaN into zero. | Reject nonfinite/out-of-range values using Jackson coercion exceptions. |
| High | **Write constraints do not enforce nesting depth.** Container starts grow state without checking the configured write-depth limit. | Validate depth before opening each container. |
| High | **Close silently discards open container content.** The generator inherits a close method whose hook does not finish containers; `AUTO_CLOSE_CONTENT` is never consulted. | Implement explicit close-content policy and preserve failures during cleanup. |
| Medium | **UTF flags do not control behavior.** Both advertised strict-default flags are unused; malformed text is silently replaced. | Implement the documented policy or remove the misleading flags. |
| Medium | **Token budgets are overcounted.** Nested scalar/container-start paths call `_updateToken`, then outer `nextToken()` calls it again. | Update token state/count once per returned token. |
| Medium | **Parser close bypasses `IOContext.close()`.** Buffer arrays are released, but the custom close override omits the base lifecycle step that returns the recycler to its pool. | Delegate through the base close lifecycle or close the context explicitly. |
| Medium | **Numeric text depends on accessor order.** After coercing `1.25` to an integer, `_numberAsString()` prefers the cached integer and can return `"1"`. | Preserve and format the original canonical value. |
| Medium | **Explicit flush ignores `FLUSH_PASSED_TO_STREAM`.** The target is flushed unconditionally. | Respect the configured stream feature. |

Sources: A index behavior (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1298), BCD decoding (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackUtil.java:216), numeric coercions (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParserBase.java:321), container starts (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:262), generator close hook (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1275), UTF encoding (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:793), token updates (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:258), parser close (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParserBase.java:195), number formatting (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1047), flush (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java:1265).

The inherited behavior behind the lifecycle/token findings is present in the official core references: `_updateToken` increments configured token counts, and the base close implementations include context cleanup. [Jackson core 3.0.0 token updates (https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/ParserMinimalBase.java\#L1171)](<https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/ParserMinimalBase.java#L1171>), [parser close (https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/ParserMinimalBase.java\#L308)](<https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/ParserMinimalBase.java#L308>), [generator close (https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/GeneratorBase.java\#L331)](<https://github.com/FasterXML/jackson-core/blob/jackson-core-3.0.0/src/main/java/tools/jackson/core/base/GeneratorBase.java#L331>).

For a concrete, **unexecuted static example**, the bytes `06 06 01 30 31 03` declare an indexed array with one entry, two body values, and an index pointing to the first. A follows that one index and accepts completion without checking the omitted body value. B encounters leftover content after the declared count.

A also has no format-specific root/entry/cumulative-name budget. With Jackson’s document limit unset, progressively received binary/container data can grow toward Java array limits. Its incremental allocation avoids immediate allocation from a malicious declared length, but does not provide a modest total-memory ceiling. A payload growth (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1123), length checks (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:1181).

**B — confirmed weaknesses and suspected defects**

| Severity/status | Finding and consequence | Recommended remediation |
|---|---|---|
| High — confirmed hardening gap | **Allocation precedes limits on several text paths.** `writeString(String)` allocates UTF-8 bytes before checking the root budget; names are encoded before name-budget checks; parser strings/names are decoded before individual length validation. Small configured limits therefore do not prevent large temporary allocations. | Preflight encoded lengths where possible; decode incrementally with character limits; validate remaining name budgets before materialization. |
| High where strict text preservation is required — confirmed | **Unicode remains permissive.** Malformed UTF-8 is replacement-decoded, malformed UTF-16 is replacement-encoded, and `writeUTF8String` does not validate its bytes. Distinct malformed names may collapse to the same decoded/wire name. | Define and implement explicit Unicode validity policy for names and values. |
| Medium — confirmed | **Whole-root and scalar materialization remain substantial.** Binary-stream writes collect full payloads and framed copies; strings are decoded eagerly. | Document peak-memory behavior; use paged/direct encoding and incremental decoding where useful. |
| Medium — confirmed validation limitation | **Trailing-index validation requires traversal to container completion.** Early close can skip those checks despite comments implying full validation before token delivery. | Correct documentation; require complete traversal for validation-sensitive use, or add an explicit validation mode. |
| Low — suspected inefficiency | **A zero-byte bulk read at a page boundary can acquire an extra page.** `appendFrom` allocates before reading, and the fallback `append` can allocate again because the logical size did not advance. | Reuse an already-acquired empty tail page; verify with a focused ownership/allocation test. |

Sources: B string encoding (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:571), name encoding (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1228), name decoding/validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:954), string decoding/validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:1179), replacement decoding (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackByteStore.java:148), UTF-8 writing (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:669), binary collection (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackGenerator.java:1321), completion validation (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:818), page acquisition (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackByteStore.java:253), zero-read fallback (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackRootReader.java:526).

Additional migration risks deserve explicit qualification:

- **Wire bytes and field traversal order change.** B changes container defaults, positive-integer encodings, decimal scale handling, and object traversal from index order to physical order. Byte equality or field-order assumptions need migration evidence.
- **Accepted inputs narrow.** Tags/custom values are rejected; numeric keys need a codec; malformed layouts accepted by A may fail in B.
- **Eight-byte sorted-object interpretation differs.** A treats width-eight indexed objects as having a trailing count; B distinguishes markers and treats sorted objects as having a leading count. This is a confirmed implementation disagreement. Determining interoperability with external producers requires format fixtures and runtime evidence.
- **`releaseBuffered()` becomes terminal in B.** It clears traversal and makes later `nextToken()` return null. Official CBOR/Smile implementations copy buffered bytes without the same terminal-state transition; callers cannot assume identical behavior.
- **Native-image and serialization configurations need validation.** B removes native-image metadata, and its optional codec interface does not require serializability even though the factory stores the codec and implements `Serializable`.

Evidence: A width-eight count handling (/tmp/vpack-static-review/jackson-3/src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackParser.java:553), B marker-specific count handling (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackLayout.java:157), B terminal buffer release (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackParser.java:1569), [CBOR buffer release (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java\#L392)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/cbor/src/main/java/tools/jackson/dataformat/cbor/CBORParser.java#L392>), [Smile buffer release (https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileParser.java\#L167)](<https://github.com/FasterXML/jackson-dataformats-binary/blob/jackson-dataformats-binary-3.2.0/smile/src/main/java/tools/jackson/dataformat/smile/SmileParser.java#L167>), B stored configuration (/tmp/vpack-static-review/jackson-3.2/src/main/java/tools/jackson/dataformat/velocypack/VPackFactory.java:35).

The recommended remediation order is: fix A’s structural/numeric/lifecycle defects wherever A remains deployed; harden B’s checks before allocation and Unicode policy; then verify migration fixtures covering container layouts, numeric extremes, compressed names, native values, multiple roots, and failure/close behavior. Throughput, peak heap, build correctness, release quality, native-image support, and deployment reliability remain outside the conclusions of this static production-source review.
