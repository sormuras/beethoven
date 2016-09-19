package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class NormalClassDeclarationTests {

  @Test
  void empty() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("Empty");
    assertEquals("class Empty {\n}\n", declaration.list());
    assertTrue(declaration.isEmpty());
    assertFalse(new NormalClassDeclaration().declareInitializer(false).getEnclosing().isEmpty());
    assertFalse(
        new NormalClassDeclaration()
            .declareField(int.class, "i")
            .getEnclosingDeclaration()
            .isEmpty());
  }

  @Test
  void generic() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("G");
    declaration.addTypeParameter(new TypeParameter());
    assertEquals("class G<T> {\n}\n", declaration.list());
    declaration.addTypeParameter(
        TypeParameter.of("I", ClassType.parameterized(Iterable.class, Long.class)));
    assertEquals("class G<T, I extends Iterable<Long>> {\n}\n", declaration.list());
  }

  @Test
  void interfaces() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("I");
    declaration.addInterface(Type.type(Runnable.class));
    assertEquals("class I implements Runnable {\n}\n", declaration.list());
    declaration.addInterface(ClassType.parameterized(Comparable.class, Byte.class));
    assertEquals("class I implements Runnable, Comparable<Byte> {\n}\n", declaration.list());
  }

  @Test
  void superclass() {
    NormalClassDeclaration declaration = new NormalClassDeclaration();
    declaration.setName("C");
    declaration.setSuperClass(ClassType.type(Object.class));
    assertEquals("class C extends Object {\n}\n", declaration.list());
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new NormalClassDeclaration().getAnnotationsTarget());
  }
}