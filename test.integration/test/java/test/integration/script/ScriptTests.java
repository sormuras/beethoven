package test.integration.script;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.script.Script;
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
