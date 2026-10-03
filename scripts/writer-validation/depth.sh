#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
cp_file=${1:-target/jmh-result/tree-write-20261003T101430Z-M92JuT/classpath.txt}
out=$(mktemp -d target/writer-stage4/depth-XXXXXX)
python3 scripts/writer-validation/instrument.py "$out"
cp="target/test-classes:target/classes:$(cat "$cp_file")"
javac -proc:none -cp "$cp" -d "$out" "$out/InstrumentedVPackGenerator.java" scripts/writer-validation/DepthExperiment.java > "$out/build.log" 2>&1
java -Xms512m -Xmx512m -cp "$out:$cp" com.arangodb.jackson.dataformat.velocypack.DepthExperiment > "$out/results.csv"
echo "$out"
