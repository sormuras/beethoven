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

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sormuras.beethoven.Listing;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScriptTests {

  private String eval(String source, Object... args) {
    return new Script(source).eval(new Listing(), args).toString();
  }

  private String eval(String source, Map<String, Object> map) {
    return new Script(source).eval(new Listing(), map).toString();
  }

  @Test
  void string() {
    assertEquals("Script [source=α, commands=[`α`]]", new Script("α").toString());
    assertEquals("[`α`, `$` -> LITERAL[:β]]", new Script("α{{$:β}}").getCommands().toString());
  }

  @Test
  void indent() {
    assertEquals("@", eval("@"));
    assertEquals("  @", eval("{{>}}@"));
    assertEquals("    @", eval("{{>>}}@"));
    assertEquals("  @", eval("{{>>>}}{{<<}}@"));
    assertEquals("@", eval("{{>>>>}}{{<<<<<<<<<<<<<<}}@"));
  }

  @Test
  void evalPosition() {
    String[] expected = {"String hello = ", "  \"world\";", "  //(-:"};
    String source = "{{T}} {{N}} = {{¶}}{{>}}{{S}}{{;}}//(-:";
    String actual = eval(source, String.class, "hello", "world");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalUserIndex() {
    String[] expected = {"String hello = ", "  \"hello\";", ""};
    String source = "{{T:0}} {{$:1}} = {{¶}}{{>}}{{S:1}}{{;}}";
    String actual = eval(source, String.class, "hello");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalUserName() {
    String[] expected = {"String hello = ", "  \"world\";", ""};
    String source = "{{T:type}} {{ N:variable }} = {{¶}}{{>}}{{ S : value }}{{;}}";
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("type", String.class);
    map.put("variable", "hello");
    map.put("value", "world");
    String actual = eval(source, map);
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void reflection() {
    assertEquals("1 2 3", eval("1 {{#toString // auto-index map w/ position }} 3", 2));
    assertEquals("1 2 3", eval("1 {{#toString:0 // auto-index map w/ selector }} 3", 2));
    assertEquals("1 2 3", eval("1 {{#toString:II // named selector }} 3", singletonMap("II", 2)));
  }
}
