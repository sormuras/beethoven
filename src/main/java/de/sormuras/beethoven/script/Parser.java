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

package de.sormuras.beethoven.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {

  private final String actionBeginMarker;
  private final String actionEndMarker;

  public Parser() {
    this("{{", "}}");
  }

  public Parser(String actionBeginMarker, String actionEndMarker) {
    this.actionBeginMarker = actionBeginMarker;
    this.actionEndMarker = actionEndMarker;
  }

  protected Action action(String snippet) {
    for (Action action : Tag.values()) {
      if (action.handles(snippet)) {
        return action;
      }
    }
    throw new IllegalArgumentException(String.format("No action handles: `%s`", snippet));
  }

  public List<Command> parse(String source) {
    List<Command> commands = new ArrayList<>();

    int currentIndex = 0;
    int beginIndex = source.indexOf(actionBeginMarker);
    while (beginIndex >= 0) {

      String literal = source.substring(currentIndex, beginIndex);
      if (!literal.isEmpty()) {
        commands.add(new Command(literal));
      }

      int endIndex = source.indexOf(actionEndMarker, beginIndex);
      if (endIndex == -1) {
        throw new IllegalArgumentException("tag end marker not found: " + source);
      }
      // include entire end marker when extracting the tag text
      endIndex += actionEndMarker.length();
      String tag = source.substring(beginIndex, endIndex);
      // strip markers from start and end and trim content
      int patternStartIndex = actionBeginMarker.length();
      int patternEndIndex = tag.length() - actionEndMarker.length();
      tag = tag.substring(patternStartIndex, patternEndIndex).trim();
      // strip custom text from tag, like "$:2 // comments are ignored"
      if (tag.contains("//")) {
        tag = tag.substring(0, tag.indexOf("//")).trim();
      }
      // extract custom selector, like "$:1" or "$:hello"
      String selector = null;
      if (tag.contains(":")) {
        int indexOfDoublePoint = tag.indexOf(":");
        selector = tag.substring(indexOfDoublePoint + 1).trim();
        try {
          selector = Integer.toString(Integer.parseInt(selector));
        } catch (NumberFormatException e) {
          // ignore
        }
        tag = tag.substring(0, indexOfDoublePoint).trim();
      }
      // identify action
      commands.add(new Command(tag, selector, action(tag)));

      // prepare next round
      currentIndex = endIndex;
      beginIndex = source.indexOf(actionBeginMarker, currentIndex);
    }
    // grab trailing literal, if any
    String literal = source.substring(currentIndex);
    if (!literal.isEmpty()) {
      commands.add(new Command(literal));
    }

    return Collections.unmodifiableList(commands);
  }
}
