---
name: velocypack-testing
description: >-
  Add or run tests and reproduce CircleCI failures in jackson-dataformat-velocypack.
  Use for regression coverage, Java 17/21/25 validation, mirrored Jackson tests,
  packaged provider/module checks, or Maven test failures. JMH is a separate skill.
---

# VelocyPack testing

Apply [AGENTS.md](../../../AGENTS.md). Run commands from the repository root with
Maven (`mvn`; no wrapper is checked in) and a full JDK, including `javac` and `jar`.
Confirm the runtime with `mvn --version`, not just the compiler's release setting.

## Reproduce CircleCI

Read [.circleci/config.yml](../../../.circleci/config.yml) and
[pom.xml](../../../pom.xml) if they have changed. The `test-jdk` workflow expands
the `test` job as follows:

| Job | Executor image | Validation commands, in order |
| --- | --- | --- |
| `test-j17` | `cimg/openjdk:17.0` | `mvn --version`, `mvn dependency:tree`, `mvn test` |
| `test-j21` | `cimg/openjdk:21.0` | Same commands |
| `test-j25` | `cimg/openjdk:25.0` | Same commands |

Select each JDK through `JAVA_HOME` and `PATH`, then run:

```sh
mvn --version
mvn dependency:tree
mvn test
```

Run matrix entries sequentially in separate clean checkouts, or clean build output
between JDKs after preserving needed artifacts. `--release 17` on JDK 25 is not a
JDK 17 test run. Validate on the available matrix JDKs and explicitly report missing
ones; do not present a single-JDK result as the full matrix.

CI provisions remote Docker, but the checked-in tests do not require an ArangoDB
server or a container fixture. Do not invent a server setup prerequisite. The
separate `deploy` job runs on JDK 17, uses release credentials/GPG, and skips test
compilation and execution. It is not validation; do not invoke deployment or
configure release secrets for ordinary development.

## Iterate with a useful test selection

```sh
# Focus on the changed contract; replace this with the relevant named test.
mvn -Dtest=VPackScalarParserTest test
# Broaden to format-specific tests (not the entire compatibility suite).
mvn -Dtest='VPack*Test' test
# Full CI test scope on the selected JDK.
mvn test
```

Use lifecycle `test`, not a standalone `surefire:test`: version generation and
`process-test-classes` must run. The latter builds the `t28-test` classified JAR
used by provider/metadata tests. Surefire includes `*IT` as well as the broad
`*Test*` patterns, so integration-named tests already run in `mvn test`; there is no
separate integration-test job/profile to substitute. `mvn verify` can be useful for
packaging changes but is not the command currently used by the CI test jobs.

Keep test compilation and Surefire on the classpath (`useModulePath=false`), and
preserve the composed `argLine` including `@{argLine}` and the `java.util` opening.
The module-metadata test separately compiles/runs a module-path consumer; disabling
these settings does not improve JPMS coverage.

## Put the regression at the right boundary

Use JUnit Jupiter and neighboring tests in
`src/test/java/tools/jackson/dataformat/velocypack/`. For compatibility failures in
`src/test/java/tools/jackson/core/` or `tools/jackson/databind/`, follow the named
wrapper into its `T32_*` fixture. These are checked-in adaptations, not production
code or disposable build output. Select the wrapper with `-Dtest`, not a fixture
without test entrypoints. Preserve meaningful coverage rather than blanket-skipping
an upstream-named family because this is a binary format.

| Change | Relevant existing examples |
| --- | --- |
| Wire layout, canonical bytes or invalid input | `VPackWireFixtureTest`, `VPackMalformedMatrixTest`, `VPackStructuredFuzzTest` and the relevant scalar/container tests |
| Bounds, allocation or root budgets | `VPackConstraintsTest`, `VPackResourceBudgetTest`, `VPackHeaderBudgetRegressionTest`, `VPackComplexityTest` |
| Source/target ownership, sequence or failure paths | `VPackRootReaderFailureTest`, `VPackScalarInputOwnershipTest`, `VPackGeneratorSegmentOwnershipTest`, lifecycle and sequence tests |
| Configuration, discovery or release metadata | `VPackMapperRebuildTest`, `VPackAttributeCodecConfigurationTest`, `VPackServiceLoaderTest`, `VPackModuleMetadataTest`, `VPackVersionTest` |

For wire tests, derive expected bytes independently from the local profile.
`VPackWireFixtureAssembler` and the checked-in `wire-vectors.txt` show the pattern;
do not build the parser's expected input with production encoding/layout helpers.
Cover the changed boundary (width transitions, malformed lengths/indexes, truncation,
accessor order, or ownership) rather than exhaustively multiplying unrelated cases.
Consume through END/EOF when testing validation: framing a root is not full parsing.

Inspect `target/surefire-reports` for selected tests, failures and unexpected skips.
A successful command with no relevant tests, or with tests disabled, is not evidence
of correctness. Distinguish dependency/toolchain failures from executed-test failures
and retain the command, JDK and failing test names in the handoff.
