package com.github.sormuras.beethoven;

import com.github.sormuras.beethoven.type.ArrayType;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.PrimitiveType;
import com.github.sormuras.beethoven.type.TypeVariable;
import com.github.sormuras.beethoven.type.VoidType;
import com.github.sormuras.beethoven.type.WildcardType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class AnnotatedTest {

  //  @Test
  //  void annotatedAnnotationDeclaration() {
  //    test(AnnotationDeclaration::new);
  //  }

  //  @Test
  //  void annotatedAnnotationElement() {
  //    test(AnnotationElement::new);
  //  }

  @Test
  void annotatedArrayDimension() {
    test(ArrayType.Dimension::new);
  }

  @Test
  void annotatedArrayType() {
    test(() -> ArrayType.array(int.class, 1));
  }

  @Test
  void annotatedClassName() {
    test(ClassType.SimpleName::new);
  }

  @Test
  void annotatedClassType() {
    test(() -> ClassType.of("pack.age", "ClassType"));
  }

  //  @Test
  //  void annotatedConstantDeclaration() {
  //    test(ConstantDeclaration::new);
  //  }

  //  @Test
  //  void annotatedEnumConstant() {
  //    test(EnumConstant::new);
  //  }

  //  @Test
  //  void annotatedEnumDeclaration() {
  //    test(EnumDeclaration::new);
  //  }

  //  @Test
  //  void annotatedFieldDeclaration() {
  //    test(FieldDeclaration::new);
  //  }

  //  @Test
  //  void annotatedInterfaceDeclaration() {
  //    test(InterfaceDeclaration::new);
  //  }

  //  @Test
  //  void annotatedMethodDeclaration() {
  //    test(MethodDeclaration::new);
  //  }

  //  @Test
  //  void annotatedMethodParameter() {
  //    test(MethodParameter::new);
  //  }

  //  @Test
  //  void annotatedNormalClassDeclaration() {
  //    test(NormalClassDeclaration::new);
  //  }

  //  @Test
  //  void annotatedPackageDeclaration() {
  //    test(PackageDeclaration::new);
  //  }

  @Test
  void annotatedPrimitiveTypes() {
    test(PrimitiveType.BooleanType::new);
    test(PrimitiveType.ByteType::new);
    test(PrimitiveType.CharType::new);
    test(PrimitiveType.DoubleType::new);
    test(PrimitiveType.FloatType::new);
    test(PrimitiveType.IntType::new);
    test(PrimitiveType.LongType::new);
    test(PrimitiveType.ShortType::new);
  }

  //  @Test
  //  void annotatedTypeParameter() {
  //    test(TypeParameter::new);
  //  }

  @Test
  void annotatedTypeVariable() {
    test(TypeVariable::new);
  }

  @Test
  void annotatedVoidType() {
    testInitial(new VoidType());
  }

  @Test
  void annotatedWildcardType() {
    test(WildcardType::new);
  }

  private void test(Supplier<? extends Annotated> supplier) {
    testInitial(supplier.get());
    testMutable(supplier.get());
    // Tests.assertSerializable(supplier.get());
  }

  private void testInitial(Annotated a) {
    assertFalse(a.isAnnotated());
    assertTrue(a.getAnnotations().isEmpty());
    assertNotNull(a.toString());
  }

  private void testMutable(Annotated a) {
    // first non-readonly access initializes annotation collection
    assertFalse(a.isAnnotated());
    assertTrue(Collections.EMPTY_LIST != a.getAnnotations());
    assertFalse(a.isAnnotated());
    a.addAnnotation(U.class);
    assertTrue(a.isAnnotated());
    assertEquals(1, a.getAnnotations().size());
    assertNotNull(a.toString());
    a.getAnnotations().clear();
    assertFalse(a.isAnnotated());
    a.addAnnotation(U.class);
    a.addAnnotation(Name.name(V.class));
    assertTrue(a.isAnnotated());
    assertEquals(2, a.getAnnotations().size());
    assertThrows(AssertionError.class, () -> a.addAnnotation(null));
  }
}
