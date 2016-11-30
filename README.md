# beethoven
Java source created with Java

## badges
[![jdk9](https://img.shields.io/badge/jdk-8-blue.svg)](https://shields.io)
[![travis](https://travis-ci.org/sormuras/beethoven.svg?branch=master)](https://travis-ci.org/sormuras/beethoven)
[![snapshots](https://img.shields.io/badge/sonatype-snapshots-green.svg)](https://oss.sonatype.org/content/repositories/snapshots/de/sormuras/beethoven/)

## features
 - [x] JDK 8
 - [x] Aligned to [JLS](https://docs.oracle.com/javase/specs/jls/se8/html/jls-19.html) syntax grammar
 - [x] Runtime compilation supporting custom annotation processors
 - [x] JavaBeans style API

## hello world
Here's a simple [HelloWorld](https://github.com/sormuras/beethoven/blob/master/src/test/java/readme/HelloWorld.java)
program. It demonstrates basic usage of the main features.
```java
Name out = Name.name(System.class, "out");

CompilationUnit unit = CompilationUnit.of("beethoven");
unit.getImportDeclarations().addSingleStaticImport(out);

ClassDeclaration symphony = unit.declareClass("Symphony", PUBLIC);
MethodDeclaration main = symphony.declareMethod(void.class, "main", PUBLIC, STATIC);
MethodParameter strings = main.declareParameter(String[].class, "strings");

main.addStatement(
    listing ->
        listing
            .add(out)
            .add(".println(")
            .add(Listable.escape("Symphony "))
            .add(" + ")
            .add(Name.name(String.class))
            .add(".join(")
            .add(Listable.escape(" - "))
            .add(", ")
            .add(strings.getName())
            .add("))"));

unit.list(System.out);
unit.launch("no.9", "The Choral");
```


The console reads like:
```text
package beethoven;

import static java.lang.System.out;

public class Symphony {

  public static void main(String... strings) {
    out.println("Symphony " + strings[0]);
  }
}

Symphony no.9 - The Choral
```
## license
```text
Copyright 2016 Christian Stein

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```