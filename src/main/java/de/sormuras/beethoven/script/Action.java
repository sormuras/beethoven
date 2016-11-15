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
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@FunctionalInterface
public interface Action {

  /** Simple actions have no parameters. */
  enum Simple implements Action {
    INDENT(">", listing -> listing.indent(1)),
    UNINDENT("<", listing -> listing.indent(-1)),
    NEWLINE("¶", Listing::newline),
    END_OF_STATEMENT(";", listing -> listing.add(';').newline()),
    ;

    final String identifier;
    final Listable listable;

    Simple(String identifier, Listable listable) {
      this.identifier = identifier;
      this.listable = listable;
    }

    @Override
    public Listing execute(Listing listing, String snippet, Object argument) {
      return listable.apply(listing);
    }

    @Override
    public boolean handles(String snippet) {
      return identifier.equals(snippet);
    }
  }

  /** Argument consuming actions. */
  enum Consumer implements Action {
    LITERAL("$", (listing, object) -> listing.add(String.valueOf(object))),
    STRING("S", (listing, object) -> listing.add(Listable.escape(String.valueOf(object)))),
    LISTABLE("L", (listing, object) -> listing.add(Listable.class.cast(object))),
    NAME("N", (listing, object) -> listing.add(Name.cast(object))),
    TYPE("T", (listing, object) -> listing.add(Type.cast(object))),
    BINARY("B", (listing, object) -> listing.add(Type.cast(object).binary())),
    ;

    final String identifier;
    final BiFunction<Listing, Object, Listing> function;

    Consumer(String identifier, BiFunction<Listing, Object, Listing> function) {
      this.identifier = identifier;
      this.function = function;
    }

    @Override
    public boolean consumesArgument() {
      return true;
    }

    @Override
    public Listing execute(Listing listing, String snippet, Object argument) {
      return function.apply(listing, argument);
    }

    @Override
    public boolean handles(String snippet) {
      return identifier.equals(snippet);
    }
  }

  /** No arg, but variable "keyword" interpretation. */
  enum Dynamic implements Action {
    INDENT_INC(">+", (listing, snippet) -> listing.indent(snippet.length())),
    INDENT_DEC("<+", (listing, snippet) -> listing.indent(-snippet.length())),
    ;

    final Pattern pattern;
    final BiFunction<Listing, String, Listing> function;

    Dynamic(String regex, BiFunction<Listing, String, Listing> function) {
      this.pattern = Pattern.compile(regex);
      this.function = function;
    }

    @Override
    public Listing execute(Listing listing, String snippet, Object argument) {
      return function.apply(listing, snippet);
    }

    @Override
    public boolean handles(String snippet) {
      return pattern.matcher(snippet).matches();
    }
  }

  enum Variable implements Action {
    CHAINED_GETTER_CALL(
        "#.+",
        (listing, snippet, argument) -> listing.addAny(reflect(snippet.substring(1), argument))),
    ;

    final Pattern pattern;
    final Action action;

    Variable(String regex, Action action) {
      this.pattern = Pattern.compile(regex);
      this.action = action;
    }

    @Override
    public boolean consumesArgument() {
      return true;
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
