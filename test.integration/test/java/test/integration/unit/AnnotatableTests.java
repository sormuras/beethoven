package test.integration.unit;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.unit.Annotatable;
import com.github.sormuras.beethoven.unit.AnnotationDeclaration;
import com.github.sormuras.beethoven.unit.AnnotationElement;
import com.github.sormuras.beethoven.unit.ConstantDeclaration;
import com.github.sormuras.beethoven.unit.EnumConstant;
import com.github.sormuras.beethoven.unit.EnumDeclaration;
import com.github.sormuras.beethoven.unit.FieldDeclaration;
import com.github.sormuras.beethoven.unit.InterfaceDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.MethodParameter;
import com.github.sormuras.beethoven.unit.ModuleDeclaration;
import com.github.sormuras.beethoven.unit.NormalClassDeclaration;
import com.github.sormuras.beethoven.unit.PackageDeclaration;
import com.github.sormuras.beethoven.unit.TypeParameter;
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
    assertFalse(annotatable.isTagged());
    assertFalse(annotatable.getTag("1").isPresent());
    annotatable.getTags();
    assertFalse(annotatable.isTagged());
    annotatable.getTags().put("1", "2");
    assertTrue(annotatable.isTagged());
    assertTrue(annotatable.getTag("1").isPresent());
  }

  @TestFactory
  Stream<DynamicTest> annotatables() {
    Function<Annotatable, String> name = a -> "annotatable(" + a.getClass().getSimpleName() + ")";
    List<Annotatable> annotatables =
        asList( //
            new AnnotationDeclaration(),
            new AnnotationElement(),
            new ConstantDeclaration(),
            new EnumConstant(),
            new EnumDeclaration(),
            new FieldDeclaration(),
            new InterfaceDeclaration(),
            new MethodDeclaration(),
            new MethodParameter(),
            new ModuleDeclaration(),
            new NormalClassDeclaration(),
            new PackageDeclaration(),
            new TypeParameter()
            //
            );
    return DynamicTest.stream(annotatables.iterator(), name, this::annotatable);
  }
}
