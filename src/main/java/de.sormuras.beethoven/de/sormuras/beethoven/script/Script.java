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

import de.sormuras.beethoven.Listing;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Script {

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
}
