package test.integration.composer;

import com.github.sormuras.beethoven.composer.ToStringComposer;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.integration.Tests;

class ToStringComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new ToStringComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("test"));
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("Empty"));
    Assertions.assertTrue(unit.compile(Object.class).toString().contains("@"));
  }
}
