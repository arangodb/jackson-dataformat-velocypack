# Running JMH with retained JFR evidence

Run from the repository root in Bash on Linux/macOS. Use the same selected full
JDK for Maven and Java, with `java`, `javac` and `jfr` on PATH. This is a local
performance workflow, not a CircleCI test job. JMH 1.37 is a test dependency in
`pom.xml`; there is no benchmark Maven profile or executable benchmark JAR.

## Build the harness

Preserve earlier `target/jmh-result` directories before any `mvn clean`. Rebuild
when sources/dependencies/JDK change; use a clean build when stale classes or
annotation-processor output are possible. Then, in the same shell:

```bash
set -euo pipefail
mvn --version
mvn test-compile dependency:build-classpath \
  -Dmaven.compiler.proc=full -DincludeScope=test \
  -Dmdep.outputFile=target/jmh-classpath.txt
test -s target/test-classes/META-INF/BenchmarkList
CP="target/test-classes:target/classes:$(cat target/jmh-classpath.txt)"
java -cp "$CP" org.openjdk.jmh.Main -l 'com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.'
```

`test-compile` builds benchmarks without running tests; it is not correctness
validation. Explicit annotation processing avoids relying on older JDK defaults.
If discovery fails, inspect compiler output, generated harness classes and
`META-INF/BenchmarkList`; do not replace a forked run with `-f 0` or benchmark an
old JAR. Run [tests](../../velocypack-testing/SKILL.md) separately.

## Existing entrypoint: understand its limits

After the build, this invokes the checked-in runner:

```bash
java -cp "$CP" com.arangodb.jackson.dataformat.velocypack.Bench 'Bench\.pojoReadCursor$'
```

`Bench.main` consumes **only the first argument** as an include regex. Its comment
about `-p` overrides does not reflect its implementation: extra arguments are
ignored. It uses the annotation defaults (3 x 2s warmup, 5 x 2s measurement, one
fork, average time in microseconds, JSON/SMILE/VPACK, `batchSize=1000`), GCProfiler,
a 512 MiB fixed heap and startup JFR with `settings=profile`.

It writes timestamped JSON and JFR under `target/jmh-result`, but supplies the same
JFR filename to every fork/case in an invocation. Later forks can overwrite earlier
recordings, even with a one-method regex because format parameters create separate
cases. Do not assume that single JFR represents every row of its JSON. Retain useful
existing artifacts, state missing attribution, and use the CLI below for new runs.

## Parameterized runs without recording collisions

Use JMH's CLI directly; explicitly restore the GC profiler, heap and startup JFR
that `Bench.main` would otherwise configure. The example measures one method and
parameter tuple with three independent forks. Annotation defaults still supply
warmup, measurement, mode and time unit.

```bash
mkdir -p target/jmh-result
RUN=$(mktemp -d target/jmh-result/run-XXXXXXXX)
mkdir "$RUN/jfr"
cmd=(java -cp "$CP" org.openjdk.jmh.Main
  '^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.pojoReadCursor$'
  -p format=VPACK -p batchSize=1000 -t 1 -f 3 -foe true
  -prof gc
  -jvmArgs "-Xms512m -Xmx512m -XX:StartFlightRecording=filename=$RUN/jfr,settings=profile,dumponexit=true"
  -rf json -rff "$RUN/results.json")
printf '%q ' "${cmd[@]}" > "$RUN/command.txt"
printf '\n' >> "$RUN/command.txt"
"${cmd[@]}" 2>&1 | tee "$RUN/console.log"
test -s "$RUN/results.json"
find "$RUN/jfr" -type f -name '*.jfr' -print
printf 'Artifacts: %s\n' "$RUN"
```

The pre-created JFR **directory** lets the JVM generate a PID/timestamp-based
filename per fork, rather than overwriting a fixed file. Keep the relative `RUN`
path as generated so the JVM option contains no spaces. Retain the command/logs
and record revision/dirty diff, JDK and machine details alongside them. Verify
recordings with `jfr summary` and associate PIDs with the benchmark/fork log; three
successful forks of this one tuple should yield three usable recordings. A failed
or killed fork may not produce complete JSON/JFR; do not analyze it as a success.

For a smoke run, replace `-f 3` with `-f 1 -wi 1 -i 1 -w 1s -r 1s`. For another
workload, change the anchored method regex; change `-p` values for format/batch
comparisons. List matches first, keep `batchSize` positive, and choose only the
matrix needed. Create a new `RUN` for **every** invocation, including each baseline
and candidate run. Do not reuse `cmd` with its old output paths.

Keep JFR on the **forked JVMs** via `-jvmArgs`, not only the launcher. The startup
recording intentionally matches the existing runner's scope, including warmup and
setup. Do not add a second JFR profiler on top or compare differently profiled
runs as though they were equivalent. Follow the [analysis skill](../SKILL.md) to
combine JSON/GC measurements with the recordings.
