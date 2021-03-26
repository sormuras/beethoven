package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.TypeVariable;
import com.github.sormuras.beethoven.unit.MethodParameter;
import java.lang.annotation.ElementType;
import org.junit.jupiter.api.Test;

class MethodParameterTests {

  private MethodParameter of(Class<?> type, String name) {
    return new MethodParameter().setType(Type.type(type)).setName(name);
  }

  @Test
  void simple() {
    assertEquals("int i", of(int.class, "i").list());
    assertEquals("int... ia1", of(int[].class, "ia1").setVariable(true).list());
    assertEquals("int[]... ia2", of(int[][].class, "ia2").setVariable(true).list());
    MethodParameter parameter =
        new MethodParameter().setType(TypeVariable.variable("T")).setName("t").setFinal(true);
    parameter.addAnnotation(Annotation.annotation(Name.name("A")));
    assertEquals("final @A T t", parameter.list());
    assertEquals(ElementType.PARAMETER, new MethodParameter().getAnnotationsTarget());
    IllegalStateException expected =
        assertThrows(IllegalStateException.class, () -> of(int.class, "i").setVariable(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }
}
