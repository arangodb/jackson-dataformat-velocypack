# Architecture and ownership

Use this map to choose the layer to inspect, not as a substitute for its code.
Class names below are under `src/main/java/tools/jackson/dataformat/velocypack/`.

## Boundaries

The root `pom.xml` builds one JAR, inheriting dependency/plugin management from
`tools.jackson.dataformat:jackson-dataformats-binary`. Runtime integration is with
Jackson core and databind; there is no separate VelocyPack runtime dependency.
`VPackFactory` extends `BinaryTSFactory`, `VPackParser` extends `ParserBase`, and
`VPackGenerator` extends `GeneratorBase`.

| Concern | Owning classes |
| --- | --- |
| Binary source/target adaptation, IO context, immutable factory configuration | `VPackFactory`, `VPackFactoryBuilder` |
| Databind configuration and saved builder state | `VPackMapper` and its `Builder` |
| Explicit native date and MinKey/MaxKey mappings | `VPackModule`, `VPackDate`, `VPackSpecialValue` |
| Outer root framing and source position | `VPackRootReader` |
| Token traversal, nested validation, numeric access, name canonicalization | `VPackParser` |
| Write state, canonical encoding, root emission | `VPackGenerator` |
| Borrowed/owned storage, ranges and recycling | `VPackByteStore`, `VPackPageSupplier`, `VPackRecyclerPageSupplier` |
| Root-local write storage and range transfer | `VPackOutputArena`, `VPackSegmentChain` |
| Physical layout, markers and scalar arithmetic | `VPackLayout`, `VPackMarker`, `VPackConstants`, `VPackNumbers`, `VPackVarInts` |
| Checked arithmetic, errors and limits | `VPackBounds`, `VPackErrors`, `VPackRootBudget`, `VPackReadConstraints`, `VPackWriteConstraints` |
| Application-defined attribute ID/name translation | `VPackAttributeNameCodec` |

## Read path

`VPackFactory -> VPackRootReader -> VPackByteStore.Range -> VPackParser -> tokens`

`VPackRootReader` acquires exactly one self-delimiting root before its first token.
It frames the outer value, **not** the complete nested structure. Byte-array input
borrows the supplied slice; forward-only `InputStream`/`DataInput` sources acquire
an owned, bounded root store. File/Path adapters flow through the factory's binary
source handling. Character input and nonblocking parsing are unsupported.

The parser traverses nested containers with explicit frames. Container lengths,
body boundaries, counts and observed child/key starts drive validation; indexes
are checked before the matching END token and never become tokens themselves.
Object tokens follow body order, not sorted index order. A START token alone is
not evidence that a container is valid.

Root buffers and root budgets are released/reset as roots advance; current scalar
accessors may still depend on the current root. Byte-array slice offsets, root-local
storage offsets and logical stream locations are distinct. Jackson document-length
limits apply across the input, while VPack root budgets restart per root. Follow
`VPackFactory._effectiveReadContext` when changing constraint precedence.

## Write path

`tokens -> VPackGenerator frames -> arena/range chains -> completed root -> target`

The generator collects container bodies in one root-local `VPackOutputArena`.
`VPackSegmentChain` links/transfers ranges rather than repeatedly flattening nested
containers. Completion chooses widths and adds headers/indexes; only the object
index is sorted, leaving body order intact. A completed root is emitted and its
storage/accounting reset before the next root. Scalars can complete immediately.

`flush()` can flush the target but does not publish an unfinished root. `close()`
handles open content according to `AUTO_CLOSE_CONTENT`; closing/flushing the
underlying target also depends on Jackson features and resource ownership.
A failed generator is terminal. Root buffering prevents premature emission, not
partial external writes if the target fails while a completed root is emitted.
`VPackDataOutputStream` preserves the DataOutput target's ownership contract.

## Configuration and native values

`VPackMapper.Builder` installs `VPackModule`, whose handlers are deliberately
limited to `VPackDate` and `VPackSpecialValue`: ordinary Java dates, `Long` and other
enums retain Jackson behavior. Native physical types remain observable through
`VPackType` and the parser/generator native APIs.

Factory copy/rebuild/serialization and mapper rebuild/serialization must preserve
configuration without sharing mutable parser/generator state. Format features can
also be supplied through Jackson read/write contexts; do not assume the factory's
flags are the final per-operation flags. The attribute codec is application
configuration, not a built-in server dictionary.

## Tests, metadata and generated code

Format tests live in `src/test/java/tools/jackson/dataformat/velocypack/`.
`src/test/java/tools/jackson/core/` and `tools/jackson/databind/` contain checked-in
compatibility adaptations, often a named test delegating to a `T32_*` fixture.
JMH lives separately in the test package
`src/test/java/com/arangodb/jackson/dataformat/velocypack/` (`Bench`, `ArangoDocuments`).

JPMS providers/exports are in `src/main/java/module-info.java`; classpath providers
are in `src/main/resources/META-INF/services/`. Both are tested. Maven creates a
classified `t28-test` JAR before tests to exercise packaged providers/metadata and
an external module-path consumer. `PackageVersion.java.in` is the version template;
edit that/POM inputs, not the generated `PackageVersion.java` or JMH harness.

The actual format documents are under `docs/`; literal wire seeds are in
`src/test/resources/tools/jackson/dataformat/velocypack/wire-vectors.txt`.
Historical `doc/...` and `wire-vectors.json` references in existing documentation
do not name files in this tree. Use the checked-in paths above.
