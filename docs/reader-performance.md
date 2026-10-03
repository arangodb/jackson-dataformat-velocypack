# Reader performance: baseline, property-name canonicalization and shared spans

The target is exactly `com.arangodb.jackson.dataformat.velocypack.Bench.treeReadCursor`, `batchSize=1000`. This stage changes no production source, writer, encoding, defaults, returned tree, parser constraints, or target workload. The only addition to `Bench` is an annotated cursor InputStream reader. Its original setup checks still run, including POJO, streaming writer, tree writer, and sequence round trips. Existing read diagnostics were already annotated; an unannotated method is never counted as a benchmark.

Source revision: `0d384618deb0ea144c71294e0b31e9d82cb152d7`. The starting index contained staged deletions under `jmh-result/`; these remain staged. The supplied October 3 reader JSON/JFR files were untracked and remain intact. Each experiment records both staged and unstaged binary patches, untracked files, source hashes, and the dirty status; revision alone does not identify a dirty build.

## Supplied measurement and reproducibility

`jmh-result/2026-10-03_14-02-55.json` is reader data:

| Format | Supplied us/op | Supplied gc.alloc.rate.norm B/op | Decimal MB/op |
| --- | ---: | ---: | ---: |
| JSON | 5,714.613 | 8,179,824.869 | 8.180 |
| SMILE | 4,245.345 | 8,180,010.156 | 8.180 |
| VPACK | 8,799.589 | 18,306,429.284 | 18.306 |

It used JDK 17.0.20, one fork, one 2-second warmup, three 1-second measurement iterations, 512 MiB minimum/maximum heap, and `-XX:StartFlightRecording=...,settings=profile` during timing. It is historical context, not acceptance evidence. `Bench.main` consumes only `args[0]` as an include regex and forces JFR, so passing JMH CLI options to it does not configure a valid acceptance run.

**The original measurement environment cannot be reproduced here.** Its `/home/michele/.../17.0.20-tem` JVM is unavailable. Original JAR hashes, dirty source state, and a complete environment lock were not supplied. The archive's `jdk.InitialSystemProperty` classpath does identify Jackson core/databind/Smile **3.2.0** and annotations **2.22**, but filenames do not establish artifact identity. New controlled baselines therefore cover every format on the available Temurin **21.0.12.1+1-LTS**, OpenJDK 64-Bit Server VM, Linux x86_64, Intel Core Ultra 7 265T (20 logical CPUs). The OS scheduler and CPU frequency scaling remain active; between-fork variation is reported rather than assumed away.

The local `pom.xml` declares the **3.2.0-SNAPSHOT BOM**. Inspection of that actual BOM shows its component property is **3.2.0**, and the default Maven dependency tree resolves core/databind/Smile 3.2.0 and annotations 2.22. There is **no `-Djackson.version` override** in the reader harness. The writer script's explicit release override is a separate workflow. October 2 result files, including their staged-deleted contents inspected through `git show HEAD:path`, contain both reader and writer benchmarks; writer results are never used as reader observations.

| Artifact | SHA-256 |
| --- | --- |
| Jackson BOM 3.2.0-SNAPSHOT POM | `774dc930d660685f7d5ae5464e21baf660bd5bdb06b10496e853b648e6c2b82b` |
| jackson-core 3.2.0 JAR | `5e353ce53c6901105dfcbf183e3220c17072e334e552b818a4bb1b99decea596` |
| jackson-databind 3.2.0 JAR | `3ef94a3dddeafc247c50230fad0315981b2ce4ae6e91cfb4368a86f328904e4f` |
| jackson-dataformat-smile 3.2.0 JAR | `5af309fac50fe5dd38e0718bdc29978951afb59bc530578659d3709cbb154343` |
| jackson-annotations 2.22 JAR | `21ddb598807d3a51a876704eb979d9296e1c6a6f47ab1826ff88c6d6a127a2d0` |

All dependencies, their adjacent POMs, imported BOMs, and **454 JDK files** are fingerprinted in `resolved-lock.json`. The reusable `target/reader-performance/frozen/` bundle copies dependency artifacts and encoded inputs. Every comparison verifies the installed JVM path and full JDK hashes, dependency resolution, frozen artifacts, encoded inputs, and cursor token transcript against that lock. A mismatch stops execution; deliberately changing an environment requires a new lock and a fresh baseline for all formats. Keep this bundle for the next stage. Each run copies the compiled class/resource directories and hashes them so subsequent Maven builds cannot change an executing fork's classes.

`jackson-api.log` records `javap` output from the resolved JARs for parser, IOContext, constraints, canonicalizer, context, and mapper APIs. It confirms Jackson 3 names such as `PROPERTY_NAME`, `streamReadContext`, `assignCurrentValue`, and byte-array slice parser APIs; no Jackson 2 API assumption is needed. `jmh-jfr-api.log` records the actual JMH 1.37 profiler bytecode.

## Commands and experiment evidence

Run from the repository root, sequentially. The harness uses offline Maven resolution without substituting dependencies. If offline artifacts are missing, resolve the unchanged POM explicitly, inspect it, and then create a controlled lock; do not fall back to a different version.

```bash
mvn -B -o test
scripts/bench-tree-read.sh prepare
scripts/bench-tree-read.sh acceptance
WARMUP=10 scripts/bench-tree-read.sh acceptance
python3 scripts/reader-performance/summarize.py target/reader-performance/acceptance-20261003T140309Z-klm7rzj6
WARMUP=10 scripts/bench-tree-read.sh profile
mvn -B -o -Dtest=ReaderRegressionTest,VPackParserFeaturesTest,VPackParserStreamingTest,VPackParserBufTest,VPackParserBootstrapperTest,VPackScalarParseTest,VPackTaggedValueTest,VPackCustomValueTest,VPackParserErrorTest test
mvn -B -o -Dreader.safetyProbes=true -Dtest=ReaderSafetyGapTest test
mvn -B -o test
```

`scripts/bench-tree-read.sh diagnostics` runs the supplemental read benchmarks with the same lock, three forks and at least five warmups. `WARMUP=10` is recommended for comparisons after the convergence findings below. `all` runs acceptance, diagnostics and profiling sequentially. `LOCK_DIR=/absolute/path/to/frozen` reuses an existing bundle. `FORKS` must be at least 3, `WARMUP` at least 5, and `PROFILE_REPEATS` at least 2. No CLI passthrough permits accidental timing instrumentation; inherited `JAVA_TOOL_OPTIONS`, `_JAVA_OPTIONS`, `JDK_JAVA_OPTIONS`, or `CLASSPATH` are rejected.

The acceptance command expands to the following JMH options. `commands.json` contains complete executable/classpath/output paths and all build, introspection, accounting, and benchmark commands:

```text
org.openjdk.jmh.Main
^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.treeReadCursor$
-p format=JSON,SMILE,VPACK -p batchSize=1000
-f 3 -t 1 -wi 10 -w 2s -i 5 -r 2s
-jvm <locked-java> -jvmArgs "-Xms512m -Xmx512m"
-prof gc -rf json -foe true -rff <run>/baseline.json
```

Acceptance has **GC profiling only**, no JFR, diagnostic profiler, accounting subclass, or JaCoCo agent in the fork. Maven test instrumentation is separate. JMH's own generated code and compiler-blackhole options are standard harness behavior. A reviewable archive is preserved at `jmh-result/reader-baseline-2026-10-03/`: controlled and exploratory JSON/logs/metadata, all six original JFR recordings and their analyses, encoded inputs/transcripts, dependency/JDK lock, diagnostic sources, test logs, original dirty-state capture, and an SHA-256 manifest. Full compiled snapshots, copied dependency JARs and XML reports remain under `target/reader-performance/` and `target/reader-preflight/`. Each output directory retains result JSON, full logs, exit codes, source/environment metadata, both Git patches, effective POM, dependency tree, original classpath, class hashes, API output, and input manifest/transcripts.

The five-warmup run is `target/reader-performance/acceptance-20261003T135904Z-wg5kyzn7`. It measured JSON 5,988.778 ± 117.252 us/op, SMILE 4,504.053 ± 48.416, and VPACK 9,056.498 ± 299.341, but is **exploratory**: VPACK fork 1's early-to-late measurement drift was −6.08%; SMILE fork 3's last-three warmup spread was 5.69%. It failed the convergence screen and prompted ten warmups for all formats.

The final harness automatically writes `summary.json` and `summary.log` after acceptance and returns an error if this screen fails, requiring a longer warmup run for all formats. The two recorded runs were screened with the same standalone helper. The screening thresholds are last-three warmup spread ≤5%, late-warmup mean versus measurement mean difference ≤5%, and first-two versus last-two measurement mean drift ≤3%, evaluated per fork. These are explicit heuristics, not proof of stationarity. Inspect raw iteration values and fork means as well; never select just the fastest fork or combine partial formats from different conditions.

The controlled baseline is `target/reader-performance/acceptance-20261003T140309Z-klm7rzj6`: **three forks, ten 2-second warmups, five 2-second measurements per fork, all nine forks pass the convergence screen**. Errors below are JMH 99.9% confidence-interval half-widths; MB is decimal. Allocation comes from `-prof gc`.

| Format | us/op ± error | gc.alloc.rate.norm B/op | MB/op | Per-fork mean us/op |
| --- | ---: | ---: | ---: | --- |
| JSON | 5,692.149 ± 118.856 | 8,273,299.729 | 8.273 | 5,820.031, 5,694.286, 5,562.130 |
| SMILE | 4,207.530 ± 98.939 | 8,273,472.077 | 8.273 | 4,141.616, 4,159.146, 4,321.828 |
| VPACK | 8,173.689 ± 159.064 | 18,791,908.225 | 18.792 | 8,166.495, 8,268.584, 8,085.988 |

The largest late-warmup spread is 4.09%, largest warmup/measurement mean difference is 1.45%, and largest absolute early/late measurement drift is 2.23%. `summary.json` retains every iteration and screening calculation. No production change separates the five- and ten-warmup runs; their difference demonstrates why warmup and environmental variance must be checked. No speedup is claimed against the historical run.


## Encoded inputs and accounting

The original deterministic setup runs at seed 42 and batch size 1000. Capture reflects its actual private byte arrays after all correctness checks; it does not regenerate a different acceptance workload. Document/cursor/sequence input sizes and hashes are preserved for each format in `inputs/manifest.json` and the lock. Cursor values below are directly measured:

| Format | Cursor bytes | Cursor SHA-256 |
| --- | ---: | --- |
| JSON | 1,329,760 | `c498286ced525de2cfd137b35afb6ed13ecbe729278cb2cbe022de8d3569bf00` |
| SMILE | 749,051 | `47c9281c086762453b1661eac9953cd1c801636ab87b5d34eacdf95a30ed6fd3` |
| VPACK | 1,144,774 | `de79b1c841db26d525375a7730d63c0a6830910569e8d825de17526343b22dcb` |

All three parsers produce **154,578 tokens, 62,628 name occurrences, 56 unique names, 16,624 containers, and maximum depth 7** (root container depth 1). No expected counts are hard-coded in the accounting executable. Their value/name/type token transcripts have identical SHA-256 `ffb1b474b2f2559eae42a186322adb5124787045910cf96fd6e12dfb511a3a7e`; full transcripts are retained, not only tree equality.

The isolated diagnostic subclass observes the actual parser frames and `_readPropertyName` calls: **5,601,516 container copy bytes**, **62,628 name decode calls**, **16,624 frames**, maximum depth **7**. Container bytes include each allocated container remainder, including index/count data; empty frames contribute zero. Scalar copies, stream refill copies, object headers, decoded Strings, tree nodes, and unrelated setup allocations are excluded. This accounting is not total B/op. Only `scripts/reader-performance/ReaderInputs.java` contains these counters, compiled into `diagnostic-classes`; that directory is absent from acceptance classpaths.

`ReaderDiagnosticBench.names` has 1,000 objects × 16 pairs, ten-byte names, identical values and nesting. `naming=repeated` uses 16 names; `unique` uses 16,000. Smile's dictionary changes encoded size as intended. `ReaderDiagnosticBench.depth` wraps the same 262,144-byte ASCII string in 1/4/7/16/32 arrays, changing depth while holding the scalar payload fixed. The accounting harness also checks that the new cursor InputStream benchmark returns the same tree as the original byte-array reader. Supplemental setup checks and `inputs/supplemental.json` verify sizes, hashes and VPACK accounting. The supplemental timing matrix has not been run; no supplemental timing values are claimed. The actual JMH registry lists the annotated methods, and their setup/accounting checks ran. These cases, `treeReadDocument`, `streamingReadCursor`, `pojoReadCursor`, both sequence sources, and `treeReadCursorInputStream` supplement the original target. Their results must not replace it.

## Separate JFR evidence

The supplied archive retains one **VPACK** recording, not three format recordings. `Bench.main` reuses one filename across forks/formats; its archived full execution stacks contain VPack reader frames. It also records setup and warmup. It cannot establish JSON/Smile allocation profiles or measurement-only attribution.

Each new profiling invocation selects one format, uses **`-f 1`**, and writes to its own `<run>/<format>-<repeat>/` directory. JMH 1.37 names its inner directory using benchmark ID plus parameters, but the inner filename remains `profile.jfr` and is not inherently fork-unique. Two repeated invocations per format avoid overwrites. These runs use `-prof jfr:...;configName=profile;stackDepth=256;verbose=true` and the isolated `MeasurementIntervals` profiler; their scores do not enter the acceptance table.

JMH's JFR profiler starts recording at the first measurement iteration and stops after the last. `MeasurementIntervals` records exact wall-clock bounds around each measurement iteration after trial setup/warmup. Profiler ordering is deliberate: JFR starts first, interval profiler stops first, so the last interval event is committed before recording stops. The analysis requires all five interval markers and filters allocation samples by those bounds, then attributes samples with a `Bench.treeReadCursor` frame. Interval totals and benchmark-attributed totals are both retained. The recording contains no setup/warmup events, but sample weights need an additional boundary correction described below. Iteration control overhead and sampling attribution remain limitations.

`ReaderJfrAnalysis` sums **`jdk.ObjectAllocationSample.weight`**, by allocation class and complete recorded stack (method, line, bytecode index). Raw event counts are retained only to describe sample availability, never as allocation proportions. It uses the recording API, avoiding the `jfr print` default five-frame display limit. Missing/truncated stack weights are reported, and the original recordings remain available. The initial analysis exposed a startup boundary artifact: JSON repeat 1’s first worker sample carried **29,971,578,592 bytes**, accrued before measurement. OpenJDK computes weight from the difference in a thread’s allocated bytes since its last committed sample ([OpenJDK implementation](https://github.com/openjdk/jdk21u/blob/master/src/hotspot/share/jfr/support/jfrObjectAllocationSample.cpp)). Timestamp filtering alone therefore cannot exclude prior allocation pressure. The final analyzer excludes the first sample per thread in each interval, preserving excluded weights and full stacks separately. This conservative rule also drops some actual measurement allocations and does not estimate unsampled interval tails. The original timestamp-only summaries are retained as `allocation-unfiltered-boundaries.json`, explicitly unsuitable for measurement attribution; `reanalysis-commands.json` records the corrected analyzer source hash and commands. Sample weights are statistical estimates over an interval, **not measured `gc.alloc.rate.norm` B/op**; no sample-count-to-B/op conversion is made.

Completed profiles: `target/reader-performance/profile-20261003T141113Z-3fsu3c6k`, two separate one-fork invocations per format, ten warmups each. Every recording has five measurement markers and zero missing/truncated interior stack weight. Weights below are decimal GB across an approximately 10-second interval, **not GB/op or B/op**.

| Recording | Interval seconds | Interior target sample weight GB | Boundary weight excluded GB |
| --- | ---: | ---: | ---: |
| json-1 | 10.024 | 14.473 | 29.992 |
| json-2 | 10.027 | 13.763 | 29.479 |
| smile-1 | 10.024 | 18.910 | 39.246 |
| smile-2 | 10.020 | 18.720 | 37.836 |
| vpack-1 | 10.038 | 17.084 | 42.235 |
| vpack-2 | 10.035 | 16.206 | 45.455 |

VPACK byte-array allocation pressure is prominent in both repeats (roughly 52–55% of interior target sample weight); this combines container/scalar/string backing arrays, not just container copying. Per-class and per-stack weights, boundary samples, interval bounds, result JSON and logs remain beside each original recording. These samples are diagnostic evidence for the next stage; only the GC-profiler results supply measured allocation per operation.


## Regression and failure inventory

`ReaderRegressionTest` adds eight passing tests with independently assembled valid bytes. The matrix covers slices with offset 5 and poison trailing bytes, compact/indexed nesting, equal-width no-index arrays, padding and widths 1/2/4/8, root sequences, bulk short reads of 1/2/3/7 bytes, signed/unsigned/date/double/BCD values and number types, UTF-8 names/values, duplicate names, context/currentName/currentValue, skipChildren, tagged/custom values, binary ownership across source mutation/close, EOF/close ownership, and enforced depth/document/root-long-string constraints. Sorted objects physically contain `z,a` pairs while their index traverses `a,z`; explicit expected token lists capture the existing index traversal contract. Unsorted fixtures assert physical order. The existing duplicate test had only one key; the new fixture actually contains two identical names and verifies strict detection.

Existing scalar, tagged/custom, buffer, bootstrapper, streaming, error and feature tests are retained, including BCD number-length constraints. Illegal type tests require rejection; no malformed-input acceptance is frozen as a contract.

The authoritative **pre-edit sequential** `mvn -B -o test` run had **6,456 tests, 12 failures, 10 errors, 3 skipped** in Maven's summary. XML counts are **6,505 tests, 61 failures, 10 errors, 3 skipped** because the converter-mixin method has 50 failing repetitions. The 22 distinct failing methods, kinds, exception types/messages and repetition counts are recorded in [reader-preexisting-failures.json](reader-preexisting-failures.json). The initial overlapping build/test run is discarded: a dependency command invoked test compilation during testing and produced class-loading errors. `target/reader-preflight/tests-sequential.log` and `sequential-reports/` are the trustworthy pre-edit evidence.

| Existing failing class | Methods | Kind |
| --- | --- | --- |
| JDKNumberDeserTest | testFloatRangeValidation; testBigArrayOfFloatPrimitives | failure; error |
| CoerceEmptyArrayTest | testOtherScalarsFromEmptyArray | failure |
| NodeFeaturesTest | testBigDecimalForJsonNodeFeature | failure |
| UnwrapSingleArrayTest | testURIAsArray; testUUIDAsArray; testClassAsArray; testSingleElementArrayDisabled | 2 failures; 2 errors |
| TruncateToMillisecondsTest | testInstantNumericTimestampTruncation | failure |
| SimpleTypeSerializationTest | testBase64Variants | failure |
| CustomSerializersTest | testCustomEscapes | failure |
| SerializationFeaturesTest | testIndentation; testIndentWithPassedGenerator; testProviderConfig | 2 failures; error |
| TestMixinSerWithViews | testDataBindingUsage | failure |
| ThreadSafetyWithConverterMixin5813Test | testConcurrentSerializationWithConverterMixin (50 repetitions) | failure |
| SequenceWriterTest | testWithExplicitType; testSimpleNonArrayNoSeparator; testSimpleNonArray; testPolymorphicNonArrayWithoutType | 4 errors |
| BasicExceptionTest | testLocationAddition | error |
| BoundsChecksForInputTest | testBoundsWithByteArrayInput | error |

`ReaderSafetyGapTest` is explicitly opt-in with `-Dreader.safetyProbes=true`. Its assertions demand rejection when configured constraints are exceeded; it never expects unsafe acceptance. Four probes cover short-string length, nested long-string length, property-name length and stream document length. They are excluded from normal runs while this stage forbids constraint changes. Existing enforced constraints continue to be tested normally.

Post-change focused parser tests: **224 tests, zero failures/errors/skips**. Post-change full suite: **6,468 tests, 12 failures, 10 errors, 7 skipped** in Maven; XML totals are 6,517/61/10/7. Failure signatures (method, kind, exception type, repetition count) exactly match all 22 pre-existing methods. The eight new valid-input tests pass; four opt-in safety probes account for the additional skips. See [reader-test-results.json](reader-test-results.json) and `target/reader-preflight/{focused,post-full}.log` with their copied report directories.

The explicit safety-probe run executes all four: **two pass** (root short-string and nested long-string constraints), **two fail** (property-name limit and InputStream document limit). The latter are newly exposed existing enforcement gaps on otherwise valid encodings. They are documented and left unfixed under the production-constraint freeze. Their tests require rejection, so unsafe acceptance is not blessed. Probe logs/XML are `target/reader-preflight/safety-probes.log` and `safety-probes-reports/`.


## Stage handoff

Keep the controlled baseline, per-fork iteration data, encoded inputs/transcripts, environment lock and separate recordings together. Before any future parser changes, compare failing method identities/kinds/types against the inventory and preserve valid token order, ownership, context and constraints. Use the same JVM and artifact lock for before/after runs, with the accepted warmup duration; repeat every format if the environment changes. Container-copy and name-decode accounting identify work to investigate, not guaranteed savings. Production optimization and safety fixes are deferred to the next authorized stage.


## Property-name canonicalization stage (Prompt 2)

This stage integrates the factory-owned byte canonicalizer into each parser. It changes no writer, tree cache, container storage, input fixture, benchmark method, JMH acceptance command, dependency version, or JVM lock. The original constructor remains available and delegates to the new overload with a private default-feature child; factory parsers receive a child of that factory's root.

### Resolved lifecycle and feature behavior

The inspected local **3.2.0 source JARs**, checked against the resolved JAR's `javap -c -p` implementation, contain `UTF8StreamJsonParser`, `SmileParser`/`SmileParserBase`, and `ByteQuadsCanonicalizer`. JSON releases symbols in buffer cleanup; Smile releases on close. Both report `willInternPropertyNames()` through `willInternStrings()` (added in 3.2). VPack releases in its guarded close's cleanup `finally`, including EOF and input-close exceptions. Bootstrap failure releases the child and recyclable IO buffer, closes the IO context, and observes existing source ownership; a close IOException is suppressed on the construction failure. Repeated close is safe, and a closed parser returns no further tokens.

| Canonicalize | Intern flag | String-key behavior | `willInternPropertyNames()` |
| --- | --- | --- | --- |
| enabled | enabled | byte lookup; intern on miss; reuse within parser and through released factory snapshot | true |
| enabled | disabled | byte lookup; reuse without forcing intern | false |
| disabled | either | placeholder; decode normally; never add or independently intern | false |

`FAIL_ON_SYMBOL_HASH_OVERFLOW` is passed unchanged to `makeChildOrPlaceholder`; Jackson's own collision checks and resize/reset behavior apply. Parser children share immutable snapshots until first write. Root publication uses Jackson's atomic snapshot logic, so simultaneously opened parsers remain independent and release does not promise to merge every concurrently added name. Jackson 3.2.0 discards child snapshots with more than **6,000 names** on release and limits hash-table growth to **65,536 primary slots**. There is no additional global cache or retained input slice. Integer attribute keys still use their original decimal `String.valueOf` path and are outside string-key canonicalization/interning.

### Byte lookup and isolated diff

Name bytes are packed directly into big-endian ints. The final quad uses the **resolved Jackson 3.2.0 UTF8StreamJsonParser's `0xff` high-byte padding**, with the actual bytes right-aligned. For valid UTF-8, 0xff cannot occur as data, so lengths and embedded/trailing NULs are unambiguous. To preserve the existing malformed-input replacement path without allowing aliases, a name whose final quad's first actual byte is 0xff, or whose first data quad is the reserved `0xffffffff`, uses an escaped representation: `[0xffffffff, byteLength, rawDataQuads...]`. The length distinguishes zero-padded partial raw quads. Normal names can never start with the reserved quad; the empty name uses that quad alone, distinct from every escaped name's arity. Thus arbitrary bytes are injectively keyed without changing decoding semantics. Lookup and insertion use exactly the same representation.

Normal 1–4 / 5–8 / 9–12-byte names use one-/two-/three-quad overloads. Longer names (and escaped malformed names beyond four bytes) use reusable parser scratch, capped by `floor(maxNameLength / 4) + 3` ints and discarded on close. No per-occurrence array, wrapper, String or copied name-byte key is created on a hit. On a miss the existing decoder runs once and Jackson copies the quad contents into its own table.

The production diff is confined to `VPackParser.java` and `VPackParserBootstrapper.java`: child transfer and failure cleanup; compatible parser overload; symbol capability/release; byte lookup and miss-only decoding; closed-parser guard; name byte-limit/range checks. `_readPropertyName` still runs for every occurrence, and the existing `setCurrentName` duplicate detector and constrained text-buffer reset still run for every returned name. `_decodePropertyName` is isolated for attribution; the diagnostic subclass now counts actual decodes separately from occurrences and receives the factory's child. Neither diagnostic classes nor test instrumentation enter acceptance forks.

The name byte-limit check runs **before every lookup, including a shared hit**, following the resolved JSON parser's byte-based name constraint. This deliberately closes the previously inventoried property-name limit gap, as required for constrained, bounded symbol lookup. Out-of-range long-name lengths and truncated name ranges are rejected. Other constraint/encoding fixes remain separate work.

### Correctness evidence

`VPackPropertyNameCanonicalizationTest` adds 12 passing tests covering empty names; final data quads of 1/2/3/4 bytes; embedded/trailing NUL; non-ASCII and supplementary characters; 126/127-byte wire boundary; 4,096–4,099-byte names; independently encoded equal names; slice bounds; short reads and multiple root refills; all eight canonicalization/interning/overflow combinations; duplicate detection on hits and misses; 20,000 unique names and root reset; seed-dependent adversarial collisions with overflow enabled/disabled; eight simultaneously opened independent children and sharing after release; EOF, repeated close and close failures; bootstrap failure cleanup; integer attribute keys; and allocated-byte accounting on warm repeated input. Reference identity is required only with canonicalization enabled.

Focused suite: **236 tests, zero failures/errors/skips**. Full suite: **6,480 tests, 12 failures, 10 errors, 7 skipped** in Maven; XML totals **6,529/61/10/7**. All 22 pre-existing failing method/kind/type/repetition signatures match exactly; there are no new failing methods. Safety probes now have three passes and one remaining failure (InputStream document limit). The warm allocation test compares the same 20,000-name fixture with symbols enabled/disabled and requires at least 800,000 bytes of removed allocation; it does not assert that all parser/tree/String allocation vanishes.

### Existing encoding bugs (separate from this optimization)

The existing `new String(..., UTF_8)` decoder replaces malformed UTF-8 rather than rejecting it. This stage preserves that decoding behavior and does not turn replacement decoding into strict validation. Cache keys include the exact bytes and length, so an invalid byte spelling cannot hit a valid spelling merely because their quad padding would otherwise coincide. No invalid UTF-8 is treated as an expected compatibility contract in the correctness tests. Strict UTF-8 rejection and the remaining InputStream document-limit gap need a separate safety patch; integer attribute-name translation remains unchanged.

### Final controlled comparison and separate profile

Final measurements are recorded here after validating the final padded-quad implementation. The earlier explicit-length-quad implementation is archived under `exploratory-length-quad/`, with its own exact diff, metadata, rejected timing runs and profiles; it is not final acceptance evidence. Its high-cardinality comparison exposed 16.25 MB/op versus 3.53 MB/op and prompted replacement of that representation with Jackson's short-name overloads plus a reserved malformed-input namespace. No container or writer code changed during that correction.


The final after run is `target/reader-performance/acceptance-20261003T202308Z-e91guu_a`, executed with `WARMUP=20 scripts/bench-tree-read.sh acceptance`. **All nine forks pass the convergence screen**. Dependency/JDK lock, input bytes and complete token transcripts still match. The original accepted before run used ten warmups; the final after run uses twenty, with the same three forks, five 2-second measurement iterations, heap, GC profiler, anchored cursor target and batch size. Both were screened for stationarity. The fresh twenty-warmup before run (`target/reader-symbols/before-w20`) failed the screen and remains exploratory; it cannot supply an accepted paired elapsed-time improvement.

| Format | Original accepted before us/op ± error | Final accepted after us/op ± error | Before B/op | After B/op |
| --- | ---: | ---: | ---: | ---: |
| JSON | 5,692.149 ± 118.856 | 5,810.504 ± 72.961 | 8,273,299.729 | 8,273,260.283 |
| SMILE | 4,207.530 ± 98.939 | 4,453.287 ± 58.707 | 8,273,472.077 | 8,273,455.594 |
| VPACK | 8,173.689 ± 159.064 | 8,395.341 ± 278.080 | 18,791,908.225 | 15,683,753.202 |

VPACK allocation falls by **3,108,155.023 B/op (16.54%)**, from 18.792 to 15.684 decimal MB/op. Final fork means are **8,228.969 / 8,735.412 / 8,221.643 us/op**. There is **no demonstrated end-to-end cursor timing improvement**: the accepted point estimate is 2.71% slower and its 99.9% interval overlaps the original baseline's interval. Unchanged JSON and Smile point estimates also increase (2.08% and 5.84%). Against the fresh but rejected twenty-warmup before mean (9,684.254 us/op), the after score is 13.31% lower; that comparison is explicitly not accepted speedup evidence. All raw iterations, fork means, screens and rejected experiments remain available; no fastest-fork selection or mixed-format run is used.

Exact accounting on the final locked cursor fixture observes **56 cold name decodes and zero warm name decodes**, with 62,628 occurrences, 16,624 frames, depth 7 and **5,601,516 container-copy bytes** in both parses. Thus the warm input removes per-occurrence decoded-name Strings/backing arrays, while string VALUES, tree nodes/maps, frames and container/scalar copies still allocate. The allocated-byte JUnit check on 20,000 repeated keys independently verifies removal of at least 800,000 bytes relative to canonicalization disabled.

The separate byte-spelling diagnostic verifies **5,489 distinct raw names / 10,978 occurrences**, including every 0/NUL/a/b/0xff combination through six bytes and escaped first/final quads through twenty bytes. Values match the legacy replacement decoder, and identical byte spellings reuse their symbols. This is encoding-gap/alias evidence, not a normative test requiring malformed UTF-8 acceptance. Four specific malformed probes (`ff61`, `c2`, `c0af`, `eda080`) also have identical before/after replacement output.


Supplemental name comparison uses the unchanged `ReaderDiagnosticBench.names`, VPACK, three forks, twenty 2-second warmups and five 2-second measurements. Before is `target/reader-symbols/names-before-w20`; final after is `target/reader-symbols/names-final-w20`. Both preserve the same encoded 276,006-byte workload, 1,000 objects × 16 pairs and string values. The final after passes all six fork screens; the before run fails on repeated fork 1 (warmup/measurement +6.02%, measurement drift −6.46%), repeated fork 3 (+3.47% drift), and unique fork 2 (−3.35% drift), so paired timing changes remain diagnostic rather than accepted percentages.

| Naming | Before us/op ± error | Final after us/op ± error | Before B/op | After B/op | Allocation change |
| --- | ---: | ---: | ---: | ---: | ---: |
| repeated (16 names) | 1,736.376 ± 77.118 | 1,515.208 ± 12.223 | 3,532,006.100 | 2,636,085.290 | −25.37% |
| unique (16,000 names) | 1,712.864 ± 49.629 | 3,472.954 ± 32.095 | 3,532,006.015 | 6,153,924.196 | **+74.23%** |

The high-cardinality allocation regression is material: an extra **2,621,918.182 B/op**, with observed timing about **2.03×** the before score. Every operation adds 16,000 names and the >6,000-name release policy discards that snapshot; decoding, interning, table additions/copying/rehashing recur. A separate allocated-byte/table diagnostic observes 16,000 child entries, 32,768 buckets and zero spillovers with the final encoding. This cost remains after removing the avoidable long-name-path overhead of the exploratory encoding. Canonicalization is beneficial for the frozen repeated-name cursor, but this patch does not claim to improve all workloads. `CANONICALIZE_PROPERTY_NAMES=false` provides the tested decode-only placeholder path; it also makes the intern flag ineffective, matching resolved Jackson behavior. No unbounded sharing or deserialized-tree cache is introduced to hide the high-cardinality cost.


Separate final profile: `WARMUP=10 scripts/bench-tree-read.sh profile`, output `target/reader-performance/profile-20261003T203850Z-z09iuq74`. Two independent one-fork invocations per format retain five measurement markers each and the unchanged first-sample-per-thread/interval boundary exclusion. All recordings have zero missing/truncated interior stack weight. Profile timing scores do not enter the acceptance table.

| Recording | Interval seconds | Interior target sample weight GB | Excluded boundary weight GB |
| --- | ---: | ---: | ---: |
| json-1 | 10.031 | 13.126 | 28.586 |
| json-2 | 10.035 | 13.317 | 28.492 |
| smile-1 | 10.031 | 17.390 | 37.596 |
| smile-2 | 10.029 | 17.143 | 37.561 |
| vpack-1 | 10.028 | 17.636 | 41.312 |
| vpack-2 | 10.035 | 18.119 | 40.383 |

These are statistical **interval sample weights, not GB/op or measured B/op**. Neither final VPACK recording has interior samples attributed to `_readPropertyName` or `_decodePropertyName`; absence of samples alone would not prove zero allocation, but exact warm decode accounting supplies separate evidence. String allocation remains (1.113 / 0.714 GB class weight), with string VALUES visible in `ParseFrame.parseValueInBuf`. Byte-array class weight remains 8.924 / 7.923 GB, including compact root/nested object/array copying and String backing arrays. LinkedHashMap entries/tables, node objects, ParseFrames, ArrayLists, and VByte `int[]` helpers remain visible. Scratch growth for longer names, cold misses, canonicalization-disabled names, and symbol-table copy/resize/add operations are other remaining allocation sites. No claim is made that total String allocation vanishes.

The review archive is [reader-symbols-2026-10-03](../jmh-result/reader-symbols-2026-10-03/). [implementation.patch](../jmh-result/reader-symbols-2026-10-03/implementation.patch) contains the exact final **two-file production diff**. The diagnostic patch, test source and logs/failure inventory, resolved source-JAR fingerprints and canonicalizer bytecode, original/final JMH results/logs/screens/commands/class hashes/metadata, name accounting, malformed-byte diagnostics, all six final JFR files/full-stack analyses, rejected exploratory implementation/results, and SHA-256 manifest are retained there. Full compiled snapshots and XML reports remain in `target/`; the original frozen bundle is unchanged.

The shared-buffer stage remains cleanly separable: every `ParseFrame` buffer layout and container-copy site is unchanged, and accounting still observes 5,601,516 copy bytes and 16,624 frames. Future work can change those buffers while continuing to pass `(byte[], offset, length)` into the isolated name lookup. Writer work, tree caching, strict malformed-UTF-8 rejection, and the remaining InputStream document-limit fix are outside this stage.

## Shared container spans and depth-based frame reuse (Prompt 3)

The production reader now traverses `byte[]` containers directly, including the
root and nonzero input slices. One coordinate convention applies everywhere:
`origin`, `containerEnd`, `contentStart`, `contentEnd`, `indexStart`, and
`currentPos` are absolute backing-array indices; ends are exclusive. The only
relative coordinates are encoded index entries, translated as `origin + offset`.
Bounds are checked before translation, multiplication, narrowing, scalar access
or child-frame creation. A child must fit within its parent's content region,
excluding that parent's count and index metadata. Sorted object indexes retain
index order even when their offsets run backward. Compact counts are decoded
backward from each container's own end. Width-8 counts remain at the container
end; optional header padding is bounded by the nine-byte header region.

Byte-array input remains caller-owned: keep the selected input slice stable
while parsing. No frame takes ownership of it, and close clears all parser/frame
buffer references without returning that array to Jackson's recycler. Returned
Strings, binary arrays and custom payloads retain their previous ownership:
Strings are decoded, binary payloads are copied, and custom payload access
returns copies. Saved values survive parser movement, stream refill and close.

Stream input retains Jackson's original recyclable IO buffer throughout. Only
one root container is materialized at a time, with its exact type byte and header;
all nested frames view that stable owned array. Materialization grows as bytes
arrive, avoiding huge allocations from forged lengths on short input. Growth
copies are linear in root size; this is not a fully incremental container reader.
No active frame refers to the refill buffer. Neither stable roots nor scalar
payload arrays are returned to the IO recycler. Empty containers allocate no
encoded byte arrays. Short reads, EOF, IO failures and zero-byte reads are handled
explicitly. `AUTO_CLOSE_SOURCE` and managed-source behavior remain unchanged.

Frames are parser-local and reused by nesting depth, with allocation following
the depth high-water mark. Every container field, including object key/value
state, is reset on reuse; release and close clear buffer references. Jackson
3.2.0's `SimpleStreamReadContext` already caches and resets its child context,
so no additional context pool was added. The Prompt 2 name canonicalizer,
factory defaults, writer, encoded inputs, token ordering and public signatures
remain unchanged. String, name, nesting and cumulative stream document limits
are enforced, including short strings and nested long strings. Malformed lengths,
counts, offsets and arithmetic yield Jackson read/constraint errors.

The separately compiled spans-only snapshot is retained in
`target/reader-spans/spans-only-VPackParser.java`; the next substage adds frame
reuse. Instrumentation is confined to test/diagnostic subclasses, absent from
the JMH timing classpath. Buffer identity checks cover roots and every child;
copy accounting counts stream header transfers, refill-to-root transfers and
root growth separately from owned scalar payloads. Distinct frame identities
measure creation independently of container starts. Exact copy counts are not
throughput measurements.

### Compatibility and malformed-fixture isolation

The spans-only focused run passed **683 tests**; the final frame-reuse focused
run passed **685 tests**, with zero failures/errors/skips. The full final run
reports **6,490 tests, 12 failures, 10 errors, 3 skips** in Maven and
**6,539/61/10/3** in XML. All 22 failing method/kind/exception-type/repetition
signatures match the Prompt 2 inventory exactly. The four former opt-in safety
probes are now permanent passing tests. `compare_tests.py` preserves the complete
failure inventory and exits unsuccessfully for a new or changed failure.

`VPackSharedSpanTest` adds ten independent-fixture tests, retaining Prompt 1's
coverage of every container family, widths 1/2/4/8, optional padding, shuffled
sorted indexes, slices with poison suffixes, multi-root sequences, numeric types,
contexts, `skipChildren`, feature defaults and source ownership. New coverage
includes multi-byte forward/reverse compact metadata; tags around all container
forms; every truncation of small container/scalar fixtures; malicious signed and
unsigned lengths/counts/offsets; a child crossing its parent's metadata boundary;
256-deep containers and 10,000 iterative tags; large string/binary/custom values;
IO/read/close failures; frame and existing Jackson context identity reuse across
2,000 mixed roots and thousands of siblings; and exact IO-recycler buffer identity.
Existing writer round trips and property-name canonicalization tests still pass.

One pre-existing fixture, `testCustomTypeLEN8_inArray`, placed its payload at
positions 10/11 instead of 11/12. Position 10 was still part of the eight-byte
length, accidentally declaring `0x5500000000000002` payload bytes. The corrected
valid fixture uses all eight length bytes and still requires its embedded value
and END_ARRAY. A separate malicious-input assertion retains the exact original
bytes and requires controlled rejection. No bounds test was weakened to accept
this malformed encoding. Malformed UTF-8 replacement semantics and integer
attribute-name translation remain as in Prompt 2.

### Exact allocation accounting

The frozen cursor remains **1,144,774 encoded bytes**, **154,578 tokens**,
**62,628 property-name occurrences**, **56 distinct names**, **16,624 containers**
and **depth 7**, with unchanged input and complete transcript hashes. Byte-array
copy accounting observes **5,601,516 → 0 container payload bytes**. The spans-only
snapshot creates 16,624 frames; the final snapshot creates **7**, independently
isolating frame reuse from span sharing. Final cold name decodes remain **56**;
a subsequent parse on the same factory has **0**, preserving Prompt 2.

Final stream accounting observes **one root buffer**, **seven frame instances**
and **3,184,774 copied encoded bytes**, comprising the complete 1,144,774-byte
root transfer (including its header) and 2,040,000 bytes copied during geometric
root-buffer growth. There are zero nested-subtree copies. This intentionally
accounts for growth copies instead of reporting just the final root length.
Scalar output copies and decoded String storage are separate from these counts.

For the unchanged 256 KiB scalar wrapped at increasing depths:

| Depth | Encoded bytes | byte[] encoded-container copy bytes | Stream encoded-container copy bytes | Frame instances |
| ---: | ---: | ---: | ---: | ---: |
| 1 | 262,158 | 0 | 766,158 | 1 |
| 4 | 262,173 | 0 | 766,173 | 4 |
| 7 | 262,188 | 0 | 766,188 | 7 |
| 16 | 262,233 | 0 | 766,233 | 16 |
| 32 | 262,313 | 0 | 766,313 | 32 |

The stream payload-copy component is constant (504,000 growth bytes plus the
root transfer); only the extra encoded headers grow with depth. The byte-array
path copies none of these encoded containers. Thousands of mixed siblings and
2,000 roots independently verify frame and Jackson-context identity reuse at
three depths. These counters establish ownership/allocation structure, not
elapsed-time improvements.

### GC-only cursor comparison and frame substage

The benchmark and encoded workload are unchanged:
`Bench.treeReadCursor`, `batchSize=1000`, one thread, 512 MiB heap, five 2-second
measurements, three forks, GC profiler only. Prompt 2's accepted before and the
final after both use twenty 2-second warmups, the same frozen dependency/JDK
lock and the same input/transcript lock. All nine final forks pass the unchanged
convergence screen. The spans-only substage uses ten warmups; VPACK fork 1 has
−3.95% measurement drift and fails the screen. That run remains exploratory;
its timing is not accepted speedup evidence. Normalized allocation is recorded
for that stage independently of timing acceptance.

| Stage / format | us/op ± JMH error | gc.alloc.rate.norm B/op | Time / contemporaneous JSON |
| --- | ---: | ---: | ---: |
| Prompt 2 before / JSON | 5,810.504 ± 72.961 | 8,273,260.283 | 1.000 |
| Prompt 2 before / SMILE | 4,453.287 ± 58.707 | 8,273,455.594 | 0.766 |
| Prompt 2 before / VPACK | 8,395.341 ± 278.080 | 15,683,753.202 | 1.445 |
| Spans only / JSON (exploratory run) | 5,922.279 ± 51.619 | 8,273,300.446 | 1.000 |
| Spans only / SMILE (exploratory run) | 4,476.276 ± 87.690 | 8,273,479.976 | 0.756 |
| Spans only / VPACK (exploratory timing) | 6,982.906 ± 149.468 | 9,629,224.108 | 1.179 |
| Spans + frame reuse / JSON | 5,693.636 ± 115.803 | 8,273,260.081 | 1.000 |
| Spans + frame reuse / SMILE | 4,183.905 ± 100.755 | 8,273,454.739 | 0.735 |
| Spans + frame reuse / VPACK | 7,082.692 ± 78.395 | 8,565,784.831 | 1.244 |

VPACK normalized allocation falls **45.384%**, removing **7,117,968.371 B/op**
from the Prompt 2 baseline. Shared spans account for **6,054,529.094 B/op** of
that measured change; the separately compiled frame substage removes another
**1,063,439.277 B/op (11.044% of the spans-only allocation)**. Raw copied bytes
exclude array headers/alignment, VByte helper arrays, frame objects, symbol
scratch and returned values; they must not be equated to `gc.alloc.rate.norm`.
The span stage also removes per-container VByte scratch arrays. Remaining frame
storage is proportional to maximum depth, not 16,624 container occurrences.

The final VPACK elapsed-time point estimate is **15.635% lower** than the accepted
Prompt 2 score, with non-overlapping reported JMH intervals. Its time ratio to
JSON falls from **1.445 to 1.244**. Unchanged JSON and Smile point estimates also
fall (2.01% and 6.05%), so the full elapsed-time difference cannot be attributed
entirely to this code change. Final VPACK fork means are **7,155.004 / 7,101.594 /
6,991.477 us/op**. There is no accepted paired timing claim for frame reuse alone:
the spans-only run failed stationarity and used a shorter warmup. Copy/frame
counts support the allocation model; elapsed-time evidence comes from JMH.

### Read-side regressions

`run_read_regressions.py` uses the frozen Prompt 2 and final class snapshots and
verifies their dependency/JDK and input/transcript identities. It runs existing
annotated readers, names and depth workloads with GC only, one fork, five
2-second warmups and three 1-second measurements. These short VPACK-only runs
are allocation diagnostics, not stationary timing acceptance; the main cursor
comparison above remains the three-format, three-fork acceptance evidence.

| Existing reader | Before B/op | After B/op | Allocation change | Before → after observed us/op |
| --- | ---: | ---: | ---: | ---: |
| POJO cursor | 15,248,468.212 | 8,130,552.523 | −46.68% | 9,903.714 → 9,362.413 |
| Sequence byte[] | 12,917,864.000 | 8,113,880.004 | −37.19% | 9,303.949 → 9,308.127 |
| Sequence InputStream | 12,925,913.935 | 9,286,035.988 | −28.16% | 9,574.977 → 9,858.116 |
| Streaming cursor | 9,675,843.214 | 2,557,926.979 | −73.56% | 6,268.978 → 5,653.894 |
| Tree cursor InputStream | 15,691,848.152 | 11,758,885.581 | −25.06% | 8,140.269 → 7,790.748 |
| Tree document | 14,032.052 | 9,704.050 | −30.84% | 7.485 → 7.185 |

There is no demonstrated sequence timing improvement: the byte-array point
estimate is essentially unchanged and the stream point estimate is 2.96% higher.
Neither is an accepted regression/speedup conclusion from these short one-fork
runs. Stream root materialization and cumulative bounds checks still have a
cost; reduced allocation does not guarantee lower elapsed time on every path.
All original benchmark setup correctness checks execute in each fork.

Repeated-name allocation falls **2,636,100.911 → 1,992,242.081 B/op (−24.42%)**;
unique-name allocation falls **6,154,023.873 → 5,510,127.725 B/op (−10.46%)**.
The high-cardinality canonicalizer cost identified in Prompt 2 remains: final
unique-name allocation is about **2.77×** repeated-name allocation, and observed
unique/repeated timing is about **2.36×**. This stage preserves canonicalization;
it does not hide its table growth, insertion, interning or release-discard cost.

| Depth around same 256 KiB scalar | Before B/op | After B/op |
| ---: | ---: | ---: |
| 1 | 525,376.543 | 263,312.254 |
| 4 | 1,312,609.130 | 264,040.257 |
| 7 | 2,099,825.739 | 264,712.262 |
| 16 | 4,461,891.537 | 267,048.272 |
| 32 | 8,662,254.728 | 271,160.287 |

The after allocation grows with frame/context/output-tree depth around one
owned decoded scalar, rather than repeatedly copying its encoded payload.
At depth 32 the reduction is **96.87%**. Exact buffer/copy identity tests provide
independent evidence for this model; these JMH allocation totals include all
parser and returned-tree allocations.

### Separate JFR and remaining hotspots

The final profile uses the unchanged separate JFR workflow: ten 2-second
warmups, five 2-second marked measurements and two independent one-fork
invocations per format. First samples per thread/interval remain excluded because
their weights may span warmup/setup/gaps. All six recordings have zero missing
or truncated interior stack weight. Profile timing scores do not enter acceptance.

| Recording | Interval seconds | Interior target sample weight GB | Excluded boundary weight GB |
| --- | ---: | ---: | ---: |
| json-1 | 10.025 | 13.603 | 29.737 |
| json-2 | 10.032 | 14.162 | 29.709 |
| smile-1 | 10.022 | 18.660 | 35.699 |
| smile-2 | 10.019 | 18.987 | 40.508 |
| vpack-1 | 10.028 | 12.062 | 23.941 |
| vpack-2 | 10.026 | 12.092 | 24.038 |

These are interval sample-weight estimates, not measured B/op or GB/op. Final
VPACK byte-array class weight is **2.786 / 2.737 GB**, with recorded parser paths
attributed to decoded string VALUES and their String backing arrays; those
constructors still call `Arrays.copyOfRange`. Such stacks must not be mistaken
for encoded-container copying merely because they include an Arrays copy.
LinkedHashMap entries account for **3.110 / 3.368 GB**, and map tables, map objects,
object/array nodes, numeric nodes and ArrayList storage remain substantial.
String class weight is **0.833 / 1.833 GB**, illustrating sampling variation.
No interior frame-allocation or container-start copy stacks appear in these two
VPACK recordings; the exact seven-frame/zero-byte accounting establishes the
stronger allocation claim independently of sampling absence.

Remaining costs include returned tree structure and decoded scalar Strings;
owned binary/custom payloads and BCD mantissa temporaries; root-scalar byte
staging; cold/disabled/high-cardinality name decoding and canonicalizer tables;
depth-proportional frame/context/list storage; and stream root-buffer allocation
and geometric growth. Stream containers remain eager per root. A fully incremental
reader with trailing index/count metadata is deferred; no whole-stream
`readAllBytes`, borrowed mutable binary payloads or process-wide frame pool was
introduced.

### Evidence and reproduction

Review evidence is retained under
[reader-spans-2026-10-03](../jmh-result/reader-spans-2026-10-03/): exact production
and diagnostic/test patches, spans-only source and frame-reuse substage diff,
unchanged public `javap` API comparison, final tests/failure-signature comparison,
encoded inputs/transcripts and exact accounting, GC-only JSON/logs/screens for
both stages, read-side before/after diagnostics, all six separate JFR recordings
and complete-stack weighted analyses, resolved locks and class/source hashes,
commands and a SHA-256 archive manifest. Complete compiled snapshots remain in
`target/reader-performance/`.

Spans-only: `acceptance-20261003T211232Z-a5rzta_p` (exploratory timing).
Final acceptance: `acceptance-20261003T212031Z-d8sl058r` (all nine screens pass).
Final JFR: `profile-20261003T213010Z-7vi9lrsn`.
Read diagnostics: `target/reader-spans/read-regressions-final`.
Reproduce acceptance and profiling with the existing frozen lock using
`WARMUP=20 scripts/bench-tree-read.sh acceptance` and
`WARMUP=10 scripts/bench-tree-read.sh profile`; run supplemental before/after
readers with `scripts/reader-performance/run_read_regressions.py` as documented
in its module header. Test instrumentation never enters timing forks.


## End-to-end checkpoint (Prompt 4, before further implementation)

The fresh post-Prompt-3 run is `acceptance-20261003T220218Z-7ot13wu3`. All nine convergence screens pass. It reuses the frozen baseline JDK/artifact/input lock and unchanged `Bench.treeReadCursor`, batch size 1000, with twenty 2-second warmups, three forks per format, five 2-second measurements, one thread, 512 MiB heap, GC profiler only. One operation reads the whole cursor into a JsonNode tree. Original JMH units remain **us/op**; ns/op below is only multiplication by 1,000. No production source has changed at this checkpoint.

| Stage / format | us/op ± JMH 99.9% error | Derived ns/op | B/op | Time / JSON | Allocation / JSON |
| --- | ---: | ---: | ---: | ---: | ---: |
| Prompt 1 baseline / JSON | 5,692.149 ± 118.856 | 5,692,148.903 | 8,273,299.729 | 1.0000 | 1.0000 |
| Prompt 1 baseline / SMILE | 4,207.530 ± 98.939 | 4,207,529.823 | 8,273,472.077 | 0.7392 | 1.0000 |
| Prompt 1 baseline / VPACK | 8,173.689 ± 159.064 | 8,173,688.891 | 18,791,908.225 | 1.4360 | 2.2714 |
| Prompt 2 symbols / JSON | 5,810.504 ± 72.961 | 5,810,503.927 | 8,273,260.283 | 1.0000 | 1.0000 |
| Prompt 2 symbols / SMILE | 4,453.287 ± 58.707 | 4,453,287.183 | 8,273,455.594 | 0.7664 | 1.0000 |
| Prompt 2 symbols / VPACK | 8,395.341 ± 278.080 | 8,395,341.200 | 15,683,753.202 | 1.4449 | 1.8957 |
| Prompt 3 spans only (exploratory) / JSON | 5,922.279 ± 51.619 | 5,922,278.800 | 8,273,300.446 | 1.0000 | 1.0000 |
| Prompt 3 spans only (exploratory) / SMILE | 4,476.276 ± 87.690 | 4,476,276.323 | 8,273,479.976 | 0.7558 | 1.0000 |
| Prompt 3 spans only (exploratory) / VPACK | 6,982.906 ± 149.468 | 6,982,906.067 | 9,629,224.108 | 1.1791 | 1.1639 |
| Prompt 3 spans and frames / JSON | 5,693.636 ± 115.803 | 5,693,636.063 | 8,273,260.081 | 1.0000 | 1.0000 |
| Prompt 3 spans and frames / SMILE | 4,183.905 ± 100.755 | 4,183,904.537 | 8,273,454.739 | 0.7348 | 1.0000 |
| Prompt 3 spans and frames / VPACK | 7,082.692 ± 78.395 | 7,082,691.669 | 8,565,784.831 | 1.2440 | 1.0354 |
| Prompt 4 fresh post-3 / JSON | 5,867.243 ± 108.022 | 5,867,242.847 | 8,273,260.589 | 1.0000 | 1.0000 |
| Prompt 4 fresh post-3 / SMILE | 4,355.362 ± 49.984 | 4,355,362.201 | 8,273,455.256 | 0.7423 | 1.0000 |
| Prompt 4 fresh post-3 / VPACK | 7,322.917 ± 43.642 | 7,322,916.710 | 8,565,785.622 | 1.2481 | 1.0354 |

The spans-only timing run failed its screen and remains exploratory. Original baseline warmup was ten iterations, the symbols and final spans stages twenty; these historical stages are not a newly matched experiment. Fresh controls rise versus Prompt 3 (JSON +3.05%, SMILE +4.10%, VPACK +3.39%); the VPACK/JSON ratio remains about 1.25. Do not interpret cross-time changes as code effects.

The original excess allocation over JSON was **10,518,608.496 B/op**; it is now **292,525.033 B/op**. Thus **97.219% of that gap disappeared**, while VPACK total allocation fell **54.418%**. The fresh allocation ratio **1.0354** meets <=1.15. The latency ratio **1.2481** fails <=1.10. The operational targets do not authorize changing measurement conditions.

JMH intervals summarize fifteen iteration observations and can understate uncertainty from forks. Fresh fork-mean 95% t intervals (three forks, two degrees of freedom) are JSON **5,587.258–6,147.227**, SMILE **4,267.712–4,443.012**, VPACK **7,231.045–7,414.789 us/op** (exact values retained in the derived comparison). These are descriptive under an independent-fork assumption, with too few forks to establish narrow guarantees. The fresh VPACK/JSON JMH endpoint envelope is **1.2182–1.2791**, a descriptive envelope, not a formal ratio confidence interval. Scheduling, frequency scaling, and between-run drift remain uncontrolled.


### Raw forks and payload identity at the checkpoint

Each cell lists all three fork means in run order. The derived `stage-comparison.json` preserves all five latency and B/op observations in every fork without changing the original JMH JSON.

| Stage / format | Fork means us/op (1 / 2 / 3) | Fork means B/op (1 / 2 / 3) |
| --- | --- | --- |
| Prompt 1 baseline / JSON | 5,820.031 / 5,694.286 / 5,562.130 | 8,273,300.117 / 8,273,299.729 / 8,273,299.340 |
| Prompt 1 baseline / SMILE | 4,141.616 / 4,159.146 / 4,321.828 | 8,273,470.840 / 8,273,470.692 / 8,273,474.699 |
| Prompt 1 baseline / VPACK | 8,166.495 / 8,268.584 / 8,085.988 | 18,791,908.192 / 18,791,908.528 / 18,791,907.955 |
| Prompt 2 symbols / JSON | 5,818.807 / 5,742.739 / 5,869.966 | 8,273,260.338 / 8,273,260.128 / 8,273,260.383 |
| Prompt 2 symbols / SMILE | 4,427.673 / 4,473.218 / 4,458.971 | 8,273,455.288 / 8,273,455.694 / 8,273,455.800 |
| Prompt 2 symbols / VPACK | 8,228.969 / 8,735.412 / 8,221.643 | 15,683,764.218 / 15,683,779.197 / 15,683,716.190 |
| Prompt 3 spans only (exploratory) / JSON | 5,944.162 / 5,901.050 / 5,921.625 | 8,273,300.518 / 8,273,300.394 / 8,273,300.426 |
| Prompt 3 spans only (exploratory) / SMILE | 4,393.654 / 4,494.181 / 4,540.994 | 8,273,478.849 / 8,273,479.124 / 8,273,481.954 |
| Prompt 3 spans only (exploratory) / VPACK | 6,998.730 / 6,848.501 / 7,101.487 | 9,629,224.189 / 9,629,223.623 / 9,629,224.511 |
| Prompt 3 spans and frames / JSON | 5,758.284 / 5,769.500 / 5,553.125 | 8,273,260.163 / 8,273,260.429 / 8,273,259.651 |
| Prompt 3 spans and frames / SMILE | 4,272.210 / 4,191.564 / 4,087.939 | 8,273,454.889 / 8,273,454.847 / 8,273,454.482 |
| Prompt 3 spans and frames / VPACK | 7,155.004 / 7,101.594 / 6,991.477 | 8,565,785.078 / 8,565,785.140 / 8,565,784.274 |
| Prompt 4 fresh post-3 / JSON | 5,762.093 / 5,986.232 / 5,853.404 | 8,273,259.875 / 8,273,261.171 / 8,273,260.722 |
| Prompt 4 fresh post-3 / SMILE | 4,352.218 / 4,392.113 / 4,321.756 | 8,273,455.048 / 8,273,455.556 / 8,273,455.165 |
| Prompt 4 fresh post-3 / VPACK | 7,346.774 / 7,341.662 / 7,280.314 | 8,565,785.361 / 8,565,785.971 / 8,565,785.535 |

Fresh encoded cursor payloads are exactly the frozen baseline: JSON **1,329,760 bytes**, SHA-256 `c498286ced525de2cfd137b35afb6ed13ecbe729278cb2cbe022de8d3569bf00`; SMILE **749,051 bytes**, `47c9281c086762453b1661eac9953cd1c801636ab87b5d34eacdf95a30ed6fd3`; VPACK **1,144,774 bytes**, `de79b1c841db26d525375a7730d63c0a6830910569e8d825de17526343b22dcb`. Complete value/name/type transcripts remain identical, SHA-256 `ffb1b474b2f2559eae42a186322adb5124787045910cf96fd6e12dfb511a3a7e`. There are 154,578 tokens, 62,628 name occurrences, 56 unique names, 16,624 containers and depth 7. `ReaderInputs` checks original benchmark setup round trips, token transcript and InputStream/tree equality before JMH.

### Fresh CPU evidence and implementation decision

Fresh profiles are `profile-20261003T221039Z-0a8h1_be`: two separate one-fork
invocations for each format, twenty 2-second warmups and five marked 2-second
measurements. They use the same frozen lock and workload. **Their timing scores
are excluded from acceptance.** `ReaderCpuAnalysis` reads `jdk.ExecutionSample`
events strictly inside the five measurement intervals, retains full method/line/
bytecode-index stacks, and counts each inclusive method once per sample. It is
compiled only in `diagnostic-classes`, absent from acceptance fork classpaths.

| VPACK CPU attribution | Repeat 1 (821 target samples) | Repeat 2 (860 target samples) |
| --- | ---: | ---: |
| `_valueByteSize`, inclusive | 58 / 7.06% | 73 / 8.49% |
| `_valueByteSize`, leaf | 42 / 5.12% | 47 / 5.47% |
| Sizing under object traversal | 44 | 54 |
| Sizing under array traversal | 6 | 6 |
| Sizing under container initialization | 8 | 13 |
| `_startContainer`, inclusive | 60 / 7.31% | 77 / 8.95% |
| `_readPropertyName`, inclusive | 126 / 15.35% | 137 / 15.93% |
| `_findPropertyName`, inclusive | 122 / 14.86% | 125 / 14.53% |
| `ParseFrame.parseValueInBuf`, inclusive | 227 / 27.65% | 241 / 28.02% |
| `String.<init>`, inclusive | 110 / 13.40% | 96 / 11.16% |
| `ObjectNode.replace`, inclusive | 137 / 16.69% | 98 / 11.40% |

Inclusive rows overlap: sizing under container initialization is already in
`_startContainer`; canonicalizer lookup is already in `_readPropertyName`;
String construction and container initialization are already in value parsing.
They must not be added as independent removable costs. There are zero missing
or truncated interval stacks in all six fresh recordings. JSON target sample
counts are 840 / 851; SMILE 786 / 829. Full control stacks are retained.

Source inspection confirms that object traversal sizes the key before its name
header is read again, and sizes the value before scalar/container decoding.
Sequential arrays also size before decoding. Container initialization sizes
again; compact length metadata is traversed again to locate the content start.
This is duplicated work, but the sizing path also validates complete token
bounds and enclosing content boundaries. A decode-once implementation must
transfer those checks into every supported scalar/name/container path and keep
validated child ends for parent resumption. Name hashing, returned Strings,
container counts/index bounds, contexts and tree construction remain necessary.

The repeated complete-method stacks identify sizing as a visible secondary
cost, rather than demonstrating a dominant removable cost. This judgment uses
two recordings, inclusive/leaf attribution, three caller categories, and code
inspection; it does not assign a cost from one inlined source line. Sampling
and inlining can misattribute work, samples can be correlated, and target-thread
execution samples do not account for all GC/safepoint/other-thread elapsed time.
**7–8.5% is neither an exact cost nor a hard speedup ceiling.** The broad
`parseValueInBuf` percentage includes scalar decoding and container creation,
not just header dispatch. The samples do not establish that the proposed
change would remove approximately 10% of end-to-end tree-read time after
preserving all safety checks. About 11.87% elapsed-time reduction would be
needed to reach the current JSON latency target.

**No decode-once experiment was implemented.** The evidence does not justify
its added state and per-encoding validation complexity under the requested
material-improvement threshold. The Prompt 2/3 improvements are retained.
There is no experimental before/after speedup to report, no experiment to
revert, and no switch/endian/token-reset substitute tuning. This is a decision
to stop speculative implementation, **not a claim that the latency target was
met or that a future architecture experiment cannot help**.

### Separate allocation profile evidence

The unchanged analyzer excludes the first allocation sample per thread per
measurement interval, because its weight may span setup/warmup/interval gaps.
All six recordings contain five measurement markers and zero missing/truncated
interior allocation stack weight. Weights are statistical interval estimates,
**not B/op**; acceptance allocation comes exclusively from JMH's GC profiler.

| Recording | Measurement seconds | Interior target weight GB | Excluded boundary weight GB |
| --- | ---: | ---: | ---: |
| JSON 1 | 10.021 | 13.030 | 55.800 |
| JSON 2 | 10.018 | 13.173 | 56.735 |
| SMILE 1 | 10.026 | 17.528 | 69.529 |
| SMILE 2 | 10.021 | 17.736 | 74.854 |
| VPACK 1 | 10.030 | 11.333 | 48.911 |
| VPACK 2 | 10.028 | 11.616 | 50.411 |

Remaining VPACK class weights include byte arrays **1.987 / 3.053 GB**, Strings
**1.076 / 1.507 GB**, LinkedHashMap entries **3.697 / 2.744 GB**, and map tables
**0.834 / 1.037 GB**. These estimates vary substantially with sampling. Decoded
string-value backing arrays and returned tree storage remain; a String backing
array copy is not an encoded-container copy. Prompt 3's exact zero-container-
copy and seven-frame accounting remains the stronger structural evidence.

### Correctness, limitations and unchanged invariants

Fresh focused parser/canonicalizer/shared-span/safety regression tests pass:
**250 tests, zero failures/errors/skips**. The fresh full suite reports
**6,490 tests, 12 failures, 10 errors, 3 skips** in Maven; XML totals are
**6,539/61/10/3**. Its 22 failing method/kind/exception-type/repetition signatures
match Prompt 3 exactly, with no new, changed or removed signatures. The full
Maven exit status is 1 because of those existing failures; it is not reported
as a clean full-suite pass. The four permanent constraint probes pass in the
focused run. All benchmark setup round trips and locked input/transcript checks
also pass. The focused XML archive retains pre-existing reports left by
Surefire; its authoritative 250-test count comes from the focused Maven log,
not summing stale reports from other classes. Full-suite XML is compared after
the full run.

Every production class and benchmark class in the fresh acceptance snapshot
has the same hash as the Prompt 3 acceptance snapshot. Only the SharedSpan test
class and its nested classes differ from that earlier pre-final-test build.
Source checks against starting HEAD `5d4b08f` confirm no changes in `src/main`,
`src/test`, `pom.xml`, the reader harness or convergence-screen implementation
in this stage. Writer output identity is checked through all locked encoded
fixtures. Dependencies, JDK lock, seed, batch size, tree-read semantics, factory
defaults, supported encodings and safety checks were not changed to achieve
these results. JMH still declares `MICROSECONDS`. No benchmark result data was
edited; the report and CPU analyses are separate derived files.

Unresolved limitations: latency still misses <=1.10 JSON; canonicalization's
high-cardinality table/interning costs from Prompt 2 remain; stream containers
still materialize whole roots and grow buffers; scalar/String and returned-tree
allocations remain; malformed UTF-8 still follows replacement decoding. The
original historical JDK 17 environment is unavailable, so no speedup against
that historical run is claimed. Fresh broad supplemental timing runs and
matched decode-once trials are **unavailable/not run**, because production code
is unchanged and the architecture experiment was not justified. Prior Prompt 3
supplemental results remain diagnostic, with their documented short-run
uncertainty; they do not become stationary timing acceptance in this stage.

### Exact commands and retained evidence

Run from `/workspace`, sequentially, with the existing
`target/reader-performance/frozen` bundle:

```bash
WARMUP=20 scripts/bench-tree-read.sh acceptance > /tmp/reader-prompt4-acceptance.log 2>&1
WARMUP=20 scripts/bench-tree-read.sh profile > /tmp/reader-prompt4-profile.log 2>&1
python3 scripts/reader-performance/analyze_cpu.py target/reader-performance/profile-20261003T221039Z-0a8h1_be > target/reader-prompt4/cpu-analysis.log
python3 scripts/reader-performance/compare_stages.py target/reader-prompt4/stage-comparison.json 'Prompt 1 baseline' target/reader-performance/acceptance-20261003T140309Z-klm7rzj6 'Prompt 2 symbols' target/reader-performance/acceptance-20261003T202308Z-e91guu_a 'Prompt 3 spans only (exploratory)' target/reader-performance/acceptance-20261003T211232Z-a5rzta_p 'Prompt 3 spans and frames' target/reader-performance/acceptance-20261003T212031Z-d8sl058r 'Prompt 4 fresh post-3' target/reader-performance/acceptance-20261003T220218Z-7ot13wu3 > target/reader-prompt4/stage-comparison.log
mvn -B -o -Dtest=ReaderRegressionTest,ReaderSafetyGapTest,VPackSharedSpanTest,VPackPropertyNameCanonicalizationTest,VPackParserFeaturesTest,VPackParserStreamingTest,VPackParserBufTest,VPackParserBootstrapperTest,VPackScalarParseTest,VPackTaggedValueTest,VPackCustomValueTest,VPackParserErrorTest test
mvn -B -o test
python3 scripts/reader-performance/compare_tests.py jmh-result/reader-spans-2026-10-03/frame-test-results.json target/reader-prompt4/full-xml target/reader-prompt4/test-results.json
```

[reader-checkpoint-2026-10-03](../jmh-result/reader-checkpoint-2026-10-03/)
retains the fresh GC-only JSON/logs/screen, all six JFR files and allocation/CPU
full-stack analyses, metadata and frozen locks, input bytes/transcripts,
class/source hashes and source-invariant checks, test logs/XML/failure-signature
comparison, diagnostic sources, and a SHA-256 manifest. Each run's
`commands.json` includes exact build/accounting/JMH executable and classpath
arguments; profiling `cpu-commands.json` records each analyzer command;
`test-commands.json` records test commands and destinations. All raw per-fork
measurements for every stage are readable in
[stage-comparison.md](../jmh-result/reader-checkpoint-2026-10-03/stage-comparison.md)
and retained at full precision in
[stage-comparison.json](../jmh-result/reader-checkpoint-2026-10-03/stage-comparison.json).
Original result files stay in their prior archives and complete compiled
snapshots remain under `target/reader-performance/`. `commands.txt` reproduces
the commands above. Review this evidence alongside the original three-stage
archives, including the explicitly exploratory spans-only run.
