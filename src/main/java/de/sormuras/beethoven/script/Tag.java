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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

enum Tag {
  // unknown
  UNKNOWN("", UnaryOperator.identity()),

  // no-arg
  INDENT_INC(">", listing -> listing.indent(1)),
  INDENT_DEC("<", listing -> listing.indent(-1)),
  NEWLINE("¶", Listing::newline),
  SEMICOLON_NEWLINE(";", listing -> listing.add(';').newline()),

  // consuming single argument
  LITERAL("$", (listing, object) -> listing.add(String.valueOf(object))),
  ESCAPED("E", (listing, object) -> listing.add(Listable.escape(String.valueOf(object)))),
  LISTABLE("X", (listing, object) -> listing.add((Listable) object)),
  NAME("N", (listing, object) -> listing.add(Name.cast(object))),
  TYPE("T", (listing, object) -> listing.add(Type.cast(object))),
  BINARY("B", (listing, object) -> listing.add(Type.cast(object).binary()));

  static final Map<String, Tag> keywordToTagMap;

  static {
    keywordToTagMap = new LinkedHashMap<>();
    Arrays.stream(Tag.values()).forEach(tag -> keywordToTagMap.put(tag.keyword, tag));
  }

  public static Tag of(String keyword) {
    return keywordToTagMap.getOrDefault(keyword, Tag.UNKNOWN);
  }

  final int count;
  final String keyword;
  final UnaryOperator<Listing> operator;
  final BiFunction<Listing, Object, Listing> function;

  Tag(String keyword, UnaryOperator<Listing> operator) {
    this.count = 0;
    this.keyword = keyword;
    this.function = null;
    this.operator = operator;
  }

  Tag(String keyword, BiFunction<Listing, Object, Listing> function) {
    this.count = 1;
    this.keyword = keyword;
    this.function = function;
    this.operator = null;
  }

  public Listing eval() {
    return eval(new Listing());
  }

  public String eval(Object value) {
    return eval(new Listing(), value).toString();
  }

  public Listing eval(Listing listing) {
    if (operator == null) {
      throw new IllegalStateException(this + " does not support no-arg application!");
    }
    return operator.apply(listing);
  }

  public Listing eval(Listing listing, Object value) {
    if (function == null) {
      throw new IllegalStateException(this + " does not support single argument application!");
    }
    return function.apply(listing, value);
  }
}
