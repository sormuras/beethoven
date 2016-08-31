package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.U;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.ElementType;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ClassTypeTest {

  @Test
  void annotationTarget() {
    assertEquals(ElementType.TYPE_USE, ClassType.of(Name.name("Unnamed")).getAnnotationTarget());
  }

  @Test
  void annotated() {
    String expected = "java.lang." + U.USE + " Comparable<java.lang.String>";
    ClassType type = ClassType.of(Comparable.class, String.class);
    type.addAnnotation(U.class);
    assertEquals(expected, type.list());
  }

  @Test
  void constructor() {
    assertEquals("Unnamed", ClassType.of(Name.name("Unnamed")).list());
    assertEquals("a.b.c.D", ClassType.of("a.b.c", "D").list());
    assertEquals("a.b.c.D.E", ClassType.of("a.b.c", "D", "E").list());
    assertEquals(
        "java.lang.Comparable<java.lang.String>",
        ClassType.of(Comparable.class, String.class).list());
  }

  @Test
  void enclosingClassType() {
    ClassType state = ClassType.of(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals(ClassType.of(Thread.class), state.getEnclosingClassType().get());
    assertEquals(Optional.empty(), ClassType.of(Thread.class).getEnclosingClassType());
  }

  @Test
  void unnamedPackage() {
    assertEquals("A", ClassType.of(Name.name("A")).toClassName());
  }
}
