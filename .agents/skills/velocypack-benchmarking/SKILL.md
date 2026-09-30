---
name: velocypack-benchmarking
description: >-
  Run JMH and analyze performance of jackson-dataformat-velocypack using benchmark
  JSON, GC metrics and JFR recordings in target/jmh-result. Use for latency,
  allocation, copying, throughput or performance-regression work, including
  analysis of existing artifacts without rerunning benchmarks.
---

# VelocyPack benchmarking and performance analysis

Apply [AGENTS.md](../../../AGENTS.md). For execution, read
[running JMH](references/running-jmh.md); for existing results, start with the
artifacts instead. Do not run the full format/workload cross-product by default.

## Select the affected workload

Inspect `src/test/java/com/arangodb/jackson/dataformat/velocypack/Bench.java` and
`ArangoDocuments.java`. The benchmark covers JSON, Smile and VPACK, with deterministic
customer documents, cursor envelopes and root sequences. Choose the affected API:
`tree*`, `pojo*`, `streaming*`, or `sequence*`. `sequenceReadInputStream` exercises
owned/recycled input pages, while `sequenceReadBytes` exercises borrowed input.
The `streamingRead*` methods also parse byte arrays; their name denotes token access,
not an InputStream source.

Keep setup's round-trip checks. Preserve equivalent data and consumed outputs when
adding a workload. Keep setup/data generation outside measured work, except costs
intentionally part of the operation; do not remove existing output allocation from
one candidate merely to improve its score.

## Establish comparability

Inventory `target/jmh-result` recursively for JSON, JFR and logs. Associate artifacts
with the revision (including dirty/untracked changes), exact command, JDK/JVM flags,
host/CPU, benchmark name, parameters and fork. Preserve them outside `target` before
cleaning or removing a worktree. Treat unknown provenance or missing recordings as
a limitation, not as evidence of a performance gain.

Use the same host, JDK, heap, profiler settings, data/seed, parameters, thread count,
mode, warmup and measurement schedule for baseline and candidate. Run separately,
without concurrent builds/tests/benchmarks, and repeat or alternate runs to check
noise. A reduced smoke run validates the harness, not performance. A harness change
must be applied equally to both versions or evaluated separately.

## Read the measurements

Compare matching JSON entries by `benchmark`, `params`, `mode` and units. The default
is average time in microseconds per operation, so lower is better. Inspect scores,
`scoreError`/confidence intervals and per-fork `rawData`, not just a percentage from
one run. Report baseline, candidate, units, relative change and uncertainty; noisy
or overlapping observations need more evidence, not an arbitrary pass threshold.

Use `gc.alloc.rate.norm` (bytes/op) for allocation comparisons; allocation rate in
MB/s also changes with execution speed. Check GC count/time and the sizes printed
by setup. Cursor and sequence operations process `batchSize` documents; their scores
are not per-document latency. Label any derived per-document normalization and do
not mix document, cursor, sequence or differing batch sizes in one comparison.

## Explain with JFR, then check the code

For each relevant recording, use `jfr summary` first to check duration, event counts
and available event types. Extract only relevant events to files, not an unfiltered
recording dump into context. For example, with `recording` set to a selected path:

```sh
jfr summary "$recording"
jfr print --events jdk.ExecutionSample,jdk.NativeMethodSample \
  --stack-depth 32 "$recording" > "${recording%.jfr}-cpu.txt"
jfr print --events jdk.ObjectAllocationSample,jdk.ObjectAllocationInNewTLAB,jdk.ObjectAllocationOutsideTLAB \
  --stack-depth 32 "$recording" > "${recording%.jfr}-allocations.txt"
```

Startup JFR includes setup, warmup and measurement. Use timestamps/logs and benchmark
worker stacks to separate them; do not attribute setup, compiler or profiler work
to the measured operation. Inspect GC pauses or monitor/park events when indicated
by the scores. Event availability varies: no samples is not proof of no cost.
Allocation samples are weighted observations, not an exact object census; use GC
profiler bytes/op to corroborate them. Allocations alone do not prove retention.

Trace hot stacks to the relevant parser/generator, byte store, arena/segment chain,
layout/number codec or Jackson databind layer. Separate observed evidence from a
hypothesis about its cause. Check whether the proposed change actually reduces
that work and preserves the affected [architecture](../../../docs/architecture.md)
and correctness tests. Report artifact paths and the remaining uncertainty with
any performance conclusion; do not make a performance claim from JFR alone.
