# Local wire profile and ambiguity ledger

## W1. Authority and interpretation
Only `doc/velocypack.md` defines the format. It is preserved byte-for-byte. Line
references here refer to that supplied document. This ledger resolves contradictions
inside it and states application policies where it is silent. It is not the original
ArangoDB specification and does not promise compatibility with it.

| ID  | Conflicting or incomplete local text                                        | Frozen decision and regression                                                                                                                               |
|-----|-----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| W01 | Lines 81-84 vs 489-490                                                      | Short length is marker-0x40; `40` is empty. `shortStringLengthUsesMarkerMinus0x40`.                                                                          |
| W02 | Lines 135-140 vs 158-161 and 233                                            | `07` has 2-byte length, count, and offsets. `indexedArray0x07UsesTwoByteOffsets`.                                                                            |
| W03 | Lines 177-178/229 vs 205-209                                                | `05` has no count or index; the trailing-count exception applies to `09`. Test both independently.                                                           |
| W04 | Generic object outline 299-304 vs specific exception 334-337                | `0e` has an 8-byte HEADER count; only `12` has the trailing count. Test both independently.                                                                  |
| W05 | Lines 427-431 contradict string lengths                                     | Correct compact example to `14 0a 41 61 31 41 62 28 10 02`.                                                                                                  |
| W06 | Padding outline 119-121 and inconsistent arithmetic 183-185                 | Use the exact allowed starts in W3; the 2-byte unindexed form starts at A+3, A+5, or A+9, never A+7.                                                         |
| W07 | Lines 346-348 say positive but explicitly include marker `30`               | Attribute ID 0 is valid. Only `30-39` and `28-2f` encode IDs; signed markers are not keys even for positive payloads.                                        |
| W08 | Lines 354-359 do not define ID comparison, duplicates, or Unicode collation | Compare RESOLVED names by unsigned UTF-8 bytes, no normalization/locale; duplicate-name policy is Jackson-configurable, duplicate index offsets never legal. |
| W09 | Compact count grammar 267-282/410-425 does not require minimal groups       | Read valid nonminimal 1-8-group encodings; write minimal groups. Empty compact values with count 0 and no body are accepted; write `01`/`0a`.                |
| W10 | BCD grammar does not define zero mantissa length or Java scale limits       | Require >=1 mantissa byte. Accept leading zero digits and negative zero; write positive zero with mantissa `00`. Java scale limitations are in W5.           |
| W11 | Final specified marker range ends at `ef`                                   | Reject `f0-ff`; do not import tags or custom-type rules.                                                                                                     |

W06, W08-W10 include explicit profile choices where no unique reading exists. Agents
must not re-open them opportunistically. A new contradiction is a blocked task with
a minimal vector, not permission to consult an excluded specification.

## W2. Complete marker classification
Hexadecimal intervals are inclusive; together they cover all 256 bytes.

| Marker     | Payload/meaning                             | Read                             | Canonical write                     |
|------------|---------------------------------------------|----------------------------------|-------------------------------------|
| `00`       | No value                                    | Reject except structural padding | Never                               |
| `01`       | Empty array                                 | Accept                           | Empty array                         |
| `02-05`    | Equal-encoded-size arrays; w=1/2/4/8        | Accept                           | When equal-array feature on         |
| `06-09`    | Indexed arrays; w=1/2/4/8                   | Accept                           | Otherwise, unless compact requested |
| `0a`       | Empty object                                | Accept                           | Empty object                        |
| `0b-0e`    | Sorted indexed objects; w=1/2/4/8           | Accept                           | Default nonempty object             |
| `0f-12`    | Obsolete unsorted indexed objects           | Accept and validate              | Never                               |
| `13/14`    | Compact array/object                        | Accept                           | Explicit write feature only         |
| `15-17`    | Reserved/illegal                            | Reject                           | Never                               |
| `18/19/1a` | Null/false/true                             | Accept                           | Same marker                         |
| `1b`       | IEEE-754 double, 8 little-endian bytes      | Accept all bit patterns          | Raw double bits                     |
| `1c`       | Signed little-endian int64 UTC epoch millis | Accept                           | Explicit native API/wrapper         |
| `1d`       | External pointer                            | Reject immediately               | Never                               |
| `1e/1f`    | MinKey/MaxKey                               | Embedded explicit enum           | Explicit native API/wrapper         |
| `20-27`    | Signed 1-8-byte two's complement integer    | Accept                           | Negative non-small values           |
| `28-2f`    | Unsigned 1-8-byte integer                   | Accept exactly                   | Nonnegative non-small values        |
| `30-39`    | Integers 0..9                               | Accept                           | Same marker                         |
| `3a-3f`    | Integers -6..-1                             | Accept                           | Same marker                         |
| `40-be`    | UTF-8 byte length marker-0x40               | Assumed valid UTF-8 on read, valid UTF-16 on write; not validated; JDK UTF-8 charset replaces invalid input | <=126 bytes |
| `bf`       | uint64 byte length then UTF-8               | Assumed valid UTF-8 on read, valid UTF-16 on write; not validated; JDK UTF-8 charset replaces invalid input | >=127 bytes |
| `c0-c7`    | 1-8-byte unsigned length then binary        | Accept                           | Smallest length width               |
| `c8-cf`    | Positive BCD; length width marker-0xc7      | Exact                            | Positive/zero BCD                   |
| `d0-d7`    | Negative BCD; length width marker-0xcf      | Exact                            | Negative BCD                        |
| `d8-ef`    | Reserved                                    | Reject                           | Never                               |
| `f0-ff`    | Undefined in local subset                   | Reject                           | Never                               |

Scalar binary/BCD length fields allow EVERY width 1 through 8, unlike container
fields, which use 1/2/4/8. A 65,536-byte native binary payload starts with
`c2 00 00 01` on canonical write, not the four-byte-length marker `c3`.
Nonminimal fixed-width scalar/container encodings are valid reads. Never invent
native float32, UUID, tag, external-pointer, symbol, or custom-value markers.

## W3. Exact fixed-container layouts
`A` is the marker address; `L` includes every byte of the container; `N` counts array
values or object PAIRS; `B` is body byte length; `w` is field width. All structural
integers are unsigned little-endian. Index offsets are relative to A, not the body.
`IDXw` contains exactly N fields. `Z(P)` contains exactly P zero bytes.

| Markers    | Layout                   | Allowed P | Body start   |
|------------|--------------------------|-----------|--------------|
| `01/0a`    | `M`                      | 0         | No body; L=1 |
| `02`       | `M L1 Z(P) BODY`         | 0,1,3,7   | A+2+P        |
| `03`       | `M L2 Z(P) BODY`         | 0,2,6     | A+3+P        |
| `04`       | `M L4 Z(P) BODY`         | 0,4       | A+5+P        |
| `05`       | `M L8 BODY`              | 0         | A+9          |
| `06/0b/0f` | `M L1 N1 Z(P) BODY IDX1` | 0,6       | A+3+P        |
| `07/0c/10` | `M L2 N2 Z(P) BODY IDX2` | 0,4       | A+5+P        |
| `08/0d/11` | `M L4 N4 BODY IDX4`      | 0         | A+9          |
| `09`       | `M L8 BODY IDX8 N8`      | 0         | A+9          |
| `0e`       | `M L8 N8 BODY IDX8`      | 0         | A+17         |
| `12`       | `M L8 BODY IDX8 N8`      | 0         | A+9          |

Lengths: unindexed `L=1+w+P+B`; header-count indexed
`L=1+2w+P+B+N*w`; trailing-count `09/12`: `L=1+w+B+N*w+w`.
Regular forms `02-12` other than `0a` represent nonempty containers. Reject empty
bodies/zero counts in those forms. For equal arrays derive `S` from the first complete
child; require `S>0`, `B%S==0`, and each child's encoded byte length exactly S.

Compute all regions with checked arithmetic before reading them. For header-count
forms `dataEnd=A+L-N*w`; for `09/12`, `dataEnd=A+L-w-N*w`. Validate the count suffix
before computing the index region. No region may overlap a header, padding, another
region, or the enclosing parent's body boundary. Count feasibility must reflect at
least one byte per array child and at least two bytes per object pair.

Padding is unambiguous because `00` cannot begin a child or key: inspect only the
at-most-seven permitted header-padding bytes, find the first nonzero marker, and
require its position to be one of the table's starts. All preceding bytes must be
zero and remain within the body. Never scan into the index or try recursively parsing
several complete candidate subtrees. Reject a first nonzero at an unlisted start,
excess zeros, nonzero "padding", or no child before dataEnd.

Arrays: index[i] must equal the observed start of child i; offsets are strictly
increasing. Objects: the index must be a BIJECTION onto all observed key starts,
not merely in range; reject duplicate, omitted, middle-of-value, or value-start
pointers. Sorted forms `0b-0e` additionally require nondecreasing resolved UTF-8 names.
Obsolete forms need no name ordering. Equal names may have different valid offsets.
Validate indexes before returning the container's END token. They never emit tokens.

## W4. Compact containers and width selection
Layout is `M V(L) BODY RV(N)`. Forward V and backward-decoded RV contain 1-8 seven-bit
little-endian groups, for values <=2^56-1. Continuation is interpreted in the direction
being read: the byte nearest the end stores the count's low seven bits and has its
high bit set when more count bytes precede it. For count 128, RV bytes are `01 80`;
for count 129, `01 81`. The terminal group has its high bit clear.
Decode the suffix from `A+L-1` without crossing the completed length header. Require
exact body exhaustion and exactly N children/pairs; no index or padding is present.
Empty compact values are `13 03 00` and `14 03 00` under profile W09.

The writer always emits no padding and minimal structural widths. Test candidate
widths 1,2,4,8 using the COMPLETE resulting L and N; increasing w changes header AND
index size and offsets. Do not select w from B alone. For compact output start with
lengthWidth=1 and iterate `L=1+lengthWidth+B+countWidth` and its minimal group width
until stable; reject >8 groups or any budget/overflow violation.

Canonical policy: empty marker first; then compact if its kind's feature is enabled;
otherwise arrays use the equal-size form when enabled and eligible, else indexed.
Objects use sorted indexed form. Only the index is sorted; body order is preserved.

## W5. Exact scalars
Use small integer markers for -6..9, then minimal signed widths for negatives and
minimal unsigned widths for nonnegatives through 2^64-1. Larger magnitude integers
use BCD exponent 0. Positive values read from signed markers remain valid values.
Sign-extend signed widths; never interpret unsigned bit 63 as a negative value.

BCD is `M uint(lengthBytes) int32(exponent) packedMantissa`. The length describes
mantissa BYTES, not digits or the exponent. Mantissa bytes are big-endian pairs of
decimal nibbles, each 0..9; exponent is little-endian signed int32. The numeric value
is signedMantissa * 10^exponent. Require a nonempty mantissa. Odd decimal digit counts
are padded with a LEADING zero nibble; never change the exponent to pad output.
Write BigDecimal from unscaledValue and exponent `-(long)scale`; preserve trailing
zeros and scale. Write zero with positive family and one `00` mantissa byte.

Java cannot preserve scale `2147483648` for wire exponent `-2147483648`; reject that
read with a located StreamReadException explaining the representability limit.
BigDecimal scale `-2147483648` cannot be negated into wire int32; reject that write
with StreamWriteException. Do not round, strip zeros, saturate, or import another
encoding to evade these limits. Leading-zero and negative-zero BCD inputs otherwise
read exactly as Java values (Java BigDecimal has no negative-zero sign).
Validate declared lengths, digit constraints, and exponent before materialization.
Do not expand a huge exponent to plain text or a huge BigInteger during tokenization.

Strings are assumed to be valid UTF-8 on read and valid UTF-16 on write, and are not
validated. Decode and encode with the JDK UTF-8 charset; invalid input is replaced
(U+FFFD on read and `?` for lone surrogates on write), not rejected. Embedded NUL is
valid. Float input widens to double; use raw double bits, preserving signed zero and finite values.
NaN/infinity are native doubles, not JSON strings; decimal/integer coercions reject
nonfinite values. NaN payload identity is not a Java-wide round-trip guarantee.

## W6. Independent fixture seeds
All bytes below are local-spec derivations, not output from the new writer.
The corresponding machine-readable seeds are `doc/wire-vectors.json`.

```text
empty string                40
one-byte string "a"          41 61
[1,2,3]                     02 05 31 32 33
[1,16] compact              13 06 31 28 10 02
{"a":1,"b":16} compact       14 0a 41 61 31 41 62 28 10 02
object body b,a,c; index a,b,c
  0b 13 03 41 62 1a 41 61 28 0c 41 63 43 78 79 7a 06 03 0a
BCD 12345 scale 0           c8 03 00 00 00 00 01 23 45
BCD 12345.0 scale 1         c8 03 ff ff ff ff 12 34 50
[1] width 8 unindexed       05 0a 00 00 00 00 00 00 00 31
[1] width 8 indexed         09 1a 00 00 00 00 00 00 00 31
                            09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00
{"a":1} width 8 sorted      0e 1c 00 00 00 00 00 00 00
                            01 00 00 00 00 00 00 00 41 61 31
                            11 00 00 00 00 00 00 00
{"a":1} width 8 obsolete    12 1c 00 00 00 00 00 00 00 41 61 31
                            09 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00
```

Add literal cases for every padding alternative, each marker width, both continuation
directions, ID keys, duplicate names with distinct offsets, and invalid permutations.
A helper may assemble independently calculated fields; it must not call production
layout, varint, UTF-8, or number code to produce the parser's expected bytes.
