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
import de.sormuras.beethoven.Name;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Script {

  public static String eval(String source, Object... args) {
    return new Script(source).eval(new Listing(), args).toString();
  }

  public static String eval(String source, Map<String, Object> map) {
    return new Script(source).eval(new Listing(), map).toString();
  }

  private final String source;
  private final List<Command> commands;

  Script(String source) {
    this(source, new Parser().parse(source));
  }

  Script(String source, List<Command> commands) {
    this.source = source;
    this.commands = commands;
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
      if (command.selectArgument()) {
        String key = command.selector;
        if (key == null) {
          key = String.valueOf(argumentConsumingCommandCounter++);
        }
        argument = map.get(key);
      }
      //      Optional<Object> result = reflect(command.snippet, argument);
      //      if (result.isPresent()) {
      //        return listing.addAny(result.get());
      //      }
      command.execute(listing, argument);
    }
    return listing;
  }

  // convert unknown snippet to chained method call sequence
  protected Optional<Object> reflect(String snippet, Object argument) {
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
      return Optional.ofNullable(argument);
    } catch (Exception exception) {
      return Optional.empty();
    }
  }
}
