package com.github.sormuras.beethoven;

import java.io.PrintStream;
import java.util.function.UnaryOperator;

/**
 * The functional {@link Listable} interface should be implemented by any class whose instances are
 * intended to be applied to a {@link Listing} instance.
 *
 * <p>The class must define a method called {@link #apply(Listing)}. This interface is designed to
 * provide a common protocol for objects that wish to contribute source code snippets.
 *
 * @see Listing
 * @see #IDENTITY
 * @see #NEWLINE
 * @see #SPACE
 */
@FunctionalInterface
public interface Listable extends UnaryOperator<Listing>, Comparable<Listable> {

  class Identity implements Listable {

    @Override
    public Listing apply(Listing listing) {
      return listing;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public String list() {
      return "";
    }

    @Override
    public String toString() {
      return "Listable.IDENTITY";
    }
  }

  /**
   * Escape Sequences for Character and String Literals.
   *
   * <p>The character and string escape sequences allow for the representation of some non-graphic
   * characters without using Unicode escapes, as well as the single quote, double quote, and
   * backslash characters, in character literals (§3.10.4) and string literals (§3.10.5).
   *
   * @param character Character to escape
   * @return Escaped character
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.6">JLS
   *     3.10.6</a>
   */
  static String escape(char character) {
    switch (character) {
      case '\b': /* \u0008: backspace (BS) */
        return "\\b";
      case '\t': /* \u0009: horizontal tab (HT) */
        return "\\t";
      case '\n': /* \u000a: linefeed (LF) */
        return "\\n";
      case '\f': /* \u000c: form feed (FF) */
        return "\\f";
      case '\r': /* \u000d: carriage return (CR) */
        return "\\r";
      case '\"': /* \u0022: double quote (") */
        return "\"";
      case '\'': /* \u0027: single quote (') */
        return "\\'";
      case '\\': /* \u005c: backslash (\) */
        return "\\\\";
      default:
        return Character.isISOControl(character)
            ? String.format("\\u%04x", (int) character)
            : Character.toString(character);
    }
  }

  /** Returns the string literal representing {@code value}, including wrapping double quotes. */
  static String escape(String value) {
    if (value == null) {
      return "null";
    }
    StringBuilder result = new StringBuilder(value.length() + 2);
    result.append('"');
    for (int i = 0; i < value.length(); i++) {
      char character = value.charAt(i);
      // trivial case: single quote must not be escaped
      if (character == '\'') {
        result.append("'");
        continue;
      }
      // trivial case: double quotes must be escaped
      if (character == '\"') {
        result.append("\\\"");
        continue;
      }
      // default case: just let character escape method do its work
      result.append(escape(character));
    }
    result.append('"');
    return result.toString();
  }

  Listable IDENTITY = new Identity();

  Listable NEWLINE = Listing::newline;

  Listable SPACE = listing -> listing.add(' ');

  @Override
  Listing apply(Listing listing);

  @Override
  default int compareTo(Listable other) {
    return comparisonKey().compareTo(other.comparisonKey());
  }

  default String comparisonKey() {
    return getClass().getSimpleName().toLowerCase() + "#" + toString().toLowerCase();
  }

  default boolean isEmpty() {
    return list().isEmpty();
  }

  default String list() {
    return list(new Listing());
  }

  default void list(PrintStream stream) {
    stream.print(list());
  }

  default String list(String lineSeparator) {
    return list(new Listing(lineSeparator));
  }

  default String list(Listing listing) {
    return listing.add(this).toString();
  }
}
