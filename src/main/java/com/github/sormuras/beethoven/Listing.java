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
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Listing {

  private final Deque<String> collectedLines = new ArrayDeque<>(512);
  private final StringBuilder currentLine = new StringBuilder(256);
  private int indentationDepth = 0;

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

  public Deque<String> getCollectedLines() {
    return collectedLines;
  }

  public StringBuilder getCurrentLine() {
    return currentLine;
  }

  public String getIndentationString() {
    return "  ";
  }

  public int getIndentationDepth() {
    return indentationDepth;
  }

  public String getLineSeparator() {
    return "\n";
  }

  public Predicate<Name> getImportNamePredicate() {
    return name -> false;
  }

  public Listing indent(int times) {
    indentationDepth += times;
    if (indentationDepth < 0) {
      indentationDepth = 0;
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
    if (indentationDepth == 0) {
      collectedLines.add(newline);
      return this;
    }
    // "insert" indentation pattern in front of the new line
    String indentationString = getIndentationString();
    int capacity = indentationDepth * indentationString.length() + newline.length();
    StringBuilder indentedLine = new StringBuilder(capacity);
    IntStream.range(0, indentationDepth).forEach(i -> indentedLine.append(indentationString));
    indentedLine.append(newline);
    collectedLines.add(indentedLine.toString());
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
