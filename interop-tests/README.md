# Native VelocyPack differential tests

This is an independent Maven project. Root builds require no native tooling. It
links an **unmodified** upstream C++ checkout through a small C ABI and Java 25 FFM;
production sources and the root POM are unchanged. Comparisons assert values,
declared projections, or types, never serialized byte identity.

## Setup and commands

Requires 64-bit Linux (x86_64 or aarch64), installed Temurin Java 25, Maven 3.9+,
Git, curl, tar, sha256sum, make, and a C++20 compiler (`g++` by default). Set
`JAVA_HOME` and `PATH` to Java 25. The runner also finds the installed SDKMAN
Temurin 25 when the shell defaults to an older JDK. If CMake is absent, setup
downloads CMake 3.31.6 into `target/tools/` and verifies its published checksum.

From the repository root, run the complete setup and suites:

```bash
./interop-tests/run.sh
```

The runner installs the production artifact with tests and test compilation
skipped, builds the shared native library, then runs this project's suites. Both
projects use Jackson **3.2.0** by default. `JACKSON_VERSION=...` or a runner argument
`-Djackson.version=...` overrides the version for both builds. Ordinary root
Maven commands retain their existing behavior.

Manual setup, with Java 25 selected:

```bash
mvn -Djackson.version=3.2.0 -Dmaven.test.skip=true install
cd interop-tests
./build-native.sh
mvn test
mvn -Dtest=NativeSmokeTest,JsonDifferentialTest,ScalarFamiliesTest,DecimalContractTest,MalformedInputTest,UtfAndPolicyTest test
mvn -Dtest=UnexpectedDefectsTest#strictReaderMustRejectInvalidUtf8Value test
```

The full suite deliberately exits nonzero for active defects. Nine named
regressions in `UnexpectedDefectsTest` have `//FIXME` evidence immediately above
their methods: seven Java UTF/BCD defects and two upstream defects (root-fraction
rounding and eight-byte object validation). The generated JSON corpus and the
object representation suite also retain their reproductions. No tests are
disabled and no unexpected exception is accepted as equivalence.

The native checkout begins at upstream `main`, then checks out the pinned
[reference commit](https://github.com/arangodb/velocypack/tree/1450560e980140578eb050199f58c4351a5d13d5).
Explicit overrides fetch upstream refs and record the resolved commit:

```bash
VPACK_REF=origin/main ./interop-tests/run.sh
./interop-tests/run.sh -Dinterop.seed=42 -Dinterop.jsonCases=50000 -Dinterop.typedCases=25000
```

`VPACK_REF` also accepts a commit or tag. `BUILD_JOBS` and `CXX` control native
build parallelism and compiler. Reuse the existing native build for subsequent
`mvn test` runs. `mvn clean` in this module removes its native checkout and tools;
run setup again afterward.

## Contracts and coverage

Shared JSON cases use an explicit streaming writer: integer tokens retain their
integer values; floating tokens become doubles. BigInteger/BigDecimal databinding
is tested separately. Every shared case is independently encoded by Java and C++
and both buffers are read by both implementations, including buffered and chunked
Java reads. Object order is ignored only for unique keys. Duplicate keys use
ordered sequences and explicit construction uniqueness options.

Integer families compare exact mathematical values. Finite doubles compare IEEE
values including negative zero; NaNs compare classification. Native `-0` JSON
output retains its sign during projection. Decimals compare exact values, with
scale assertions only on independent preservation vectors. Independent encoders
cover nonminimal widths and representations the native builder cannot produce.

The full run exercises all 19 upstream ValueTypes and all 256 head bytes,
including reserved-byte rejection, all custom heads, numeric keys, padding,
container length/count/index transitions, complete tag chains, date extrema,
and depth/string/name/precision limits. Coverage means **exercised under a stated
policy**, not successful conversion for every type. BCD requires exact Java
numeric results and native error **2 (NotImplemented)**. Native dumping defaults
to failure for non-JSON types; explicit binary/date/nonfinite projections are
tested separately. Java exposes dates as integers, strips tags while exposing
the innermost tag's long bit pattern, and uses decimal string names for integer
keys. Native numeric-key translation is tested with a scoped attribute table.
Legacy indexed unsorted objects have an explicit native rejection policy. A
zero-length custom payload has separate Java opaque-value and native rejection
contracts. External wire pointers are rejected; native external resolution keeps
its referent alive and never exports a pointer.

Defaults are 10,000 generated JSON cases and 5,000 typed cases, fixed seed
1592614637, maximum depth 8, and 1 MiB per case. The decimal precision budget is
4,096 digits, with 4,095/4,096/4,097 boundaries. Generated failures are shrunk
deterministically and remain failures. Corpus fingerprints support repeatability
checks. This is systematic finite coverage, not exhaustive enumeration of
unbounded precision, input size, or combinations.

## Reports

Everything downloaded or built for native testing is ignored under `target/`.
`target/reports/run.properties` records revision, seed, corpus fingerprints,
versions, build options and limits; `tool-versions.txt` records runner versions.
`summary.properties` separates passed contracts, failures, JSON equivalence,
typed equivalence, projections, rejections, and native unsupported operations.
Outcome counters count checks; JUnit reports count test cases. Unsupported
operations are never counted as JSON equivalence. `type-coverage.tsv` records
type outcomes and heads; the summary lists missing types/heads for selected runs.

`failures.jsonl` includes case, policy, metadata, JSON, hex inputs, expected and
actual results, options, and first differing path. `shrunk/` holds reduced
generated inputs. `isolation/` holds malformed native inputs, child-JVM results,
and crash logs; each input is run in a fresh JVM with a 10-second timeout before
validated dumping/inspection. Native crashes are isolated test failures.
`target/surefire-reports/` contains JUnit XML/text reports. Native results are
copied into Java-owned arrays and freed deterministically; ownership checks and
the final outstanding-allocation count detect leaks.
