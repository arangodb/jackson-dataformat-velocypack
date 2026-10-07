#!/usr/bin/env python3
import pathlib,json,collections,sys
base=pathlib.Path('target/writer-stage4');summaries=[]
for directory in sorted(base.glob('jfr-*')):
    if not (directory/'execution-samples.json').exists():continue
    events=json.loads((directory/'execution-samples.json').read_text())['recording']['events']
    leaf=collections.Counter();inclusive=collections.Counter();n=0
    def frames(e):return (e['values'].get('stackTrace') or {}).get('frames',[])
    def name(f):return f['method']['type']['name'].replace('/','.')+'.'+f['method']['name']
    for e in events:
        f=frames(e)
        # Exclude startup/setup samples where the cursor operation is absent.
        if not any(name(x)=='com.arangodb.jackson.dataformat.velocypack.Bench.treeWriteCursor' for x in f):continue
        n+=1;leaf[name(f[0])]+=1;inclusive.update(set(map(name,f)))
    allocations=json.loads((directory/'allocation-samples.json').read_text())['recording']['events']
    classes=collections.Counter();sites=collections.Counter();weight=0
    for e in allocations:
        f=frames(e)
        if not any(name(x)=='com.arangodb.jackson.dataformat.velocypack.Bench.treeWriteCursor' for x in f):continue
        v=e['values'];w=v['weight'];weight+=w;classes[v['objectClass']['name']]+=w
        if f:sites[name(f[0])]+=w
    summary={'directory':str(directory),'cursorStackSamples':n,'topLeafFrames':leaf.most_common(20),'topInclusiveFrames':inclusive.most_common(20),'allocationSampleWeightBytes':weight,'allocationClasses':classes.most_common(10),'allocationLeafSites':sites.most_common(20),'limitations':'Sampling estimates with JIT/inlining attribution; operation-filtered stacks exclude setup but include operation warmup. Weighted allocations are not exact operation totals.'}
    (directory/'analysis.json').write_text(json.dumps(summary,indent=2)+'\n');summaries.append(summary)
(base/'profile-summary.json').write_text(json.dumps(summaries,indent=2)+'\n')
for s in summaries:print(s['directory'],s['cursorStackSamples'],s['topLeafFrames'][:8])
