# beethoven
Java source created with Java

## badges
[![jdk8](https://img.shields.io/badge/jdk-8-blue.svg)](https://shields.io)
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

  public static void main(String[] strings) {
    out.println("Symphony " + String.join(" - ", strings));
  }
}

Symphony no.9 - The Choral
```

## composers

Composers visit unit objects and apply new features to the visited objects.

- `ConstructorComposer` generates a parameter for each declared field.
- `EqualsComposer` generates an trivial `Object#equals(Object)` implementation
delegating equality check to `this.hashCode()` after checking runtime type
equality.
- `HashCodeComposer` generates trivial implementation delegating to `Objects#hashCode(Object)`
or `Objects#hash(Object...)`.
- `ImportsComposer` generates import statements for types used within the
compilation unit.
- `PropertyComposer` generates a bean property: a field, a getter and optionally
a setter.
- `ToStringComposer` generates a simple implementation utilizing a `StringBuilder`
instance as a target for each field's string representation.

```java
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("pool");
    ClassDeclaration car = unit.declareClass("Car");
    car.setModifiers(Modifier.PUBLIC);

    new PropertyComposer()
        .setType(String.class)
        .setName("name")
        .setSetterAvailable(false)
        .setFieldFinal(true)
        .apply(car);

    new PropertyComposer().setType(Number.class).setName("gear").apply(car);

    new PropertyComposer()
        .setType(State.class)
        .setName("state")
        .setSetterRequiresNonNullValue(true)
        .setSetterReturnsThis(true)
        .setFieldInitializer(listing -> listing.add(Name.cast(State.NEW)))
        .apply(car);

    new ConstructorComposer().apply(car);
    new EqualsComposer().apply(car);
    new HashCodeComposer().apply(car);
    new ToStringComposer().apply(car);
```

The composer-enriched code reads:

```java
package pool;

public class Car {

  private final String name;

  private Number gear;

  private Thread.State state = Thread.State.NEW;

  public Car(String name, Number gear, Thread.State state) {
    this.name = name;
    this.gear = gear;
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public Number getGear() {
    return gear;
  }

  public void setGear(Number gear) {
    this.gear = gear;
  }

  public Thread.State getState() {
    return state;
  }

  public Car setState(Thread.State state) {
    this.state = java.util.Objects.requireNonNull(state, "Property `state` requires non `null` values!");
    return this;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    return hashCode() == other.hashCode();
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(name, gear, state);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Car");
    builder.append('[');
    builder.append("name").append('=').append(name);
    builder.append(", ").append("gear").append('=').append(gear);
    builder.append(", ").append("state").append('=').append(state);
    builder.append(']');
    return builder.toString();
  }
}
```

## license
```text
Copyright 2017 Christian Stein

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
