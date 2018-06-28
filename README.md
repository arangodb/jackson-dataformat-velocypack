![ArangoDB-Logo](https://docs.arangodb.com/assets/arangodb_logo_2016_inverted.png)

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
    <version>0.1.1</version>
  </dependency>
</dependencies>
```

## Usage

Just create an instance of `VPackMapper` simply by:

```java
ObjectMapper mapper = new VPackMapper();
```

## Usage within ArangoDB Java driver

The [ArangoDB Java driver](https://github.com/arangodb/arangodb-java-driver) allows to use a custom serializer to de-/serialize documents, edges and query results. Just create an instance of `VelocyJack` and pass it to the driver through `ArangoDB.Builder.serializer(ArangoSerialization)`.

```java
ArangoDB arango = new ArangoDB.Builder().serializer(new VelocyJack()).build();
```

# Learn more

- [ArangoDB](https://www.arangodb.com/)
- [ChangeLog](ChangeLog.md)
