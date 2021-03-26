package test.integration.composer;

import com.github.sormuras.beethoven.composer.HashCodeComposer;
import test.integration.Tests;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class HashCodeComposerTests {

  @Test
  void empty() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    new HashCodeComposer().apply(empty);

    Tests.assertEquals(getClass(), "empty", unit);
    unit.compile();
  }

  @Test
  void single() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration single = unit.declareClass("Single");
    single.setModifiers(Modifier.PUBLIC);
    single.declareField(String.class, "text");
    new HashCodeComposer().apply(single);

    Tests.assertEquals(getClass(), "single", unit);
    unit.compile();
  }

  @Test
  void xyz() throws Exception {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(Objects.class);
    ClassDeclaration xyz = unit.declareClass("Xyz");
    xyz.setModifiers(Modifier.PUBLIC);
    xyz.declareField(String.class, "x");
    xyz.declareField(boolean.class, "y");
    xyz.declareField(Thread.State.class, "z");
    new HashCodeComposer().apply(xyz);

    Tests.assertEquals(getClass(), "xyz", unit);
    unit.compile();
  }
}
