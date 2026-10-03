#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
label=${1:-post-rewrite}
cp_file=${2:-target/jmh-result/tree-write-20261003T101430Z-M92JuT/classpath.txt}
out=$(mktemp -d "target/writer-stage4/jfr-${label}-XXXXXX")
cp="target/test-classes:target/classes:$(cat "$cp_file")"
command=(java -cp "$cp" org.openjdk.jmh.Main '^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.treeWriteCursor$' -p format=VPACK -p batchSize=1000 -t 1 -f 1 -wi 3 -w 2s -i 5 -r 2s -jvmArgs "-Xms512m -Xmx512m -XX:StartFlightRecording=filename=$(pwd)/$out/recording.jfr,settings=profile" -prof gc -rf json -rff "$out/results.json" -foe true)
printf '%q ' "${command[@]}" > "$out/command.txt"
printf '\n' >> "$out/command.txt"
"${command[@]}" > "$out/run.log" 2>&1
jfr summary "$out/recording.jfr" > "$out/summary.txt"
jfr print --json --stack-depth 128 --events jdk.ExecutionSample "$out/recording.jfr" > "$out/execution-samples.json"
jfr print --json --stack-depth 128 --events jdk.ObjectAllocationSample "$out/recording.jfr" > "$out/allocation-samples.json"
echo "$out"
