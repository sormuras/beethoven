package test.integration;

import static com.github.sormuras.beethoven.Compilation.compile;
import static com.github.sormuras.beethoven.Compilation.source;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.sormuras.beethoven.Compilation;
import java.lang.reflect.Method;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class CompilationTests {

  @Test
  void compileString() {
    assertEquals("€", compile("enum €{}").getName());
    assertEquals("A", compile("enum A {}").getName());
    assertEquals("A", compile("class A {}").getName());
    assertEquals("A", compile("interface A {}").getName());
    assertEquals("A", compile("@interface A {}").getName());
    assertEquals("a.A", compile("package a; enum A {}").getName());
    assertEquals("a.b.A", compile("package a.b; class A {}").getName());
    assertEquals("a.b.c.A", compile("package a.b.c; interface A {}").getName());
    assertEquals("a.b.c.d.A", compile("package a.b.c.d; @interface A {}").getName());
    assertThrows(IllegalArgumentException.class, () -> compile(""));
    assertThrows(IllegalArgumentException.class, () -> compile("package abc;"));
    assertThrows(RuntimeException.class, () -> compile("A", "enum E {}"));
  }

  @Test
  void hi() throws Exception {
    String code = "public class Hi { public String greet(String who) { return \"Hi \" + who;}}";
    JavaFileObject file = source("Hi.java", code);
    ClassLoader loader = compile(file);
    Class<?> hiClass = loader.loadClass("Hi");
    Object hiInstance = hiClass.getConstructor().newInstance();
    Method greetMethod = hiClass.getMethod("greet", String.class);
    assertEquals("Hi world", greetMethod.invoke(hiInstance, "world"));
    assertThrows(ClassNotFoundException.class, () -> loader.loadClass("Hello"));
  }

  @Test
  void lastModified() {
    JavaFileObject jfo = new Compilation.CharContentFileObject("abc", "abc");
    assertNotEquals(0L, jfo.getLastModified());
  }

  @Test
  void multipleClassesWithDependingOnEachOther() {
    String codeA = "package a; public class A {}";
    String codeB = "package b; class B extends a.A {}";
    JavaFileObject fileA = source("listing/A.java", codeA);
    JavaFileObject fileB = source("listing/B.java", codeB);
    compile(fileA, fileB);
  }

  @Test
  void syntaxError() {
    assertThrows(Exception.class, () -> compile(source("F.java", "class 1F {}")));
  }
}
