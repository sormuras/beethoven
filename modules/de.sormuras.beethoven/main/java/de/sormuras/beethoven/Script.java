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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Script {

  public enum Tag implements Action {
    LITERAL("\\$", Consumes.ARG, (listing, tag, arg) -> listing.add(String.valueOf(arg))),

    STRING("S", Consumes.ARG, (listing, tag, arg) -> listing.add(Listable.escape((String) arg))),

    LISTABLE("L", Consumes.ARG, (listing, tag, arg) -> listing.add(Listable.class.cast(arg))),

    NAME("N", Consumes.ARG, (listing, tag, arg) -> listing.add(Name.cast(arg))),

    // TYPE("T", Consumes.ARG, (listing, tag, arg) -> listing.add(Type.cast(arg))),

    // BINARY("B", Consumes.ARG, (listing, tag, arg) -> listing.add(Type.cast(arg).binary())),

    NEWLINE("¶", Consumes.NONE, (listing, tag, arg) -> listing.newline()),

    CLOSE_STATEMENT(";", Consumes.NONE, (listing, tag, arg) -> listing.add(';').newline()),

    INDENT(">", Consumes.NONE, (listing, tag, arg) -> listing.indent(1)),

    UNINDENT("<", Consumes.NONE, (listing, tag, arg) -> listing.indent(-1)),

    INDENT_INC(">+", Consumes.TAG, (listing, tag, arg) -> listing.indent(tag.length())),

    INDENT_DEC("<+", Consumes.TAG, (listing, tag, arg) -> listing.indent(-tag.length())),

    REFLECT("#.+", Consumes.ALL, (listing, tag, arg) -> listing.addAny(reflect(tag, arg)));

    // convert unknown tag to chained method call sequence
    static Object reflect(String tag, Object argument) {
      Objects.requireNonNull(tag, "tag must not be null");
      Objects.requireNonNull(argument, "argument must not be null");
      if (tag.startsWith("#")) {
        tag = tag.substring(1);
      }
      try (Scanner scanner = new Scanner(tag)) {
        scanner.useDelimiter(Name.DOT);
        while (scanner.hasNext()) {
          String name = scanner.next();
          Method method;
          try {
            method = argument.getClass().getMethod(name);
          } catch (NoSuchMethodException e) {
            name = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            method = argument.getClass().getMethod(name);
          }
          argument = method.invoke(argument);
        }
        return argument;
      } catch (ReflectiveOperationException exception) {
        throw new IllegalArgumentException("Can't reflect tag: " + tag, exception);
      }
    }

    final Pattern pattern;
    final Consumes consumes;
    final Action action;

    Tag(String regex, Consumes consumes, Action action) {
      this.pattern = Pattern.compile(regex);
      this.consumes = consumes;
      this.action = action;
    }

    @Override
    public Consumes consumes() {
      return consumes;
    }

    @Override
    public Listing execute(Listing listing, String tag, Object arg) {
      return action.execute(listing, tag, arg);
    }

    @Override
    public boolean handles(String tag) {
      return pattern.matcher(tag).matches();
    }
  }


  private final String source;
  private final List<Command> commands;

  public Script(String source) {
    this.source = source;
    this.commands = new Parser().parse(source);
  }

  public List<Command> getCommands() {
    return commands;
  }

  public String getSource() {
    return source;
  }

  public Listing eval(Listing listing, Object... args) {
    Map<String, Object> map = new LinkedHashMap<>();
    IntStream.range(0, args.length).forEach(i -> map.put(Integer.toString(i), args[i]));
    return eval(listing, map);
  }

  public Listing eval(Listing listing, Map<String, Object> map) {
    int argumentConsumingCommandCounter = 0;
    for (Command command : commands) {
      Object argument = null;
      if (command.consumesArgument()) {
        String key = command.selector;
        if (key == null) {
          key = String.valueOf(argumentConsumingCommandCounter++);
        }
        argument = map.get(key);
        if (argument == null) {
          throw new IllegalArgumentException("No argument for `" + key + "` available in: " + map);
        }
      }
      command.execute(listing, argument);
    }
    return listing;
  }

  @Override
  public String toString() {
    return "Script [source=" + getSource() + ", commands=" + getCommands() + "]";
  }

  class Parser {

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

  /** Compiled script execution command. */
  public class Command {

    final String tag;
    final String selector;
    final Action action;

    public Command(String text) {
      this.tag = text;
      this.selector = null;
      this.action = null;
    }

    public Command(String tag, String selector, Action action) {
      this.tag = tag;
      this.selector = selector;
      this.action = action;
    }

    public Listing execute(Listing listing, Object argument) {
      if (action == null) {
        return listing.add(tag);
      }
      return action.execute(listing, tag, argument);
    }

    public boolean consumesArgument() {
      return action != null && action.consumes().arg();
    }

    @Override
    public String toString() {
      String quoted = "`" + tag + "`";
      if (action == null) {
        return quoted;
      }
      String quotedAndAction = quoted + " -> " + action;
      if (!consumesArgument()) {
        return quotedAndAction;
      }
      return quotedAndAction + "[:" + selector + "]";
    }
  }

  @FunctionalInterface
  public interface Action {

    enum Consumes {
      NONE,
      TAG,
      ARG,
      ALL;

      public boolean arg() {
        return this == ARG || this == ALL;
      }
    }

    Listing execute(Listing listing, String tag, Object arg);

    default Consumes consumes() {
      return Consumes.ALL;
    }

    default boolean handles(String tag) {
      return false;
    }
  }

}
