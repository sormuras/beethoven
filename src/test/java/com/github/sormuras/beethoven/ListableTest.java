package com.github.sormuras.beethoven;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListableTest {

  @Test
  void identity() {
    assertTrue(Listable.IDENTITY.isEmpty());
    assertEquals("", Listable.IDENTITY.list());
    assertEquals("", Listable.IDENTITY.list(new Listing()));
    assertEquals("Listable.IDENTITY", Listable.IDENTITY.toString());
  }

  @Test
  void empty() {
    assertTrue(Listable.IDENTITY.isEmpty());
    assertTrue(Listable.NEWLINE.isEmpty());
    assertFalse(Listable.SPACE.isEmpty());
  }

  @Test
  void list() {
    assertEquals("", Listable.IDENTITY.list());
    assertEquals("", Listable.NEWLINE.list()); // initial new line is ignored
    assertEquals(" ", Listable.SPACE.list());
  }

  @Test
  void simple() {
    Listable a = listing -> listing.add('a');
    assertEquals("a", a.list());
    assertFalse(a.isEmpty());
  }

  @Test
  void escapeCharacter() {
    assertEquals("a", Listable.escape('a'));
    assertEquals("b", Listable.escape('b'));
    assertEquals("c", Listable.escape('c'));
    assertEquals("%", Listable.escape('%'));
    // common escapes
    assertEquals("\\b", Listable.escape('\b'));
    assertEquals("\\t", Listable.escape('\t'));
    assertEquals("\\n", Listable.escape('\n'));
    assertEquals("\\f", Listable.escape('\f'));
    assertEquals("\\r", Listable.escape('\r'));
    assertEquals("\"", Listable.escape('"'));
    assertEquals("\\'", Listable.escape('\''));
    assertEquals("\\\\", Listable.escape('\\'));
    // octal escapes
    assertEquals("\\u0000", Listable.escape('\0'));
    assertEquals("\\u0007", Listable.escape('\7'));
    assertEquals("?", Listable.escape('\77'));
    assertEquals("\\u007f", Listable.escape('\177'));
    assertEquals("¿", Listable.escape('\277'));
    assertEquals("ÿ", Listable.escape('\377'));
    // unicode escapes
    assertEquals("\\u0000", Listable.escape('\u0000'));
    assertEquals("\\u0001", Listable.escape('\u0001'));
    assertEquals("\\u0002", Listable.escape('\u0002'));
    assertEquals("€", Listable.escape('\u20AC'));
    assertEquals("☃", Listable.escape('\u2603'));
    assertEquals("♠", Listable.escape('\u2660'));
    assertEquals("♣", Listable.escape('\u2663'));
    assertEquals("♥", Listable.escape('\u2665'));
    assertEquals("♦", Listable.escape('\u2666'));
    assertEquals("✵", Listable.escape('\u2735'));
    assertEquals("✺", Listable.escape('\u273A'));
    assertEquals("／", Listable.escape('\uFF0F'));
  }

  @Test
  void escapeString() {
    assertNull(null, Listable.escape(null));
    escapeString("abc");
    escapeString("♦♥♠♣");
    escapeString("€\\t@\\t$", "€\t@\t$");
    escapeString("abc();\\ndef();", "abc();\ndef();");
    escapeString("This is \\\"quoted\\\"'!'", "This is \"quoted\"'!'");
    escapeString("e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0");
  }

  private void escapeString(String string) {
    escapeString(string, string);
  }

  private void escapeString(String expected, String value) {
    assertEquals('"' + expected + '"', Listable.escape(value));
  }
}
