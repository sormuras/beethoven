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

package de.sormuras.beethoven.type;

import static de.sormuras.beethoven.Style.CANONICAL;
import static de.sormuras.beethoven.Style.LAST;
import static de.sormuras.beethoven.Style.SIMPLE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.U;
import de.sormuras.beethoven.V;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassTypeTests {

  @Test
  void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, ClassType.type("", "Unnamed").getAnnotationsTarget());
  }

  @Test
  void binaryForUnnamedPackage() {
    Assertions.assertEquals("A", ClassType.type(Name.name("A")).binary());
  }

  @Test
  void createAnnotated() {
    String expected = U.USE + " " + V.USE + " String";
    ClassType type = Type.withAnnotations(ClassType.type(String.class), U.class, V.class);
    assertEquals(expected, type.list());
    assertTrue(type.isAnnotated());
    assertFalse(type.isGeneric());
  }

  @Test
  void createAnnotatedAndParameterized() {
    String expected = U.USE + " Comparable<" + V.USE + " String>";
    ClassType string = ClassType.type(String.class).annotated(i -> V.SINGLETON);
    ClassType comparable = ClassType.type(Comparable.class).annotated(i -> U.SINGLETON);
    ClassType type = comparable.parameterized(i -> singletonList(string));
    assertEquals(expected, type.list());
    assertTrue(type.isAnnotated());
    assertTrue(type.isGeneric());
  }

  @Test
  void createParameterized() {
    String expected = "Comparable<String>";
    ClassType type = ClassType.parameterized(Comparable.class, String.class);
    assertEquals(expected, type.list());
    assertFalse(type.isAnnotated());
    assertTrue(type.isGeneric());
    assertThrows(Error.class, () -> ClassType.parameterized(Byte.class, Byte.class));
    assertThrows(Error.class, () -> ClassType.parameterized(Comparable.class, int.class));
  }

  @Test
  void constructor() {
    assertEquals("Unnamed", ClassType.type(Name.name("Unnamed")).list());
    assertEquals("a.b.c.D", ClassType.type("a.b.c", "D").list());
    assertEquals("a.b.c.D.E", ClassType.type("a.b.c", "D", "E").list());
  }

  @Test
  void imports() {
    ClassType state = ClassType.type(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list(new Listing(CANONICAL)));
    assertEquals("Thread.State", state.list(new Listing(SIMPLE)));
    assertEquals("State", state.list(new Listing(LAST)));
    assertFalse(state.isAnnotated());
    assertFalse(state.isGeneric());
  }

  @Test
  void handcrafted() throws Exception {
    List<Annotation> annotations = new ArrayList<>();
    annotations.add(Annotation.annotation(Name.name("UUU")));
    annotations.add(Annotation.annotation(V.class));
    ClassType.Simple name = new ClassType.Simple(annotations, "Name", emptyList());
    assertEquals("@UUU " + V.USE + " Name", name.list());

    ClassType nested =
        ClassType.type(
            "a.b.c",
            ClassType.simple("Top", Number.class),
            ClassType.simple("Nested", Integer.class, Long.class));
    assertEquals("a.b.c.Top<Number>.Nested<Integer, Long>", nested.list());
  }

  @Test
  void string() throws Exception {
    assertTrue(ClassType.type("a.b.c", "D").toString().startsWith("ClassType {@0 a.b.c.D // 0x"));
  }
}
