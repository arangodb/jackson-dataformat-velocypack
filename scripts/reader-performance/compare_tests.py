"""Compare full Surefire failure signatures without hiding pre-existing failures.
Usage: compare_tests.py BASELINE_JSON REPORT_DIRECTORY OUTPUT_JSON
"""
from collections import Counter
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

baseline = json.loads(Path(sys.argv[1]).read_text())
totals = Counter()
methods = {}
for path in sorted(Path(sys.argv[2]).glob('TEST-*.xml')):
    suite = ET.parse(path).getroot()
    for key in ('tests', 'failures', 'errors', 'skipped'):
        totals[key] += int(suite.attrib.get(key, 0))
    for case in suite.findall('testcase'):
        for kind in ('failure', 'error'):
            failure = case.find(kind)
            if failure is None:
                continue
            name = case.attrib['classname'] + '.' + case.attrib['name']
            entry = dict(kind=kind, type=failure.attrib.get('type'),
                         message=failure.attrib.get('message'), occurrences=1)
            if name in methods:
                previous = methods[name]
                if (previous['kind'], previous['type']) != (kind, entry['type']):
                    raise SystemExit('Mixed failure kinds/types in repeated test: ' + name)
                previous['occurrences'] += 1
            else:
                methods[name] = entry

def signatures(entries):
    return {name: (e['kind'], e['type'], e['occurrences']) for name, e in entries.items()}

before, after = signatures(baseline['methods']), signatures(methods)
result = dict(xmlTotals=dict(totals), methods=methods, failureSignaturesMatch=before == after,
              newOrChangedFailures={k: v for k, v in after.items() if before.get(k) != v},
              removedOrChangedFailures={k: v for k, v in before.items() if after.get(k) != v})
Path(sys.argv[3]).write_text(json.dumps(result, indent=2) + '\n')
print(json.dumps({k: v for k, v in result.items() if k != 'methods'}, indent=2))
if not result['failureSignaturesMatch']:
    sys.exit(1)
