package test.integration.composer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.composer.ImportsComposer;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.CompilationUnit;
import com.github.sormuras.beethoven.unit.NormalClassDeclaration;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;
import test.integration.Tests;

class ImportsComposerTests {

  @Test
  void empty() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    ClassDeclaration empty = unit.declareClass("Empty");
    empty.setModifiers(Modifier.PUBLIC);
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void extendsObject() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    NormalClassDeclaration empty = unit.declareClass("ExtendsObject");
    empty.setModifiers(Modifier.PUBLIC);
    empty.setSuperClass(ClassType.OBJECT);
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void extendsTypeInSamePackage() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    NormalClassDeclaration empty = unit.declareClass("ExtendsTypeInSamePackage");
    empty.setModifiers(Modifier.PUBLIC);
    empty.setSuperClass(ClassType.type(Name.name("test", "TypeInSamePackage")));
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertEquals(expected, actual);
  }

  @Test
  void instantAndNumberFields() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(java.time.Instant.class);
    unit.getImportDeclarations().addSingleTypeImport(java.util.Date.class);
    NormalClassDeclaration empty = unit.declareClass("Fields");
    empty.setModifiers(Modifier.PUBLIC);
    empty.declareField(java.time.Instant.class, "timeInstant");
    empty.declareField(ClassType.type(Name.name("test", "Instant")), "testInstant");
    empty.declareField(Number.class, "langNumber");
    empty.declareField(ClassType.type(Name.name("test", "Number")), "testNumber");
    String expected = unit.list();
    new ImportsComposer().apply(unit);
    String actual = unit.list();
    assertNotEquals(expected, actual);
    Tests.assertEquals(getClass(), "instantAndNumberFields", unit);
  }

  @Test
  void unused() {
    CompilationUnit unit = new CompilationUnit();
    unit.setPackageName("test");
    unit.getImportDeclarations().addSingleTypeImport(java.time.Instant.class);
    unit.getImportDeclarations().addSingleTypeImport(java.util.Date.class);
    unit.declareInterface("Unused");
    // first, keep user-declared imports
    new ImportsComposer().setRemoveUnused(false).apply(unit);
    assertEquals(
        "package test;\n"
            + "\n"
            + "import java.time.Instant;\n"
            + "import java.util.Date;\n"
            + "\n"
            + "interface Unused {\n"
            + "}\n",
        unit.list("\n"));
    // now, remove unused imports
    new ImportsComposer().setRemoveUnused(true).apply(unit);
    assertEquals("package test;\n\ninterface Unused {\n}\n", unit.list("\n"));
  }
}
