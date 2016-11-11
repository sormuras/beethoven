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

package de.sormuras.beethoven;

import static de.sormuras.beethoven.Template.Tag.BINARY;
import static de.sormuras.beethoven.Template.Tag.ESCAPED;
import static de.sormuras.beethoven.Template.Tag.INDENT_DEC;
import static de.sormuras.beethoven.Template.Tag.INDENT_INC;
import static de.sormuras.beethoven.Template.Tag.LITERAL;
import static de.sormuras.beethoven.Template.Tag.NAME;
import static de.sormuras.beethoven.Template.Tag.TYPE;
import static de.sormuras.beethoven.type.ClassType.parameterized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class TemplateTests {

  @Test
  void tagIndent() {
    Listing listing = new Listing();
    assertEquals(0, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
    assertEquals(1, INDENT_INC.eval(listing).getCurrentIndentationDepth());
    assertEquals(2, INDENT_INC.eval(listing).getCurrentIndentationDepth());
    assertEquals(1, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
    assertEquals(0, INDENT_DEC.eval(listing).getCurrentIndentationDepth());
  }

  @Test
  void tagWithValue() {
    assertEquals("\"1\" + 3", LITERAL.eval("\"1\" + 3"));
    assertEquals("\"\\\"1\\\" + 3\"", ESCAPED.eval("\"1\" + 3"));
    assertEquals("Thread.State.BLOCKED", NAME.eval(Thread.State.BLOCKED));
    assertEquals("int[][][]", TYPE.eval(int[][][].class));
    assertEquals("java.util.List<Byte>", TYPE.eval(parameterized(List.class, Byte.class)));
    assertEquals("long", BINARY.eval(long.class));
    assertEquals("java.lang.Object", BINARY.eval(Object.class));
    assertEquals("[Ljava.lang.Object;", BINARY.eval(Object[].class));
    assertEquals("[[[Z", BINARY.eval(boolean[][][].class));
  }
}
