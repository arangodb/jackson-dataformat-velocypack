#!/usr/bin/env python3
"""Generate the final tables without selecting forks or dropping measurements."""
import pathlib,json,hashlib,statistics
root=pathlib.Path.cwd(); artifacts=root/'target/writer-stage4'
def run_from(log,prefix=root):
    return prefix/pathlib.Path((artifacts/log).read_text().splitlines()[0])
before=run_from('refreshed-before-harness.log',artifacts/'original');after=run_from('final-after-harness.log')
stage1=root/'target/jmh-result/tree-write-20261002T223110Z-Ku1k3Z'
initial=root/'target/jmh-result/tree-write-20261003T101430Z-M92JuT'
def read(p):return json.loads(p.read_text())
def by_format(p):return {r['params']['format']:r for r in read(p)}
b=by_format(before/'baseline.json');a=by_format(after/'baseline.json');s1=by_format(stage1/'baseline.json');i=by_format(initial/'baseline.json')
def mean(r):return r['primaryMetric']['score']/1000
def error(r):return r['primaryMetric']['scoreError']/1000
def alloc(r):return r['secondaryMetrics']['gc.alloc.rate.norm']['score']
def ci(r):return [x/1000 for x in r['primaryMetric']['scoreConfidence']]
low=ci(b['VPACK'])[0]/ci(a['VPACK'])[1];high=ci(b['VPACK'])[1]/ci(a['VPACK'])[0];ratio=mean(b['VPACK'])/mean(a['VPACK']);passed=low>=5
fm=lambda r:f"{mean(r):.6f} ± {error(r):.6f}"
rel=lambda p:str(p.relative_to(root))
def row(format):
    old,new=b[format],a[format]
    return f"| {format} | {fm(old)} | {fm(new)} | {mean(old)/mean(new):.3f}× | {alloc(old):,.0f} | {alloc(new):,.0f} |"
lines=['# Writer performance: independently validated final result','',
       f"**At least 5× was {'MEASURED, with the conservative uncertainty envelope above 5×' if passed else 'NOT established by measurement'}.** Fresh original writer: {fm(b['VPACK'])} ms/cursor; final writer: {fm(a['VPACK'])} ms/cursor. The mean speedup is **{ratio:.6f}×**. Dividing the endpoints of the separate JMH 99.9% intervals gives a conservative speedup envelope **[{low:.6f}, {high:.6f}]×** (an endpoint comparison, not a fitted ratio confidence interval). "+('The envelope straddles 5×; this is uncertain, not a rounded pass.' if low<5<=high else 'The measured interval stays below 5×.' if high<5 else ''),
       '',f"The fresh 5× threshold is {mean(b['VPACK'])/5:.6f} ms/cursor. The preserved stage-1 baseline was {fm(s1['VPACK'])} ms/cursor: its reference ratio is {mean(s1['VPACK'])/mean(a['VPACK']):.6f}×. Historical 32.904 ms/cursor and its 6.581 ms equivalent are context only. No historical, exploratory or JFR score determines the acceptance verdict.",
       '', '## Comparable cursor measurements','',
       'All scores aggregate all 15 measured iterations across three forks. Time is ms/cursor; JMH JSON uses us/op and is divided by 1000. Errors are JMH 99.9% confidence half-widths. Allocation is bytes/cursor (B); no binary/decimal MB conversion is used.', '',
       '| Format | Fresh original ms/cursor | Final ms/cursor | Mean speedup | Original B/cursor | Final B/cursor |',
       '| --- | ---: | ---: | ---: | ---: | ---: |']+[row(f) for f in ['JSON','SMILE','VPACK']]
lines+=['',f"VPACK allocation falls by {(1-alloc(a['VPACK'])/alloc(b['VPACK']))*100:.2f}% ({alloc(b['VPACK'])/alloc(a['VPACK']):.3f}× less), to {alloc(a['VPACK']):,.0f} B/cursor. This satisfies the 8,000,000 B aspiration and the preferred 10× reduction; allocation does not substitute for the time requirement.",
        '', '| Format | Original GC count | Final GC count | Original GC time (ms) | Final GC time (ms) |', '| --- | ---: | ---: | ---: | ---: |']
for f in ['JSON','SMILE','VPACK']:
    lines.append(f"| {f} | {b[f]['secondaryMetrics']['gc.count']['score']:.0f} | {a[f]['secondaryMetrics']['gc.count']['score']:.0f} | {b[f]['secondaryMetrics']['gc.time']['score']:.0f} | {a[f]['secondaryMetrics']['gc.time']['score']:.0f} |")
lines+=['','GC totals cover the 15 two-second measurement iterations per format, excluding warmup. They are profiler collection counts and time, not per-operation pause estimates. Default G1 remains unchanged. JSON and Smile are controls; their complete intervals and fork results remain in the JSON artifacts. The host scheduler was not pinned, and run-to-run/fork variability is represented rather than removed.','',
        '| Format | Document B | Cursor B | Sequence B |', '| --- | ---: | ---: | ---: |',
        '| JSON | 1,213 | 1,329,760 | 1,329,477 |', '| SMILE | 874 | 749,051 | 748,832 |', '| VPACK | 1,029 | 1,144,774 | 1,144,546 |','',
        'These sizes match the original writer. Bench setup validates round trips in every measured case. The seeded untagged fixture is byte-identical; the legitimate tagged-output changes are listed below. Each tree/POJO operation constructs a generator and returns a newly owned byte[]. Streaming uses a size-hinted ByteArrayOutputStream and returns its fresh copy. Sequence writing retains generator capacity across roots inside one operation, then returns a fresh array. These output-storage differences prevent treating streaming, tree and sequence allocation as equivalent paths.','',
        '## Broader regression measurements','',
        'Both the original and final writer repeat the stage-1 auxiliary matrix: one fork, one thread, batch sizes 1 and 1000, the same three warmups/five measurements/two-second durations, fixed 512 MB heap, GC profiling and no JFR. Errors in these one-fork cases are particularly wide; ratios describe means, not guarantees. TreeWriteDocument always serializes one document even with batchSize=1000.','',
        '| Operation | Format | Batch | Original ms/op | Final ms/op | Original B/op | Final B/op |', '| --- | --- | ---: | ---: | ---: | ---: | ---: |']
key=lambda r:(r['benchmark'],r['params']['format'],r['params']['batchSize'])
old={key(r):r for r in read(before/'regressions.json')};new={key(r):r for r in read(after/'regressions.json')}
assert old.keys()==new.keys() and len(new)==24
for k in sorted(new):
    x,y=old[k],new[k];lines.append(f"| {k[0].split('.')[-1]} | {k[1]} | {k[2]} | {fm(x)} | {fm(y)} | {alloc(x):,.0f} | {alloc(y):,.0f} |")
lines+=['','The complete stage-1 auxiliary results remain in docs/writer-rewrite-before-results.json; the table above uses the additional stage-4 original rerun. No small-document or sequence score is used for cursor acceptance.','',
        '## Substantial remedy and profiles','',
        f"The incoming stage-3 writer failed to establish the target in its first stage-4 three-fork run: {fm(i['VPACK'])} ms/cursor, {alloc(i['VPACK']):,.0f} B/cursor. A separate one-fork JFR recording identified short text encoding and ObjectNode/ArrayNode traversal as prominent paths. Container closure was 80/1,227 inclusive samples (6.52%), while UTF-8 encoding was 446/1,227 leaf samples (36.35%). These samples include setup/warmup, are subject to sampling/JIT attribution, and are not precise cost shares.",
        '', 'A guarded ASCII bulk-copy prototype in the generic encoder measured 8.047598 ± 3.946727 ms/cursor in one fork and did not establish improvement. The final remedy specializes short String/name writes: scan the entire range for ASCII, use String.getBytes(begin,end,target,offset) to copy into shared payload, and retain the existing encoder for Unicode/surrogates. This overload is deliberately guarded because it truncates Unicode. JDK source confirms compact Latin-1 strings use System.arraycopy. The source-specific path avoids generic String/char[] dispatch for ordinary names/values; long strings, char[] slices and root text retain their established behavior. No temporary UTF-8 byte arrays or cached serialized values are introduced.',
        '',f"The final three-fork mean is {mean(i['VPACK'])/mean(a['VPACK']):.6f}× faster than the incoming stage-3 mean; the intervals are shown above and in raw artifacts, so this ratio is not itself proof of an isolated remedy effect. Allocation remains essentially unchanged. Separate final JFR runs identify short-String/name work, tree traversal and capacity/context dispatch as the remaining sampled paths; their sampled results and diagnostic depth counters appear below. No JFR-derived or projected speedup is claimed.",
        '', 'Ancestor payload moves were measured explicitly. The profile did not identify them as the dominant cursor cost, and the final time target is established, so a shared paged-storage/coalesced-span prototype was not warranted by this workload. Such a design could help deep large values, but would add span/container metadata, ordering and final emission complexity. The current writer remains in-place and moves content once per enclosing container; it is neither depth-independent nor zero-copy.',
        '', '## Independent interoperability','',
        'The actual production writer exports 40 binary fixtures and independent JSON expectations. The external checker parses the JSON with the official C++ library, runs Validator on the generated bytes, checks ArrayIterator against Slice.at, checks ObjectIterator against Slice.get, and exercises sequential physical iteration too. It verifies exact tag numbers/counts separately, tests disallowTags=true rejection, and compares binary/Unicode/scalar values. It does not use this project’s VPACK parser or frozen writer to establish wire validity.',
        '', 'Fixtures cover nested compact containers; minimal self-inclusive lengths 127→129 and 16,383→16,385 for arrays and objects; counts 127/128 and 16,383/16,384; mixed indexed-array lengths 255→260 and 65,535→65,544; equal-width no-index arrays crossing 1/2/4-byte widths; sorted and unsorted objects; Unicode names/values; numeric IDs; empty and chained short/long tagged containers; and the complete 1,000-document cursor.',
        '', 'Official current commit: **1450560e980140578eb050199f58c4351a5d13d5**. It validates and exercises access on 37 fixtures, and deliberately rejects the three indexed-unsorted fixtures because upstream removed types 0x0f–0x12 in commit [8002fc5ae1bce417e9a4ae500165f028198f6f01](https://github.com/arangodb/velocypack/commit/8002fc5ae1bce417e9a4ae500165f028198f6f01). This is an existing interoperability limitation of the retained noncompact unsorted layout, not newly malformed output.',
        '', 'The compatible official commit immediately before removal, **5fc5a4e00c9fa17d41d41ff758a2d86684d744a2**, validates all 40 fixtures and passes 316,606 object lookups and 260,536 array iteration/access checks (traversal revisits nested objects). Both builds are unmodified Release libraries. The checker also uses -DNDEBUG consistently with Release; the old header contains a stale assertion excluding unsorted head bytes despite its TypeMap/width/access support. No production dependency or library patch is added.',
        '', 'Validator options are deliberate: validateUtf8Strings=true, disallowTags=false, disallowExternals=true, disallowCustom=true, disallowBCD=false, nestingLimit=256, attributeTranslator set. AttributeTranslatorScope supplies Slice name translation for IDs 1..6; raw it.key(false) also confirms numeric keys. A separate fixture uses nonmonotonic translated names to demonstrate the pre-existing limitation: numeric-key sorting orders encoded IDs, while indexed name lookup expects translated-name order. The actual new and frozen original writers produce identical bytes for that fixture, Validator accepts it, and untranslated iteration succeeds. Both builds miss 4/6 names under that deliberately mismatched translation order; this is recorded as an existing semantic restriction.',
        '', 'Both official validators explicitly return NotImplemented for BCD even when allowed. The decimal 123.45 is independently checked as c8 03 fe ff ff ff 01 23 45: positive type, three mantissa bytes, exponent -2 and packed digits 012345. The initial hand-written expectation used an invalid trailing f nibble; it was corrected from the official specification, not by copying the writer output. The Java BCD assertion plus C++ NotImplemented check keeps this unsupported validation distinct from a pass. Six independent BCD vectors additionally cover negative mantissas, positive/negative exponents, a beyond-long BigInteger and a two-byte mantissa-length header. A separate ownership check verifies fresh equal byte[] outputs and mutation isolation for all three formats across tree cursor/document, POJO cursor, streaming cursor and sequence operations. External/custom values are intentionally not admitted by this validator configuration.',
        '', '## Verification and bug-fix ledger','',
        'Final focused Maven tests: 146, zero failures/errors. Full Maven suite: 6,456 tests, 12 failures, 10 errors, three skipped. Comparing failing method identities, kinds and exception types yields exactly the 22 pre-existing failures, zero added/removed/changed. XML totals are 6,505 tests/61 failures because the 50 converter-mixin repetitions are counted individually. The full suite is therefore not green; it is unchanged. The tests were repeated after diagnostics; final logs/XML are post-validation-focused.log, post-validation-focused-reports/, post-validation-full-suite.log and post-validation-full-reports/ under target/writer-stage4/. Frozen oracle, all-mask differential tests, Bench, ArangoDocuments, POM and dependency lock remain unchanged.',
        '', '| Change | Observable correction / limitation | Evidence |', '| --- | --- | --- |',
        '| Object tags | Keep prefix after name before scalar/container | Independent corrected vectors and C++ lookup/iteration |',
        '| Array tags | Keep prefix with composite, count one wrapped item | Independent vectors and C++ ArrayIterator/Slice.at |',
        '| Nested raw fragments | Reject ambiguous writeRaw/writeBytes before mutation; root blocks pass through | Characterization and focused tests |',
        '| Nested raw None/empty values | Reject invalid leading None/empty value; preserve valid padding/custom bytes | Differential/payload tests |',
        '| Incomplete nested calls | Reject dangling prefixes/missing values and prefixes before names | Tag/payload tests |',
        '| Output failure replay | Retire pending lengths; disallow more values after IOException | Ownership failure injection tests |',
        '| Failure ownership | Still close/flush authorized target; suppress secondary failure | Ownership tests |',
        '| Terminal retained memory | Drop arena/scratch/frame/output references; recycle only owned small buffer | Ownership tests |',
        '| Stage-4 ASCII specialization | Performance-only; preserve Unicode, surrogate replacement and bytes | Focused/full tests, C++ checks and comparable final JMH |',
        '', 'Limitations remain: caller output materialization and geometric arena growth; one header-compaction move per enclosing container; two additional content passes for reordered sorted objects; legacy encoded-ID sorting; latest C++ unsorted-layout incompatibility; BCD cannot be validated by C++; Java-array bounds/heap limits; LENIENT retains legacy ? replacement and raw UTF-8 remains caller-validated; unknown-length binary, Reader strings and textual raw writes are unsupported; raw-value blocks are caller-owned validity; failed streamed input can expose a partial root; dangling root tags remain pass-through. There is no cross-generator arena pool, buffer-reuse requirement, per-token value tree, altered fixture/default, GC tuning or serialized-output cache.',
        '', '## Environment and preserved artifacts','',
        'Host: Intel Core Ultra 7 265T, 20 CPUs, x86_64, Fedora 44, Linux 7.2.8-200.fc44.x86_64/glibc 2.43. JDK Eclipse Temurin 21.0.12.1+1-LTS; Maven 3.9.6; JMH 1.37; compiler release/source/target 17, UTF-8/parameters; fixed -Xms512m -Xmx512m. GC profiler only in acceptance/regression runs; no JFR or JaCoCo agent in timing JVMs. Use -Djackson.version=3.2.0, offline builds and the unchanged dependency checksum lock; core/databind/Smile 3.2.0 and annotations 2.22. Effective POM, dependency tree, resolved checksums, JVM/CPU/source metadata and exact JMH argv are captured for both runs.',
        '',f"Fresh original checkout: 622449bb2933604b4d090c86e0e0748b556334a1; original writer SHA-256 cc6d9cc29fd2193f283fdc0fd99b6247c37cec91af44d026d68b3c7a3ff16b7b. Final writer SHA-256 {hashlib.sha256((root/'src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java').read_bytes()).hexdigest()}. Dependency lock SHA-256 2a36a5ee3d04800ea6278188425e974bb446a19b52d4d08871d8e83c5796ea65. C++ GCC 16.2.1 (Red Hat 16.2.1-2), CMake 3.31.6, C++20, Release/O3/NDEBUG, SSE4.2, xxhash, IPO disabled. Actual compile flags and compile_commands.json remain in the two build directories.",
        '',f"- Fresh before: `{rel(before)}/`.",f"- Final after: `{rel(after)}/`.",f"- Stage-1 before: `{rel(stage1)}/`; historical profiles: `jmh-result/treeWriteCursor-2026-10-02/` (unchanged).",f"- Initial stage-4 result: `{rel(initial)}/`; generic ASCII experiment: `target/writer-stage4/ascii-exploratory/`.",
        '- Fixtures/checker logs, C++ checkouts/builds, source snapshots, checksums, test logs/XML/failure comparison, depth and unique JFR directories: `target/writer-stage4/`.',
        '- Portable final raw metric/fork summary: `docs/writer-performance-results.json`. Earlier stage artifacts in docs/ and target/writer-stage1, writer-stage2, writer-stage3 remain preserved.',
        '', '## Exact reproduction','',
        'Run commands sequentially to avoid compilation/tests/profiles competing with timing. Do not use Bench.main (its JFR filename is shared). Commands.json in each timing directory expands the full runtime classpath and JMH argv exactly. The harness still names cursor files baseline.json and all-mode purpose “unmodified writer baseline”; final-after provenance here overrides that generic label.',
        '', '```bash',
        '# Provision the locked Maven artifacts; then use the same JDK and offline configuration.',
        'mvn -B -o -Djackson.version=3.2.0 -Dtest=VPackWriterDifferentialTest,VPackWriterOwnershipTest,VPackWriterPayloadTest,VPackWriterTagRegressionTest,LegacyWriterCharacterizationTest,VPackGeneratorContainerTest,VPackGeneratorAdvancedTest,VPackGeneratorFeaturesTest,VPackGeneratorErrorTest test',
        'mvn -B -o -Djackson.version=3.2.0 test',
        '# Fresh original writer in an isolated checkout (no production dependency):',
        'git worktree add --detach target/writer-stage4/original 622449bb2933604b4d090c86e0e0748b556334a1',
        'mkdir -p target/writer-stage4/original/scripts',
        'cp scripts/bench-tree-write.sh scripts/tree-write-dependencies.json target/writer-stage4/original/scripts/',
        '(cd target/writer-stage4/original && FORKS=3 REGRESSION_FORKS=1 scripts/bench-tree-write.sh all)',
        'FORKS=3 REGRESSION_FORKS=1 scripts/bench-tree-write.sh all',
        '# Harness expands this acceptance invocation for JSON, SMILE and VPACK:',
        "java -cp '<captured test/classes/dependency classpath>' org.openjdk.jmh.Main '^com\\.arangodb\\.jackson\\.dataformat\\.velocypack\\.Bench\\.treeWriteCursor$' -p batchSize=1000 -f 3 -t 1 -wi 3 -w 2s -i 5 -r 2s -jvmArgs '-Xms512m -Xmx512m' -prof gc -rf json -foe true -p format=JSON,SMILE,VPACK -rff '<unique directory>/baseline.json'",
        '```', '', 'C++ build and checks (CMake was unavailable on this host, so a local pinned binary was used):','', '```bash',
        'mkdir -p target/writer-stage4',
        'curl -L --fail https://github.com/Kitware/CMake/releases/download/v3.31.6/cmake-3.31.6-linux-x86_64.tar.gz -o target/writer-stage4/cmake.tar.gz',
        'sha256sum target/writer-stage4/cmake.tar.gz',
        '# Expected: 5a1133ff103c71eb5120e2cc3de922733e7d8a26a98ae716397e8676adb367bf',
        'tar -xzf target/writer-stage4/cmake.tar.gz -C target/writer-stage4',
        'git clone https://github.com/arangodb/velocypack.git target/writer-stage4/velocypack',
        'git -C target/writer-stage4/velocypack checkout --detach 1450560e980140578eb050199f58c4351a5d13d5',
        'git -C target/writer-stage4/velocypack worktree add --detach ../velocypack-compatible 5fc5a4e00c9fa17d41d41ff758a2d86684d744a2',
        'target/writer-stage4/cmake-3.31.6-linux-x86_64/bin/cmake -S target/writer-stage4/velocypack -B target/writer-stage4/cpp-build -DCMAKE_BUILD_TYPE=Release -DUseIPO=OFF -DBuildTests=OFF -DBuildSseOpt=ON -DHashType=xxhash',
        'target/writer-stage4/cmake-3.31.6-linux-x86_64/bin/cmake --build target/writer-stage4/cpp-build --target velocypack -j2',
        '# The old revision enables SSE automatically; its unused BuildSseOpt argument is recorded in configure.log.',
        'target/writer-stage4/cmake-3.31.6-linux-x86_64/bin/cmake -S target/writer-stage4/velocypack-compatible -B target/writer-stage4/cpp-compatible-build -DCMAKE_BUILD_TYPE=Release -DUseIPO=OFF -DBuildTests=OFF -DBuildSseOpt=ON -DHashType=xxhash',
        'target/writer-stage4/cmake-3.31.6-linux-x86_64/bin/cmake --build target/writer-stage4/cpp-compatible-build --target velocypack -j2',
        'c++ -std=c++20 -O2 -DNDEBUG -Itarget/writer-stage4/velocypack/include scripts/writer-validation/interop.cpp target/writer-stage4/cpp-build/libvelocypack.a -o target/writer-stage4/interop',
        'c++ -std=c++20 -O2 -DNDEBUG -Itarget/writer-stage4/velocypack-compatible/include scripts/writer-validation/interop.cpp target/writer-stage4/cpp-compatible-build/libvelocypack.a -o target/writer-stage4/interop-compatible',
        f'cp_file={rel(after)}/classpath.txt',
        'validation_cp="target/test-classes:target/classes:$(cat "$cp_file")"',
        'java -cp "$validation_cp" com.arangodb.jackson.dataformat.velocypack.WriterInteropFixtures target/writer-stage4/fixtures',
        'javac -proc:none -cp "$validation_cp" -d target/writer-stage4 scripts/writer-validation/NumericLookupLimit.java',
        'java -cp "target/writer-stage4:$validation_cp" com.arangodb.jackson.dataformat.velocypack.NumericLookupLimit target/writer-stage4/fixtures',
        'javac -proc:none -cp "$validation_cp" -d target/writer-stage4 scripts/writer-validation/BcdVectors.java scripts/writer-validation/BenchOwnership.java',
        'java -cp "target/writer-stage4:$validation_cp" com.arangodb.jackson.dataformat.velocypack.BcdVectors',
        'java -cp "target/writer-stage4:$validation_cp" com.arangodb.jackson.dataformat.velocypack.BenchOwnership',
        'target/writer-stage4/interop-compatible target/writer-stage4/fixtures',
        'target/writer-stage4/interop target/writer-stage4/fixtures current',
        'scripts/writer-validation/depth.sh "$cp_file"',
        'scripts/writer-validation/profile.sh final-1 "$cp_file"',
        'scripts/writer-validation/profile.sh final-2 "$cp_file"',
        'scripts/writer-validation/profile.sh final-3 "$cp_file"',
        'python3 scripts/writer-validation/report.py',
        '```','', 'Profile.sh creates a fresh directory per one-fork invocation and records its exact argv; recording.jfr is unique in that directory. Depth.sh generates a disposable instrumented writer copy from production source, compiles it in its own directory and runs the experiment with the same fixed heap. Counters never enter the acceptance writer. This command order reruns both C++ checks and every final measurement after the production change. The report generator reads the two harness path logs from this session; copy them alongside results when relocating artifacts.']
# The only legitimate byte-identity exceptions are the previously demonstrated tag corrections.
idx=next(i for i,line in enumerate(lines) if line.startswith('Limitations remain:'))
lines[idx:idx]=['Concrete default-layout tag corrections (tag 7; hex bytes):', '',
    '| Calls | Original bytes | Corrected bytes |', '| --- | --- | --- |',
    '| Object a: tag, null | 14 06 41 61 18 01 | 14 08 41 61 ee 07 18 01 |',
    '| Object a: tag, empty array | 14 06 41 61 01 01 | 14 08 41 61 ee 07 01 01 |',
    '| Array: tag, empty array | 13 04 01 01 | 13 06 ee 07 01 01 |',
    '| Array: null, tag, empty array, true | 13 08 18 01 ee 07 1a 03 | 13 08 18 ee 07 01 1a 03 |', '',
    'These independently specified corrections preserve requested prefixes. The last case keeps the same size while fixing placement. Rejected malformed raw fragments/incomplete nested calls have no valid legacy bytes to preserve. All other valid layouts, numeric representations and UTF replacement policy retain the frozen byte contract; the stage-4 specialization introduces no byte changes.', '']
# Insert the measured diagnostics before interoperability, retaining every round/profile in artifacts.
import csv,collections
path_file=artifacts/'depth-path.txt'
if path_file.exists():
    depth_dir=root/pathlib.Path(path_file.read_text().strip())
    rows=list(csv.reader((depth_dir/'results.csv').open()))
    groups=collections.defaultdict(list);cursor=[]
    for r in rows:
        if r and r[0] in ('array','object'):groups[(r[0],int(r[1]))].append(r)
        if r and r[0]=='cursor':cursor.append(r)
    section=['## Diagnostic depth experiment','',
             'A fixed 262,144-byte binary scalar is wrapped in 0–128 one-child arrays or objects (object key child). Depth 0 uses the bounded root-scalar path. One diagnostic JVM uses a 512 MB fixed heap, 0.5-second warmup per case, then three 0.5-second rounds. The table reports mean time and the observed range of those rounds, not a JMH confidence interval. ThreadMXBean records allocated bytes on the operation thread. Every operation creates its own generator/output and fresh returned array. A volatile length sink consumes the result. No parser or reference encoder runs in timed loops.',
             '', 'A disposable copy of the final generator adds counters at actual Arrays.copyOf growth and System.arraycopy header/sort sites; production/JMH classes contain no counters. Capacity growth counts the old array capacity, including ordinary root-array growth. Output target growth, storage writes and the final returned-array copy are counted separately. Metadata stack/sort-ID moves are additional and excluded from the payload-copy columns.', '',
             '| Container | Depth | Mean ms/op | Round range ms/op | Allocated B/op | Arena growth copies B | Header moves B |',
             '| --- | ---: | ---: | --- | ---: | ---: | ---: |']
    diagnostic=[]
    for (kind,depth),values in sorted(groups.items()):
        times=[float(r[4])/1000 for r in values];allocations=[float(r[5]) for r in values];r=values[0]
        section.append(f"| {kind} | {depth} | {statistics.mean(times):.6f} | {min(times):.6f}–{max(times):.6f} | {statistics.mean(allocations):,.0f} | {int(r[7]):,} | {int(r[8]):,} |")
        diagnostic.append({'kind':kind,'depth':depth,'timeMsPerOp':times,'allocatedBytesPerOp':allocations,'arenaGrowthCopiesBytes':int(r[7]),'headerMovesBytes':int(r[8]),'sortingCopiesBytes':int(r[9]),'outputBytes':int(r[6]),'outputGrowthCopiesBytes':int(r[11]),'outputStorageCopiesBytes':int(r[12]),'returnedArrayCopiesBytes':int(r[13])})
    section+=['', 'Header moves grow with enclosing depth: the same scalar payload moves again at each close. Allocation changes much less because the shared arena eliminates recursive payload allocations. This is direct evidence of depth-dependent copying; high-depth large scalars remain a limitation. The CSV includes exact output size, arena capacity, output-growth copies, one storage pass and one returned-array pass for every round.', '',
              'The same counted writer also traverses the unmodified cursor tree (read from its own encoded bytes), using ordinary unsorted and sorted-key layouts:', '',
              '| Cursor layout | Output B | Arena allocated capacities B | Arena growth copies B | Header moves B | Sorting content copies B |',
              '| --- | ---: | ---: | ---: | ---: | ---: |']
    for r in cursor:section.append(f"| {'sorted' if r[1]=='true' else 'default'} | {int(r[2]):,} | {int(r[6]):,} | {int(r[3]):,} | {int(r[4]):,} | {int(r[5]):,} |")
    section+=['', 'Sorting copies count both the payload-to-scratch and scratch-to-payload passes for each reordered object; the separate header move still occurs. Arena growth copies and final caller-owned materialization remain costs. Bench uses ByteArrayBuilder for the tree path: it adds one segmented-storage pass and one final-array pass of 1,144,774 B each. The diagnostic uses a counted ByteArrayOutputStream solely to expose its separate output-copy costs; its allocation is not equated with the Bench tree allocation.', '',f"Exact source and raw rounds: `{rel(depth_dir)}/InstrumentedVPackGenerator.java` and `results.csv`. No paged-storage prototype is claimed or required after the measured cursor acceptance pass.", '']
    idx=lines.index('## Independent interoperability');lines[idx:idx]=section
    (artifacts/'depth-summary.json').write_text(json.dumps(diagnostic,indent=2)+'\n')
profiles_path=artifacts/'profile-summary.json'
if profiles_path.exists():
    profiles=read(profiles_path)
    section=['## Separate JFR recordings','',
             'Post-rewrite profiles use three separate one-fork invocations, each with a unique recording directory/file. They use the unchanged cursor operation, 3×2-second warmups and 5×2-second measurements, one thread and 512 MB heap. JFR timing scores are not used for acceptance. Event exports use jfr print --stack-depth 128 to expose the full recorded stacks (the CLI default would show only five frames). The summary filters execution/allocation stacks to Bench.treeWriteCursor: setup is excluded, operation warmup remains. Sampling, truncated stacks and JIT/inlining attribution limit the interpretation. Allocation-sample weights are estimates, not exact B/op.', '',
             '| Recording | Cursor execution samples | Most frequent leaf frames (sample counts) |', '| --- | ---: | --- |']
    for profile in profiles:
        top='; '.join(f"{name.rsplit('.',1)[-1]} {n}" for name,n in profile['topLeafFrames'][:4])
        section.append(f"| {profile['directory']} | {profile['cursorStackSamples']:,} | {top} |")
    section+=['', 'The per-recording analysis.json files retain inclusive frames, allocation classes and allocation sites; recording.jfr, exact command.txt, run.log, results.json, summary.txt and the event JSON exports remain beside them. The first stage-3 diagnostic invocation started while the initial C++ fixture check was finishing; this separate profiled run is not timing-acceptance evidence. Final JFR runs, depth experiment, compilation/tests and comparable timing runs execute sequentially.', '']
    idx=lines.index('## Independent interoperability');lines[idx:idx]=section
# Keep the auxiliary control recheck separate from required cursor acceptance.
control_paths=[artifacts/'json-streaming-before-path.txt',artifacts/'json-streaming-after-path.txt']
if all(p.exists() for p in control_paths):
    control_dirs=[root/pathlib.Path(p.read_text().strip()) for p in control_paths]
    controls=[read(p/'results.json')[0] for p in control_dirs]
    section=['The one-fork JSON streaming cursor case initially rose from 4.230918 ± 0.107636 to 4.488308 ± 0.045163 ms/op (about 6.08%), with a 32,000 B/op allocation shift. Because JSON code and its resolved artifacts are unchanged, this control concern triggered an additional three-fork before/after recheck using identical heap/warmup/measurement/GC settings and no JFR.', '',
             f"Recheck: **{fm(controls[0])} ms/cursor before**, **{fm(controls[1])} ms/cursor after**, with {alloc(controls[0]):,.0f} and {alloc(controls[1]):,.0f} B/cursor respectively. The confidence intervals overlap and the slowdown did not reproduce. Allocation varies across forks; JIT escape/materialization differences are a possible explanation, not a demonstrated cause. The Smile streaming control also showed an approximately 32,000 B/op shift in the opposite direction. These observations are retained rather than silently normalized away.", '',
             f"Raw recheck results/exact argv: `{rel(control_dirs[0])}/` and `{rel(control_dirs[1])}/`. Reproduce with `python3 scripts/writer-validation/control-recheck.py` (uses this session's captured before/after classpaths).", '']
    idx=lines.index('## Substantial remedy and profiles');lines[idx:idx]=section
summary={'beforeRun':rel(before),'afterRun':rel(after),'stage1BeforeRun':rel(stage1),'verdict':{'atLeast5xMeasured':passed,'meanSpeedup':ratio,'conservativeIntervalEndpointSpeedup':[low,high]},'before':read(before/'baseline.json'),'after':read(after/'baseline.json'),'beforeRegressions':read(before/'regressions.json'),'afterRegressions':read(after/'regressions.json'),'failureComparison':read(artifacts/'final-failure-comparison.json')}
import math
def finite(value):
    if isinstance(value,float) and not math.isfinite(value):return None
    if isinstance(value,dict):return {k:finite(v) for k,v in value.items()}
    if isinstance(value,list):return [finite(v) for v in value]
    return value
(root/'docs/writer-performance-results.json').write_text(json.dumps(finite(summary),indent=2,allow_nan=False)+'\n')
(root/'docs/writer-performance.md').write_text('\n'.join(line.rstrip() for line in lines)+'\n')
print(json.dumps(summary['verdict'],indent=2))
