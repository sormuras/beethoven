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

import static de.sormuras.beethoven.script.Action.Simple.END_OF_STATEMENT;
import static de.sormuras.beethoven.script.Action.Simple.NEWLINE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.sormuras.beethoven.Listing;
import org.junit.jupiter.api.Test;

class ActionTests {

  private final Listing listing = new Listing("\n");

  private Listing execute(Action action) {
    return action.execute(listing, null, null);
  }

  @Test
  void indent() {
    assertEquals(0, execute(NEWLINE).getCurrentIndentationDepth());
    //    assertEquals(0, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
    //    assertEquals(1, INDENT_INC.eval(listing).getCurrentIndentationDepth());
    //    assertEquals(2, INDENT_INC.eval(listing).getCurrentIndentationDepth());
    //    assertEquals(1, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
    //    assertEquals(0, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
  }

  @Test
  void newlines() {
    listing.add('o');
    assertEquals("o", listing.toString());
    assertEquals("o\n", execute(NEWLINE).toString());
    assertEquals("o\n;\n", execute(END_OF_STATEMENT).toString());
  }

  //  @Test
  //  void value() {
  //    assertEquals("\"1\" + 3", LITERAL.eval("\"1\" + 3"));
  //    assertEquals("\"\\\"1\\\" + 3\"", ESCAPED.eval("\"1\" + 3"));
  //    assertEquals("Thread.State.BLOCKED", NAME.eval(Thread.State.BLOCKED));
  //    assertEquals("int[][][]", TYPE.eval(int[][][].class));
  //    assertEquals("java.util.List<Byte>", TYPE.eval(parameterized(List.class, Byte.class)));
  //    assertEquals("long", BINARY.eval(long.class));
  //    assertEquals("java.lang.Object", BINARY.eval(Object.class));
  //    assertEquals("[L" + "java.lang.Object" + ";", BINARY.eval(Object[].class));
  //    assertEquals("[[[Z", BINARY.eval(boolean[][][].class));
  //    assertEquals(" ", LISTABLE.eval(Listable.SPACE));
  //    assertEquals("1234", LISTABLE.eval((Listable) listing -> listing.add("123").add('4')));
  //  }

  @Test
  void action() {
    assertEquals(Action.Simple.NEWLINE, Action.action("¶"));
    assertEquals(Action.Simple.END_OF_STATEMENT, Action.action(";"));
    assertEquals(Action.Arg.LITERAL, Action.action("$"));
    assertEquals(Action.Arg.STRING, Action.action("S"));
    assertEquals(Action.Dynamic.INDENT_INC, Action.action(">"));
    assertEquals(Action.Dynamic.INDENT_INC, Action.action(">>"));
    assertEquals(Action.Dynamic.INDENT_INC, Action.action(">>>"));
    assertEquals(Action.Dynamic.INDENT_DEC, Action.action("<"));
    assertEquals(Action.Dynamic.INDENT_DEC, Action.action("<<"));
    assertEquals(Action.Dynamic.INDENT_DEC, Action.action("<<<"));
  }
}
