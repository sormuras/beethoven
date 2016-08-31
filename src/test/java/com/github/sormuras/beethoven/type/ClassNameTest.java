package com.github.sormuras.beethoven.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.JavaAnnotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.V;
import org.junit.jupiter.api.Test;

class ClassNameTest {

  @Test
  void annotated() throws Exception {
    ClassName name = new ClassName();
    name.setName("Name");
    assertEquals("Name", name.list());
    name.addAnnotation(JavaAnnotation.annotation(Name.name("UUU")));
    assertEquals("@UUU Name", name.list());
    name.addAnnotation(V.class);
    assertEquals("@UUU " + V.USE + " Name", name.list());
  }
}
