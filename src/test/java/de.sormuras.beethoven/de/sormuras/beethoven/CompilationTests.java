/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import static de.sormuras.beethoven.Compilation.compile;
import static de.sormuras.beethoven.Compilation.source;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class CompilationTests {

  @Test
  void compileString() {
    assertEquals("€", compile("enum €{}").getName());
    assertEquals("A", compile("enum A {}").getName());
    assertEquals("A", compile("class A {}").getName());
    assertEquals("A", compile("interface A {}").getName());
    assertEquals("A", compile("@interface A {}").getName());
    assertEquals("a.A", compile("package a; enum A {}").getName());
    assertEquals("a.b.A", compile("package a.b; class A {}").getName());
    assertEquals("a.b.c.A", compile("package a.b.c; interface A {}").getName());
    assertEquals("a.b.c.d.A", compile("package a.b.c.d; @interface A {}").getName());
    assertThrows(IllegalArgumentException.class, () -> compile(""));
    assertThrows(IllegalArgumentException.class, () -> compile("package abc;"));
    assertThrows(RuntimeException.class, () -> compile("A", "enum E {}"));
  }

  @Test
  void hi() throws Exception {
    String code = "public class Hi { public String greet(String who) { return \"Hi \" + who;}}";
    JavaFileObject file = source("Hi.java", code);
    ClassLoader loader = compile(file);
    Class<?> hiClass = loader.loadClass("Hi");
    Object hiInstance = hiClass.getConstructor().newInstance();
    Method greetMethod = hiClass.getMethod("greet", String.class);
    assertEquals("Hi world", greetMethod.invoke(hiInstance, "world"));
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("Hello"));
  }

  @Test
  void lastModified() {
    JavaFileObject jfo = new Compilation.CharContentFileObject("abc", "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }

  @Test
  void multipleClassesWithDependingOnEachOther() {
    String codeA = "package a; public class A {}";
    String codeB = "package b; class B extends a.A {}";
    JavaFileObject fileA = source("listing/A.java", codeA);
    JavaFileObject fileB = source("listing/B.java", codeB);
    compile(fileA, fileB);
  }

  @Test
  void syntaxError() {
    assertThrows(Exception.class, () -> compile(source("F.java", "class 1F {}")));
  }
}
