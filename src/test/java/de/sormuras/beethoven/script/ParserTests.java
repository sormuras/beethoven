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

import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class ParserTests {

  private final Parser parser = new Parser();

  @Test
  void action() {
    assertEquals(Action.Simple.INDENT, parser.action(">"));
    assertEquals(Action.Simple.UNINDENT, parser.action("<"));
    assertEquals(Action.Simple.NEWLINE, parser.action("¶"));
    assertEquals(Action.Simple.END_OF_STATEMENT, parser.action(";"));
    assertEquals(Action.Consumer.LITERAL, parser.action("$"));
    assertEquals(Action.Consumer.STRING, parser.action("S"));
    assertEquals(Action.Consumer.BINARY, parser.action("B"));
    assertEquals(Action.Consumer.LISTABLE, parser.action("L"));
    assertEquals(Action.Consumer.NAME, parser.action("N"));
    assertEquals(Action.Consumer.TYPE, parser.action("T"));
    assertEquals(Action.Dynamic.INDENT_INC, parser.action(">>"));
    assertEquals(Action.Dynamic.INDENT_INC, parser.action(">>>"));
    assertEquals(Action.Dynamic.INDENT_DEC, parser.action("<<"));
    assertEquals(Action.Dynamic.INDENT_DEC, parser.action("<<<"));
    assertEquals(Action.Variable.CHAINED_GETTER_CALL, parser.action("#test"));
  }

  @Test
  void indent() {
    UnaryOperator<String> operator = source -> parser.parse(source).get(0).toString();
    assertEquals("`>` -> INDENT", operator.apply("{{>}}"));
    assertEquals("`>>` -> INDENT_INC", operator.apply("{{>>}}"));
    assertEquals("`<` -> UNINDENT", operator.apply("{{<}}"));
    assertEquals("`<<` -> INDENT_DEC", operator.apply("{{<<}}"));
  }
}
