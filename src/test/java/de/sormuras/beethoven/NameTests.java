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

package de.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class NameTests {

  @SupportedAnnotationTypes({"X", "x.X"})
  @SupportedSourceVersion(SourceVersion.RELEASE_8)
  static class ElementNameProcessor extends AbstractProcessor {
    final List<Name> all = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      TypeElement tex = processingEnv.getElementUtils().getTypeElement("X");
      if (tex != null) {
        roundEnv.getElementsAnnotatedWith(tex).forEach(e -> all.add(Name.name(e)));
      }
      tex = processingEnv.getElementUtils().getTypeElement("x.X");
      if (tex != null) {
        roundEnv.getElementsAnnotatedWith(tex).forEach(e -> all.add(Name.name(e)));
      }
      return false;
    }
  }

  @Test
  void array() {
    assertEquals("", Name.name(int[][].class).packageName());
    assertEquals("int[][]", Name.name(int[][].class).canonical());
    assertEquals("int[][]", Name.name(int[][].class).simpleNames());
    assertEquals("java.lang", Name.name(Byte[][].class).packageName());
    assertEquals("java.lang.Byte[][]", Name.name(Byte[][].class).canonical());
    assertEquals("Byte[][]", Name.name(Byte[][].class).simpleNames());
  }

  @Test
  void cast() throws Exception {
    Name name = Name.reflect(Objects.class, "hash");
    assertNull(Name.cast(null));
    assertSame(name, Name.cast(name));
    assertEquals(Name.name(Object.class), Name.cast(Object.class));
    assertEquals(Name.name(Thread.State.BLOCKED), Name.cast(Thread.State.BLOCKED));
    assertEquals(Name.name("abc", "X"), Name.cast(new String[] {"abc", "X"}));
    assertEquals(Name.name("abc", "X"), Name.cast(List.of("abc", "X")));
    assertEquals(Name.name(Math.class.getField("PI")), Name.cast(Math.class.getField("PI")));
    assertThrows(IllegalArgumentException.class, () -> Name.cast(BigInteger.ZERO));
  }

  @Test
  void elementInUnnamedPackage() {
    JavaFileObject a = Compilation.source("A", "@X class A {}");
    JavaFileObject x = Compilation.source("X", "@interface X {}");
    ElementNameProcessor p = new ElementNameProcessor();
    Compilation.compile(null, List.of(), List.of(p), List.of(a, x));
    assertEquals(1, p.all.size());
    assertEquals(Name.name("A"), p.all.get(0));
  }

  @Test
  void elementInNamedPackage() {
    JavaFileObject a = Compilation.source("x.A", "package x; @x.X class A {}");
    JavaFileObject x = Compilation.source("x.X", "package x; @interface X {}");
    ElementNameProcessor p = new ElementNameProcessor();
    Compilation.compile(null, List.of(), List.of(p), List.of(a, x));
    assertEquals(1, p.all.size());
    assertEquals(Name.name("x", "A"), p.all.get(0));
  }

  @Test
  void enclosed() {
    Name.name(void.class);
    Name state = Name.name(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.canonical());
    assertTrue(state.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name thread = state.enclosing();
    assertEquals("java.lang.Thread", thread.canonical());
    assertTrue(thread.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name lang = thread.enclosing();
    assertEquals("java.lang", lang.canonical());
    assertTrue(lang.isEnclosed());
    assertTrue(state.isJavaLangPackage());

    Name java = lang.enclosing();
    assertEquals("java", java.canonical());
    assertFalse(java.isEnclosed());
    assertFalse(java.isJavaLangPackage());
    assertThrows(IllegalStateException.class, java::enclosing);
  }

  @Test
  void field() {
    assertEquals("java.lang.Math.PI", Name.reflect(Math.class, "PI").canonical());
    assertThrows(Error.class, () -> Name.reflect(Object.class, "PI"));
    assertThrows(Error.class, () -> Name.reflect(Class.class, "PO"));
  }

  @Test
  void equalsAndHashcode() {
    assertEquals(Name.name(byte.class), new Name(0, List.of("byte")));
    assertEquals(Name.name(Object.class), Name.name("java", "lang", "Object"));
    assertEquals(Name.name(Objects.class), Name.name("java", "util", "Objects"));
    assertEquals(Name.name(Thread.class), Name.name("java", "lang", "Thread"));
    assertEquals(Name.name(Thread.State.class), Name.name("java", "lang", "Thread", "State"));
    // same instance
    Name integer = Name.name(int.class);
    assertEquals(integer, integer);
    // falsify
    // noinspection ObjectEqualsNull
    assertFalse(Name.name(byte.class).equals(null));
    Object byteClass = byte.class; // bypass EqualsBetweenInconvertibleTypes "error"
    assertFalse(Name.name(byte.class).equals(byteClass));
    assertFalse(Name.name(byte.class).equals(new Name(0, List.of("some", "byte"))));
  }

  @Test
  void name() {
    assertEquals("A", Name.name("A").canonical());
    assertEquals("", Name.name("A").packageName());
    assertEquals("A", Name.name("A").simpleNames());
    assertEquals("A", Name.name("A").lastName());
    assertEquals(1, Name.name("A").size());

    assertEquals("a", Name.name("a").canonical());
    assertEquals("a", Name.name("a").packageName());
    assertEquals("", Name.name("a").simpleNames());
    assertEquals("a", Name.name("a").lastName());
    assertEquals("not present", Name.name("a").topLevelName().orElse("not present"));

    assertEquals("a.b", Name.name("a", "b").canonical());
    assertEquals("a.b", Name.name("a", "b").packageName());
    assertEquals("", Name.name("a", "b").simpleNames());
    assertEquals("b", Name.name("a", "b").lastName());

    assertEquals("a.b.C", Name.name("a", "b", "C").canonical());
    assertEquals("a.b", Name.name("a", "b", "C").packageName());
    assertEquals("C", Name.name("a", "b", "C").simpleNames());
    assertEquals("C", Name.name("a", "b", "C").lastName());
    assertEquals(3, Name.name("a", "b", "C").size());

    assertEquals("java.lang.Object", Name.name(Object.class).canonical());
    assertEquals("java.lang", Name.name(Object.class).packageName());
    assertEquals("Object", Name.name(Object.class).simpleNames());
    assertEquals("Object", Name.name(Object.class).lastName());
    assertEquals("Object", Name.name(Object.class).topLevelName().orElse("FAIL!"));
    assertTrue(Name.name(Object.class).isJavaLangObject());

    assertEquals("java.lang.Thread.State.NEW", Name.name(Thread.State.NEW).canonical());
    assertEquals("java.lang", Name.name(Thread.State.NEW).packageName());
    assertEquals("Thread.State.NEW", Name.name(Thread.State.NEW).simpleNames());
    assertEquals("NEW", Name.name(Thread.State.NEW).lastName());
    assertEquals("Thread", Name.name(Thread.State.NEW).topLevelName().orElse("FAIL!"));
    assertFalse(Name.name(Thread.State.NEW).isJavaLangObject());
    assertEquals(5, Name.name(Thread.State.NEW).size());

    assertThrows(AssertionError.class, () -> new Name(-1, List.of("a")));
    assertThrows(AssertionError.class, () -> new Name(2, List.of("a")));
  }

  @Test
  void nameOfLocalClass() {
    class Local {}
    assertEquals("Local", Name.name(Local.class).canonical());
    assertEquals("", Name.name(Local.class).packageName());
    assertEquals("Local", Name.name(Local.class).simpleNames());
    assertEquals("Local", Name.name(Local.class).lastName());
    assertEquals("Local", Name.name(Local.class).topLevelName().orElse("FAIL!"));
  }

  @Test
  void primitive() {
    assertEquals("boolean", Name.name(boolean.class).canonical());
    assertEquals("byte", Name.name(byte.class).canonical());
    assertEquals("char", Name.name(char.class).canonical());
    assertEquals("double", Name.name(double.class).canonical());
    assertEquals("float", Name.name(float.class).canonical());
    assertEquals("int", Name.name(int.class).canonical());
    assertEquals("long", Name.name(long.class).canonical());
    assertEquals("short", Name.name(short.class).canonical());
    assertEquals("void", Name.name(void.class).canonical());
  }

  @Test
  void string() {
    assertEquals("Name{/void}", Name.name(void.class).toString());
    assertEquals("Name{/int}", Name.name(int.class).toString());
    assertEquals("Name{/int[]}", Name.name(int[].class).toString());
    assertEquals("Name{/int[][]}", Name.name(int[][].class).toString());
    assertEquals("Name{java.lang/Thread.State[]}", Name.name(Thread.State[].class).toString());
    assertEquals("Name{java.lang/Thread.State.NEW}", Name.name(Thread.State.NEW).toString());
  }
}
