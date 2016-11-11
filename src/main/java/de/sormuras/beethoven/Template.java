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

import de.sormuras.beethoven.type.Type;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/** Simple placeholder template script evaluation support. */
public interface Template {

  /** Used by a {@link Scanner#useDelimiter(Pattern)} delimiter pattern. */
  Pattern METHODCHAIN_PATTERN = Pattern.compile("\\{\\{|\\.|}}");

  /** Used to find placeholders framed by double curly braces. */
  Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{[^{^}]+?}}");

  enum Tag {
    LITERAL("$", Template::addLiteral),
    ESCAPED("E", Template::addEscaped),
    NAME("N", Template::addName),
    TYPE("T", Template::addType),
    BINARY("B", Template::addBinary),
    //
    INDENT_INC(">", Template::addIndentIncrease),
    INDENT_DEC("<", Template::addIndentDecrease),
    ;

    final String keyword;
    final UnaryOperator<Listing> operator;
    final BiFunction<Listing, Object, Listing> function;

    Tag(String keyword, UnaryOperator<Listing> operator) {
      this.keyword = keyword;
      this.function = null;
      this.operator = operator;
    }

    Tag(String keyword, BiFunction<Listing, Object, Listing> function) {
      this.keyword = keyword;
      this.function = function;
      this.operator = null;
    }

    public String eval(Object value) {
      return eval(new Listing(), value).toString();
    }

    public Listing eval(Listing listing) {
      return operator.apply(listing);
    }

    public Listing eval(Listing listing, Object value) {
      if (operator != null) {
        return operator.apply(listing);
      }
      if (function != null) {
        return function.apply(listing, value);
      }
      throw new AssertionError();
    }
  }

  static Listing addIndentIncrease(Listing listing) {
    return listing.indent(1);
  }

  static Listing addIndentDecrease(Listing listing) {
    return listing.indent(-1);
  }

  static Listing addLiteral(Listing listing, Object object) {
    return listing.add(String.valueOf(object));
  }

  static Listing addEscaped(Listing listing, Object object) {
    return listing.add(Listable.escape(String.valueOf(object)));
  }

  static Listing addName(Listing listing, Object name) {
    return listing.add(Name.cast(name));
  }

  static Listing addType(Listing listing, Object type) {
    return listing.add(Type.cast(type));
  }

  static Listing addBinary(Listing listing, Object type) {
    return listing.add(Type.cast(type).binary());
  }
}
