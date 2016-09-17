package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class TypeParameterTests {

  @Test
  void simple() {
    assertEquals("T", new TypeParameter().getName());
    assertEquals("T", TypeParameter.of("T").list());
    assertEquals(ElementType.TYPE_PARAMETER, new TypeParameter().getAnnotationsTarget());
    assertEquals("T extends S", TypeParameter.of("T", "S").list());
    Type run = ClassType.type(Runnable.class);
    assertEquals("S extends Runnable", TypeParameter.of("S", run).list());
    // annotated
    TypeParameter parameter = new TypeParameter();
    parameter.addAnnotation(Annotation.annotation(Name.name("A")));
    assertEquals("@A T", parameter.list());
  }

  @Test
  void boundWithTypeVariable() {
    TypeParameter tp = TypeParameter.of("T", "TV");
    assertEquals("T extends TV", tp.list());
    assertEquals("TV", tp.getBoundTypeVariable().get().getIdentifier());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void boundWithClassType() {
    TypeParameter tp = new TypeParameter();
    tp.addBounds(ClassType.type(Number.class), ClassType.type(Cloneable.class));
    tp.addBounds();
    assertEquals("T extends Number & Cloneable", tp.list());
    assertEquals(false, tp.getBoundTypeVariable().isPresent());
    assertEquals(false, tp.getBounds().isEmpty());
    // clears bounds by setting bound type variable
    tp.setBoundTypeVariable("S");
    assertEquals(true, tp.getBoundTypeVariable().isPresent());
    assertEquals(true, tp.getBounds().isEmpty());
  }

  @Test
  void constructorFailsWithIllegalName() {
    expectThrows(AssertionError.class, () -> TypeParameter.of("123"));
  }
}
