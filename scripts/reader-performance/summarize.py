"""Summarize acceptance JSON and inspect per-fork warmup/measurement convergence.
Usage: python3 scripts/reader-performance/summarize.py RUN_DIRECTORY
The thresholds are screening heuristics, not a proof of stationary performance.
"""
import json
from pathlib import Path
import re
import statistics
import sys

out = Path(sys.argv[1])
results = json.loads((out/'baseline.json').read_text())
rows = []
for result in results:
    metric = result['primaryMetric']
    alloc = result['secondaryMetrics']['gc.alloc.rate.norm']
    rows.append(dict(format=result['params']['format'], usPerOp=metric['score'], error=metric['scoreError'],
                     bytesPerOp=alloc['score'], allocationError=alloc['scoreError'],
                     forkMeans=[statistics.mean(f) for f in metric['rawData']],
                     measurementRaw=metric['rawData']))
forks = []
current = None
pending = None
fmt = None
for line in (out/'baseline.log').read_text().splitlines():
    m = re.search(r'format = (JSON|SMILE|VPACK)', line)
    if m: fmt = m[1]
    m = re.match(r'# Fork: (\d+) of', line)
    if m:
        current = dict(format=fmt, fork=int(m[1]), warmup=[], measurement=[])
        forks.append(current)
    m = re.match(r'(# Warmup Iteration|Iteration)\s+\d+:\s*(\d+(?:\.\d+)?)?', line)
    if m and current is not None:
        pending = 'warmup' if m[1].startswith('#') else 'measurement'
        if m[2]:
            current[pending].append(float(m[2])); pending = None
    elif pending and re.match(r'^\d+\.\d+ us/op$', line):
        current[pending].append(float(line.split()[0])); pending = None
for fork in forks:
    w, m = fork['warmup'][-3:], fork['measurement']
    if len(w) != 3 or len(m) != 5:
        raise SystemExit('Incomplete fork data: '+str(fork))
    avg = statistics.mean(m)
    fork['lateWarmupSpreadPercent'] = (max(w)-min(w))/statistics.mean(w)*100
    fork['lateWarmupToMeasurementPercent'] = (statistics.mean(w)/avg-1)*100
    fork['measurementEarlyToLatePercent'] = (statistics.mean(m[-2:])/statistics.mean(m[:2])-1)*100
    fork['screenPass'] = (fork['lateWarmupSpreadPercent'] <= 5 and
                          abs(fork['lateWarmupToMeasurementPercent']) <= 5 and
                          abs(fork['measurementEarlyToLatePercent']) <= 3)
summary = dict(rows=rows, convergence=forks, convergenceScreenPass=all(f['screenPass'] for f in forks),
               criteria='last 3 warmups spread <=5%; warmup/measurement mean difference <=5%; first/last 2 measurement drift <=3%')
(out/'summary.json').write_text(json.dumps(summary,indent=2)+'\n')
for row in rows:
    print(f"{row['format']}: {row['usPerOp']:.3f} ± {row['error']:.3f} us/op; {row['bytesPerOp']:.3f} B/op; fork means {row['forkMeans']}")
print('Convergence screen:', summary['convergenceScreenPass'])
if not summary['convergenceScreenPass']:
    print('Inspect summary.json and rerun ALL formats with WARMUP=10 (or longer); retain this run as exploratory.')
