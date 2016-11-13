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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ScriptTests {

  @Test
  void dynamicIndent() {
    assertEquals("`>` -> INDENT_INC[:null]", Script.compile("{{>}}").parts.get(0).toString());
    assertEquals("`>>` -> UNKNOWN[:null]", Script.compile("{{>>}}").parts.get(0).toString());
    assertEquals("    @", Script.eval("{{>>}}@"));
    assertEquals("    @", Script.eval("{{>>>>}}{{<<}}@"));
  }

  @Test
  void evalPosition() {
    String[] expected = {"String hello = ", "  \"world\";", "  //(-:"};
    String source = "{{T}} {{N}} = {{¶}}{{>}}{{E}}{{;}}//(-:";
    String actual = Script.eval(source, String.class, "hello", "world");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalCustomIndex() {
    String[] expected = {"String hello = ", "  \"hello\";", ""};
    String source = "{{T:0}} {{$:1}} = {{¶}}{{>}}{{E:1}}{{;}}";
    String actual = Script.eval(source, String.class, "hello");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalNamed() {
    String[] expected = {"String hello = ", "  \"world\";", ""};
    String source = "{{T:type}} {{N:variable}} = {{¶}}{{>}}{{E:value}}{{;}}";
    Map<String, Object> map = Map.of("type", String.class, "variable", "hello", "value", "world");
    String actual = Script.eval(source, map);
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }
}
