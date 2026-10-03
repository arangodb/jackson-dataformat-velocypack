"""Controlled reader experiments; all subprocess arguments are passed without a shell."""
import hashlib
import json
import os
from pathlib import Path
import platform
import shutil
import subprocess
import sys
import tempfile
import time

ROOT = Path.cwd()
MODE = sys.argv[1]
if MODE not in ('prepare', 'acceptance', 'diagnostics', 'profile', 'all'):
    sys.exit('Expected prepare, acceptance, diagnostics, profile, or all')
for key in ('JAVA_TOOL_OPTIONS', '_JAVA_OPTIONS', 'JDK_JAVA_OPTIONS', 'CLASSPATH'):
    if os.environ.get(key):
        sys.exit(f'Unset {key}: timing JVMs must have no implicit instrumentation/options')
FORKS = int(os.environ.get('FORKS', '3'))
WARMUP = int(os.environ.get('WARMUP', '5'))
REPEATS = int(os.environ.get('PROFILE_REPEATS', '2'))
if FORKS < 3 or WARMUP < 5 or REPEATS < 2:
    sys.exit('Require FORKS>=3, WARMUP>=5 and PROFILE_REPEATS>=2')
base = ROOT / 'target/reader-performance'
base.mkdir(parents=True, exist_ok=True)
out = Path(tempfile.mkdtemp(prefix=MODE+'-'+time.strftime('%Y%m%dT%H%M%SZ', time.gmtime())+'-', dir=base))
lock = Path(os.environ.get('LOCK_DIR', str(base/'frozen'))).resolve()
print(f'Output: {out}\nLock: {lock}', flush=True)
commands = []

def sha(p):
    h = hashlib.sha256()
    with Path(p).open('rb') as f:
        for chunk in iter(lambda: f.read(1 << 20), b''):
            h.update(chunk)
    return h.hexdigest()

def capture(cmd):
    return subprocess.check_output(cmd, text=True, stderr=subprocess.STDOUT)

def write_json(path, value):
    path.write_text(json.dumps(value, indent=2, sort_keys=True)+'\n')

def run(cmd, log, cwd=ROOT):
    commands.append(dict(argv=[str(x) for x in cmd], cwd=str(cwd), log=str(log)))
    write_json(out/'commands.json', commands)
    print(f'Running {log.name}', flush=True)
    with log.open('w') as stream:
        rc = subprocess.run([str(x) for x in cmd], cwd=cwd, stdout=stream, stderr=subprocess.STDOUT).returncode
    log.with_suffix('.exit-code.txt').write_text(str(rc)+'\n')
    if rc:
        sys.exit(f'Command failed ({rc}); see {log}')

java = Path(shutil.which('java')).resolve()
jdk = java.parent.parent
# Fingerprint the entire installed JDK, including native libraries and configuration.
jdk_hashes = {str(p.relative_to(jdk)): sha(p) for p in sorted(jdk.rglob('*')) if p.is_file()}
metadata = dict(commit=capture(['git', 'rev-parse', 'HEAD']).strip(),
                status=capture(['git', 'status', '--porcelain=v1']),
                javaExecutable=str(java), java=capture([str(java), '-version']),
                maven=capture(['mvn', '-version']), cpu=capture(['lscpu']),
                cpuAffinity=sorted(os.sched_getaffinity(0)), harnessInvocation=sys.argv,
                platform=platform.platform(), mode=MODE, forks=FORKS, warmup=WARMUP,
                environment={k: os.environ.get(k) for k in ('JAVA_HOME','LANG','LC_ALL','TZ','MAVEN_OPTS')},
                sourceHashes={str(p): sha(p) for d in ('src','scripts','docs') for p in sorted(Path(d).rglob('*')) if p.is_file()},
                pomSha256=sha('pom.xml'), dependencyOverride=None)
write_json(out/'metadata.json', metadata)
for name, args in [('unstaged.patch', ['git','diff','--binary']),
                   ('staged.patch', ['git','diff','--cached','--binary'])]:
    (out/name).write_text(capture(args))
# Patches cannot represent untracked input files: archive those separately.
for name in capture(['git','ls-files','--others','--exclude-standard','-z']).split('\0'):
    if name and Path(name).is_file():
        dest = out/'untracked'/name
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(name, dest)

mvn = ['mvn','-B','-o','-DskipTests','-Djacoco.skip=true','test-compile',
       'org.apache.maven.plugins:maven-dependency-plugin:3.8.1:build-classpath',
       '-Dmdep.includeScope=test',f'-Dmdep.outputFile={out}/classpath.txt',
       'org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree',f'-DoutputFile={out}/dependencies.txt',
       'org.apache.maven.plugins:maven-help-plugin:3.5.2:effective-pom',f'-Doutput={out}/effective-pom.xml']
run(mvn, out/'build.log')
jars = [Path(p) for p in (out/'classpath.txt').read_text().strip().split(os.pathsep)]
repo = next(p for p in jars[0].parents if p.name == 'repository')
artifacts = {str(p.relative_to(repo)): sha(p) for jar in jars for p in (jar, jar.with_suffix('.pom')) if p.exists()}
for rel in ('tools/jackson/jackson-bom/3.2.0-SNAPSHOT/jackson-bom-3.2.0-SNAPSHOT.pom',
            'org/junit/junit-bom/6.1.0-RC1/junit-bom-6.1.0-RC1.pom'):
    artifacts[rel] = sha(repo/rel)
fingerprint = dict(artifacts=artifacts, jdkPath=str(jdk), jdkHashes=jdk_hashes)
write_json(out/'resolved-lock.json', fingerprint)
if (lock/'lock.json').exists():
    if json.loads((lock/'lock.json').read_text()) != fingerprint:
        sys.exit('Dependency/JDK lock mismatch. Inspect resolved-lock.json; use a new controlled baseline for ALL formats if changing environments.')
else:
    lock.mkdir(parents=True, exist_ok=True)
    for rel in artifacts:
        dest = lock/'artifacts'/rel
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(repo/rel, dest)
    write_json(lock/'lock.json', fingerprint)
for rel, digest in artifacts.items():
    if sha(lock/'artifacts'/rel) != digest:
        sys.exit('Frozen artifact corrupted: '+rel)
for d in ('classes','test-classes'):
    shutil.copytree(ROOT/'target'/d, out/d)
cp = os.pathsep.join([str(out/'test-classes'), str(out/'classes')]+[str(lock/'artifacts'/p.relative_to(repo)) for p in jars])
write_json(out/'classHashes.json', {str(p.relative_to(out)): sha(p) for d in ('classes','test-classes') for p in sorted((out/d).rglob('*')) if p.is_file()})
run([jdk/'bin/javap','-classpath',cp,'tools.jackson.core.JsonParser','tools.jackson.core.io.IOContext',
     'tools.jackson.core.StreamReadConstraints','tools.jackson.core.sym.ByteQuadsCanonicalizer',
     'tools.jackson.core.util.SimpleStreamReadContext','tools.jackson.databind.ObjectMapper'], out/'jackson-api.log')
run([jdk/'bin/javap','-classpath',cp,'-c','-p','org.openjdk.jmh.profile.JavaFlightRecorderProfiler'], out/'jmh-jfr-api.log')
# Diagnostics live in a separate directory that is never on the timing classpath.
diag = out/'diagnostic-classes'
diag.mkdir()
sources = sorted((ROOT/'scripts/reader-performance').glob('*.java'))
run([jdk/'bin/javac','-proc:none','-cp',cp,'-d',diag]+sources, out/'diagnostic-build.log')
diagcp = str(diag)+os.pathsep+cp
run([java,'-cp',diagcp,'com.arangodb.jackson.dataformat.velocypack.ReaderInputs',out/'inputs'], out/'inputs.log')
inputs = json.loads((out/'inputs/manifest.json').read_text())
inputs = {fmt: {key: value for key, value in entry.items() if key != 'accounting'} for fmt, entry in inputs.items()}
if (lock/'inputs.json').exists():
    if json.loads((lock/'inputs.json').read_text()) != inputs:
        sys.exit('Encoded inputs or token transcripts changed. Do not compare against the frozen baseline.')
else:
    write_json(lock/'inputs.json', inputs)
    shutil.copytree(out/'inputs', lock/'inputs')
run([java,'-cp',cp,'org.openjdk.jmh.Main','-l'], out/'benchmarks.log')
common = ['-t','1','-wi',str(WARMUP),'-w','2s','-i','5','-r','2s',
          '-jvm',str(java),'-jvmArgs','-Xms512m -Xmx512m','-rf','json','-foe','true']
target = r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.treeReadCursor$'
jmh = [java,'-cp',cp,'org.openjdk.jmh.Main']
if MODE in ('acceptance','all'):
    run(jmh+[target,'-p','format=JSON,SMILE,VPACK','-p','batchSize=1000','-f',str(FORKS),'-prof','gc']+common+
        ['-rff',out/'baseline.json'], out/'baseline.log')
    run([sys.executable, ROOT/'scripts/reader-performance/summarize.py', out], out/'summary.log')
    if not json.loads((out/'summary.json').read_text())['convergenceScreenPass']:
        sys.exit('Convergence screen failed. Keep this run as exploratory; increase WARMUP and rerun ALL formats with the same lock.')
if MODE in ('diagnostics','all'):
    cases = [(r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.(treeReadDocument|streamingReadCursor|pojoReadCursor|sequenceReadBytes|sequenceReadInputStream|treeReadCursorInputStream)$', ['-p','batchSize=1000'], 'read-paths'),
             (r'^com\.arangodb\.jackson\.dataformat\.velocypack\.ReaderDiagnosticBench\.names$', [], 'names'),
             (r'^com\.arangodb\.jackson\.dataformat\.velocypack\.ReaderDiagnosticBench\.depth$', [], 'depth')]
    for pattern, params, label in cases:
        run(jmh+[pattern,'-p','format=JSON,SMILE,VPACK','-f',str(FORKS),'-prof','gc']+params+common+
            ['-rff',out/(label+'.json')], out/(label+'.log'))
if MODE in ('profile','all'):
    for fmt in ('JSON','SMILE','VPACK'):
        for repeat in range(1, REPEATS+1):
            dest = out/f'{fmt.lower()}-{repeat}'
            dest.mkdir()
            # JMH stops profilers in reverse order: commit the marker before JFR.stop.
            run([java,'-cp',diagcp,'org.openjdk.jmh.Main',target,'-p','format='+fmt,'-p','batchSize=1000','-f','1',
                 '-prof',f'jfr:dir={dest};configName=profile;stackDepth=256;verbose=true',
                 '-prof','com.arangodb.jackson.dataformat.velocypack.MeasurementIntervals']+common+
                ['-rff',dest/'results.json'], dest/'run.log')
            recordings = list(dest.rglob('profile.jfr'))
            if len(recordings) != 1:
                sys.exit('Expected exactly one recording per one-fork invocation')
            run([java,'-cp',diagcp,'com.arangodb.jackson.dataformat.velocypack.ReaderJfrAnalysis',
                 recordings[0],dest/'allocation.json'], dest/'analysis.log')
print(f'Results preserved: {out}', flush=True)
