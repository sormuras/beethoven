package com.github.sormuras.beethoven;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.Locale.GERMAN;
import static java.lang.Math.PI;

class ListingTest {

  static class Face {
    @SuppressWarnings("unused")
    public Optional<?> empty() {
      return Optional.empty();
    }

    @SuppressWarnings("unused")
    public Optional<Listable> smile() {
      return Optional.of(listing -> listing.add("(:"));
    }
  }

  static class Omitting extends Listing {
    @Override
    public boolean isOmitJavaLangPackage() {
      return true;
    }
  }

  static class Importing extends Omitting {
    @Override
    public Predicate<Name> getImportNamePredicate() {
      return name -> true;
    }
  }

  @Test
  void defaults() {
    Listing empty = new Listing();
    assertTrue(empty.getCurrentLine().length() == 0);
    assertTrue(empty.getCollectedLines().isEmpty());
    assertEquals("  ", empty.getIndentationString());
    assertEquals("\n", empty.getLineSeparator());
    assertEquals("", empty.toString());
  }

  @Test
  void addChar() {
    assertEquals("a", new Listing().add('a').toString());
  }

  @Test
  void addCharSequence() {
    assertEquals("abc", new Listing().add("abc").toString());
  }

  @Test
  void addFormattedString() {
    assertEquals("abc", new Listing().fmt("a%sc", "b").toString());
    assertEquals("abc", new Listing().fmt("abc", new Object[0]).toString());
    assertEquals("3,14159", new Listing().fmt(GERMAN, "%.5f", PI).toString());
    assertEquals("3,14159", new Listing().fmt(GERMAN, "3,14159").toString());
  }

  @Test
  void addListable() {
    assertEquals("", new Listing().add(Listable.IDENTITY).toString());
    assertEquals("", new Listing().add((Listable) null).toString());
  }

  @Test
  void addName() {
    Name object = Name.name(Object.class);
    Name pi = Name.name(Math.class, "PI");
    Name map = Name.name(Map.class);
    Name entry = Name.name(Map.Entry.class);
    assertEquals("java.lang.Object", new Listing().add(object).toString());
    assertEquals("Object", new Omitting().add(object).toString());
    assertEquals("Math.PI", new Omitting().add(pi).toString());
    assertEquals("java.util.Map", new Omitting().add(map).toString());
    assertEquals("java.util.Map.Entry", new Omitting().add(entry).toString());
    assertEquals("Object", new Importing().add(object).toString());
    assertEquals("Map", new Importing().add(map).toString());
    assertEquals("Entry", new Importing().add(entry).toString());
  }

  @Test
  void addWithPlaceholder() {
    String expected = "java.lang.System.out.println(\"123\"); // 0 String";
    String source = "{N}.out.println({S}); // {hashCode} {getClass.getSimpleName.toString}";
    assertEquals(expected, new Listing().add(source, System.class, "123", "", "$").toString());
    assertEquals(" ", new Listing().add("{L}", Listable.SPACE).toString());
    assertEquals("x.Y", new Listing().add("{enclosing}", Name.name("x", "Y", "Z")).toString());
    assertEquals("(:", new Listing().add("{smile}", new Face()).toString());
    assertEquals("{{}}", new Listing().add("{{empty}{}}", new Face()).toString());
    assertThrows(Exception.class, () -> new Listing().add("{xxx}", ""));
    assertThrows(Exception.class, () -> new Listing().add("{toString.toString.xxx}", ""));
  }

  @Test
  void indent() {
    Listing listing = new Listing();
    listing.add("BEGIN").newline();
    listing.indent(1).add("writeln('Hello, world.')").newline().indent(-1);
    listing.add("END.").newline();
    assertEquals(0, listing.getCurrentIndentationDepth());
    assertEquals("BEGIN\n  writeln('Hello, world.')\nEND.\n", listing.toString());
    listing.indent(-100);
    assertEquals(0, listing.getCurrentIndentationDepth());
  }

  @Test
  void newlineProducesOnlyOneSingleBlankLine() {
    Listing listing = new Listing();
    assertEquals(0, listing.getCurrentLine().length());
    listing.newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    listing.add("BEGIN").newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    listing.add("END.").newline().newline().newline().newline().newline();
    assertEquals(0, listing.getCurrentLine().length());
    assertEquals("BEGIN\n\nEND.\n\n", listing.toString());
    assertEquals(0, listing.getCurrentLine().length());
  }

  @Test
  void script() throws Exception {
    Listing listing = new Listing();
    listing.add("var fun = function(name) { return \"Hi \" + name; };").newline();
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    engine.eval(listing.toString());
    Invocable invocable = (Invocable) engine;
    assertEquals("Hi Bob", invocable.invokeFunction("fun", "Bob"));
  }
}
