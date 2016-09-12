package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.Tests;
import com.github.sormuras.beethoven.type.Type;
import java.lang.annotation.ElementType;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class InterfaceDeclarationTest {

  @Test
  void empty() {
    TypeDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Empty");
    assertEquals("interface Empty {\n}\n", declaration.list());
  }

  @Test
  void everything() {
    InterfaceDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Everything");
    declaration.addTypeParameter(new TypeParameter());
    declaration.addInterface(Type.type(Runnable.class));
    declaration.declareConstant(Type.type(String.class), "EMPTY_TEXT", "");
    declaration
        .declareConstant(Type.type(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.declareConstant(Type.type(double.class), "E", Name.name(Math.class, "E"));
    declaration.declareMethod(BigInteger.class, "id");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new InterfaceDeclaration().getAnnotationsTarget());
  }
}
