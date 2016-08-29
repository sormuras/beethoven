package com.github.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
@Generated("not generated, not visible at runtime")
class JavaAnnotationTest {

  @Test
  void annotations() {
    List<JavaAnnotation> annotations = JavaAnnotation.annotations(JavaAnnotationTest.class);
    assertEquals(1, annotations.size());
    assertEquals(All.class.getCanonicalName(), annotations.get(0).getTypeName().canonical());
    Map<String, List<Listable>> members = annotations.get(0).getMembers();
    assertEquals("1701", members.get("p").get(0).list());
    assertEquals("1701", JavaAnnotation.values(members.get("p")).list());
    assertEquals("{9, 8, 1}", JavaAnnotation.values(members.get("m")).list());
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
    Error error = expectThrows(AssertionError.class, () -> JavaAnnotation.annotation(illegal));
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
        JavaAnnotation.annotation(JavaAnnotationTest.class, All.class).list());
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
        JavaAnnotation.annotation(getClass().getAnnotation(All.class), true).list());
  }

  @Test
  void simpleMarkerAnnotation() {
    JavaAnnotation marker = JavaAnnotation.annotation(Test.class);
    assertEquals("@" + Test.class.getCanonicalName(), marker.list());
    assertEquals("Anno{Name{org.junit.jupiter.api/Test}, members={}}", marker.toString());
  }

  @Test
  void singleElementAnnotation() {
    Class<Generated> type = Generated.class;
    JavaAnnotation tag = JavaAnnotation.annotation(type, "(-:");
    assertEquals("@" + type.getCanonicalName() + "(\"(-:\")", tag.list());
    JavaAnnotation tags = JavaAnnotation.annotation(type, "(", "-", ":");
    assertEquals("@" + type.getCanonicalName() + "({\"(\", \"-\", \":\"})", tags.list());
  }

  @Test
  void singleElementAnnotationUsingEnumValues() {
    JavaAnnotation target = JavaAnnotation.annotation(Target.class, ElementType.TYPE);
    String t = Target.class.getCanonicalName();
    String et = ElementType.class.getCanonicalName();
    assertEquals("@" + t + "(" + et + ".TYPE)", target.list());
    target.addObject("value", ElementType.PACKAGE);
    assertEquals("@" + t + "({" + et + ".TYPE, " + et + ".PACKAGE})", target.list());
  }

  @Test
  void singleElementNotNamedValue() {
    JavaAnnotation a = new JavaAnnotation(Name.name("a", "A"));
    a.addMember("a", JavaAnnotation.value("zzz"));
    assertEquals("@a.A(a = \"zzz\")", a.list());
    a.addMember("b", JavaAnnotation.value(4711));
    assertEquals("@a.A(a = \"zzz\", b = 4711)", a.list());
  }

  @Test
  void value() {
    assertEquals("int.class", JavaAnnotation.value(int.class).list());
    assertEquals("java.lang.Thread.State.class", JavaAnnotation.value(Thread.State.class).list());
    assertEquals("java.lang.Thread.State.NEW", JavaAnnotation.value(Thread.State.NEW).list());
    assertEquals("\"a\"", JavaAnnotation.value("a").list());
    assertEquals("2.718282F", JavaAnnotation.value((float) Math.E).list());
    assertEquals("2.718281828459045", JavaAnnotation.value(Math.E).list());
    assertEquals("9223372036854775807L", JavaAnnotation.value(Long.MAX_VALUE).list());
    assertEquals("'!'", JavaAnnotation.value('!').list());
    assertEquals("null", JavaAnnotation.value(null).list());
    assertEquals("0", JavaAnnotation.value(BigInteger.ZERO).list());
    assertEquals(" ", JavaAnnotation.value(Listable.SPACE).list());
  }

  @Test
  void values() {
    Listable x = listing -> listing.add('x');
    assertSame(Listable.IDENTITY, JavaAnnotation.values(Collections.emptyList()));
    assertEquals("x", JavaAnnotation.values(Collections.singletonList(x)).list());
    assertEquals("{x, x}", JavaAnnotation.values(Arrays.asList(x, x)).list());
  }
}
