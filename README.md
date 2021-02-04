![ArangoDB-Logo](https://www.arangodb.com/docs/assets/arangodb_logo_2016_inverted.png)

# VelocyPack dataformat for Jackson

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.arangodb/jackson-dataformat-velocypack/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.arangodb/jackson-dataformat-velocypack)

This project contains a [Jackson](https://github.com/FasterXML/jackson) extension for reading and writing [VelocyPack](https://github.com/arangodb/velocypack) encoded data.

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

## Usage

Just create an instance of `VPackMapper` simply by:

```java
ObjectMapper mapper = new VPackMapper();
```

## Within ArangoDB Java driver

### Usage

Since version 4.5.2 the [ArangoDB Java driver](https://github.com/arangodb/arangodb-java-driver) allows to use a custom 
serializer to de-/serialize documents, edges and query results. Just create an instance of `ArangoJack` and pass it to the driver through `ArangoDB.Builder.serializer(ArangoSerialization)`.

```java
ArangoDB arango = new ArangoDB.Builder().serializer(new ArangoJack()).build();
```

### Configure

```java
ArangoJack arangoJack = new ArangoJack();
arangoJack.configure((mapper) -> {
  // your configuration here
});
ArangoDB arango = new ArangoDB.Builder().serializer(arangoJack).build();
```

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

- [ArangoDB](https://www.arangodb.com/)
- [ChangeLog](ChangeLog.md)
