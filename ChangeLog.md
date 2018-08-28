# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/) and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[unreleased]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.2...HEAD
[0.1.2]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/arangodb/jackson-dataformat-velocypack/compare/0.1.0...0.1.1
