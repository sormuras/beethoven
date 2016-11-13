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

package de.sormuras.beethoven.script;

import de.sormuras.beethoven.Listing;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

class Script {

  static String eval(String source, Object... args) {
    return compile(source).eval(new Listing(), args).toString();
  }

  static String eval(String source, Map<String, Object> bindings) {
    return compile(source).eval(new Listing(), bindings).toString();
  }

  static Script compile(String source) {
    // Logger logger = Logger.getLogger(Template.class.getName());
    // logger.fine(format("Parsing source: `%s`", source));

    List<Part> parts = new ArrayList<>();

    String tagStartMarker = "{{";
    String tagEndMarker = "}}";

    int argumentCounter = 0;
    int currentIndex = 0;
    int beginIndex = source.indexOf(tagStartMarker);
    while (beginIndex >= 0) {

      String literal = source.substring(currentIndex, beginIndex);
      if (!literal.isEmpty()) {
        // logger.fine(format("literal: %03d..%03d = `%s`", currentIndex, beginIndex, literal));
        parts.add(new Part(literal));
      }

      int endIndex = source.indexOf(tagEndMarker, beginIndex) + tagEndMarker.length();
      if (endIndex < beginIndex) {
        throw new IllegalArgumentException("source marker syntax error: " + source);
      }
      String pattern = source.substring(beginIndex, endIndex);
      // logger.fine(format(" marker: %03d..%03d = `%s`", beginIndex, endIndex, pattern));
      // strip markers from start and end
      int patternStartIndex = tagStartMarker.length();
      int patternEndIndex = pattern.length() - tagEndMarker.length();
      pattern = pattern.substring(patternStartIndex, patternEndIndex);
      // strip custom text from pattern, like "$:2 // comments are ignored"
      if (pattern.contains("//")) {
        pattern = pattern.substring(0, pattern.indexOf("//")).trim();
      }
      // extract custom selector, like "$:1" or "$:hello"
      String selector = null;
      if (pattern.contains(":")) {
        int indexOfDoublePoint = pattern.indexOf(":");
        String indicator = pattern.substring(indexOfDoublePoint + 1);
        try {
          selector = Integer.toString(Integer.parseInt(indicator));
        } catch (NumberFormatException e) {
          selector = indicator;
        }
        pattern = pattern.substring(0, indexOfDoublePoint).trim();
      }
      Tag tag = Tag.of(pattern);
      // no custom selector available?
      if (selector == null && tag.count == 1) {
        selector = Integer.toString(argumentCounter++);
      }
      // logger.fine(format("    tag: `%s` -> %s [%d]", pattern, tag, argumentIndex));
      parts.add(new Part(tag, selector, pattern));

      // prepare next round
      currentIndex = endIndex;
      beginIndex = source.indexOf(tagStartMarker, currentIndex);
    }
    String literal = source.substring(currentIndex);
    if (!literal.isEmpty()) {
      //logger.fine(format("literal: %03d..%03d = `%s`", currentIndex, beginIndex, literal));
      parts.add(new Part(literal));
    }
    return new Script(source, parts);
  }

  final String source;
  final List<Part> parts;

  public Script(String source, List<Part> parts) {
    this.source = source;
    this.parts = parts;
  }

  public Listing eval(Listing listing, Object... bindings) {
    Map<String, Object> map = new LinkedHashMap<>();
    IntStream.range(0, bindings.length).forEach(i -> map.put(Integer.toString(i), bindings[i]));
    return eval(listing, map);
  }

  public Listing eval(Listing listing, Map<String, Object> bindings) {
    for (Part part : parts) {
      Tag tag = part.tag;
      if (tag == null) {
        listing.add(part.snippet);
        continue;
      }
      if (tag == Tag.UNKNOWN) {
        if (part.snippet.startsWith(">")) {
          listing.indent((int) part.snippet.chars().filter(c -> c == '>').count());
          continue;
        }
        if (part.snippet.startsWith("<")) {
          listing.indent(-(int) part.snippet.chars().filter(c -> c == '<').count());
          continue;
        }
        throw new IllegalStateException("Unknown part not handled: " + part);
      }
      if (tag.count == 0) {
        tag.eval(listing);
        continue;
      }
      assert tag.count == 1;
      Object value = bindings.get(part.selector);
      tag.eval(listing, value);
    }
    return listing;
  }
}
