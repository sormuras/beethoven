package com.github.sormuras.beethoven;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListableTest {

  @Test
  void identity() {
    assertTrue(Listable.IDENTITY.isEmpty());
    assertEquals("", Listable.IDENTITY.list());
    assertEquals("", Listable.IDENTITY.list(new Listing()));
    assertEquals("Listable.IDENTITY", Listable.IDENTITY.toString());
  }

  @Test
  void simple() {
    Listable a = listing -> listing.add('a');
    assertEquals("a", a.list());
    assertFalse(a.isEmpty());
  }
}
