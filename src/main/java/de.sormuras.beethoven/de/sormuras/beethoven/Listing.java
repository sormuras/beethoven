/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import de.sormuras.beethoven.script.Script;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Listing {

  private final Deque<String> collectedLines = new ArrayDeque<>(512);
  private final Set<Name> collectedNames = new TreeSet<>();
  private int currentIndentationDepth = 0;
  private final StringBuilder currentLine = new StringBuilder(256);
  private final String[] indentationLookupTable = new String[23];
  private final String lineSeparator;
  private final Styling styling;

  public Listing() {
    this(System.lineSeparator());
  }

  public Listing(String lineSeparator) {
    this("  ", lineSeparator, Style::auto);
  }

  public Listing(Style style) {
    this(style.styling());
  }

  public Listing(Styling styling) {
    this("  ", System.lineSeparator(), styling);
  }

  public Listing(String indent, String lineSeparator, Styling styling) {
    this.lineSeparator = lineSeparator;
    this.styling = styling;
    // populate indentation lookup table
    indentationLookupTable[0] = "";
    for (int i = 1; i < indentationLookupTable.length; i++) {
      indentationLookupTable[i] = indentationLookupTable[i - 1] + indent;
    }
  }

  public Listing add(char character) {
    currentLine.append(character);
    return this;
  }

  public Listing add(CharSequence text) {
    currentLine.append(text);
    return this;
  }

  /** Applies the passed listable instance to this listing. */
  public Listing add(Listable listable) {
    if (listable == null) {
      return this;
    }
    return listable.apply(this);
  }

  /** Add list of listables using newline separator. */
  public Listing addAll(List<? extends Listable> listables) {
    return addAll(listables, Listable.NEWLINE);
  }

  /**
   * Add list of listables using given textual separator inline.
   *
   * <p>For example: {@code "a, b, c"}, {@code "a & b & c"} or {@code "[][][]"}
   */
  public Listing addAll(List<? extends Listable> listables, CharSequence separator) {
    return addAll(listables, listing -> listing.add(separator));
  }

  /** Add list of listables using given listable separator. */
  public Listing addAll(List<? extends Listable> listables, Listable separator) {
    if (listables.isEmpty()) {
      return this;
    }
    if (listables.size() == 1) {
      add(listables.get(0));
      return this;
    }
    Spliterator<? extends Listable> spliterator = listables.spliterator();
    spliterator.tryAdvance(this::add);
    spliterator.forEachRemaining(listable -> separator.apply(this).add(listable));
    return this;
  }

  /** Add all listables using atomic boolean reference for newline management. */
  public Listing addAll(List<? extends Listable> listables, AtomicBoolean needsNewline) {
    if (listables.isEmpty()) {
      return this;
    }
    if (needsNewline.get()) {
      newline();
    }
    listables.forEach(this::add);
    needsNewline.set(true);
    return this;
  }

  /** Guess and use add method by reflecting on the given object instance. */
  public Listing addAny(Object object) {
    if (object instanceof Optional) {
      Optional<?> optional = (Optional<?>) object;
      if (optional.isPresent()) {
        object = optional.get();
      }
    }
    if (object instanceof CharSequence) {
      return add((CharSequence) object);
    }
    if (object instanceof Listable) {
      return add((Listable) object);
    }
    return add(String.valueOf(object));
  }

  public Listing eval(String source, Object... args) {
    return new Script(source).eval(this, args);
  }

  public Listing fmt(Locale locale, String format, Object... args) {
    return add(args.length == 0 ? format : String.format(locale, format, args));
  }

  public Listing fmt(String format, Object... args) {
    return add(args.length == 0 ? format : String.format(format, args));
  }

  public Set<Name> getCollectedNames() {
    return collectedNames;
  }

  public Deque<String> getCollectedLines() {
    return collectedLines;
  }

  public StringBuilder getCurrentLine() {
    return currentLine;
  }

  /** Used by {@code #newline()} to get the next line of this listing. */
  public String getCurrentLineAsString() {
    return currentLine.toString();
  }

  public int getCurrentIndentationDepth() {
    return currentIndentationDepth;
  }

  public String getIndentationString() {
    return indentationLookupTable[1];
  }

  /** The separator separating joined lines. */
  public String getLineSeparator() {
    return lineSeparator;
  }

  public Styling getStyling() {
    return styling;
  }

  public Listing indent(int times) {
    currentIndentationDepth += times;
    if (currentIndentationDepth < 0) {
      currentIndentationDepth = 0;
    }
    return this;
  }

  public boolean isLastLineEmpty() {
    return collectedLines.isEmpty() || collectedLines.getLast().isEmpty();
  }

  /** Carriage return and line feed. */
  public Listing newline() {
    String newline = getCurrentLineAsString();
    currentLine.setLength(0);
    // trivial case: empty line (only add it if last line is not empty)
    if (newline.isEmpty()) {
      if (!isLastLineEmpty()) {
        collectedLines.add("");
      }
      return this;
    }
    // prepend indentation pattern in front of the new line
    collectedLines.add(indentationLookupTable[currentIndentationDepth] + newline);
    return this;
  }

  @Override
  public String toString() {
    // prepend indentation pattern in front of the non-empty current (and now last) line
    String lastLine = currentLine.toString();
    if (!lastLine.isEmpty()) {
      lastLine = indentationLookupTable[currentIndentationDepth] + lastLine;
    }
    if (collectedLines.isEmpty()) {
      return lastLine;
    }
    String lineSeparator = getLineSeparator();
    return String.join(lineSeparator, collectedLines) + lineSeparator + lastLine;
  }
}
