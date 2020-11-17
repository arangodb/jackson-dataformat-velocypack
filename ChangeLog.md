# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.1] - 2020-10-14

- fixed generation of vpack with trailing zeros

## [0.3.0] - 2020-10-12

- miscellaneous fixes to provide compliance with jackson-databind tests 
- jackson v2.10.5

## [0.2.0] - 2020-08-03

- compatibility with Java Platform Module System: moved `VelocyJack` under `com.arangodb.jackson.dataformat.velocypack` package

## [0.1.5] - 2019-10-23

- arangodb-java-driver v6.4.1
- jackson v2.9.10

## [0.1.4] - 2019-08-19

- using java-velocypack version 1.4.2

## [0.1.3] - 2018-08-28

### Fixed

- fixed deserializing of raw VelocyPack objects and arrays
- fixed parsing of inner maps

## [0.1.2] - 2018-06-28

### Fixed

- fixed mapping of nested objects
- fixed parsing of json to velocypack within `VelocyJack`

## [0.1.1] - 2018-06-25

### Fixed

- fixed use of `ArangoSerializer.Options`

[unreleased]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.4...HEAD
[0.1.4]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.0...0.1.1
