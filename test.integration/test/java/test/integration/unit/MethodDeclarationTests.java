package test.integration.unit;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.TypeParameter;
import test.integration.Tests;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import java.util.List;
import org.junit.jupiter.api.Test;

class MethodDeclarationTests {

  @Test
  void constructor() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("<init>");
    assertEquals("<init>();\n", m.list("\n"));
    assertEquals(ElementType.METHOD, m.getAnnotationsTarget());
    assertEquals(true, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = assertThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
    // put into context
    // m.setEnclosingDeclaration(NormalClassDeclaration.of("Abc"));
    // assertEquals("Abc();\n", m.list());
  }

  @Test
  void declaration() {
    MethodDeclaration m = new MethodDeclaration();
    m.setName("m");
    assertEquals("void m();\n", m.list("\n"));
    assertEquals("m()", m.applyCall(new Listing()).toString());
    assertEquals(ElementType.METHOD, m.getAnnotationsTarget());
    assertEquals(false, m.isConstructor());
    assertEquals(false, m.isModified());
    assertEquals(false, m.isVarArgs());
    Exception expected = assertThrows(IllegalStateException.class, () -> m.setVarArgs(true));
    assertEquals(true, expected.toString().contains("no parameter"));
  }

  @Test
  void emptyList() {
    ClassType listOfT =
        ClassType.type(List.class).parameterized(i -> singletonList(TypeVariable.variable("T")));
    MethodDeclaration emptyList = new MethodDeclaration();
    emptyList.addAnnotation(SuppressWarnings.class, "unchecked");
    emptyList.addModifier("public", "static", "final");
    emptyList.addTypeParameter(new TypeParameter());
    emptyList.setReturnType(listOfT);
    emptyList.setName("emptyList");
    emptyList.addStatement("return ({{L}}) EMPTY_LIST", listOfT);
    Tests.assertEquals(getClass(), "emptyList", emptyList);
  }

  @Test
  void runnable() {
    MethodDeclaration runnable = new MethodDeclaration();
    runnable.addAnnotation(Override.class);
    runnable.addModifier("public");
    runnable.setName("run");
    runnable.declareParameter(getClass(), "this");
    runnable.addThrows(RuntimeException.class);
    runnable.addThrows(TypeVariable.variable("X"));
    runnable.addStatement("System.out.println({{S}})", "Running!");
    Tests.assertEquals(getClass(), "runnable", runnable);
    assertEquals(true, runnable.isModified());
    assertEquals(false, runnable.isVarArgs());
    assertSame(runnable, runnable.getParameters().get(0).getMethodDeclaration().get());
    assertEquals("run(this)", runnable.applyCall(new Listing()).toString());
    Exception expected = assertThrows(IllegalStateException.class, () -> runnable.setVarArgs(true));
    assertEquals(true, expected.toString().contains("array type expected"));
  }

  @Test
  void varArgs() {
    MethodDeclaration var = new MethodDeclaration();
    var.setName("var");
    var.declareParameter(Type.type(int[].class), "numbers");
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list("\n"));
    var.setVarArgs(true);
    assertEquals(true, var.isVarArgs());
    assertEquals("void var(int... numbers);\n", var.list("\n"));
    var.setVarArgs(false);
    assertEquals(false, var.isVarArgs());
    assertEquals("void var(int[] numbers);\n", var.list("\n"));
  }

  @Test
  void callWithoutArgument() {
    MethodDeclaration method = new MethodDeclaration();
    method.setName("noArg");
    assertEquals("noArg()", method.applyCall(new Listing()).toString());
  }

  @Test
  void callWithSingleArgument() {
    MethodDeclaration method = new MethodDeclaration();
    method.setName("singleArg");
    method.declareParameter(int.class, "value");
    assertEquals("singleArg(value)", method.applyCall(new Listing()).toString());
    assertEquals("singleArg(x)", method.applyCall(new Listing(), "x").toString());
  }

  @Test
  void callWithManyArguments() {
    MethodDeclaration method = new MethodDeclaration();
    method.setName("manyArgs");
    method.declareParameter(int.class, "a1");
    method.declareParameter(byte.class, "a2");
    method.declareParameter(short.class, "a3");
    assertEquals("manyArgs(a1, a2, a3)", method.applyCall(new Listing()).toString());
    assertEquals("manyArgs(x, y, z)", method.applyCall(new Listing(), "x", "y", "z").toString());
  }
}
