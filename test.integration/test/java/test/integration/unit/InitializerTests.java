package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.unit.Initializer;
import org.junit.jupiter.api.Test;

class InitializerTests {

  @Test
  void enclosing() {
    Initializer initializer = new Initializer();
    assertEquals(null, initializer.getEnclosing());
  }
}
