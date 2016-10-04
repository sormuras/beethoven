package de.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sormuras.beethoven.Annotation;
import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.Tests;
import java.lang.annotation.ElementType;
import java.net.URI;
import org.junit.jupiter.api.Test;

class PackageDeclarationTests {

  @Test
  void empty() {
    assertTrue(new PackageDeclaration().isEmpty());
  }

  @Test
  void list() {
    // unnamed
    assertEquals("", new PackageDeclaration().list());
    // simple
    assertEquals("package abc;\n", PackageDeclaration.of("abc").list());
    assertEquals("package abc.xyz;\n", PackageDeclaration.of("abc.xyz").list());
    // with annotation(s)
    PackageDeclaration annotated = PackageDeclaration.of("abc.xyz");
    annotated.addAnnotation(Annotation.annotation(Name.name("abc", "PackageAnnotation")));
    Tests.assertEquals(getClass(), "annotated", annotated);
    // with (hand-crafted) Javadoc
    Listing listing = new Listing();
    listing.add("/**").newline();
    listing.add(" * Testing Javadoc on PackageDeclaration element.").newline();
    listing.add(" *").newline();
    listing.add(" * @since 1.0").newline();
    listing.add(" */").newline();
    listing.add(annotated);
    Tests.assertEquals(getClass(), "javadoc", listing);
  }

  @Test
  void separator() {
    assertEquals(Listable.NEWLINE, new PackageDeclaration().getAnnotationsSeparator());
  }

  @Test
  void target() {
    assertEquals(ElementType.PACKAGE, PackageDeclaration.of("t").getAnnotationsTarget());
  }

  @Test
  void unnamedAsStringFails() {
    Error error = assertThrows(AssertionError.class, () -> PackageDeclaration.of(""));
    assertTrue(error.getMessage().contains("blank"));
  }

  @Test
  void resolve() {
    assertEquals("Tag", new PackageDeclaration().resolve("Tag"));
    assertEquals("abc.xyz.Tag", PackageDeclaration.of("abc.xyz").resolve("Tag"));
  }

  @Test
  void uri() {
    assertEquals(URI.create("Tag"), new PackageDeclaration().toUri("Tag"));
    assertEquals(URI.create("abc/xyz/Tag"), PackageDeclaration.of("abc.xyz").toUri("Tag"));
  }
}
