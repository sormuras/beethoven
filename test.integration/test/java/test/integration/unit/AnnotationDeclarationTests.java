package test.integration.unit;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.unit.AnnotationDeclaration;
import com.github.sormuras.beethoven.unit.TypeDeclaration;
import test.integration.Tests;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.WildcardType;
import java.lang.annotation.ElementType;
import java.util.Formatter;
import org.junit.jupiter.api.Test;

class AnnotationDeclarationTests {

  @Test
  void empty() {
    TypeDeclaration declaration = new AnnotationDeclaration();
    declaration.setName("Empty");
    assertEquals("@interface Empty {\n}\n", declaration.list("\n"));
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
        ClassType.type(java.lang.Class.class).parameterized(i -> singletonList(extendsFormatter)),
        "formatterClass");
    Tests.assertEquals(getClass(), "everything", declaration);
    assertFalse(declaration.isEmpty());
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new AnnotationDeclaration().getAnnotationsTarget());
  }
}
