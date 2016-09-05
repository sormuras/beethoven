package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Importing;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.Omitting;
import com.github.sormuras.beethoven.U;
import com.github.sormuras.beethoven.V;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClassTypeTest {

  @Test
  void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, ClassType.type("", "Unnamed").getAnnotationsTarget());
  }

  @Test
  void binaryForUnnamedPackage() {
    assertEquals("A", ClassType.type(Name.name("A")).binary());
  }

  @Test
  void createAnnotated() {
    String expected = "java.lang." + U.USE + " " + V.USE + " String";
    ClassType type = Type.annotated(ClassType.type(String.class), U.class, V.class);
    assertEquals(expected, type.list());
    assertTrue(type.isAnnotated());
    assertFalse(type.isGeneric());
  }

  @Test
  void createAnnotatedAndParameterized() {
    String expected = "java.lang." + U.USE + " Comparable<java.lang." + V.USE + " String>";
    ClassType string = ClassType.type(String.class).annotated(i -> V.SINGLETON);
    ClassType comparable = ClassType.type(Comparable.class).annotated(i -> U.SINGLETON);
    ClassType type = comparable.parameterized(i -> List.of(string));
    assertEquals(expected, type.list());
    assertTrue(type.isAnnotated());
    assertTrue(type.isGeneric());
  }

  @Test
  void createParameterized() {
    String expected = "java.lang.Comparable<java.lang.String>";
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
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals("Thread.State", state.list(new Omitting()));
    assertEquals("State", state.list(new Importing(Listing.NameMode.LAST)));
    assertFalse(state.isAnnotated());
    assertFalse(state.isGeneric());
  }

  @Test
  void handcrafted() throws Exception {
    List<Annotation> annotations = new ArrayList<>();
    annotations.add(Annotation.annotation(Name.name("UUU")));
    annotations.add(Annotation.annotation(V.class));
    ClassType.Simple name = new ClassType.Simple(annotations, "Name", List.of());
    assertEquals("@UUU " + V.USE + " Name", name.list());

    ClassType nested =
        ClassType.type(
            "a.b.c",
            ClassType.simple("Top", Number.class),
            ClassType.simple("Nested", Integer.class, Long.class));
    assertEquals("a.b.c.Top<Number>.Nested<Integer, Long>", nested.list(new Omitting()));
  }

  @Test
  void string() throws Exception {
    assertTrue(ClassType.type("a.b.c", "D").toString().startsWith("ClassType {@0 a.b.c.D // 0x"));
  }
}
