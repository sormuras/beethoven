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

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listing {

  /** Used by a {@link Scanner#useDelimiter(Pattern)} delimiter pattern. */
  public static final Pattern METHODCHAIN_PATTERN = Pattern.compile("\\{\\{|\\.|}}");

  /** Used to find placeholders framed by double curly braces. */
  public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{[^{^}]+?}}");

  private final Deque<String> collectedLines = new ArrayDeque<>(512);
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

  /**
   * Parse source string and replace placeholders with {@link #add(CharSequence)}-calls to this
   * {@link Listing} instance.
   *
   * <p>Simple placeholders:
   *
   * <ul>
   *   <li><b>{{S}}</b> {@link String} <b>without</b> escaping, same as: {@link #add(CharSequence)}
   *   <li><b>{{E}}</b> {@link String} with escaping, same as: {@code add(escape(arg))}
   *   <li><b>{{L}}</b> {@link Listable} same as: {@code add((Listable)(arg))}
   *   <li><b>{{N}}</b> {@link Name} same as: {@code add(Name.cast(arg))}
   * </ul>
   *
   * <p>Layout placeholders:
   *
   * <ul>
   *   <li><b>{{&gt;}}</b> same as: {@code indent(1)}
   *   <li><b>{{&lt;}}</b> same as: {@code indent(-1)}
   *   <li><b>{{&gt;...n-times}}</b> same as: {@code indent(n)}
   *   <li><b>{{&lt;...n-times}}</b> same as: {@code indent(-n)}
   *   <li><b>{{;}}</b> same as: {@code add(';').newline()}
   * </ul>
   *
   * Every unknown placeholder is treated as a zero-argument method chain call.
   */
  public Listing eval(String source, Object... args) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(source);

    int argumentIndex = 0;
    int sourceIndex = 0;
    while (matcher.find()) {
      if (sourceIndex < matcher.start()) {
        add(source.substring(sourceIndex, matcher.start()));
      }
      sourceIndex = matcher.end();
      String placeholder = matcher.group(0);
      // strip custom text from placeholder, like "{{$ // comments are ignored }}"
      if (placeholder.contains("//")) {
        placeholder = placeholder.substring(0, placeholder.indexOf("//")).trim() + "}}";
      }
      // handle indentation
      if (placeholder.startsWith("{{>")) {
        if (placeholder.equals("{{>}}")) {
          indent(1);
        } else {
          indent((int) placeholder.chars().filter(c -> c == '>').count());
        }
        continue;
      }
      if (placeholder.startsWith("{{<")) {
        if (placeholder.equals("{{<}}")) {
          indent(-1);
        } else {
          indent((int) placeholder.chars().filter(c -> c == '<').count());
        }
        continue;
      }
      // handle simple placeholders
      if (placeholder.equals("{{;}}")) {
        add(';').newline();
        continue;
      }
      if (placeholder.equals("{{S}}")) {
        add(String.valueOf(args[argumentIndex++]));
        continue;
      }
      if (placeholder.equals("{{E}}")) {
        add(Listable.escape(String.valueOf(args[argumentIndex++])));
        continue;
      }
      if (placeholder.equals("{{N}}")) {
        add(Name.cast(args[argumentIndex++]));
        continue;
      }
      if (placeholder.equals("{{L}}")) {
        add((Listable) args[argumentIndex++]);
        continue;
      }
      // convert unknown placeholder to chained method call sequence
      Object argument = args[argumentIndex++];
      Scanner scanner = new Scanner(placeholder);
      try {
        scanner.useDelimiter(METHODCHAIN_PATTERN);
        Object result = argument;
        while (scanner.hasNext()) {
          String name = scanner.next();
          Method method;
          try {
            method = result.getClass().getMethod(name);
          } catch (NoSuchMethodException e) {
            name = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            method = result.getClass().getMethod(name);
          }
          result = method.invoke(result);
        }
        if (result instanceof Optional) {
          Optional<?> optional = (Optional<?>) result;
          if (!optional.isPresent()) {
            continue;
          }
          result = optional.get();
        }
        if (result instanceof Name) {
          add((Name) result);
          continue;
        }
        if (result instanceof Listable) {
          add((Listable) result);
          continue;
        }
        add(String.valueOf(result));
        scanner.close();
      } catch (Exception exception) {
        throw new IllegalArgumentException(
            "Error parsing: '" + placeholder + "' source='" + source + "'", exception);
      }
    }
    add(source.substring(sourceIndex));
    return this;
  }

  public Listing fmt(Locale locale, String format, Object... args) {
    return add(args.length == 0 ? format : String.format(locale, format, args));
  }

  public Listing fmt(String format, Object... args) {
    return add(args.length == 0 ? format : String.format(format, args));
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
    if (collectedLines.isEmpty()) {
      return currentLine.toString();
    }
    String lineSeparator = getLineSeparator();
    return String.join(lineSeparator, collectedLines) + lineSeparator + currentLine.toString();
  }
}
