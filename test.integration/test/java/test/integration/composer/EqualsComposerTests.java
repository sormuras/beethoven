package test.integration.composer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.sormuras.beethoven.composer.EqualsComposer;
import test.integration.Tests;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class EqualsComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new EqualsComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);

    Object a = unit.compile(Object.class);
    Object b = unit.compile(Object.class);
    assertSame(a, a);
    assertEquals(a, a);
    assertNotSame(a, b);
    assertNotEquals(a, b);
  }
}
