![ArangoDB-Logo](https://user-images.githubusercontent.com/3998723/207981337-79d49127-48fc-4c7c-9411-8a688edca1dd.png)

# VelocyPack dataformat for Jackson

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.arangodb/jackson-dataformat-velocypack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.arangodb/jackson-dataformat-velocypack)
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/arangodb/jackson-dataformat-velocypack/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/arangodb/jackson-dataformat-velocypack/tree/main)

This project contains a [Jackson](https://github.com/FasterXML/jackson) extension for reading and writing [VelocyPack](https://github.com/arangodb/velocypack) encoded data.
It is compatible with Jackson 3.
It has no separate VelocyPack runtime dependency. 

## VelocyPack specification coverage

This library implements only a subset of the [complete VelocyPack specification](https://github.com/arangodb/velocypack/blob/main/VelocyPack.md). 
Its supported wire format is described in [the local format document](docs/velocypack.md) and the [wire profile](docs/wire-profile.md).

The following features in the complete specification are unsupported:

- **Tagged values (`0xee`–`0xef`):** The upstream specification defines one-byte and eight-byte logical type tags. The local format document omits tagging and treats these markers as reserved. This library rejects tagged values and does not write them.
- **Custom types (`0xf0`–`0xff`):** The upstream specification defines custom payloads with fixed or encoded lengths. The local format document does not define these types, and this library rejects their markers and does not write them.
- **External values (`0x1d`):** These are in-memory pointers in the upstream specification, not portable serialized data. This library rejects them and does not write them.

The local wire profile also fixes ambiguities in the shared part of the format, including short-string lengths and some container widths, counts, and padding. 
Consult [the wire profile](docs/wire-profile.md) for those choices and for Java-specific limits on representing packed BCD decimals.

## Maven

To add the dependency to your project with maven, add the following code to your pom.xml:

```XML
<dependencies>
  <dependency>
    <groupId>com.arangodb</groupId>
    <artifactId>jackson-dataformat-velocypack</artifactId>
    <version>x.y.z</version>
  </dependency>
</dependencies>
```

## Use

```java
import tools.jackson.dataformat.velocypack.VPackFactory;
import tools.jackson.dataformat.velocypack.VPackMapper;
import tools.jackson.dataformat.velocypack.VPackReadConstraints;

VPackFactory factory = VPackFactory.builder().build();
VPackMapper mapper = VPackMapper.builder(factory).build();
byte[] first = mapper.writeValueAsBytes("hello");
byte[] second = mapper.writeValueAsBytes(42);
Object value = mapper.readValue(first, Object.class);
```

For streaming access, use `factory.createParser(byte[])` and
`factory.createGenerator(OutputStream)`. The factory also inherits binary
byte-array/slice, InputStream, DataInput, File and Path source methods, and
OutputStream, DataOutput, File and Path target methods:

| Direction        | Supported inputs/outputs                                          |
|------------------|-------------------------------------------------------------------|
| Parser source    | `byte[]` (and slices), `InputStream`, `DataInput`, `File`, `Path` |
| Generator target | `OutputStream`, `DataOutput`, `File`, `Path`                      |
| Unsupported      | Reader, String and `char[]` parser sources; Writer targets        |


## Jackson datatype and language modules

The `VPackMapper` can be configured with [Jackson datatype modules](https://github.com/FasterXML/jackson#third-party-datatype-modules)
as well as [Jackson JVM Language modules](https://github.com/FasterXML/jackson#jvm-language-modules).

### Kotlin

[Kotlin language module](https://github.com/FasterXML/jackson-module-kotlin) enables support for Kotlin native types 
and can be registered in the following way:

```kotlin
val mapper = VPackMapper().apply {
    registerModule(KotlinModule())
}
```

### Scala

[Scala language module](https://github.com/FasterXML/jackson-module-scala) enables support for Scala native types 
and can be registered in the following way:

```scala
val mapper = new VPackMapper()
mapper.registerModule(DefaultScalaModule)
```

# Learn more

- [Arango](https://arango.ai)
- [ChangeLog](ChangeLog.md)
