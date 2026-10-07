#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p target/reports
# Select the installed Temurin 25 when the shell default is an older JDK.
if ! java -version 2>&1 | head -1 | grep -q '"25'; then
  for candidate in /home/codex/.sdkman/candidates/java/25*-tem; do
    if [[ -x $candidate/bin/java ]]; then export JAVA_HOME="$candidate"; export PATH="$JAVA_HOME/bin:$PATH"; break; fi
  done
fi
java -version 2>&1 | head -1 | grep -q '"25' || { echo 'Set JAVA_HOME and PATH to the installed Temurin Java 25' >&2; exit 1; }
command -v mvn >/dev/null || { echo 'Maven 3.9+ is required' >&2; exit 1; }
jackson_version=${JACKSON_VERSION:-3.2.0}
for argument in "$@"; do
  case "$argument" in -Djackson.version=*) jackson_version=${argument#-Djackson.version=};; esac
done
{ java -version 2>&1; mvn -version; } > target/reports/tool-versions.txt
flattened_existed=false
[[ ! -e ../.flattened-pom.xml ]] || flattened_existed=true
mvn -B -f ../pom.xml -Djackson.version="$jackson_version" -Dmaven.test.skip=true install > target/production-build.log 2>&1 || { cat target/production-build.log; exit 1; }
if [[ $flattened_existed == false && -f ../.flattened-pom.xml ]]; then rm -- ../.flattened-pom.xml; fi
./build-native.sh > target/native-build.log 2>&1 || { cat target/native-build.log; exit 1; }
mvn -B -Djackson.version="$jackson_version" test "$@"
