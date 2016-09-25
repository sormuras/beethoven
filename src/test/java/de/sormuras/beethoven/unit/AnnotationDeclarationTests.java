package de.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import de.sormuras.beethoven.type.ClassType;
import de.sormuras.beethoven.type.Type;
import de.sormuras.beethoven.type.WildcardType;
import java.lang.*;
import java.lang.annotation.ElementType;
import java.util.Formatter;
import java.util.List;
import org.junit.jupiter.api.Test;

class AnnotationDeclarationTests {

  @Test
  void empty() {
    TypeDeclaration declaration = new AnnotationDeclaration();
    declaration.setName("Empty");
    assertEquals("@interface Empty {\n}\n", declaration.list());
    assertTrue(declaration.isEmpty());
    assertFalse(
        new AnnotationDeclaration()
            .declareConstant(Type.type(int.class), "constant", l -> l)
            .getEnclosingDeclaration()
            .isEmpty());
    assertFalse(
        new AnnotationDeclaration()
            .declareElement(Type.type(int.class), "element")
            .getEnclosingDeclaration()
            .isEmpty());
  }

  @Test
  void everything() {
    WildcardType extendsFormatter = WildcardType.extend(Formatter.class);
    AnnotationDeclaration declaration = new AnnotationDeclaration();
    declaration.setName("Everything");
    declaration.declareConstant(Type.type(String.class), "EMPTY_TEXT", "");
    declaration
        .declareConstant(Type.type(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.declareConstant(Type.type(double.class), "E", Name.name(Math.class, "E"));
    declaration.declareElement(Type.type(int.class), "id");
    declaration
        .declareElement(Type.type(String.class), "date", "201608032129")
        .addAnnotation(Deprecated.class);
    declaration.declareElement(
        ClassType.type(java.lang.Class.class).parameterized(i -> List.of(extendsFormatter)),
        "formatterClass");
    Tests.assertEquals(getClass(), "everything", declaration);
    assertFalse(declaration.isEmpty());
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new AnnotationDeclaration().getAnnotationsTarget());
  }
}
