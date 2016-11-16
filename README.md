# hdfjavaio

[![Build Status](https://travis-ci.org/markroyer/hdfjavaio.svg?branch=master)](https://travis-ci.org/markroyer/hdfjavaio)

HDFJavaIO is a Java library for creating HDF files in Java for
languages such as Octave/Matlab.  This library makes it easy to read
and write files Octave/Matlab files.


## Install

The most recent version of the library is **1.0** and can be installed
using Eclipse's plugin manager, or it can be downloaded directly
below.

### Install Using Eclipse

The library has been written as an OSGI (Eclipse plugin), so the
easiest way to use the software is to add the following URL to the
list of available software sites in eclipse.

```
https://raw.githubusercontent.com/wiki/markroyer/hdfjavaio/edu.umaine.cs.hdfjavaio.repository/updates
```

The above repository contains the dependencies for the library.

Alternatively, you may only want to add the following composite
repository, which contains a number of libraries that I find useful
for working with HDFJavaIO files.

```
https://rawgit.com/markroyer/p2-repositories/master
```

### Install Using Direct Download

The HDFJavaIO library can be directly downloaded from the following
links.  Please note, however, that these binaries also require the
HDFJava library from the
[HDFGroup](https://www.hdfgroup.org/products/java/).

| Version | Binary               | Source                                   |
| ------- | -------------------- | ---------------------------------------- |
| **1.0** | [hdfjavaio_1.0.0.jar](https://raw.githubusercontent.com/wiki/markroyer/hdfjavaio/edu.umaine.cs.hdfjavaio.repository/updates/1.0/1.0.0.v20160829-2316/plugins/edu.umaine.cs.hdfjavaio_1.0.0.v20160829-2316.jar) | [edu.umaine.cs.hdfjavaio.source_1.0.0.jar](https://raw.githubusercontent.com/wiki/markroyer/hdfjavaio/edu.umaine.cs.hdfjavaio.repository/updates/1.0/1.0.0.v20160829-2316/plugins/edu.umaine.cs.hdfjavaio.source_1.0.0.v20160829-2316.jar)


## Building

The project can be built using maven from the
`edu.umaine.cs.hdfjavaio.parent` directory. Typing

```bash
mvn clean verify
```

will compile the project.  To create a p2 repository containing all of
the related libraries in

```bash
../../hdfjavaio.wiki/
```

append the optional profile command `p2-release` as shown below.

```bash
mvn clean verify -P p2-release
```

## LICENSE

The license is GPL3.  See the included LICENSE file for details.

## Thanks

The ant build scripts are based on code from:

[http://www.lorenzobettini.it/2015/01/creating-p2-composite-repositories-during-the-build/](http://www.lorenzobettini.it/2015/01/creating-p2-composite-repositories-during-the-build/)

<!--  LocalWords:  hdfjavaio HDF Matlab OSGI mvn HDFJava HDFJavaIO
 -->
<!--  LocalWords:  HDFGroup
 -->
