/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven.unit;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Compilation;
import de.sormuras.beethoven.Counter;
import de.sormuras.beethoven.Generated;
import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.composer.ImportsComposer;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import de.sormuras.beethoven.type.TypeVariable;
import de.sormuras.beethoven.type.WildcardType;
import java.io.IOException;
import java.io.Serializable;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompilationUnitTests {

  @Test
  void empty() {
    assertTrue(new CompilationUnit().isEmpty());
    assertFalse(CompilationUnit.of("a").isEmpty());
    CompilationUnit unit = new CompilationUnit();
    unit.getImportDeclarations().addSingleTypeImport(Objects.class);
    assertFalse(unit.isEmpty());
    unit = new CompilationUnit();
    unit.declareClass("C");
    assertFalse(unit.isEmpty());
    // Tests.assertSerializable(unit);
  }

  @Test
  void enterprise() {
    CompilationUnit unit = CompilationUnit.of("uss");
    NormalClassDeclaration enterprise = unit.declareClass("Enterprise");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise.addInterface(ClassType.parameterized(Supplier.class, String.class));
    enterprise.declareField(String.class, "text").addModifier("private", "final");
    enterprise.declareField(Number.class, "number").addModifier("private", "final");
    MethodDeclaration constructor = enterprise.declareConstructor();
    constructor.addModifier(Modifier.PUBLIC);
    constructor.declareParameter(String.class, "text");
    constructor.declareParameter(Number.class, "number");
    constructor.addStatement("this.text = text");
    constructor.addStatement("this.number = number");
    MethodDeclaration getter = enterprise.declareMethod(String.class, "get");
    getter.addAnnotation(Override.class);
    getter.addModifier(Modifier.PUBLIC);
    getter.addStatement("return text + '-' + number");
    assertEquals("uss.Enterprise", enterprise.toType().list());

    Supplier<?> spaceship = unit.compile(Supplier.class, "NCC", (short) 1701);

    assertEquals("Enterprise", spaceship.getClass().getSimpleName());
    assertEquals("NCC-1701", spaceship.get());
    Tests.assertEquals(getClass(), "enterprise", unit);
  }

  @Test
  void packageName() {
    assertEquals("", new CompilationUnit().getPackageName());
    assertEquals("a.b.c", CompilationUnit.of("a.b.c").getPackageName());
  }

  @Test
  void eponymousDeclaration() {
    // empty unit
    assertFalse(new CompilationUnit().getEponymousDeclaration().isPresent());
    // single type in unit
    CompilationUnit singleTypeUnit = new CompilationUnit();
    singleTypeUnit.declareClass("NonPublic");
    assertTrue(singleTypeUnit.getEponymousDeclaration().isPresent());
    assertEquals("NonPublic", singleTypeUnit.getEponymousDeclaration().get().getName());
    // multiple top level classes
    assertEquals("Gamma", Units.simple().getEponymousDeclaration().get().getName());
  }

  @Test
  void top() {
    assertEquals(3, Units.simple().getDeclarations().size());
    CompilationUnit unit = CompilationUnit.of("top");
    unit.declareAnnotation("A").declareAnnotation("X");
    unit.declareEnum("E").declareEnum("X");
    unit.declareClass("C").declareClass("X").declareClass("Z");
    TypeDeclaration x = unit.declareInterface("I").declareInterface("X");
    assertEquals(4, unit.getDeclarations().size());
    assertFalse(unit.getDeclarations().get(0).isEmpty()); // A
    assertTrue(unit.getDeclarations().get(0).getDeclarations().get(0).isEmpty()); // A.X
    assertEquals(Tests.load(CompilationUnitTests.class, "top"), unit.list());
    assertEquals("top.I.X", x.toType().list());
  }

  @Test
  void simple() {
    assertEquals(Tests.load(Units.class, "simple"), Units.simple().list());
  }

  @Test
  void processed() throws Exception {
    CompilationUnit unit = CompilationUnit.of("test");
    ClassDeclaration enterprise = unit.declareClass("Class");
    enterprise.addModifier(Modifier.PUBLIC);
    enterprise.declareField(Object.class, "field1").addAnnotation(Counter.Mark.class);
    enterprise
        .declareField(
            ClassType.type(Comparable.class).parameterized(i -> List.of(WildcardType.wildcard())),
            "field2")
        .addAnnotation(Counter.Mark.class);
    enterprise
        .declareField(
            ClassType.type(Map.Entry.class)
                .parameterized(
                    i ->
                        i == 0
                            ? List.of()
                            : List.of(
                                WildcardType.supertype(String.class),
                                WildcardType.extend(Runnable.class))),
            "field3")
        .addAnnotation(Counter.Mark.class);
    enterprise.declareField(int[].class, "field4").addAnnotation(Counter.Mark.class);
    enterprise.declareField(int[][][].class, "field5").addAnnotation(Counter.Mark.class);
    enterprise.declareField(String[][].class, "field6").addAnnotation(Counter.Mark.class);
    Tests.assertEquals(getClass(), "processed", unit);
    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), List.of(counter), List.of(unit.toJavaFileObject()));
    assertEquals(6, counter.marked.size());
    Assertions.assertEquals(
        "java.util.Map.Entry<? super String, ? extends Runnable>",
        counter.types.get("field3").list());
    Assertions.assertEquals("int[]", counter.types.get("field4").list());
    Assertions.assertEquals("int[][][]", counter.types.get("field5").list());
    Assertions.assertEquals("String[][]", counter.types.get("field6").list());
  }

  @Test
  void abc() throws Exception {
    CompilationUnit unit = Units.abc();
    Tests.assertEquals(getClass(), "abc", unit);

    Counter counter = new Counter();
    Compilation.compile(null, emptyList(), List.of(counter), List.of(unit.toJavaFileObject()));
    assertEquals(2, counter.marked.size());
    Assertions.assertEquals("A.B.C", counter.types.get("raw").list());
    Assertions.assertEquals("A<I>.B<I, I>.C<I, I, I>", counter.types.get("parametered").list());
  }

  @Test
  void unnamed() throws Exception {
    CompilationUnit unnamed = new CompilationUnit();
    unnamed.declareClass("Unnamed").addModifier(Modifier.PUBLIC);
    assertEquals("Unnamed", unnamed.compile(Object.class).getClass().getTypeName());
    assertThrows(Error.class, () -> unnamed.compile(Object.class, "unused", "arguments"));
    // with types supplier...
    Supplier<java.lang.Class<?>[]> types0 = () -> new java.lang.Class<?>[0];
    assertEquals("Unnamed", unnamed.compile(Object.class, types0).getClass().getTypeName());
    Supplier<java.lang.Class<?>[]> types1 = () -> new java.lang.Class<?>[1];
    assertThrows(Error.class, () -> unnamed.compile(Object.class, types1));
  }

  @Test
  void crazy() throws Exception {
    Annotation tag = Annotation.annotation(Name.name("Tag"));
    ClassType taggedRunnable = ClassType.type(Runnable.class).annotated(i -> List.of(tag));
    ClassType taggedString = ClassType.type(String.class).annotated(i -> List.of(tag));
    ClassType taggedThread = ClassType.type(Thread.class).annotated(i -> List.of(tag));
    ClassType listOfStrings = ClassType.parameterized(List.class, String.class);

    CompilationUnit unit = CompilationUnit.of("abc.xyz");
    unit.getPackageDeclaration()
        .addAnnotation(Generated.class, "https://", "github.com/sormuras/listing");
    unit.getImportDeclarations()
        .addSingleTypeImport(Assertions.class)
        .addTypeImportOnDemand(Name.name("abc"))
        .addSingleStaticImport(Name.name(Collections.class.getMethod("shuffle", List.class)))
        .addStaticImportOnDemand(Name.name(Objects.class));
    unit.declareAnnotation("TestAnno");
    EnumDeclaration e1 = unit.declareEnum("TestEnum");
    e1.addAnnotation(Generated.class, "An enum for testing");
    e1.addModifier(Modifier.PROTECTED);
    e1.addInterface(Type.type(Serializable.class));
    e1.declareConstant("A");
    e1.declareConstant("B");
    e1.declareConstant("C");
    unit.declareInterface("TestIntf");
    NormalClassDeclaration simple = unit.declareClass("SimpleClass");
    simple.addModifier("public", "final");
    simple.addTypeParameter(TypeParameter.of("S", taggedRunnable));
    simple.addTypeParameter(TypeParameter.of("T", "S"));
    simple.setSuperClass(taggedThread);
    simple.addInterface(Type.type(Cloneable.class));
    simple.addInterface(Type.type(Runnable.class));
    FieldDeclaration i = simple.declareField(int.class, "i");
    i.addModifier("private", "volatile");
    i.setInitializer(l -> l.add("4711"));
    simple
        .declareField(taggedString, "s")
        .setInitializer(l -> l.add(Listable.escape("The Story about \"Ping\"")));
    simple
        .declareField(listOfStrings, "l")
        .setInitializer(l -> l.add("java.util.Collections.emptyList()"));
    MethodDeclaration run = simple.declareMethod(void.class, "run");
    run.addAnnotation(Override.class);
    run.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    run.addStatement("System.out.println({{S}})", "Hallo Welt!");
    MethodDeclaration calc = simple.declareMethod(TypeVariable.variable("N"), "calc");
    calc.addModifier(Modifier.STATIC);
    calc.addTypeParameter(TypeParameter.of("N", Type.type(Number.class)));
    calc.declareParameter(int.class, "i");
    calc.addThrows(Exception.class);
    calc.addStatement("return null");

    assertSame(simple, i.getEnclosingDeclaration());
    Tests.assertEquals(getClass(), "crazy", unit);
    // Tests.assertSerializable(unit);
  }

  @Test
  void imports() throws Exception {
    CompilationUnit unit = CompilationUnit.of("abc.xyz");
    unit.getImportDeclarations().addSingleTypeImport(Callable.class);
    unit.getImportDeclarations().addSingleStaticImport(Name.name(Math.class, "E"));
    NormalClassDeclaration imports = unit.declareClass("Imports");
    imports.addInterface(ClassType.parameterized(Callable.class, Number.class));
    MethodDeclaration call = imports.declareMethod(Number.class, "call");
    call.addModifier("public");
    Block body = new Block();
    body.add(
        l ->
            l.add("return ")
                .add(Name.name(Math.class, "E"))
                .add(" * ")
                .add(Name.name(Math.class, "PI"))
                .add(';')
                .newline());
    call.setBody(body);
    unit.compile();
    Tests.assertEquals(getClass(), "imports", unit.list());
  }

  @Test
  void launch() throws IOException {
    Name out = Name.name(System.class, "out");

    Path tempFilePath = Files.createTempFile("beethoven-launch-", ".java");

    CompilationUnit unit = CompilationUnit.of("unit");
    unit.getImportDeclarations().addSingleStaticImport(out);

    ClassDeclaration symphony = unit.declareClass("Launch", Modifier.PUBLIC);
    MethodDeclaration main =
        symphony.declareMethod(void.class, "main", Modifier.PUBLIC, Modifier.STATIC);
    main.addThrows(IOException.class);
    MethodParameter args = main.declareParameter(String[].class, "args");
    main.addStatement(
        listing ->
            listing
                .add(Name.name(Files.class))
                .add(".write(")
                .add(Name.name(Paths.class))
                .add(".get(")
                .add(args.getName())
                .add("[0])")
                .add(", ")
                .add(Name.name(String.class))
                .add(".join(")
                .add(Listable.escape("\n"))
                .add(", ")
                .add(args.getName())
                .add(").getBytes()")
                .add(")"));

    new ImportsComposer().apply(unit);
    String listed = unit.list(System.lineSeparator());
    Tests.assertEquals(getClass(), "launch", listed);

    unit.launch(tempFilePath.toString(), "Second,", "Third line.");
    List<String> lines = Files.readAllLines(tempFilePath);
    assertEquals(tempFilePath.toString(), lines.get(0));
    assertEquals("Second,", lines.get(1));
    assertEquals("Third line.", lines.get(2));
  }

  @Test
  void launchWithoutMainMethodFails() throws IOException {
    Exception exception = assertThrows(Exception.class, () -> Units.abc().launch());
    assertEquals(NoSuchMethodException.class, exception.getCause().getClass());
  }
}
