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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
      command.execute(listing, argument);
    }
    return listing;
  }
}
