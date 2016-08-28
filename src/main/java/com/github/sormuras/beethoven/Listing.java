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

package com.github.sormuras.beethoven;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Listing {

  public static final Pattern METHODCHAIN_PATTERN = Pattern.compile("\\{|\\.|\\}");
  public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{.+?\\}");

  private final Deque<String> collectedLines = new ArrayDeque<>(512);
  private final StringBuilder currentLine = new StringBuilder(256);
  private int currentIndentationDepth = 0;
  private final String[] indentationTable = new String[20];

  public Listing() {
    indentationTable[0] = "";
    IntStream.range(1, indentationTable.length)
        .forEach(i -> indentationTable[i] = indentationTable[i - 1] + getIndentationString());
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

  /** Add name respecting name predicate result. */
  public Listing add(Name name) {
    // imported name only emits its last name
    if (getImportNamePredicate().test(name)) {
      return add(name.lastName());
    }
    // handle "java.lang" member
    if (isOmitJavaLangPackage() && name.isJavaLangPackage()) {
      return add(String.join(".", name.simpleNames()));
    }
    return add(name.canonical());
  }

  /**
   * Parse source string and replace placeholders with {@link #add}-calls to this {@link Listing}
   * instance. TODO Enumerate supported placeholders. TODO Introduce extension points allowing
   * custom placeholder (handling).
   */
  public Listing add(String source, Object... args) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(source);

    int argumentIndex = 0;
    int sourceIndex = 0;
    while (matcher.find()) {
      if (sourceIndex < matcher.start()) {
        add(source.substring(sourceIndex, matcher.start()));
      }
      sourceIndex = matcher.end();
      // handle simple placeholder
      String placeholder = matcher.group(0);
      if (placeholder.equals("{S}")) {
        String string = args[argumentIndex++].toString();
        // TODO add(Tool.escape(string));
        add('"');
        add(string);
        add('"');
        continue;
      }
      if (placeholder.equals("{N}")) {
        add(Name.cast(args[argumentIndex++]));
        continue;
      }
      if (placeholder.equals("{L}")) {
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
          result = result.getClass().getMethod(scanner.next()).invoke(result);
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

  public String getIndentationString() {
    return "  ";
  }

  public int getCurrentIndentationDepth() {
    return currentIndentationDepth;
  }

  public String getLineSeparator() {
    return "\n";
  }

  public Predicate<Name> getImportNamePredicate() {
    return name -> false;
  }

  public Listing indent(int times) {
    currentIndentationDepth += times;
    if (currentIndentationDepth < 0) {
      currentIndentationDepth = 0;
    }
    return this;
  }

  public boolean isLastLineEmpty() {
    if (collectedLines.isEmpty()) {
      return true;
    }
    return collectedLines.getLast().isEmpty();
  }

  public boolean isOmitJavaLangPackage() {
    return false;
  }

  /** Carriage return and line feed. */
  public Listing newline() {
    String newline = currentLine.toString(); // Tool.trimRight(currentLine.toString());
    currentLine.setLength(0);
    // trivial case: empty line (only add it if last line is not empty)
    if (newline.isEmpty()) {
      if (!isLastLineEmpty()) {
        collectedLines.add("");
      }
      return this;
    }
    // trivial case: no indentation, just add the line
    if (currentIndentationDepth == 0) {
      collectedLines.add(newline);
      return this;
    }
    // prepend indentation pattern in front of the new line
    collectedLines.add(indentationTable[currentIndentationDepth] + newline);
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
