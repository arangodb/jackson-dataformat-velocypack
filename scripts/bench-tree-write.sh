#!/usr/bin/env bash
# Usage: [FORKS=3] [REGRESSION_FORKS=3] [SMALL_BATCH=1] [LARGE_BATCH=1000]
#        scripts/bench-tree-write.sh [acceptance|regressions|all]
# Uses frozen release dependencies explicitly; pom.xml is unchanged. No JFR.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
mode=${1:-all}
case "$mode" in acceptance|regressions|all) ;; *) echo 'Expected acceptance, regressions, or all' >&2; exit 2;; esac
forks=${FORKS:-3}
regression_forks=${REGRESSION_FORKS:-$forks}
small=${SMALL_BATCH:-1}
large=${LARGE_BATCH:-1000}
for n in "$forks" "$regression_forks" "$small" "$large"; do
    [[ "$n" =~ ^[1-9][0-9]*$ ]] || { echo 'Forks and batch sizes must be positive integers' >&2; exit 2; }
done
mkdir -p target/jmh-result
out=$(mktemp -d "target/jmh-result/tree-write-$(date -u +%Y%m%dT%H%M%SZ)-XXXXXX")
printf '%s\n' "$out"
# Generate locally. Never reuse report classpath paths. No test execution/instrumentation in timing JVMs.
mvn -B -o -Djackson.version=3.2.0 -DskipTests test-compile \
    org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath \
    -Dmdep.includeScope=test "-Dmdep.outputFile=$out/classpath.txt" \
    org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree "-DoutputFile=$out/dependencies.txt" \
    org.apache.maven.plugins:maven-help-plugin:3.5.2:effective-pom "-Doutput=$out/effective-pom.xml" \
    > "$out/build.log" 2>&1
python3 - "$out" "$mode" "$forks" "$regression_forks" "$small" "$large" <<'PY'
import hashlib, json, os, pathlib, platform, shutil, subprocess, sys
out = pathlib.Path(sys.argv[1]).resolve()
mode, forks, regression_forks, small, large = sys.argv[2:]
root = pathlib.Path.cwd()
sha = lambda p: hashlib.sha256(pathlib.Path(p).read_bytes()).hexdigest()
cp_jars = pathlib.Path(out/'classpath.txt').read_text().strip().split(os.pathsep)
# Maven dependency coordinates rather than machine-specific absolute paths in the lock.
resolved = {}
for p in cp_jars:
    path = pathlib.Path(p)
    # Locate Maven repository by its group/artifact layout, not a fixed home path.
    pom = path.with_suffix('.pom')
    # Coordinates from dependency tree are also recorded; lock paths are relative to repository root.
    repo = next((parent for parent in path.parents if parent.name == 'repository'), None)
    if repo is None: raise SystemExit('Cannot locate Maven repository for '+p)
    resolved[str(path.relative_to(repo))] = sha(path)
    if pom.exists(): resolved[str(pom.relative_to(repo))] = sha(pom)
# Freeze the imported release BOM as well.
repo = next(parent for parent in pathlib.Path(cp_jars[0]).parents if parent.name == 'repository')
for rel in ['tools/jackson/jackson-bom/3.2.0/jackson-bom-3.2.0.pom',
            'org/junit/junit-bom/6.1.0-RC1/junit-bom-6.1.0-RC1.pom']:
    resolved[rel] = sha(repo/rel)
lock = json.loads(pathlib.Path('scripts/tree-write-dependencies.json').read_text())
(out/'resolved-checksums.json').write_text(json.dumps(resolved, indent=2, sort_keys=True)+'\n')
if resolved != lock:
    raise SystemExit('Dependency checksum lock mismatch; inspect resolved-checksums.json. Do not compare timings until dependencies match.')
def capture(cmd): return subprocess.check_output(cmd, stderr=subprocess.STDOUT, text=True)
metadata = {
    'purpose':'unmodified writer baseline' if mode == 'all' else mode,
    'jacksonOverride':'-Djackson.version=3.2.0', 'offlineBuild':True,
    'commit':capture(['git','rev-parse','HEAD']).strip(),
    'dirtyState':capture(['git','status','--porcelain=v1']),
    'java':capture(['java','-version']), 'maven':capture(['mvn','-version']),
    'javaExecutable':str(pathlib.Path(shutil.which('java')).resolve()),
    'platform':platform.platform(),
    'cpu':capture(['lscpu']),
    'sourceSha256':{str(p):sha(p) for parent in ['src/main','src/test','scripts','docs'] for p in sorted(pathlib.Path(parent).rglob('*')) if p.is_file()},
    'pomSha256':sha('pom.xml'), 'mode':mode,
    'forks':int(forks), 'regressionForks':int(regression_forks), 'regressionBatchSizes':[small,large],
    'timingProfiler':'gc only; no JFR',
}
(out/'metadata.json').write_text(json.dumps(metadata,indent=2)+'\n')
(out/'dirty.patch').write_text(capture(['git','diff','--binary']))
cp = os.pathsep.join([str(root/'target/test-classes'),str(root/'target/classes')]+cp_jars)
common = ['-t','1','-wi','3','-w','2s','-i','5','-r','2s','-jvmArgs','-Xms512m -Xmx512m',
          '-prof','gc','-rf','json','-foe','true','-p','format=JSON,SMILE,VPACK']
base = [shutil.which('java'),'-cp',cp,'org.openjdk.jmh.Main']
commands=[]
if mode in ('acceptance','all'):
    commands.append((base+[r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.treeWriteCursor$',
                          '-p','batchSize=1000','-f',forks]+common+['-rff',str(out/'baseline.json')],out/'baseline.log'))
if mode in ('regressions','all'):
    commands.append((base+[r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.(treeWriteDocument|pojoWriteCursor|streamingWriteCursor|sequenceWrite)$',
                          '-p','batchSize='+small+','+large,'-f',regression_forks]+common+['-rff',str(out/'regressions.json')],out/'regressions.log'))
(out/'commands.json').write_text(json.dumps([cmd for cmd,log in commands],indent=2)+'\n')
for cmd,log in commands:
    print('Running '+str(log),flush=True)
    with log.open('w') as stream:
        result=subprocess.run(cmd,stdout=stream,stderr=subprocess.STDOUT)
    (out/(log.stem+'-exit-code.txt')).write_text(str(result.returncode)+'\n')
    if result.returncode: raise SystemExit('JMH failed; see '+str(log))
print('Results: '+str(out),flush=True)
PY
