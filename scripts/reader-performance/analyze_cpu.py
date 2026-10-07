"""Analyze a completed separate profile run; never execute during acceptance.
Usage: analyze_cpu.py PROFILE_RUN_DIRECTORY
"""
import json
from pathlib import Path
import subprocess
import sys

run = Path(sys.argv[1]).resolve()
java = json.loads((run / 'metadata.json').read_text())['javaExecutable']
lock = run.parent / 'frozen'
jars = []
for name in (run / 'classpath.txt').read_text().strip().split(':'):
    path = Path(name)
    repo = next(p for p in path.parents if p.name == 'repository')
    jars.append(str(lock / 'artifacts' / path.relative_to(repo)))
cp = ':'.join([str(run / 'diagnostic-classes'), str(run / 'test-classes'), str(run / 'classes')] + jars)
commands = []
rows = []
for fmt in ('json', 'smile', 'vpack'):
    for repeat in (1, 2):
        directory = run / f'{fmt}-{repeat}'
        recordings = list(directory.rglob('profile.jfr'))
        if len(recordings) != 1 or not (directory / 'allocation.json').exists():
            sys.exit('Incomplete profile: ' + str(directory))
        cmd = [java, '-cp', cp, 'com.arangodb.jackson.dataformat.velocypack.ReaderCpuAnalysis', str(recordings[0]), str(directory / 'cpu.json')]
        commands.append(dict(argv=cmd, log=str(directory / 'cpu.log')))
        (run / 'cpu-commands.json').write_text(json.dumps(commands, indent=2) + '\n')
        with (directory / 'cpu.log').open('w') as log:
            result = subprocess.run(cmd, stdout=log, stderr=subprocess.STDOUT)
        (directory / 'cpu.exit-code.txt').write_text(str(result.returncode) + '\n')
        if result.returncode:
            sys.exit('CPU analysis failed: ' + str(directory))
        analysis = json.loads((directory / 'cpu.json').read_text())
        rows.append(dict(recording=directory.name, targetSamples=analysis['targetExecutionSamples'],
                         intervalSamples=analysis['intervalExecutionSamples'],
                         missingStacks=analysis['missingIntervalStacks'], truncatedStacks=analysis['truncatedIntervalStacks'],
                         topInclusive=sorted(analysis['inclusiveMethodSamples'].items(), key=lambda x: -x[1])[:30],
                         topLeaf=sorted(analysis['leafMethodSamples'].items(), key=lambda x: -x[1])[:30],
                         parserInclusive={k: v for k, v in analysis['inclusiveMethodSamples'].items() if 'VPackParser' in k}))
        print(directory.name, 'target samples', analysis['targetExecutionSamples'], 'parser inclusive', rows[-1]['parserInclusive'])
(run / 'cpu-summary.json').write_text(json.dumps(rows, indent=2) + '\n')
