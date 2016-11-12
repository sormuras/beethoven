/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import static de.sormuras.beethoven.Style.LAST;
import static de.sormuras.beethoven.Style.SIMPLE;
import static java.lang.Math.PI;
import static java.util.Locale.GERMAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.jupiter.api.Test;

class ListingTests {

  static class Face {
    public Optional<?> empty() {
      return Optional.empty();
    }

    public Optional<Listable> smile() {
      return Optional.of(listing -> listing.add("(:"));
    }
  }

  @Test
  void defaults() {
    Listing empty = new Listing();
    assertTrue(empty.getCurrentLine().length() == 0);
    assertTrue(empty.getCollectedLines().isEmpty());
    assertEquals("  ", empty.getIndentationString());
    assertEquals(System.lineSeparator(), empty.getLineSeparator());
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
  void addListOfListables() {
    List<Listable> list = new ArrayList<>();
    assertEquals("", new Listing().addAll(list).toString());
    list.add(Annotation.value('a'));
    assertEquals("'a'", new Listing().addAll(list).toString());
    list.add(Annotation.value('z'));
    assertEquals("'a'\n'z'", new Listing("\n").addAll(list).toString());
    assertEquals("'a'-'z'", new Listing().addAll(list, "-").toString());
  }

  @Test
  void addName() {
    Name object = Name.name(Object.class);
    Name pi = Name.name(Math.class, "PI");
    Name map = Name.name(Map.class);
    Name entry = Name.name(Map.Entry.class);
    assertEquals("Object", new Listing().add(object).toString());
    assertEquals("Math.PI", new Listing().add(pi).toString());
    assertEquals("java.util.Map", new Listing().add(map).toString());
    assertEquals("java.util.Map.Entry", new Listing().add(entry).toString());
    assertEquals("Object", new Listing(SIMPLE).add(object).toString());
    assertEquals("Map", new Listing(SIMPLE).add(map).toString());
    assertEquals("Map.Entry", new Listing(SIMPLE).add(entry).toString());
    assertEquals("Object", new Listing(LAST).add(object).toString());
    assertEquals("Map", new Listing(LAST).add(map).toString());
    assertEquals("Entry", new Listing(LAST).add(entry).toString());
    assertThrows(AssertionError.class, () -> new Listing(" ", "\n", name -> null).add(pi));
  }

  @Test
  void eval() {
    String expected = "System.out.print(\"123\"); // 0 String";
    String source = "{{N}}.{{S}}.print({{E}}); // {{hashCode}} {{class.simpleName.toString}}";
    String actual = new Listing().eval(source, System.class, "out", "123", "", "*").toString();
    assertEquals(expected, actual);
    assertEquals(" ", new Listing().eval("{{L}}", Listable.SPACE).toString());
    assertEquals(" ", new Listing().eval("{{L // single space char}}", Listable.SPACE).toString());
    assertEquals("x.Y", new Listing().eval("{{enclosing}}", Name.name("x", "Y", "Z")).toString());
    assertEquals("x.Y", new Listing().eval("{{enclosing//}}", Name.name("x", "Y", "Z")).toString());
    assertEquals("(:", new Listing().eval("{{smile}}", new Face()).toString());
    assertEquals("{{}}", new Listing().eval("{{{empty}}{}}", new Face()).toString());
    assertThrows(Exception.class, () -> new Listing().eval("{{xxx}}"));
    assertThrows(Exception.class, () -> new Listing().eval("{{toString.toString.xxx}}"));
  }

  @Test
  void evalMultiline() {
    String[] lines = {
      "try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();",
      "    ObjectOutputStream stream = new ObjectOutputStream()) {",
      "  stream.writeObject(object);",
      "  return stashByteArray(target, bytes.toByteArray());",
      "} catch (Exception e) {",
      "  throw new RuntimeException(\"Writing object failed!\", e);",
      "}",
      ""
    };
    Listing listing = new Listing(Style.SIMPLE);
    Name bos = Name.name(ByteArrayOutputStream.class);
    Name oos = Name.name(ObjectOutputStream.class);
    listing.eval("try ({{N}} bytes = new {{N}}(){{;}}", bos, bos);
    listing.eval("{{>>}}{{N}} stream = new {{N}}()) {", oos, oos).newline();
    listing.eval("{{<}}stream.writeObject({{S}}){{;}}", "object");
    listing.eval("return {{S}}({{S}}, bytes.toByteArray()){{;}}", "stashByteArray", "target");
    listing.eval("{{<}}} catch ({{N}} e) {", Exception.class).newline();
    listing.eval("{{>}}throw new RuntimeException({{E}}, e){{;}}", "Writing object failed!");
    listing.eval("{{<}}}").newline();
    String expected = String.join(listing.getLineSeparator(), lines);
    assertEquals(expected, listing.toString());
  }

  @Test
  void indent() {
    Listing listing = new Listing("\n");
    listing.add("BEGIN").newline();
    listing.indent(1).add("writeln('Hello, world.')").newline().indent(-1);
    listing.add("END.").newline();
    assertEquals(0, listing.getCurrentIndentationDepth());
    assertEquals("BEGIN\n  writeln('Hello, world.')\nEND.\n", listing.toString());
    listing.indent(-100);
    assertEquals(0, listing.getCurrentIndentationDepth());
  }

  @Test
  void indentLastLineIfNotEmpty() {
    Listing listing = new Listing("\n");
    assertEquals("", listing.toString());
    assertEquals("", listing.indent(3).toString());
    assertEquals("      1", listing.add('1').toString());
    assertEquals("      1\n      2", listing.newline().add('2').toString());
    assertEquals("      1\n      2\n", listing.newline().toString());
  }

  @Test
  void newlineProducesOnlyOneSingleBlankLine() {
    Listing listing = new Listing("\n");
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
    listing.eval("var hi = function(name) { return {{E}} + name; };", "Ho ").newline();
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    engine.eval(listing.toString());
    Invocable invocable = (Invocable) engine;
    assertEquals("Ho Bob", invocable.invokeFunction("hi", "Bob"));
  }
}
