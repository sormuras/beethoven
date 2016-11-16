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

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import de.sormuras.beethoven.Name;
import de.sormuras.beethoven.type.Type;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

@FunctionalInterface
public interface Action {

  enum Consumes {
    NONE,
    ARGUMENT,
    SNIPPET,
    ALL
  }

  enum Tag implements Action {
    INDENT(">", Consumes.NONE, (listing, snippet, argument) -> listing.indent(1)),
    UNINDENT("<", Consumes.NONE, (listing, snippet, argument) -> listing.indent(-1)),
    NEWLINE("¶", Consumes.NONE, (listing, snippet, argument) -> listing.newline()),
    CLOSE_STATEMENT(";", Consumes.NONE, (listing, snippet, argument) -> listing.add(';').newline()),

    LITERAL(
        "\\$",
        Consumes.ARGUMENT,
        (listing, snippet, object) -> listing.add(String.valueOf(object))),
    STRING(
        "S",
        Consumes.ARGUMENT,
        (listing, snippet, object) -> listing.add(Listable.escape(String.valueOf(object)))),
    LISTABLE(
        "L",
        Consumes.ARGUMENT,
        (listing, snippet, object) -> listing.add(Listable.class.cast(object))),
    NAME("N", Consumes.ARGUMENT, (listing, snippet, object) -> listing.add(Name.cast(object))),
    TYPE("T", Consumes.ARGUMENT, (listing, snippet, object) -> listing.add(Type.cast(object))),
    BINARY(
        "B",
        Consumes.ARGUMENT,
        (listing, snippet, object) -> listing.add(Type.cast(object).binary())),

    INDENT_INC(
        ">+", Consumes.SNIPPET, (listing, snippet, object) -> listing.indent(snippet.length())),
    INDENT_DEC(
        "<+", Consumes.SNIPPET, (listing, snippet, object) -> listing.indent(-snippet.length())),

    CHAINED_GETTER_CALL(
        "#.+",
        Consumes.ALL,
        (listing, snippet, argument) -> listing.addAny(reflect(snippet.substring(1), argument))),
    ;

    final Pattern pattern;
    final Consumes consumes;
    final Action action;

    Tag(String regex, Consumes consumes, Action action) {
      this.pattern = Pattern.compile(regex);
      this.consumes = consumes;
      this.action = action;
    }

    @Override
    public boolean consumesArgument() {
      return consumes == Consumes.ARGUMENT || consumes == Consumes.ALL;
    }

    @Override
    public Listing execute(Listing listing, String snippet, Object argument) {
      return action.execute(listing, snippet, argument);
    }

    @Override
    public boolean handles(String snippet) {
      return pattern.matcher(snippet).matches();
    }
  }

  // convert unknown snippet to chained method call sequence
  static Object reflect(String snippet, Object argument) {
    Objects.requireNonNull(argument, "argument must not be null");
    try (Scanner scanner = new Scanner(snippet)) {
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
      throw new IllegalArgumentException("Can't reflect over: " + snippet, exception);
    }
  }

  Listing execute(Listing listing, String snippet, Object argument);

  default boolean consumesArgument() {
    return false;
  }

  default boolean handles(String snippet) {
    return false;
  }
}
