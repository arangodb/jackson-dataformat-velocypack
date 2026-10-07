# Writer rewrite progress

Stage 4 independently validates the wire format and completes the final
measurements. The final short-String ASCII specialization retains shared payload,
primitive stacks, direct UTF-8 fallback and independent output ownership. The
fresh original-writer comparison measures **5.278819x** speedup: **34.268252 ±
0.365802 ms/cursor** before, **6.491651 ± 0.124966 ms/cursor** after, with a
conservative interval-endpoint speedup envelope **5.123834–5.439888x**. Allocation
is **6,110,199 B/cursor**, output remains **1,144,774 B**, and every operation
returns a fresh owned byte array. **At least 5x was MEASURED.**

All 146 focused tests pass. The full suite retains the same 22 pre-existing
failing methods. The compatible pinned official C++ build checks all 40 fixtures; the current
pinned build checks 37 supported fixtures. Removed unsorted-index layouts and BCD
validation limitations
are explicitly distinguished. See [writer-performance.md](writer-performance.md)
for the final before/after matrix, profiles, depth experiment, limitations and
exact reproduction commands. The stage-1, stage-2 and stage-3 records below
remain historical evidence; their non-acceptance statements describe those stages.

The stage-1 and stage-2 records below remain historical evidence. Statements
about their implementation and measurements describe those stages. Current
architecture and API limitations are in [writer-implementation.md](writer-implementation.md).

## Repository and build environment

No AGENTS.md exists in /workspace or its ancestors. Starting checkout:
`622449bb2933604b4d090c86e0e0748b556334a1`, clean (`git status --porcelain=v1`
empty). Existing Bench.java/ArangoDocuments.java contents were preserved, including
any earlier user work already committed. Historical `jmh-result/` artifacts were
read and left unchanged. Their hashes, original source hashes, JVM/Maven/OS/CPU
information and the effective-POM hash are recorded in
[writer-rewrite-environment.json](writer-rewrite-environment.json).

JDK: Eclipse Temurin 21.0.12.1+1-LTS. Maven: 3.9.6. Compiler release/source/target:
17, UTF-8, parameters enabled; compiler plugin 3.8.1; Surefire 3.2.5; JaCoCo 0.8.14;
enforcer 3.5.0 rules passed. The full effective configuration is in the run
directory's `effective-pom.xml`, with the dependency tree and classpath alongside it.

pom.xml requests Jackson BOM 3.2.0-SNAPSHOT. The locally cached timestamped BOM
`3.2.0-20260608.224911-11` sets its internal Jackson version to **3.2.0**, and
annotations to **2.22**. Thus a snapshot *BOM* request can resolve release *JARs*;
the supplied report is consistent with this cached configuration. Stage 1 makes
that choice explicit using **`-Djackson.version=3.2.0`**, importing the release BOM.
This is a command-line override, not a pom.xml edit or an attempt to fix failing
tests. Before and after measurements must use this same override and the locked
checksums. Jackson core/databind/Smile resolve to 3.2.0; annotations 2.22; JMH 1.37;
SLF4J 2.0.17; JUnit Jupiter/Platform 6.1.0-RC1; AssertJ 3.27.6; Mockito 5.21.0.
All resolved test dependency JAR/POM hashes and release BOM hashes are frozen in
[scripts/tree-write-dependencies.json](../scripts/tree-write-dependencies.json).
The script refuses to time a checksum mismatch. Maven builds run offline after
initial local dependency resolution; use the captured dependency tree to provision
these versions if another machine's local cache is incomplete.

## Commands and test status

Initial unchanged attempt: `mvn -B test`, logged to
`target/writer-stage1/requested-build.log`. Compilation succeeded, but Surefire
failed discovery on stale `jmh_generated.Bench_readTreeJson_jmhTest`, with
`NoClassDefFoundError: Bench$Data`. No tests executed in that attempt. Existing
classes, generated sources, Maven status and discovery dumps were moved intact to
`target/writer-stage1/preexisting-build/`; no `mvn clean` deleted historical results.

Fresh full original suite: `mvn -B -Djackson.version=3.2.0 test`, logged to
`target/writer-stage1/original-suite.log`; XML/text reports are preserved in
`target/writer-stage1/original-suite-reports/`. Result: **6,404 tests, 12 failures,
10 errors, 3 skipped**, exit 1. The repeated ThreadSafetyWithConverterMixin5813Test
has 50 failing repetitions in its XML; Surefire's final summary counts its method
as one failure. All existing VPack generator tests passed. The 22 distinct failing
test methods and messages are recorded by name in
[writer-rewrite-preexisting-failures.json](writer-rewrite-preexisting-failures.json).
These failures have not been hidden, disabled or repaired in this stage.

Targeted new tests:
`mvn -B -o -Djackson.version=3.2.0 -Dtest=VPackWriterDifferentialTest,LegacyWriterCharacterizationTest test`.
The initial vectors exposed four hand-counted length errors in the new tests;
those expectations were corrected by counting headers/content/index/trailer bytes.
The suite then passed. An overlapping local compile attempt caused a transient
"No tests matching pattern" build failure; a serial rerun passed. Final validation
status and final full-suite results are recorded below after all test additions.

## Contract decisions

Valid outputs require byte identity across all 16 layout masks. LENIENT_UTF_ENCODING
is separately tested both ways and preserves String.getBytes(UTF_8)'s `?`
replacement behavior. Compact/sorted features act at close, integer encoding at
scalar write; widths are 1/2/4/8. Offset means prefilled output tail, not skipped
bytes. Sorted duplicates retain stable insertion order. Scalar arrays and caller
bytes/chars are copied immediately. Root raw blocks, including multiple encoded
values, pass through unchanged. Explicit tags are prefixes, unlike raw methods
which consume value context slots.

Known lost/misplaced tags have independent corrected vectors and are excluded from
byte identity. Invalid container raw captures/counts and dangling prefixes are
characterized only on LegacyVPackGenerator. The rewrite must not invent new raw
fragment semantics. Ambiguous placements remain explicitly undecided. See the
contract document for exact wire examples and tests.

## Baseline capture

Command:
`FORKS=3 REGRESSION_FORKS=1 scripts/bench-tree-write.sh all`.
This builds the test classpath locally and invokes **org.openjdk.jmh.Main** with
exact anchored benchmark matches. Acceptance: treeWriteCursor, batchSize=1000,
JSON/SMILE/VPACK, `-t 1 -wi 3 -w 2s -i 5 -r 2s -f 3`,
`-jvmArgs '-Xms512m -Xmx512m' -prof gc -rf json -foe true`. No JFR and no JaCoCo
agent in the timing JVMs. Regression runs use the same timing/heap/profiler settings
with **one fork** at batch sizes **1 and 1000** for treeWriteDocument,
pojoWriteCursor, streamingWriteCursor and sequenceWrite, all three formats.
The script defaults regression forks to FORKS; one fork was explicitly selected
for these auxiliary baselines. treeWriteDocument always writes one document;
batchSize still changes trial setup as Bench currently defines it.

Each script invocation creates a unique `target/jmh-result/tree-write-...` directory.
The first attempt `tree-write-20261002T222916Z-GYyxok` stopped during the offline
build because maven-help-plugin 3.5.1 lacked cached transitive dependencies
(maven-plugin-tools-generators 3.13.1 and xstream 1.4.20). It contains no results.
The script now explicitly uses the already resolved help plugin 3.5.2; benchmark
and application dependencies did not change.

The measurement run is
`target/jmh-result/tree-write-20261002T223110Z-Ku1k3Z/`:
`baseline.json`, `baseline.log`, `regressions.json`, `regressions.log`,
`commands.json`, `metadata.json`, `resolved-checksums.json`, `dependencies.txt`,
`classpath.txt`, `effective-pom.xml`, `dirty.patch` and exit-code files.
The source/dirty state in metadata describes the launch; a few additional test
coverage checks and the final progress documentation were added during measurement.
The production writer, benchmark and fixture were unchanged throughout.

Timing acceptance compares the future writer with this **fresh same-environment**
VPACK score divided by five, using the identical frozen dependency lock and
benchmark call semantics. The historical 32.904 ms/op and derived 6.581 ms/op are
context only. Baseline/regression scores and final test verification follow below.

## Fresh acceptance baseline (completed)

| Format | Mean ms/op | JMH error ms/op | Allocated B/op |
| --- | ---: | ---: | ---: |
| JSON | 5.334817 | 0.290689 | 3494651.544 |
| SMILE | 3.047668 | 0.099267 | 1439710.027 |
| VPACK | 37.206990 | 0.588794 | 65064952.509 |

VPACK baseline: **37.206990 ms/op**; later 5x acceptance threshold:
**7.441398 ms/op**. Scores use the unchanged original production writer; the
historical 32.904 ms/op was not used as this run's baseline. JMH primary units
are us/op in the JSON; this table converts to ms/op. Errors are JMH's reported
99.9% confidence interval half-widths. Baseline invocation exited 0.

## Auxiliary regression baselines (completed)

Both JMH invocations exited 0; all 27 cases completed Bench setup validation.
The raw JMH results remain in the unique target run directory; a portable summary
is in [writer-rewrite-before-results.json](writer-rewrite-before-results.json).
The auxiliary run used one fork per case; later regression checks should use the
same settings and interpret small differences with that limitation.

| Benchmark | Format | Batch size | Mean us/op | Allocated B/op |
| --- | --- | ---: | ---: | ---: |
| pojoWriteCursor | JSON | 1 | 4.473 | 3568.0 |
| pojoWriteCursor | SMILE | 1 | 3.707 | 3704.0 |
| pojoWriteCursor | VPACK | 1 | 38.769 | 67896.1 |
| pojoWriteCursor | JSON | 1000 | 4489.513 | 3727139.1 |
| pojoWriteCursor | SMILE | 1000 | 2791.941 | 1672203.3 |
| pojoWriteCursor | VPACK | 1000 | 32221.478 | 65408911.0 |
| sequenceWrite | JSON | 1 | 3.578 | 4336.0 |
| sequenceWrite | SMILE | 1 | 2.927 | 3856.0 |
| sequenceWrite | VPACK | 1 | 28.414 | 46144.1 |
| sequenceWrite | JSON | 1000 | 4335.422 | 3843513.8 |
| sequenceWrite | SMILE | 1000 | 2814.838 | 1731994.2 |
| sequenceWrite | VPACK | 1000 | 29100.753 | 46355476.2 |
| streamingWriteCursor | JSON | 1 | 4.383 | 4968.0 |
| streamingWriteCursor | SMILE | 1 | 3.133 | 4672.0 |
| streamingWriteCursor | VPACK | 1 | 35.626 | 68760.1 |
| streamingWriteCursor | JSON | 1000 | 4314.005 | 3737649.7 |
| streamingWriteCursor | SMILE | 1000 | 2354.195 | 1594359.5 |
| streamingWriteCursor | VPACK | 1000 | 31403.652 | 65254020.3 |
| treeWriteDocument | JSON | 1 | 3.292 | 2848.0 |
| treeWriteDocument | SMILE | 1 | 2.537 | 2704.0 |
| treeWriteDocument | VPACK | 1 | 27.789 | 44840.1 |
| treeWriteDocument | JSON | 1000 | 3.909 | 2848.0 |
| treeWriteDocument | SMILE | 1000 | 2.611 | 2704.0 |
| treeWriteDocument | VPACK | 1000 | 27.887 | 44720.1 |

## Final validation and next stage

Final full command: `mvn -B -o -Djackson.version=3.2.0 test`, exit 1.
`target/writer-stage1/final-suite.log` and preserved
`target/writer-stage1/final-suite-reports/` show **6,421 tests, 12 failures,
10 errors, 3 skipped**. All **17 new test methods passed** (13 differential
methods and 4 characterization methods); the differential class took 0.667 s
in this suite. Comparing XML method names and failure/error types found **no
new or removed failures** relative to the original suite. The 22 existing
failures remain listed by name and message in the linked JSON.

A final offline configuration check with **no Jackson override** also completed:
`mvn -B -o org.apache.maven.plugins:maven-help-plugin:3.5.2:effective-pom
-Doutput=target/writer-stage1/requested-effective-pom.xml
org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree
-DoutputFile=target/writer-stage1/requested-dependencies.txt
org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath
-Dmdep.includeScope=test
-Dmdep.outputFile=target/writer-stage1/requested-classpath.txt`.
Its dependency tree and runtime test classpath are identical to the explicit
3.2.0 override. The requested effective POM is retained and hashed separately;
the release BOM override freezes the choice for future measurements.

Verification also passed: script syntax (`bash -n`), frozen source equivalence
after class rename, dependency checksum lock, exact JMH parameters and case
counts, historical artifact checksums and `git diff --check`. Final dirty state
contains only new documentation, scripts and five test-only Java files.
`git diff --exit-code -- src/main pom.xml` is empty; Bench, ArangoDocuments
and historical jmh-result files also have no changes. Initial source hashes
and final source hashes are under `target/writer-stage1/`. No stage 2 production
work has started, and no speedup is claimed.

Stage 2 should implement the growable payload and primitive container/index
stacks, retain close-time feature decisions and separate output-buffer behavior,
and satisfy the valid-output differential corpus. Implement explicit tag
placement using the independently specified corrections. Decide ambiguous
container raw calls deliberately rather than copying malformed captures or
inventing implicit fragment semantics. Then rerun correctness checks and the
same frozen acceptance/regression measurements before reporting performance.


## Stage 2 implementation (completed)

Only `VPackGenerator.java` changed in production. The frozen writer, original
stage-1 differential/characterization tests, Bench, ArangoDocuments, pom.xml,
dependency lock and historical `jmh-result/` files remain unchanged. The initial
stage-2 workspace already contained untracked `docs/` and `scripts/`; their
existing contents were preserved, with this progress update and a new portable
[stage-2 results summary](writer-rewrite-stage2-results.json) added.

Implemented invariants:

- One payload `byte[]` is allocated lazily for the first root container and grows
  geometrically (approximately 1.5x). It is independent of the IOContext output
  buffer. Root scalars still use the original output buffer. Only this generator
  retains its payload capacity between roots; logical payload/offset positions
  reset immediately on root completion.
- Primitive arrays hold open-container start/kind, offset-stack base, completed
  direct-child count and next-item boundary. A shared primitive offset/key-end
  stack holds only direct children of open containers. Parent item/pair starts
  precede a child's segment. Closing a child retires its entire segment, leaving
  parent boundaries and all open ancestor starts valid. Child sizes come from
  boundaries, without rescanning values or using a parser to serialize them.
- Each opening reserves **nine internal bytes**. Closing samples compact/sorted
  features, chooses the final representation and removes the reservation. Empty
  containers become the original one-byte empty type. Compact size solves the
  fixed-point equation with checked `long` sums, writes minimal forward length
  VBytes directly, and writes the count directly in reverse order. There are no
  VByte result arrays or padded/nonminimal compact headers.
- Header compaction moves content left with at most one overlapping
  `System.arraycopy` per nonempty container. Indexed arrays, equal-width arrays
  without indexes, and sorted/unsorted indexed objects preserve the legacy total
  size/width selection for 1/2/4/8-byte fields. Index bytes and the width-8 trailing
  count are included before choosing width or narrowing. Package-visible size
  helpers exercise width 8 and overflow without enormous allocations. Payload
  sizes above `Integer.MAX_VALUE - 8` fail with `IllegalStateException` before
  integer overflow; allocation can still naturally fail if the heap is exhausted.
- Sorted objects retain stable **physical pair order**, not just sorted indexes.
  A primitive stable merge sort compares encoded key spans, skipping string
  headers while retaining numeric IDs' legacy encoded-byte comparison. It has no
  extracted key arrays, boxed IDs, or per-comparison allocation. Already ordered
  pairs avoid scratch copies. Reordering uses generator-owned reusable scratch.
  Its extra copy cost is **one content pass into scratch plus one content pass
  back**, in addition to the independent header-compaction pass; primitive merge
  sorting also copies IDs. Payload growth copies are a separate cost.
- Nested strings, keys, scalars, caller binary/UTF-8 bytes and raw complete values
  go directly into the shared payload. String.getBytes(UTF_8) and BCD helpers
  remain temporary encoders with their prior representation rules. Streamed binary
  data reads directly into the payload, or through the existing output buffer at
  root, with no per-binary temporary array. Root streamed binaries retain their
  successful-call buffering/visibility for small/large inputs and custom offsets.
  Fixed-width numeric data and BCD exponents reserve space and use bulk little-
  endian writes rather than routing each byte through the container stack.
- Primitive-array shortcuts use the same start/scalar/end backend, advance each
  Jackson item context once, and finish/count each item once. Defaults, scalar
  integer widths, raw NaN bits, surrogate replacement, duplicate detection,
  current-value/context APIs and close-time feature decisions remain intact.
- On root-container completion, pending root-output bytes flush first and the
  actual payload range is passed directly to `OutputStream.write(byte[],off,len)`.
  No root-sized copy is made by the generator. Unfinished headers stay private;
  completed containers are immediately visible. `streamWriteOutputBuffered`
  continues to report only the output tail. Original IOContext release paths,
  custom prefix/offset ownership and flush/close flags remain intact.
- ContainerState, capture streams/lists, recursive byte-array builders and
  allocating key extraction were removed. Production container assembly uses no
  ByteArrayOutputStream, whole-document value tree, legacy fallback, global pool,
  off-heap memory or precomputed fixtures. Caller-owned output streams and Bench
  still materialize their output as before.

## Stage 2 intended differences and raw limitations

`VPackWriterTagRegressionTest` has standalone independent corrected vectors for
all four proven lost/misplaced-tag examples in the contract. Object scalar and
container values now keep their prefixes; prefixes before array containers stay
with that item and cannot drift to a subsequent scalar. Tests also cover both
layout families, sorted pairs, chained short/long tags, negative long tag bits
and tagged primitive arrays. Tags do not consume a value context slot. Parent
item/pair starts and lengths include their tag bytes. The frozen oracle and its
bug-characterization assertions were not edited.

Root `writeRaw(byte)` and `writeBytes` remain exact pass-through, including None
bytes and blocks containing multiple values, with their existing context position
updates. **Nested calls to those two APIs now raise StreamWriteException before
modifying context or payload**. The legacy nested-fragment behavior has no coherent
item/count contract; silently treating fragments as tags or complete values would
invent one. Use `writeTaggedValuePrefix` for explicit prefixes and the existing
`writeRawValue(SerializableString)` for one complete pre-encoded value. The latter
copies bytes without parsing; the caller remains responsible for exactly one valid
value. General validation of arbitrary pre-encoded blocks is deliberately absent.

A leading None (`0x00`) or empty pre-encoded nested value is rejected, since None
is not legal on wire and can masquerade as optional no-index-array padding.
Valid padded pre-encoded children are preserved byte for byte and independently
round-trip tested. Ordinary generated noncompact headers retain the valid legacy
unpadded layout. Nested dangling prefixes, missing object values, and object tags
before a property name fail clearly instead of disappearing or becoming padding.
Dangling root prefixes still pass through; they have no valid following value to
preserve. Invalid nested raw blocks and these malformed sequences are outside the
valid byte-identity corpus, as stage 1 specified.

## Stage 2 checks

All commands use the frozen `-Djackson.version=3.2.0` override and offline Maven.
Initial focused run passed **111 tests**, including all 13 differential methods,
all 4 frozen characterization methods and all four existing generator suites.
The exploratory primary benchmark was launched immediately after this initial
rewrite passed, **before UTF-8 work or further polish**. Expanded checks then
added tag/storage/arithmetic regressions; the final streamed-binary integration
was validated after both timing invocations finished, so compiled classes did not
change during timing.

Final focused command:
`mvn -B -o -Djackson.version=3.2.0
-Dtest=VPackWriterDifferentialTest,LegacyWriterCharacterizationTest,VPackWriterTagRegressionTest,VPackWriterPayloadTest,VPackGeneratorContainerTest,VPackGeneratorAdvancedTest,VPackGeneratorFeaturesTest,VPackGeneratorErrorTest test`.
Result: **128 tests, zero failures/errors**, exit 0. Logs/reports are under
`target/writer-stage2/final-focused.log` and `final-focused-reports/`.

Stage-2 additions comprise 17 methods: 6 independent tag regressions and 11
payload tests. They cover direct arena output identity, lazy allocation, nine-byte
reservations, retirement across 24,000 grandchildren, sequence reuse, 128 nested
frames, stable mixed UTF-8/long/numeric key sorting, shortcut context counts,
minimal compact lengths through a real 2 MB value plus larger arithmetic-only
boundaries, width-8/trailing-count arithmetic, overflow, invalid nested raw/None
fragments, valid padding, unfinished-header invisibility, tiny custom buffers,
raw numeric bits, direct binary stream reads and root buffered/visible lengths
against the oracle over custom prefix offsets. The original all-mask differential
suite and original source-hash assertion continue to pass unchanged.

Final full command: `mvn -B -o -Djackson.version=3.2.0 test`, exit 1.
`target/writer-stage2/final-full-suite.log` and `final-full-suite-reports/` report
**6,438 tests, 12 failures, 10 errors, 3 skipped**. All writer tests pass.
Comparing XML method names and failure/error types with stage 1 found **zero new
or removed failures**: the same 22 distinct failing methods remain. The XML totals
count the 50 converter-mixin repetitions individually (6,487 tests / 61 failures),
whereas Surefire's final summary counts that failing method once; this is the same
reporting difference as stage 1. The comparison is preserved in
`target/writer-stage2/final-failure-comparison.json` and the portable results JSON.
The earlier full run before binary integration had the same 22 failing methods.

Also passed: `git diff --check`, unchanged oracle hash, production assembly search
(no ContainerState, capture lists/streams, encodeVByte/reverseBytes or boxed sort
IDs), and diff checks preserving pom.xml, Bench, ArangoDocuments, original tests,
legacy source and historical result files. Both benchmark builds passed the
frozen dependency checksum lock.

## Stage 2 exploratory performance (NON-ACCEPTANCE)

Primary command: `FORKS=1 scripts/bench-tree-write.sh acceptance`.
Run: `target/jmh-result/tree-write-20261003T075931Z-OMjAYJ/`.
Although the harness mode/file names say acceptance/baseline, this **one-fork**
run is exploratory non-acceptance data. `exploratory-notes.json` labels it explicitly.
It used unchanged Bench.treeWriteCursor, batchSize=1000, all three formats, one
thread, 3 x 2 s warmups, 5 x 2 s measurements, 512 MB fixed heap, GC profiler,
org.openjdk.jmh.Main and **no JFR**. Environment and dependency lock match stage 1.

| Format | Stage-1 mean ms/op (3 forks) | Exploratory mean ms/op (1 fork) | Exploratory JMH error ms/op | Before B/op | Exploratory B/op |
| --- | ---: | ---: | ---: | ---: | ---: |
| JSON | 5.334817 | 5.252791 | 0.177644 | 3494651.544 | 3494651.409 |
| SMILE | 3.047668 | 2.948196 | 0.059872 | 1439710.027 | 1439709.022 |
| VPACK | 37.206990 | 7.712182 | 0.693518 | 65064952.509 | 9291762.430 |

The provisional VPACK reference ratio is **4.824x**, with approximately **85.72%**
lower allocation. This is below the eventual 7.441398 ms/op threshold and is
**not** evidence of meeting the 5x acceptance target. Only a final comparable
three-fork run after the remaining stages can establish that target. The initial
measurement precedes minor context-close polish and the final binary-stream
integration; Bench fixtures contain no InputStream binary calls.

Auxiliary command:
`FORKS=1 REGRESSION_FORKS=1 scripts/bench-tree-write.sh regressions`.
Run: `target/jmh-result/tree-write-20261003T080738Z-33mye1/`.
All 24 cases completed setup validation, exit 0, with the same settings and batch
sizes 1/1000. These one-fork results are also non-acceptance evidence. This run
includes context-close polish and precedes binary-stream integration. The complete
JSON/SMILE/VPACK timing/allocation results and errors are in the portable summary.

| Benchmark (VPACK) | Batch | Before us/op | Exploratory us/op | Before B/op | Exploratory B/op |
| --- | ---: | ---: | ---: | ---: | ---: |
| treeWriteDocument | 1 | 27.789 | 4.078 | 44840.1 | 8888.0 |
| treeWriteDocument | 1000 | 27.887 | 4.089 | 44720.1 | 8888.0 |
| pojoWriteCursor | 1 | 38.769 | 5.776 | 67896.1 | 9976.0 |
| pojoWriteCursor | 1000 | 32221.478 | 5524.360 | 65408911.0 | 9524237.1 |
| streamingWriteCursor | 1 | 35.626 | 4.679 | 68760.1 | 11200.0 |
| streamingWriteCursor | 1000 | 31403.652 | 4730.161 | 65254020.3 | 9512840.4 |
| sequenceWrite | 1 | 28.414 | 4.505 | 46144.1 | 10192.0 |
| sequenceWrite | 1000 | 29100.753 | 4716.104 | 46355476.2 | 5712640.3 |

Output sizes remain unchanged: VPACK document **1,029 B**, cursor **1,144,774 B**,
sequence **1,144,546 B**. JSON remains 1,213 / 1,329,760 / 1,329,477 B and Smile
874 / 749,051 / 748,832 B. Bench setup round-trip validation passed for every case;
the valid-output differential fixture also retains exact bytes.

## Stage 2 cost inspection and remaining work

Because the initial cursor result was just below the eventual 5x target, a separate
non-timing JVM sampled ThreadMXBean stacks at 2 ms after warmup. No JFR was used.
The 3,875 samples are **safepoint biased**, so they identify hot paths, not precise
CPU shares: writeName was the top frame in 43.59% of samples (48.36% inclusive),
writeString in 14.14% (20.90% inclusive), and _verifyValueWrite in 10.76%.
Container closure appeared in 13.01% inclusive; payload capacity checks and direct
VByte encoding also remain visible. Key/value UTF-8 encoding and context/property
writes are now the main observed paths, rather than recursive materialization.
Caller output-array construction/allocation is still part of Bench's unchanged
semantics. Diagnostic source/log: `target/writer-stage2/StackProfile.java` and
`diagnostic-stack-profile.log`.

A separate instrumented traversal of the same cursor tree counted payload and
copy work (not timing data). It produced identical **1,144,774 B** output, with
**22 arena allocations**, **3,854,439 B** in summed allocated capacities, final
capacity **1,284,995 B**, and **2,569,444 B** copied by growth. Across **16,624**
container closes, header compaction moved **5,585,181 B** of content. Default
unsorted objects made **zero sorting copies**. It observed **98,284 getBytes
calls** producing **870,931 B** of temporary encoded bytes, excluding array
headers/alignment and internal UTF-16 encoder temporaries. Peak live offsets:
**1,031**, capacity **1,303**; peak open depth: **7**. This explains substantial
remaining allocation despite removing value captures. The diagnostic lives in
`target/writer-stage2/PayloadAccounting.java` and `diagnostic-payload-accounting.log`.
Its first launch raced Maven recompilation and failed to load VPackUtil; a serial
rerun after Maven finished passed. Neither timed invocation overlapped compilation.

Stage 3 can now address direct UTF-8 encoding, retaining the frozen surrogate,
API-overload and header semantics. Further performance work should focus on the
measured property/string paths and capacity/output allocation, while preserving
close-time representation choices and bounded child metadata. Stage 4 still needs
the final correctness/regression checks and comparable **three-fork** acceptance
run through the locked harness. No 5x target is claimed in stage 2.

## Stage 3 architecture and ownership

Stage 3 retains the shared arena, nine-byte container reservations, primitive
stacks, stable physical pair sorting and separate small IOContext output buffer.
The production change remains confined to `VPackGenerator.java`; Bench,
ArangoDocuments, pom.xml, dependency lock, the frozen oracle and the original
stage-1 differential/characterization tests remain unchanged. Implementation
invariants and API limitations are documented in
[writer-implementation.md](writer-implementation.md) and the generator Javadoc.

The first text implementation computes exact UTF-8 length using `long`, then
encodes directly into shared storage. `char[]` slices no longer construct an
intermediate String. General/root strings never reserve three times their UTF-16
length. Huge root text streams through the small output buffer and leaves no
buffered payload tail on successful return, matching legacy large-root visibility.
Raw UTF-8 byte APIs still copy exact caller bytes without validation.

Resolved Jackson **3.2.0** source inspection established that normal
`SerializableString` writes must use `getValue()`: `SerializedString.asUnquotedUTF8`
invokes `JsonStringEncoder.encodeAsUTF8`, which throws `IllegalArgumentException`
for malformed surrogates. The legacy generator instead used
`getValue().getBytes(UTF_8)` and replaced each malformed surrogate with ASCII `?`.
Both LENIENT feature states retain that behavior. `writeRawValue` intentionally
continues to use the caller's `asUnquotedUTF8` wire representation.

Known-length stream binary direct filling was already present in the final
stage-2 implementation. Stage 3 keeps it and adds short/zero-progress read,
premature-EOF, input failure and caller-ownership checks. Containers fill their
payload range directly; huge root binaries use bounded output chunks. Caller
arrays are copied or synchronously emitted during the write call. No borrowed
pending arrays, root-sized scalar arena, BCD rewrite, parser change or numeric
property translation is introduced.

Lifecycle inspection used resolved `GeneratorBase.close`, `IOContext` allocation,
release and close implementations. GeneratorBase calls `_releaseBuffers` from a
`finally` block, then closes IOContext and marks the generator closed. Output
release is now centralized there. The generator clears arena/scratch, frame and
offset references on terminal close, and releases only the original recyclable
output buffer. A nonrecyclable custom buffer is never returned to IOContext.
There is no multi-megabyte arena donation to the small write-encoding recycler
and no document pool. Reuse remains local to one generator's active-root capacity.

## Explicit bug-fix ledger (stages 2 and 3)

| Scope | Legacy/stage-2 problem | Deliberate corrected behavior and evidence |
| --- | --- | --- |
| Stage 2: object tags | Prefix lost before scalar or composite object values | Prefix remains after name and before wrapped value; independent stage-1 corrected vectors |
| Stage 2: array tags | Prefix lost before a composite or drifted to the next scalar | Parent boundary starts before prefix; empty/nonempty arrays and objects finish/count exactly once |
| Stage 2: raw fragments | Nested raw APIs dropped or merged bytes with inconsistent item counts | Nested `writeRaw`/`writeBytes` rejected before context/payload mutation; root multi-value blocks retain pass-through |
| Stage 2: invalid raw value | Empty or leading None nested raw value could be lost or resemble padding | Clear error; valid padded containers and custom pre-encoded values retain their bytes |
| Stage 2: incomplete nested calls | Dangling prefixes/missing object values could disappear or cause index errors | Container close rejects incomplete values; object prefixes must follow a property name |
| Stage 3: output replay | Failed buffer write retained a tail that could be emitted again after partial acceptance | Pending lengths retired before output calls; output IOExceptions prohibit further values/names/tags; flush/close never replay bytes |
| Stage 3: failure ownership | Failed final buffer write skipped an authorized target close/flush | Close attempts target action under existing ownership flags, then releases buffers; secondary close failure is suppressed |
| Stage 3: retained memory | Closed generators retained payload, sort scratch and nonrecyclable buffer references | Terminal cleanup drops references without recycling caller-owned output or document storage |

Direct UTF-8 encoding is a performance change, **not** a new malformed-surrogate
policy. Repeated close and exactly-once output recycling were already supported
by GeneratorBase; expanded failure tests establish those guarantees after the new
cleanup path. Frozen bug-characterization assertions and oracle source remain
unchanged. Nested fragment semantics, multi-value raw validation and dangling
root prefix behavior are explicitly described in the implementation document;
no parser is used to define new boundaries.

## Stage 3 initial checks and experiment

Focused command uses offline Maven and `-Djackson.version=3.2.0`, adding
`VPackWriterOwnershipTest` to the same eight suites used for stage 2. The first
complete stage-3 focused run passed **146 tests, zero failures/errors**. Logs and
XML reports are in `target/writer-stage3/final-focused.log` and
`final-focused-reports/` (subsequent final verification is recorded below).

The first full command, `mvn -B -o -Djackson.version=3.2.0 test`, reports
**6,456 tests, 12 failures, 10 errors, 3 skipped**, exit 1. XML totals are
6,505 / 61 failures / 10 errors / 3 skipped because of the previously recorded
converter-mixin repetition reporting difference. Method/kind/type comparison
found **exactly the same 22 distinct failing methods, zero new or removed**.
Reports and comparison are under `target/writer-stage3/final-full-suite-reports/`
and `final-failure-comparison.json`.

Initial exploratory command: `FORKS=1 scripts/bench-tree-write.sh acceptance`.
Run: `target/jmh-result/tree-write-20261003T085014Z-43yYyd/`, exit 0.
The harness mode/file names do not make this an acceptance run: it has **one
fork**, unchanged fresh-generator/output-array operations, frozen dependency
checksums, all formats, one thread, 3 x 2 s warmups, 5 x 2 s measurements,
512 MB fixed heap and the GC profiler, **no JFR**. Tests and compilation did not
overlap timing. `exploratory-notes.json` marks its purpose.

| Format | Stage-2 exploratory ms/op | Initial stage-3 ms/op | JMH error ms/op | Initial stage-3 B/op |
| --- | ---: | ---: | ---: | ---: |
| JSON | 5.252791 | 5.168903 | 0.193817 | 3494650.620 |
| SMILE | 2.948196 | 2.973374 | 0.184469 | 1439709.331 |
| VPACK | 7.712182 | 7.834565 | 0.519328 | 6110211.031 |

VPACK allocation fell **34.24%** from stage 2's 9,291,762.430 B/op, but time
stayed roughly flat within exploratory uncertainty. The stage-1 reference ratio
is only **4.749x**, below the eventual 5x target. This experiment demonstrates why
allocation alone cannot establish progress on time/op. It motivates separate
follow-up cost inspection before selecting the next substantial change.

## Stage 3 follow-up: bounded short-string headers

The first full auxiliary run completed all 24 cases, exit 0, using
`FORKS=1 REGRESSION_FORKS=1 scripts/bench-tree-write.sh regressions`:
`target/jmh-result/tree-write-20261003T085159Z-iiWaFL/`. It is explicitly marked
exploratory/non-acceptance and precedes the short-string change below.

After both initial timing runs finished, a **separate** non-JFR JVM used the
stage-2 ThreadMXBean stack sampler. No diagnostic overlapped tests, compilation
or timing. Its 3,906 samples are safepoint biased, not CPU-percentage or timing
measurements. Top frames included `_encodeUtf8` in 33.92% of samples,
`_utf8Length` in 30.80%, `_verifyValueWrite` in 10.70%, and `_ensurePayload` in
5.02%. UTF-8 string/name work appeared in 70.05% inclusive, container close in
10.24%, and output array construction in 2.84%. This identifies text as a
substantial candidate rather than justifying recycler changes or GC tuning.
Source/log: `target/writer-stage3/StackProfile.java`, `initial-stack-profile.log`.

Separate instrumented traversal confirmed **97,974 of 98,284** text calls
(**99.68%**) have at most 42 UTF-16 code units, with **859,651** total code units
and **870,931** encoded bytes. The arena and header costs were unchanged from
stage 2: 22 arena allocations, 3,854,439 B allocated capacities, 2,569,444 B growth
copies, 1,284,995 B final capacity and 5,585,181 B of header moves across 16,624
container closes. Default unsorted objects made zero sorting copies. Diagnostics
reference-encode text to count expected bytes; those diagnostic-only arrays are
not production allocations. Log: `initial-payload-accounting.log`.

The final implementation therefore uses a local one-byte header for **container
text of at most 42 UTF-16 code units**. It reserves at most 126 payload bytes,
encodes once and patches the actual byte length. Even all three-byte characters
fit the short header, and valid surrogate pairs need less space. There is no
string-header move. Longer and root strings retain exact-length precomputation.
This removes the extra length scan for 99.68% of this fixture's text calls while
keeping long ASCII allocation exact. It also keeps the ASCII encoder loop free
of surrogate processing, which is reached only for surrogate code units.
This is a bounded encoding choice, not a new wire or UTF error policy.

Final focused verification again passed **146 tests, zero failures/errors**.
The repeated final full suite again reports **6,456 tests, 12 failures,
10 errors, 3 skipped**, with **zero new or removed failing methods**. Final logs,
XML reports and the method/type comparison are under `target/writer-stage3/`.
No benchmark or frozen oracle tests were edited. The new 18 methods comprise
17 ownership/UTF/binary/raw tests and one additional independent tag matrix.

## Stage 3 final exploratory results (NON-ACCEPTANCE)

Command: `FORKS=1 REGRESSION_FORKS=1 scripts/bench-tree-write.sh all`.
Run: `target/jmh-result/tree-write-20261003T090245Z-dGibJC/`. Both cursor and
24-case regression invocations exited 0. Settings, frozen dependencies and fresh
output/generator semantics are unchanged; timing uses GC profiling and **no JFR**.
Compilation/tests finished before the run, and diagnostic JVMs ran after it.
Portable results, including every format, raw iteration times, JMH errors and
allocation, are in [writer-rewrite-stage3-results.json](writer-rewrite-stage3-results.json).

The harness's generic `all` metadata purpose says “unmodified writer baseline”,
and its cursor file is named `baseline.json`. **Neither label describes this
run**: it measures the rewritten writer with **one fork** and is explicitly
marked exploratory/non-acceptance in `exploratory-notes.json` and the portable
summary. The reference remains stage 1's frozen three-fork baseline.

| Format | Stage-1 reference ms/op | Stage-2 exploratory ms/op | Final stage-3 exploratory ms/op | JMH error ms/op | Final stage-3 B/op |
| --- | ---: | ---: | ---: | ---: | ---: |
| JSON | 5.334817 | 5.252791 | 6.224636 | 2.643881 | 3494657.267 |
| SMILE | 3.047668 | 2.948196 | 3.029749 | 0.081746 | 1439709.977 |
| VPACK | 37.206990 | 7.712182 | 8.127976 | 0.408947 | 6110212.034 |

The provisional VPACK reference ratio is **4.578x**, below the eventual 5x
threshold of **7.441398 ms/op**. Allocation is **90.61%** below the original
writer and **34.24%** below stage 2, but **no stage-2-to-stage-3 time/op improvement
is established**. The JSON control was unusually variable (individual measured
iterations ranged 5.582–7.232 ms/op). These one-fork observations and overlapping
errors do not prove the short-string path improved cursor latency, nor can they
be promoted to final acceptance or explained away as GC tuning.

| Benchmark (VPACK) | Batch | Stage-2 exploratory us/op | Final stage-3 exploratory us/op | Final JMH error us/op | Stage-2 B/op | Final stage-3 B/op |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| treeWriteDocument | 1 | 4.078 | 4.045 | 0.654 | 8888.0 | 6088.0 |
| treeWriteDocument | 1000 | 4.089 | 3.815 | 0.093 | 8888.0 | 6088.0 |
| pojoWriteCursor | 1 | 5.776 | 5.735 | 0.136 | 9976.0 | 6664.0 |
| pojoWriteCursor | 1000 | 5524.360 | 5603.014 | 154.668 | 9524237.1 | 6342686.0 |
| streamingWriteCursor | 1 | 4.679 | 4.673 | 0.056 | 11200.0 | 7952.0 |
| streamingWriteCursor | 1000 | 4730.161 | 4828.599 | 102.251 | 9512840.4 | 6299288.7 |
| sequenceWrite | 1 | 4.505 | 4.633 | 0.098 | 10192.0 | 7392.0 |
| sequenceWrite | 1000 | 4716.104 | 4752.079 | 37.652 | 5712640.3 | 2531600.4 |

VPACK document/cursor/sequence sizes remain **1,029 / 1,144,774 / 1,144,546 B**.
JSON remains 1,213 / 1,329,760 / 1,329,477 B; Smile remains
874 / 749,051 / 748,832 B. All setup round-trip checks pass. The regression
measurements mostly retain stage-2 timing while reducing allocation; neither
small-document cases nor sequence throughput substitute for cursor acceptance.

## Stage 3 final cost inspection and remaining limits

After all timing finished, the instrumented cursor traversal was repeated for
default and sorted-key output. Both produced **1,144,774 B** and matched the
frozen legacy writer in their respective layouts. Arena growth, header moves,
text byte counts and live metadata bounds remained identical to the initial
accounting: **22** arena allocations / **3,854,439 B** summed capacities /
**2,569,444 B** growth copies, **5,585,181 B** header moves, final arena capacity
**1,284,995 B**, peak **1,031** live offsets (capacity **1,303**) and depth **7**.
Default sorting adds zero copies. Enabling sorted keys reorders **11,689 objects**
and adds **6,880,894 B** of content-copy traffic (two content passes for each
reordered object); primitive merge-sort ID moves are additional. Sorting does
not change encoded size or semantic content. Logs:
`final-payload-accounting.log`, `final-sorted-payload-accounting.log`.

There are **75,326 per-value context dispatches**, including 16,624 container
starts, plus **62,628 property names**. Text encoding accounts for **98,284**
combined name/value calls and **870,931 B** of encoded bytes, with **zero production
String.getBytes temporary arrays**. No BCD calls appear in this fixture, so there
is no evidence here for a substantial BCD optimization. The generator emits the
root arena directly with no generator final copy. Resolved ByteArrayBuilder
source inspection separately establishes two caller output passes of
**1,144,774 B each**: segmented storage and the fresh returned array. Those caller
copies and arena growth allocation remain part of the unchanged benchmark.

Since cursor time still missed the target, a second separate diagnostic JVM
sampled the final implementation to guide remaining work, rather than launching
GC/pool changes. It captured **3,883 safepoint-biased samples**, not precise CPU
shares. `_utf8Length` fell from 30.80% to **2.37%** of top frames; `_encodeUtf8`
now appears in **58.56%**, `_verifyValueWrite` in **11.23%**, `_ensurePayload` in
**4.94%**, and caller `ByteArrayBuilder.toByteArray` in **2.14%**. This is consistent
with eliminating the short-string length pass, but does not prove a latency
benefit. UTF-8 encoding and context/name dispatch remain the next substantial
paths to investigate. Growth/header/sorting copies were audited and quantified,
not replaced through speculative architecture changes. Log:
`target/writer-stage3/final-stack-profile.log`.

No cross-generator document recycling was added: allocation alone did not
establish a material time benefit, while measured text paths remain prominent.
Only Jackson's original independent small output-buffer recycling is used. No
custom global/ThreadLocal pool, benchmark generator reuse, output-array reuse,
GC tuning, parser change, UTF policy change or collections of tiny tweaks are
introduced. Input stream failures may expose partial root values; unknown lengths
and textual raw/Reader APIs remain unsupported. Nested fragment APIs remain
explicitly documented/rejected, while complete raw/custom validity is the
caller's responsibility. General failed-input recovery and BCD encoding remain
outside this change.

Stage 3 completes the requested copy/ownership implementation and expanded
regressions. **The time target remains unmet in exploratory evidence.** Stage 4
still requires any material remaining time reduction, final correctness and
regression checks, and a comparable **three-fork** acceptance run against the
locked stage-1 baseline. No 5x acceptance claim is made.


## Stage 4 independently validated final result

The final performance/interop report is [writer-performance.md](writer-performance.md),
with portable metrics in [writer-performance-results.json](writer-performance-results.json).
The final primary measurements use three forks and the unchanged stage-1 harness.
An additional original checkout repeats both cursor acceptance and the 24-case
regression matrix, preserving the stage-1 baseline separately.

Test-only counters establish depth-dependent payload moves: a 262,144-byte scalar
under 128 arrays moves 33,595,712 B during header compaction; under 128 objects it
moves 33,645,248 B. Ordinary cursor growth copies 2,569,444 B and header compaction
moves 5,585,181 B; sorted objects add 6,880,894 B of reorder traffic. The design
does not provide depth-independent copying or zero-copy output.

Final test logs, fixtures, both C++ checkouts/builds, three unique final JFR recordings
and counted depth source/results remain under `target/writer-stage4/`. Historical
artifacts are preserved. No production dependency, GC change, fixture/default
change, buffer reuse requirement, serialized-result cache or per-token value tree
was introduced.
