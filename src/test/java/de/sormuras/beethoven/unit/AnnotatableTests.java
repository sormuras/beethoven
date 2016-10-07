package de.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Listable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class AnnotatableTests {

  @Test
  void annotatableExtendsListable() {
    assertTrue(Listable.class.isAssignableFrom(Annotatable.class));
  }

  private void annotatable(Annotatable annotatable) {
    assertFalse(annotatable.isAnnotated());
    annotatable.addAnnotation(Deprecated.class);
    assertTrue(annotatable.isAnnotated());
  }

  @TestFactory
  Stream<DynamicTest> annotatables() {
    Function<Annotatable, String> name = a -> "annotatable(" + a.getClass().getSimpleName() + ")";
    List<Annotatable> annotatables =
        List.of( //
            new AnnotationDeclaration(),
            new AnnotationElement(),
            new ConstantDeclaration(),
            new EnumConstant(),
            new EnumDeclaration(),
            new FieldDeclaration(),
            new InterfaceDeclaration(),
            new MethodDeclaration(),
            new MethodParameter(),
            new NormalClassDeclaration(),
            new PackageDeclaration(),
            new TypeParameter()
            //
            );
    return DynamicTest.stream(annotatables.iterator(), name, this::annotatable);
  }
}
