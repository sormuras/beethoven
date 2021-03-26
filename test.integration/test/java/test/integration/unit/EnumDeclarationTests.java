package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Listable;
import test.integration.Tests;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.EnumDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.NormalClassDeclaration;
import java.lang.annotation.ElementType;
import javax.lang.model.element.Modifier;
import org.junit.jupiter.api.Test;

class EnumDeclarationTests {

  @Test
  void empty() {
    ClassDeclaration declaration = new EnumDeclaration();
    declaration.setName("Empty");
    assertEquals("enum Empty {\n}\n", declaration.list("\n"));
  }

  @Test
  void everything() {
    EnumDeclaration declaration = new EnumDeclaration();
    declaration.setName("Everything");
    declaration.addInterface(Type.type(Runnable.class));
    assertEquals("enum Everything implements Runnable {\n}\n", declaration.list("\n"));
    declaration.addInterface(ClassType.parameterized(Comparable.class, Byte.class));
    assertEquals(
        "enum Everything implements Runnable, Comparable<Byte> {\n}\n", declaration.list("\n"));
    declaration.getInterfaces().clear();
    declaration.declareConstant("A");
    assertEquals("enum Everything {\n\n  A\n}\n", declaration.list("\n"));
    declaration.declareConstant("B", Listable.IDENTITY).addAnnotation(Deprecated.class);
    NormalClassDeclaration cbody = new NormalClassDeclaration();
    MethodDeclaration toString = cbody.declareMethod(String.class, "toString");
    toString.addStatement("return \"c\" + i");
    toString.addModifier(Modifier.PUBLIC);
    declaration.declareConstant("C", l -> l.add("123"), cbody);
    declaration.declareField(int.class, "i");
    declaration.declareConstructor().addStatement("this(0)");
    MethodDeclaration ctor = declaration.declareConstructor();
    ctor.declareParameter(int.class, "i");
    ctor.addStatement("this.i = i");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new EnumDeclaration().getAnnotationsTarget());
  }
}
