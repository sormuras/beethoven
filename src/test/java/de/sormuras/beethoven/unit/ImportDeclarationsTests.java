package de.sormuras.beethoven.unit;

import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Style;
import de.sormuras.beethoven.Tests;
import java.lang.annotation.ElementType;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImportDeclarationsTests {

  private static void check(Consumer<ImportDeclarations> consumer, String... expected) {
    ImportDeclarations declarations = new ImportDeclarations();
    assertTrue(declarations.isEmpty());
    consumer.accept(declarations);
    Listing listing = new Listing();
    listing.add(declarations);
    assertEquals(Arrays.asList(expected), new ArrayList<>(listing.getCollectedLines()));
    if (expected.length > 0) {
      assertFalse(declarations.isEmpty());
    }
  }

  private static void empty(ImportDeclarations declarations) {}

  @Test
  void emptyIsEmpty() {
    check(ImportDeclarationsTests::empty);
    assertTrue(new ImportDeclarations().isEmpty());
  }

  @Test
  void imports() throws Exception {
    ImportDeclarations imports = new ImportDeclarations();
    imports.addSingleStaticImport(Name.name(STATIC));
    imports.addSingleStaticImport(Name.name("org", "junit", "Assert", "assertEquals"));
    imports.addSingleStaticImport(Name.name("org", "junit", "Assert", "assertFalse"));
    imports.addSingleStaticImport(Name.name("org", "junit", "Assert", "assertTrue"));
    imports.addStaticImportOnDemand(Name.name(Objects.class));
    imports.addSingleTypeImport(Name.name(ElementType.class));
    imports.addSingleTypeImport(Name.name(Member.class));
    imports.addTypeImportOnDemand(Name.name("java.util"));

    Tests.assertEquals(getClass(), "imports", imports);

    Assertions.assertEquals(Style.LAST, imports.style(Name.name(STATIC)));
    Assertions.assertEquals(Style.LAST, imports.style(Name.name(Objects.class, "requireNonNull")));
    Assertions.assertEquals(Style.LAST, imports.style(Name.name(Member.class)));
    Assertions.assertEquals(
        Style.LAST, imports.style(Name.name("org", "junit", "Assert", "assertTrue")));

    // java.util.*
    Assertions.assertEquals(Style.SIMPLE, imports.style(Name.name(Set.class)));
    Assertions.assertEquals(Style.SIMPLE, imports.style(Name.name(Map.class)));
    Assertions.assertEquals(Style.SIMPLE, imports.style(Name.name(Map.Entry.class)));

    // not imported, so expect canonical style
    Assertions.assertEquals(Style.CANONICAL, imports.style(Name.name(Test.class)));
    Assertions.assertEquals(Style.CANONICAL, imports.style(Name.name("com", "what", "Ever")));

    Listing listing = new Listing(23, "  ", "\n", imports::style);
    listing.add(Name.name(STATIC)).newline();
    listing.add(Name.name(Objects.class, "requireNonNull")).newline();
    listing.add(Name.name(Test.class)).newline();
    assertEquals("STATIC\nrequireNonNull\norg.junit.jupiter.api.Test\n", listing.toString());
  }

  @Test
  void singleStaticImport() throws Exception {
    Member micros = TimeUnit.class.getField("MICROSECONDS");
    Name parameter = Name.name("java.lang.annotation", "ElementType", "PARAMETER");
    check(
        imports ->
            imports
                .addSingleStaticImport(Thread.State.NEW)
                .addSingleStaticImport(micros)
                .addSingleStaticImport(parameter),
        "import static java.lang.Thread.State.NEW;",
        "import static java.lang.annotation.ElementType.PARAMETER;",
        "import static java.util.concurrent.TimeUnit.MICROSECONDS;");
  }

  @Test
  void singleTypeImport() {
    check(i -> i.addSingleTypeImport(Set.class), "import java.util.Set;");
    check(
        imports ->
            imports
                .addSingleTypeImport(Set.class)
                .addSingleTypeImport(Map.Entry.class)
                .addSingleTypeImport(Name.name("java.io", "File")),
        "import java.io.File;",
        "import java.util.Map.Entry;",
        "import java.util.Set;");
  }

  @Test
  void staticImportOnDemand() {
    check(
        imports -> imports.addStaticImportOnDemand(Name.name("java.lang", "Thread", "State")),
        "import static java.lang.Thread.State.*;");
  }

  @Test
  void typeImportOnDemand() {
    check(imports -> imports.addTypeImportOnDemand(Name.name("java.util")), "import java.util.*;");
  }
}
