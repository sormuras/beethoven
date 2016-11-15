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
import java.util.function.BiFunction;
import java.util.regex.Pattern;

//  // unknown, but argument is required, like ("{{getClass.getSimpleName.toString}}", "123")
//  UNKNOWN,
//
//  // no-arg
//  INDENT_INC(">", listing -> listing.indent(1)),
//  INDENT_DEC("<", listing -> listing.indent(-1)),
//  NEWLINE("¶", Listing::newline),
//  SEMICOLON_NEWLINE(";", listing -> listing.add(';').newline()),
//
//  // consuming single argument
//  LITERAL("$", (listing, object) -> listing.add(String.valueOf(object))),
//  ESCAPED("E", (listing, object) -> listing.add(Listable.escape(String.valueOf(object)))),
//  LISTABLE("X", (listing, object) -> listing.add((Listable) object)),
//  NAME("N", (listing, object) -> listing.add(Name.cast(object))),
//  TYPE("T", (listing, object) -> listing.add(Type.cast(object))),
//  BINARY("B", (listing, object) -> listing.add(Type.cast(object).binary())),
//
//  // dynamic and well-known, no argument is permitted
//  DYNAMIC_INDENT_INC_N("// n-times `>>[...>]` increase indentation", UnaryOperator.identity()),
//  DYNAMIC_INDENT_DEC_N("// n-times `<<[...<]` decrease indentation", UnaryOperator.identity());
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
  enum Arg implements Action {
    LITERAL("$", (listing, object) -> listing.add(String.valueOf(object))),
    STRING("S", (listing, object) -> listing.add(Listable.escape(String.valueOf(object)))),
    ;

    final String identifier;
    final BiFunction<Listing, Object, Listing> function;

    Arg(String identifier, BiFunction<Listing, Object, Listing> function) {
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

  static Action action(String snippet) {
    for (Action action : Simple.values()) {
      if (action.handles(snippet)) {
        return action;
      }
    }
    for (Action action : Arg.values()) {
      if (action.handles(snippet)) {
        return action;
      }
    }
    for (Action action : Dynamic.values()) {
      if (action.handles(snippet)) {
        return action;
      }
    }
    return null;
  }

  Listing execute(Listing listing, String snippet, Object argument);

  default boolean consumesArgument() {
    return false;
  }

  default boolean handles(String snippet) {
    return false;
  }
}
