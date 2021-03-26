package test.integration.script;

import static com.github.sormuras.beethoven.script.Tag.BINARY;
import static com.github.sormuras.beethoven.script.Tag.CLOSE_STATEMENT;
import static com.github.sormuras.beethoven.script.Tag.INDENT;
import static com.github.sormuras.beethoven.script.Tag.LISTABLE;
import static com.github.sormuras.beethoven.script.Tag.LITERAL;
import static com.github.sormuras.beethoven.script.Tag.NAME;
import static com.github.sormuras.beethoven.script.Tag.NEWLINE;
import static com.github.sormuras.beethoven.script.Tag.REFLECT;
import static com.github.sormuras.beethoven.script.Tag.STRING;
import static com.github.sormuras.beethoven.script.Tag.TYPE;
import static com.github.sormuras.beethoven.script.Tag.UNINDENT;
import static com.github.sormuras.beethoven.type.ClassType.parameterized;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.script.Action;
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
  void defaults() {
    Action noop = (l, t, a) -> l;
    assertEquals(Action.Consumes.ALL, noop.consumes());
    assertFalse(noop.handles(""));
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
    assertEquals("o\n;\n", execute(CLOSE_STATEMENT).toString());
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
    assertEquals("Byte", execute(REFLECT, "#class.simpleName", Byte.MAX_VALUE));
  }
}
