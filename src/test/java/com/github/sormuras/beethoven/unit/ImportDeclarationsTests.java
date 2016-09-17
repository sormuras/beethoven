package com.github.sormuras.beethoven.unit;

import static com.github.sormuras.beethoven.Name.name;
import static javax.lang.model.element.Modifier.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.Style;
import com.github.sormuras.beethoven.Tests;
import java.lang.annotation.ElementType;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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
    imports.addSingleStaticImport(name(STATIC));
    imports.addSingleStaticImport(name("org", "junit", "Assert", "assertEquals"));
    imports.addSingleStaticImport(name("org", "junit", "Assert", "assertFalse"));
    imports.addSingleStaticImport(name("org", "junit", "Assert", "assertTrue"));
    imports.addStaticImportOnDemand(name(Objects.class));
    imports.addSingleTypeImport(name(ElementType.class));
    imports.addSingleTypeImport(name(Member.class));
    imports.addTypeImportOnDemand(name("java.util"));

    Tests.assertEquals(getClass(), "imports", imports);

    assertEquals(Style.LAST, imports.style(name(STATIC)));
    assertEquals(Style.LAST, imports.style(name(Objects.class, "requireNonNull")));
    assertEquals(Style.LAST, imports.style(name(Member.class)));
    assertEquals(Style.LAST, imports.style(name("org", "junit", "Assert", "assertTrue")));

    // java.util.*
    assertEquals(Style.SIMPLE, imports.style(name(Set.class)));
    assertEquals(Style.SIMPLE, imports.style(name(Map.class)));
    assertEquals(Style.SIMPLE, imports.style(name(Map.Entry.class)));

    // not imported, so expect canonical style
    assertEquals(Style.CANONICAL, imports.style(name(Test.class)));
    assertEquals(Style.CANONICAL, imports.style(name("com", "what", "Ever")));

    Listing listing = new Listing(imports::style);
    listing.add(name(STATIC)).newline();
    listing.add(name(Objects.class, "requireNonNull")).newline();
    listing.add(name(Test.class)).newline();
    assertEquals("STATIC\nrequireNonNull\norg.junit.jupiter.api.Test\n", listing.toString());
  }

  @Test
  void singleStaticImport() throws Exception {
    Member micros = TimeUnit.class.getField("MICROSECONDS");
    Name parameter = name("java.lang.annotation", "ElementType", "PARAMETER");
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
                .addSingleTypeImport(name("java.io", "File")),
        "import java.io.File;",
        "import java.util.Map.Entry;",
        "import java.util.Set;");
  }

  @Test
  void staticImportOnDemand() {
    check(
        imports -> imports.addStaticImportOnDemand(name("java.lang", "Thread", "State")),
        "import static java.lang.Thread.State.*;");
  }

  @Test
  void typeImportOnDemand() {
    check(imports -> imports.addTypeImportOnDemand(name("java.util")), "import java.util.*;");
  }
}
