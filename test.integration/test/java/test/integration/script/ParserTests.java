package test.integration.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.script.Parser;
import com.github.sormuras.beethoven.script.Tag;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

class ParserTests {

  private final Parser parser = new Parser();

  @Test
  void action() {
    assertEquals(Tag.INDENT, parser.action(">"));
    assertEquals(Tag.UNINDENT, parser.action("<"));
    assertEquals(Tag.NEWLINE, parser.action("¶"));
    assertEquals(Tag.CLOSE_STATEMENT, parser.action(";"));
    assertEquals(Tag.LITERAL, parser.action("$"));
    assertEquals(Tag.STRING, parser.action("S"));
    assertEquals(Tag.BINARY, parser.action("B"));
    assertEquals(Tag.LISTABLE, parser.action("L"));
    assertEquals(Tag.NAME, parser.action("N"));
    assertEquals(Tag.TYPE, parser.action("T"));
    assertEquals(Tag.INDENT_INC, parser.action(">>"));
    assertEquals(Tag.INDENT_INC, parser.action(">>>"));
    assertEquals(Tag.INDENT_DEC, parser.action("<<"));
    assertEquals(Tag.INDENT_DEC, parser.action("<<<"));
    assertEquals(Tag.REFLECT, parser.action("#test"));
  }

  @Test
  void indent() {
    UnaryOperator<String> operator = source -> parser.parse(source).get(0).toString();
    assertEquals("`>` -> INDENT", operator.apply("{{>}}"));
    assertEquals("`>>` -> INDENT_INC", operator.apply("{{>>}}"));
    assertEquals("`<` -> UNINDENT", operator.apply("{{<}}"));
    assertEquals("`<<` -> INDENT_DEC", operator.apply("{{<<}}"));
  }

  @Test
  void syntaxErrors() {
    assertThrows(Exception.class, () -> parser.parse("{{ missing end marker }"));
  }
}
