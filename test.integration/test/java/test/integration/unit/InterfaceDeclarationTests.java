package test.integration.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.sormuras.beethoven.Name;
import test.integration.Tests;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.unit.InterfaceDeclaration;
import com.github.sormuras.beethoven.unit.TypeDeclaration;
import com.github.sormuras.beethoven.unit.TypeParameter;
import java.lang.annotation.ElementType;
import java.math.BigInteger;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class InterfaceDeclarationTests {

  @Test
  void empty() {
    TypeDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Empty");
    assertEquals("interface Empty {\n}\n", declaration.list("\n"));
  }

  @Test
  void everything() {
    InterfaceDeclaration declaration = new InterfaceDeclaration();
    declaration.setName("Everything");
    declaration.addTypeParameter(new TypeParameter());
    declaration.addInterface(Type.type(Runnable.class));
    declaration.declareConstant(Type.type(String.class), "EMPTY_TEXT", "");
    declaration
        .declareConstant(Type.type(float.class), "PI", l -> l.add("3.141F"))
        .addAnnotation(Deprecated.class);
    declaration.declareConstant(Type.type(double.class), "E", Name.name(Math.class, "E"));
    declaration.declareMethod(BigInteger.class, "id");
    Tests.assertEquals(getClass(), "everything", declaration);
  }

  @Test
  void nested() {
    InterfaceDeclaration top = new InterfaceDeclaration();
    top.setName("Top");
    InterfaceDeclaration nested = top.declareInterface("Nested");
    InterfaceDeclaration base64 = nested.declareInterface("Base64");
    Consumer<InterfaceDeclaration> constants =
        declaration -> {
          declaration.declareConstant(top.toType(), "topper", (Object) null);
          declaration.declareConstant(nested.toType(), "nested", (Object) null);
          declaration.declareConstant(base64.toType(), "base64", (Object) null);
        };
    constants.accept(top);
    constants.accept(nested);
    constants.accept(base64);
    Tests.assertEquals(getClass(), "nested", top);
  }

  @Test
  void target() {
    assertEquals(ElementType.TYPE, new InterfaceDeclaration().getAnnotationsTarget());
  }
}
