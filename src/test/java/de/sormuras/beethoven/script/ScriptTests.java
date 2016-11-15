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
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class ScriptTests {

  @Test
  void indent() {
    UnaryOperator<String> operator = source -> new Parser().parse(source).get(0).toString();
    assertEquals("`>` -> INDENT", operator.apply("{{>}}"));
    assertEquals("`>>` -> INDENT_INC", operator.apply("{{>>}}"));
    assertEquals("`<` -> UNINDENT", operator.apply("{{<}}"));
    assertEquals("`<<` -> INDENT_DEC", operator.apply("{{<<}}"));
    assertEquals("@", Script.eval("@"));
    assertEquals("  @", Script.eval("{{>}}@"));
    assertEquals("    @", Script.eval("{{>>}}@"));
    assertEquals("  @", Script.eval("{{>>>}}{{<<}}@"));
    assertEquals("@", Script.eval("{{>>>>}}{{<<<<<<<<<<<<<<}}@"));
  }

  @Test
  void evalPosition() {
    String[] expected = {"String hello = ", "  \"world\";", "  //(-:"};
    String source = "{{T}} {{N}} = {{¶}}{{>}}{{S}}{{;}}//(-:";
    String actual = Script.eval(source, String.class, "hello", "world");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalUserIndex() {
    String[] expected = {"String hello = ", "  \"hello\";", ""};
    String source = "{{T:0}} {{$:1}} = {{¶}}{{>}}{{S:1}}{{;}}";
    String actual = Script.eval(source, String.class, "hello");
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void evalUserName() {
    String[] expected = {"String hello = ", "  \"world\";", ""};
    String source = "{{T:type}} {{ N:variable }} = {{¶}}{{>}}{{ S : value }}{{;}}";
    Map<String, Object> map = Map.of("type", String.class, "variable", "hello", "value", "world");
    String actual = Script.eval(source, map);
    assertEquals(String.join(System.lineSeparator(), expected), actual);
  }

  @Test
  void reflection() {
    assertEquals("1 2 3", Script.eval("1 {{#toString // auto-index map w/ position }} 3", 2));
    assertEquals("1 2 3", Script.eval("1 {{#toString:0 // auto-index map w/ selector }} 3", 2));
    assertEquals("1 2 3", Script.eval("1 {{#toString:II // named selector }} 3", Map.of("II", 2)));
  }
}
