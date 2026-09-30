# Working on jackson-dataformat-velocypack

This is a single-module, binary-only Jackson 3 dataformat, not the ArangoDB Java
client. Keep Java 17 source/API/bytecode compatibility; CI also runs on newer JDKs.
Production packages are `tools.jackson.dataformat.velocypack`; the Maven group and
benchmark package use `com.arangodb`. Jackson annotations still use
`com.fasterxml.jackson.annotation`.

## Load only what the task needs

| Task | Guidance |
| --- | --- |
| Implement, fix, extend, or refactor code; update dependencies | [Development skill](.agents/skills/velocypack-development/SKILL.md) |
| Add/select tests or reproduce CI failures | [Testing skill](.agents/skills/velocypack-testing/SKILL.md) |
| Run JMH or analyze results/JFR recordings | [Benchmarking skill](.agents/skills/velocypack-benchmarking/SKILL.md) |
| Locate an implementation boundary | [Architecture](docs/architecture.md) |

These skills can be read as Markdown when the agent has no native skill loader.
Do not preload all of them or the mirrored Jackson test tree.

## Repository contracts

Use [docs/velocypack.md](docs/velocypack.md) together with the frozen decisions in
[docs/wire-profile.md](docs/wire-profile.md) for wire behavior. Do not silently
replace the local subset with upstream VelocyPack semantics. A proposed contract
change needs an explicit scope and regression vectors, not an incidental refactor.

Use `pom.xml` (including its inherited configuration) and `.circleci/config.yml`
as the build/CI sources of truth. Update affected guidance when those change; do
not alter the build or weaken tests just to make an instruction work.

Before handing off, report the behavior changed, validation commands and JDKs,
results, and any untested scope. Keep generated files and benchmark artifacts out
of the change; preserve needed `target/jmh-result` evidence before cleaning.
