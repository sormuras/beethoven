package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.unit.AnnotationDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import com.github.sormuras.beethoven.unit.DeclarationContainer;
import com.github.sormuras.beethoven.unit.EnumDeclaration;
import com.github.sormuras.beethoven.unit.InterfaceDeclaration;
import com.github.sormuras.beethoven.unit.NormalClassDeclaration;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class DeclarationContainerTests {

  @Test
  void compilationUnit() {
    test(CompilationUnit::new);
  }

  @Test
  void annotationDeclaration() {
    test(AnnotationDeclaration::new);
  }

  @Test
  void classDeclaration() {
    test(NormalClassDeclaration::new);
  }

  @Test
  void enumDeclaration() {
    test(EnumDeclaration::new);
  }

  @Test
  void interfaceDeclaration() {
    test(InterfaceDeclaration::new);
  }

  private void test(Supplier<DeclarationContainer> supplier) {
    illegalJavaNameFails(supplier.get());
    duplicateSiblingNameFails(supplier.get());
    duplicateNestedNameFails(supplier.get());
  }

  private void duplicateNestedNameFails(DeclarationContainer container) {
    DeclarationContainer parent = container.declareClass("A");
    Exception e = assertThrows(Exception.class, () -> parent.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
    DeclarationContainer child = parent.declareClass("B");
    e = assertThrows(Exception.class, () -> child.declareClass("A"));
    assertTrue(e.getMessage().contains("nested"));
  }

  private void duplicateSiblingNameFails(DeclarationContainer container) {
    container.declareClass("A");
    Exception e = assertThrows(Exception.class, () -> container.declareClass("A"));
    assertTrue(e.getMessage().contains("duplicate"));
  }

  private void illegalJavaNameFails(DeclarationContainer container) {
    Exception e = assertThrows(Exception.class, () -> container.declareClass("123"));
    assertTrue(e.getMessage().contains("valid"));
  }
}
