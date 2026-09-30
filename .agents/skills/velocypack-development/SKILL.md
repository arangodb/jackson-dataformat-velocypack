---
name: velocypack-development
description: >-
  Maintain and extend jackson-dataformat-velocypack. Use for bug fixes, features,
  refactoring parser/generator internals or Jackson APIs, and dependency or
  packaging changes. Not a guide to using the library in an application.
---

# VelocyPack development

Apply [AGENTS.md](../../../AGENTS.md). Consult the relevant section of the
[architecture map](../../../docs/architecture.md) when locating ownership.

## Choose the contract and layer

Trace the affected operation from factory/mapper through parser or generator.
For a bug, identify the violated contract and a reproducer; for a refactor, identify
the observable behavior to preserve. Follow adjacent code's style and visibility;
do not expose internal storage or test probes as public API for convenience.

Read the applicable [wire-profile](../../../docs/wire-profile.md) section for
format work rather than loading the whole specification. Reuse layout, bounds,
number and varint helpers where they own the operation. In particular, preserve:

- The distinction between valid noncanonical input and canonical output; container
  widths versus scalar length widths; body order versus sorted object indexes.
- Exact unsigned integers and BCD scale, lazy numeric conversion and accessor-order
  independence. Do not introduce rounding, exponent expansion during tokenization,
  or new native markers as shortcuts. UTF-8 replacement behavior is a local policy,
  not an invitation to add stricter validation incidentally.
- Checked arithmetic and limits before allocation/access. Keep both Jackson stream
  constraints and VPack root budgets, including counts, names, headers and indexes.

For a previously unresolved wire contradiction, isolate a minimal byte vector and
state the decision needed. Do not infer a new policy from an excluded upstream type.

## Preserve the affected lifecycle

For parser/storage changes, check borrowed versus owned input, ranges surviving
until their last accessor, release at root transitions/EOF/error/close, recycling,
and byte locations. For generator changes, check range transfer, root accounting,
completion/flush/close and failure cleanup. Preserve the no-repeated-subtree-copy
architecture; use existing ownership and complexity tests to guard refactors.

For configuration changes, follow factory builder/copy/rebuild/serialization and
mapper saved state, plus read/write context overrides. Keep native wrapper mappings
separate from ordinary Jackson types. Test the affected binary adapters rather
than assuming byte-array coverage establishes stream/DataInput/DataOutput behavior.

## Dependencies and packaging

Inspect inherited versions/plugin executions with `mvn help:effective-pom` when
changing the build. Keep Java 17 compatibility and intentional dependency scopes;
Jackson annotations retaining their `com.fasterxml` namespace is not a migration
error. Preserve JPMS and classpath provider declarations together.

Keep the pre-test classified JAR execution, the POM's Gradle metadata marker, and
plugin ordering (CycloneDX before Gradle metadata; Gradle metadata last). Change
version-generation inputs, not generated output; inspect `VPackVersionTest` and
`VPackModuleMetadataTest` when changing release metadata.

## Validate the change

Use the [testing skill](../velocypack-testing/SKILL.md) to add focused regression
coverage and choose CI-equivalent validation. Wire regressions need independent
bytes, not only parser/writer round trips. Update affected public Javadocs and
user documentation for an API or behavior change.

Use the [benchmarking skill](../velocypack-benchmarking/SKILL.md) for performance
claims or changes to hot paths, allocation or copying. Do not trade away bounds,
wire validation or ownership guarantees to improve a benchmark score.
