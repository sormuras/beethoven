# beethoven
Java source created with Java

## badges
[![jdk8](https://img.shields.io/badge/jdk-8-blue.svg)](https://shields.io)

## features
 - [x] JDK 8
 - [x] Aligned to [JLS](https://docs.oracle.com/javase/specs/jls/se8/html/jls-19.html) syntax grammar
 - [x] Runtime compilation supporting custom annotation processors
 - [x] JavaBeans style API

## hello world
Here's a simple [HelloWorld](https://github.com/sormuras/beethoven/blob/main/test.integration/test/java/readme/HelloWorld.java)
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

## script

The `println`-statement from above, can be scripted as:
```java
    main.addStatement(
        // script source
        "{{N // out}}.println({{S}} + {{N // join}}({{S}}, {{#getName // of parameter}}))",

        // arguments
        Name.reflect(System.class, "out"),
        "Symphony ",
        Name.reflect(String.class, "join"),
        " // ",
        main.declareParameter(String[].class, "arguments").setVariable(true));
```

See [Tag](https://github.com/sormuras/beethoven/blob/main/com.github.sormuras.beethoven/main/java/com/github/sormuras/beethoven/script/Tag.java)
enum for available accelerator tags and [script tests](https://github.com/sormuras/beethoven/tree/main/test.integration/test/java/test/integration/script)
supported syntax features.

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
