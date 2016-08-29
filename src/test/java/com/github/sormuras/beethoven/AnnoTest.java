package com.github.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.math.BigInteger;
import javax.annotation.Generated;
import org.junit.jupiter.api.Test;

@All(
  o = @Target(ElementType.TYPE),
  p = 1701,
  f = 11.1,
  e = (float) Math.E,
  m = {9, 8, 1},
  l = Override.class,
  j = @Documented,
  q = @Transient,
  r = {Float.class, Double.class}
)
class AnnoTest {

  @Test
  void annos() {
    assertEquals(1, Anno.annos(AnnoTest.class.getAnnotations()).size());
  }

  @Test
  void illegalAnnotationFails() {
    java.lang.annotation.Annotation illegal =
        Tests.proxy(
            java.lang.annotation.Annotation.class,
            (proxy, method, args) -> {
              if (method.getName().equals("annotationType")) {
                return Generated.class;
              }
              if (method.getName().equals("toString")) {
                return "IllegalAnnotation";
              }
              throw new AssertionError("IllegalAnnotation");
            });
    Error error = expectThrows(AssertionError.class, () -> Anno.anno(illegal));
    assertTrue(error.getMessage().startsWith("Reflecting IllegalAnnotation failed:"));
  }

  @Test
  void reflect() {
    assertEquals(
        "@"
            + All.class.getCanonicalName()
            + "("
            + "e = 2.718282F, "
            + "f = 11.1, "
            + "l = java.lang.Override.class, "
            + "m = {9, 8, 1}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient, "
            + "r = {java.lang.Float.class, java.lang.Double.class}"
            + ")",
        Anno.anno(AnnoTest.class, All.class).list());
  }

  @Test
  void reflectWithDefaults() {
    assertEquals(
        "@"
            + All.class.getCanonicalName()
            + "("
            + "a = 5, "
            + "b = 6, "
            + "c = 7, "
            + "d = 8L, "
            + "e = 2.718282F, "
            + "f = 11.1, "
            + "g = {'\\u0000', '쫾', 'z', '€', 'ℕ', '\"', '\\'', '\\t', '\\n'}, "
            + "h = true, "
            + "i = java.lang.Thread.State.BLOCKED, "
            + "j = @java.lang.annotation.Documented, "
            + "k = \"kk\", "
            + "l = java.lang.Override.class, "
            + "m = {9, 8, 1}, "
            + "n = {java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient(true), "
            + "r = {java.lang.Float.class, java.lang.Double.class}"
            + ")",
        Anno.anno(getClass().getAnnotation(All.class), true).list());
  }

  @Test
  void simpleMarkerAnnotation() {
    Anno marker = Anno.anno(Test.class);
    assertEquals("@" + Test.class.getCanonicalName(), marker.list());
    assertEquals("Anno{Name{org.junit.jupiter.api/Test}, members={}}", marker.toString());
  }

  @Test
  void singleElementAnnotation() {
    Class<Generated> type = Generated.class;
    Anno tag = Anno.anno(type, "(-:");
    assertEquals("@" + type.getCanonicalName() + "(\"(-:\")", tag.list());
    Anno tags = Anno.anno(type, "(", "-", ":");
    assertEquals("@" + type.getCanonicalName() + "({\"(\", \"-\", \":\"})", tags.list());
  }

  @Test
  void singleElementAnnotationUsingEnumValues() {
    Anno target = Anno.anno(Target.class, ElementType.TYPE);
    String t = Target.class.getCanonicalName();
    String et = ElementType.class.getCanonicalName();
    assertEquals("@" + t + "(" + et + ".TYPE)", target.list());
    target.addObject("value", ElementType.PACKAGE);
    assertEquals("@" + t + "({" + et + ".TYPE, " + et + ".PACKAGE})", target.list());
  }

  @Test
  void singleElementNotNamedValue() {
    Anno a = new Anno(Name.name("a", "A"));
    a.addMember("a", Anno.value("zzz"));
    assertEquals("@a.A(a = \"zzz\")", a.list());
    a.addMember("b", Anno.value(4711));
    assertEquals("@a.A(a = \"zzz\", b = 4711)", a.list());
  }

  @Test
  void value() {
    assertEquals("int.class", Anno.value(int.class).list());
    assertEquals("java.lang.Thread.State.class", Anno.value(Thread.State.class).list());
    assertEquals("java.lang.Thread.State.NEW", Anno.value(Thread.State.NEW).list());
    assertEquals("\"a\"", Anno.value("a").list());
    assertEquals("2.718282F", Anno.value((float) Math.E).list());
    assertEquals("2.718281828459045", Anno.value(Math.E).list());
    assertEquals("9223372036854775807L", Anno.value(Long.MAX_VALUE).list());
    assertEquals("'!'", Anno.value('!').list());
    assertEquals("null", Anno.value(null).list());
    assertEquals("0", Anno.value(BigInteger.ZERO).list());
    assertEquals(" ", Anno.value(Listable.SPACE).list());
  }
}
