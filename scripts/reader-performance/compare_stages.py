"""Read-only comparison of complete GC-only treeReadCursor result files.
Usage: compare_stages.py OUTPUT_JSON LABEL RUN_DIRECTORY [LABEL RUN_DIRECTORY ...]
Preserves original JMH us/op; ns/op is a derived display conversion.
"""
import json
from pathlib import Path
import statistics
import sys

output = Path(sys.argv[1])
args = sys.argv[2:]
if not args or len(args) % 2:
    sys.exit(__doc__)
stages = []
for label, directory in zip(args[::2], args[1::2]):
    run = Path(directory)
    raw = json.loads((run / 'baseline.json').read_text())
    screen = json.loads((run / 'summary.json').read_text())
    metadata = json.loads((run / 'metadata.json').read_text())
    formats = {}
    for result in raw:
        if result['benchmark'] != 'com.arangodb.jackson.dataformat.velocypack.Bench.treeReadCursor' or result['params']['batchSize'] != '1000':
            sys.exit('Unexpected workload: ' + directory)
        time = result['primaryMetric']
        allocation = result['secondaryMetrics']['gc.alloc.rate.norm']
        if time['scoreUnit'] != 'us/op' or allocation['scoreUnit'] != 'B/op':
            sys.exit('Unexpected original units: ' + directory)
        forks = [statistics.mean(f) for f in time['rawData']]
        if len(forks) != 3:
            sys.exit('The fork t interval in this report requires exactly three forks')
        formats[result['params']['format']] = dict(
            originalUnit=time['scoreUnit'], usPerOp=time['score'], nsPerOp=time['score'] * 1000,
            jmh999ErrorUs=time['scoreError'], jmh999IntervalUs=time['scoreConfidence'],
            bytesPerOp=allocation['score'], allocationJmh999Error=allocation['scoreError'],
            allocationJmh999Interval=allocation['scoreConfidence'],
            forkMeansUs=forks, forkMeansBytes=[statistics.mean(f) for f in allocation['rawData']],
            fork95IntervalUs=[statistics.mean(forks) + sign * 4.302652729911275 * statistics.stdev(forks) / len(forks)**0.5 for sign in (-1, 1)],
            rawUs=time['rawData'], rawBytes=allocation['rawData'])
    if set(formats) != {'JSON', 'SMILE', 'VPACK'}:
        sys.exit('Require all three controls in each run')
    reference = formats['JSON']
    for row in formats.values():
        row['timeRatioToJson'] = row['usPerOp'] / reference['usPerOp']
        row['allocationRatioToJson'] = row['bytesPerOp'] / reference['bytesPerOp']
        row['timeRatioJmhEndpointEnvelope'] = [row['jmh999IntervalUs'][0] / reference['jmh999IntervalUs'][1],
                                              row['jmh999IntervalUs'][1] / reference['jmh999IntervalUs'][0]]
    reference['timeRatioJmhEndpointEnvelope'] = [1.0, 1.0]
    stages.append(dict(label=label, run=str(run.resolve()), convergenceScreenPass=screen['convergenceScreenPass'],
                       warmup=metadata['warmup'], formats=formats))
result = dict(stages=stages, interpretation='Derived comparison; raw JMH data are read unchanged. Errors are JMH 99.9% intervals over iteration observations. Fork 95% intervals use three independent fork means and t(2); only three forks, scheduling and temporal drift remain limitations. Ratio endpoint envelopes are descriptive, not formal ratio confidence intervals. One operation reads the entire 1000-document cursor into a JsonNode tree.')
output.write_text(json.dumps(result, indent=2) + '\n')
markdown = ['# Raw per-fork observations', '',
            'Derived from unchanged JMH JSON, rounded here to three decimals. JSON retains full precision. Each fork has five measurements; time remains us/op and allocation B/op.', '']
for stage in stages:
    markdown += [f"## {stage['label']}", '',
                 f"Run: `{stage['run']}`; convergence screen: {stage['convergenceScreenPass']}; warmups: {stage['warmup']}.", '',
                 '| Format / fork | Five raw measurements us/op | Five raw measurements B/op |',
                 '| --- | --- | --- |']
    for fmt, row in stage['formats'].items():
        for fork, (times, allocations) in enumerate(zip(row['rawUs'], row['rawBytes']), 1):
            markdown.append(f"| {fmt} / {fork} | " + ', '.join(f'{v:.3f}' for v in times) + ' | ' + ', '.join(f'{v:.3f}' for v in allocations) + ' |')
    markdown.append('')
output.with_suffix('.md').write_text('\n'.join(markdown) + '\n')
for stage in stages:
    for fmt, row in stage['formats'].items():
        print(f"{stage['label']} / {fmt}: {row['usPerOp']:.3f} ± {row['jmh999ErrorUs']:.3f} us/op; {row['nsPerOp']:.3f} ns/op; {row['bytesPerOp']:.3f} B/op; time/JSON {row['timeRatioToJson']:.4f}; allocation/JSON {row['allocationRatioToJson']:.4f}; forks {row['forkMeansUs']}; screen {stage['convergenceScreenPass']}")
