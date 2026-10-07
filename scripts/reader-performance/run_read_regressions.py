"""Supplemental reader diagnostics on frozen before/after classes.
Usage: run_read_regressions.py BEFORE_RUN AFTER_RUN OUTPUT_DIRECTORY
These short one-fork runs diagnose allocation regressions; they are not cursor
acceptance measurements and do not establish stationary timing improvements.
"""
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys

before, after, output = map(lambda p: Path(p).resolve(), sys.argv[1:])
output.mkdir(parents=True, exist_ok=False)
if json.loads((before / 'resolved-lock.json').read_text()) != json.loads((after / 'resolved-lock.json').read_text()):
    sys.exit('Before/after artifact or JDK lock differs')
for field in ('documentBytes', 'cursorBytes', 'sequenceBytes', 'shape'):
    b = json.loads((before / 'inputs/manifest.json').read_text())['VPACK'][field]
    a = json.loads((after / 'inputs/manifest.json').read_text())['VPACK'][field]
    if b != a:
        sys.exit('Before/after input bytes or complete transcript differs: ' + field)
for variable in ('JAVA_TOOL_OPTIONS', '_JAVA_OPTIONS', 'JDK_JAVA_OPTIONS', 'CLASSPATH'):
    if os.environ.get(variable):
        sys.exit('Implicit JVM options are not allowed: ' + variable)
java = str(Path(shutil.which('java')).resolve())
commands = []
common = ['-p', 'format=VPACK', '-f', '1', '-t', '1', '-wi', '5', '-w', '2s',
          '-i', '3', '-r', '1s', '-prof', 'gc', '-jvm', java,
          '-jvmArgs', '-Xms512m -Xmx512m', '-rf', 'json', '-foe', 'true']
cases = [
    ('read-paths', r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.(treeReadDocument|streamingReadCursor|pojoReadCursor|sequenceReadBytes|sequenceReadInputStream|treeReadCursorInputStream)$', ['-p', 'batchSize=1000']),
    ('names', r'^com\.arangodb\.jackson\.dataformat\.velocypack\.ReaderDiagnosticBench\.names$', []),
    ('depth', r'^com\.arangodb\.jackson\.dataformat\.velocypack\.ReaderDiagnosticBench\.depth$', [])
]
for stage, run in [('before', before), ('after', after)]:
    # Use the same frozen artifact copies as acceptance, never live build outputs.
    jars = []
    for name in (run / 'classpath.txt').read_text().strip().split(':'):
        path = Path(name)
        repository = next(p for p in path.parents if p.name == 'repository')
        jars.append(str(run.parent / 'frozen/artifacts' / path.relative_to(repository)))
    cp = ':'.join([str(run / 'test-classes'), str(run / 'classes')] + jars)
    stage_dir = output / stage
    stage_dir.mkdir()
    for label, pattern, params in cases:
        cmd = [java, '-cp', cp, 'org.openjdk.jmh.Main', pattern] + common + params + ['-rff', str(stage_dir / (label + '.json'))]
        commands.append(dict(stage=stage, label=label, argv=cmd))
        (output / 'commands.json').write_text(json.dumps(commands, indent=2) + '\n')
        print(stage, label, flush=True)
        with (stage_dir / (label + '.log')).open('w') as log:
            result = subprocess.run(cmd, stdout=log, stderr=subprocess.STDOUT)
        (stage_dir / (label + '.exit-code.txt')).write_text(str(result.returncode) + '\n')
        if result.returncode:
            sys.exit('Diagnostic failed: ' + str(stage_dir / label))
metadata = dict(before=str(before), after=str(after), java=java,
                beforeClassHashesSha256=hashlib.sha256((before / 'classHashes.json').read_bytes()).hexdigest(),
                afterClassHashesSha256=hashlib.sha256((after / 'classHashes.json').read_bytes()).hexdigest(),
                interpretation='Short one-fork gc-only diagnostics; no stationary timing acceptance or speedup claims')
(output / 'metadata.json').write_text(json.dumps(metadata, indent=2) + '\n')
