#!/usr/bin/env python3
"""Compile a disposable, counted copy: no counters/branches in production or acceptance JVMs."""
import pathlib,sys
source=pathlib.Path('src/main/java/com/arangodb/jackson/dataformat/velocypack/VPackGenerator.java').read_text()
source=source.replace('VPackGenerator','InstrumentedVPackGenerator')
source=source.replace('private static final int MIN_BUFFER_LENGTH = 256;', '''private static final int MIN_BUFFER_LENGTH = 256;
    public long growthCopies, headerCopies, sortingCopies, capacityBytes;
    public int growths;''')
a='''if (_payload == null) _payload = new byte[grownCapacity(0, required)];
        else if (required > _payload.length) {
            _payload = Arrays.copyOf(_payload, grownCapacity(_payload.length, required));
        }'''
b='''if (_payload == null) { _payload = new byte[grownCapacity(0, required)]; ++growths; capacityBytes += _payload.length; }
        else if (required > _payload.length) {
            growthCopies += _payload.length;
            _payload = Arrays.copyOf(_payload, grownCapacity(_payload.length, required));
            ++growths; capacityBytes += _payload.length;
        }'''
assert a in source
source=source.replace(a,b)
source=source.replace('System.arraycopy(_payload, contentStart, _payload, start + header, contentLength);','headerCopies += contentLength;\n                System.arraycopy(_payload, contentStart, _payload, start + header, contentLength);')
source=source.replace('System.arraycopy(_payload, from, _sortScratch, pos, len);','sortingCopies += len;\n            System.arraycopy(_payload, from, _sortScratch, pos, len);')
source=source.replace('System.arraycopy(_sortScratch, 0, _payload, contentStart, contentLength);','sortingCopies += contentLength;\n        System.arraycopy(_sortScratch, 0, _payload, contentStart, contentLength);')
p=pathlib.Path(sys.argv[1]);p.mkdir(parents=True,exist_ok=True);(p/'InstrumentedVPackGenerator.java').write_text(source)
