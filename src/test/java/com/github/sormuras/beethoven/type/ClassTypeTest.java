package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Importing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.Omitting;
import com.github.sormuras.beethoven.U;
import com.github.sormuras.beethoven.V;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class ClassTypeTest {

  //  @Test
  //  void annotationTarget() {
  //    assertEquals(ElementType.TYPE_USE, ClassType.argument(Name.name("Unnamed")).getAnnotationTarget());
  //  }

  @Test
  void parameterized() {
    String expected = "java.lang." + U.USE + " Comparable<java.lang." + V.USE + " String>";
    ClassType string = ClassType.type(String.class).toAnnotatedType(V.SINGLETON);
    ClassType comparable = ClassType.type(Comparable.class).toAnnotatedType(U.SINGLETON);
    ClassType type = comparable.toParameterizedType(i -> Collections.singletonList(string));
    assertEquals(expected, type.list());
  }

  //  @Test
  //  void constructor() {
  //    assertEquals("Unnamed", ClassType.argument(Name.name("Unnamed")).list());
  //    assertEquals("a.b.c.D", ClassType.argument("a.b.c", "D").list());
  //    assertEquals("a.b.c.D.E", ClassType.argument("a.b.c", "D", "E").list());
  //    assertEquals(
  //        "java.lang.Comparable<java.lang.String>",
  //        ClassType.argument(Comparable.class, String.class).list());
  //  }

  @Test
  void imports() {
    ClassType state = ClassType.type(Thread.State.class);
    assertEquals("java.lang.Thread.State", state.list());
    assertEquals("Thread.State", state.list(new Omitting()));
    assertEquals("State", state.list(new Importing()));
  }

  @Test
  void handcrafted() throws Exception {
    List<Annotation> annotations = new ArrayList<>();
    annotations.add(Annotation.annotation(Name.name("UUU")));
    annotations.add(Annotation.annotation(V.class));
    ClassType.Simple name = new ClassType.Simple(annotations, "Name", Collections.emptyList());
    assertEquals("@UUU " + V.USE + " Name", name.list());
  }

  //  @Test
  //  void unnamedPackage() {
  //    assertEquals("A", ClassType.argument(Name.name("A")).toClassName());
  //  }
}
