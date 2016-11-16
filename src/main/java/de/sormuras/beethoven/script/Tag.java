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

public enum Tag implements Action {
  LITERAL("\\$", Consumes.ARG, (listing, tag, arg) -> listing.add(String.valueOf(arg))),

  STRING("S", Consumes.ARG, (listing, tag, arg) -> listing.add(Listable.escape((String) arg))),

  LISTABLE("L", Consumes.ARG, (listing, tag, arg) -> listing.add(Listable.class.cast(arg))),

  NAME("N", Consumes.ARG, (listing, tag, arg) -> listing.add(Name.cast(arg))),

  TYPE("T", Consumes.ARG, (listing, tag, arg) -> listing.add(Type.cast(arg))),

  BINARY("B", Consumes.ARG, (listing, tag, arg) -> listing.add(Type.cast(arg).binary())),

  NEWLINE("¶", Consumes.NONE, (listing, tag, arg) -> listing.newline()),

  CLOSE_STATEMENT(";", Consumes.NONE, (listing, tag, arg) -> listing.add(';').newline()),

  INDENT(">", Consumes.NONE, (listing, tag, arg) -> listing.indent(1)),

  UNINDENT("<", Consumes.NONE, (listing, tag, arg) -> listing.indent(-1)),

  INDENT_INC(">+", Consumes.TAG, (listing, tag, arg) -> listing.indent(tag.length())),

  INDENT_DEC("<+", Consumes.TAG, (listing, tag, arg) -> listing.indent(-tag.length())),

  REFLECT("#.+", Consumes.ALL, (listing, tag, arg) -> listing.addAny(reflect(tag, arg)));

  // convert unknown tag to chained method call sequence
  static Object reflect(String tag, Object arg) {
    if (tag.startsWith("#")) {
      tag = tag.substring(1);
    }
    Objects.requireNonNull(arg, "arg must not be null");
    try (Scanner scanner = new Scanner(tag)) {
      scanner.useDelimiter(Name.DOT);
      while (scanner.hasNext()) {
        String name = scanner.next();
        Method method;
        try {
          method = arg.getClass().getMethod(name);
        } catch (NoSuchMethodException e) {
          name = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
          method = arg.getClass().getMethod(name);
        }
        arg = method.invoke(arg);
      }
      return arg;
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
