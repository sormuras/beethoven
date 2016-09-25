package com.github.sormuras.beethoven.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InitializerTests {

  @Test
  void enclosing() {
    Initializer initializer = new Initializer();
    assertEquals(null, initializer.getEnclosing());
  }
}
