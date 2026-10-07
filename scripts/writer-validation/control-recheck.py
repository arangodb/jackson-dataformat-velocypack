#!/usr/bin/env python3
import pathlib,json,subprocess,tempfile
root=pathlib.Path.cwd();base=root/'target/writer-stage4'
for label,work,run in [('before',base/'original',base/'original/target/jmh-result/tree-write-20261003T102841Z-059N0U'),('after',root,root/'target/jmh-result/tree-write-20261003T103826Z-0YqXFJ')]:
    directory=pathlib.Path(tempfile.mkdtemp(prefix='json-streaming-'+label+'-',dir=base))
    cp=':'.join([str(work/'target/test-classes'),str(work/'target/classes'),(run/'classpath.txt').read_text().strip()])
    command=['java','-cp',cp,'org.openjdk.jmh.Main',r'^com\.arangodb\.jackson\.dataformat\.velocypack\.Bench\.streamingWriteCursor$',
             '-p','format=JSON','-p','batchSize=1000','-t','1','-f','3','-wi','3','-w','2s','-i','5','-r','2s','-jvmArgs','-Xms512m -Xmx512m','-prof','gc','-rf','json','-rff',str(directory/'results.json'),'-foe','true']
    (directory/'command.json').write_text(json.dumps(command,indent=2)+'\n')
    with (directory/'run.log').open('w') as log:subprocess.run(command,cwd=work,stdout=log,stderr=subprocess.STDOUT,check=True)
    (base/('json-streaming-'+label+'-path.txt')).write_text(str(directory.relative_to(root))+'\n')
    print(directory,flush=True)
