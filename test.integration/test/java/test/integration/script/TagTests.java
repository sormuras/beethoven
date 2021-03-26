package test.integration.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.script.Tag;
import org.junit.jupiter.api.Test;

class TagTests {

  @Test
  void reflectNullFails() {
    assertThrows(NullPointerException.class, () -> Tag.reflect(null, 0));
    assertThrows(NullPointerException.class, () -> Tag.reflect("#", null));
    assertThrows(Exception.class, () -> Tag.reflect("#thisDoesNotExist", new Object()));
  }

  @Test
  void reflectWithAutoBoxing() {
    assertEquals(1, Tag.reflect("#", 1));
    assertEquals(Integer.class, Tag.reflect("#class", 1));
    assertEquals("Integer", Tag.reflect("#class.simpleName", 1));
    assertEquals(30, Tag.reflect("#class.simpleName.hashCode.byteValue.intValue", 1));
  }
}
