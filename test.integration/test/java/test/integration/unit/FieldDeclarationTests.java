package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.unit.FieldDeclaration;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class FieldDeclarationTests {

  @Test
  void empty() {
    FieldDeclaration i = new FieldDeclaration();
    i.setType(Type.type(int.class));
    i.setName("i");
    assertEquals("int i;\n", i.list("\n"));
    assertEquals(ElementType.FIELD, i.getAnnotationsTarget());
    assertEquals(false, i.isModified());
    assertNull(i.getEnclosingDeclaration());
    i.setInitializer(l -> l.add(Integer.toString(4711)));
    assertEquals("int i = 4711;\n", i.list("\n"));
  }
}
