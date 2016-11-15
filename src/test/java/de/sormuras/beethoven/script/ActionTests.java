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

import static de.sormuras.beethoven.script.Action.Consumer.BINARY;
import static de.sormuras.beethoven.script.Action.Consumer.LISTABLE;
import static de.sormuras.beethoven.script.Action.Consumer.LITERAL;
import static de.sormuras.beethoven.script.Action.Consumer.NAME;
import static de.sormuras.beethoven.script.Action.Consumer.STRING;
import static de.sormuras.beethoven.script.Action.Consumer.TYPE;
import static de.sormuras.beethoven.script.Action.Simple.END_OF_STATEMENT;
import static de.sormuras.beethoven.script.Action.Simple.INDENT;
import static de.sormuras.beethoven.script.Action.Simple.NEWLINE;
import static de.sormuras.beethoven.script.Action.Simple.UNINDENT;
import static de.sormuras.beethoven.script.Action.Variable.CHAINED_GETTER_CALL;
import static de.sormuras.beethoven.type.ClassType.parameterized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sormuras.beethoven.Listable;
import de.sormuras.beethoven.Listing;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActionTests {

  private final Listing listing = new Listing("\n");

  private Listing execute(Action action) {
    return action.execute(listing, null, null);
  }

  private String execute(Action action, Object argument) {
    return execute(action, null, argument);
  }

  private String execute(Action action, String snippet, Object argument) {
    return action.execute(new Listing("\n"), snippet, argument).toString();
  }

  @Test
  void action() {
    assertEquals(Action.Simple.INDENT, Action.action(">"));
    assertEquals(Action.Simple.UNINDENT, Action.action("<"));
    assertEquals(Action.Simple.NEWLINE, Action.action("¶"));
    assertEquals(Action.Simple.END_OF_STATEMENT, Action.action(";"));
    assertEquals(Action.Consumer.LITERAL, Action.action("$"));
    assertEquals(Action.Consumer.STRING, Action.action("S"));
    assertEquals(Action.Dynamic.INDENT_INC, Action.action(">>"));
    assertEquals(Action.Dynamic.INDENT_INC, Action.action(">>>"));
    assertEquals(Action.Dynamic.INDENT_DEC, Action.action("<<"));
    assertEquals(Action.Dynamic.INDENT_DEC, Action.action("<<<"));
  }

  @Test
  void indent() {
    assertEquals(0, execute(NEWLINE).getCurrentIndentationDepth());
    assertEquals(0, execute(UNINDENT).getCurrentIndentationDepth());
    assertEquals(1, execute(INDENT).getCurrentIndentationDepth());
    assertEquals(2, execute(INDENT).getCurrentIndentationDepth());
    assertEquals(1, execute(UNINDENT).getCurrentIndentationDepth());
    assertEquals(0, execute(UNINDENT).getCurrentIndentationDepth());
  }

  @Test
  void newlines() {
    assertEquals("o", listing.add('o').toString());
    assertEquals("o\n", execute(NEWLINE).toString());
    assertEquals("o\n;\n", execute(END_OF_STATEMENT).toString());
  }

  @Test
  void value() {
    assertEquals("\"1\" + 3", execute(LITERAL, "\"1\" + 3"));
    assertEquals("\"\\\"1\\\" + 3\"", execute(STRING, "\"1\" + 3"));
    assertEquals("Thread.State.BLOCKED", execute(NAME, Thread.State.BLOCKED));
    assertEquals("int[][][]", execute(TYPE, int[][][].class));
    assertEquals("java.util.List<Byte>", execute(TYPE, parameterized(List.class, Byte.class)));
    assertEquals("long", execute(BINARY, long.class));
    assertEquals("java.lang.Object", execute(BINARY, Object.class));
    assertEquals("[L" + "java.lang.Object" + ";", execute(BINARY, Object[].class));
    assertEquals("[[[Z", execute(BINARY, boolean[][][].class));
    assertEquals(" ", execute(LISTABLE, Listable.SPACE));
    assertEquals("1234", execute(LISTABLE, (Listable) listing -> listing.add("123").add('4')));
    assertEquals("Byte", execute(CHAINED_GETTER_CALL, "#class.simpleName", Byte.MAX_VALUE));
  }
}
