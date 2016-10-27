/*
 * Copyright (C) 2016 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
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
class AnnotationTests {

  @Test
  void annotations() {
    List<Annotation> annotations = Annotation.annotations(AnnotationTests.class);
    assertEquals(1, annotations.size());
    assertEquals(All.class.getCanonicalName(), annotations.get(0).getTypeName().canonical());
    Map<String, List<Listable>> members = annotations.get(0).getMembers();
    assertEquals("1701", members.get("p").get(0).list());
    assertEquals("1701", Annotation.values(members.get("p")).list());
    assertEquals("{9, 8, 1}", Annotation.values(members.get("m")).list());
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
    Error error = assertThrows(AssertionError.class, () -> Annotation.annotation(illegal));
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
            + "l = Override.class, "
            + "m = {9, 8, 1}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient, "
            + "r = {Float.class, Double.class}"
            + ")",
        Annotation.annotation(AnnotationTests.class, All.class).list());
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
            + "i = Thread.State.BLOCKED, "
            + "j = @java.lang.annotation.Documented, "
            + "k = \"kk\", "
            + "l = Override.class, "
            + "m = {9, 8, 1}, "
            + "n = {java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.METHOD}, "
            + "o = @java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE), "
            + "p = 1701, "
            + "q = @java.beans.Transient(true), "
            + "r = {Float.class, Double.class}"
            + ")",
        Annotation.annotation(getClass().getAnnotation(All.class), true).list());
  }

  @Test
  void simpleMarkerAnnotation() {
    Annotation marker = Annotation.annotation(Test.class);
    assertEquals("@" + Test.class.getCanonicalName(), marker.list());
    assertEquals("Annotation{Name{org.junit.jupiter.api/Test}, members={}}", marker.toString());
  }

  @Test
  void singleElementAnnotation() {
    Class<Generated> type = Generated.class;
    Annotation tag = Annotation.annotation(type, "(-:");
    assertEquals("@" + type.getCanonicalName() + "(\"(-:\")", tag.list());
    Annotation tags = Annotation.annotation(type, "(", "-", ":");
    assertEquals("@" + type.getCanonicalName() + "({\"(\", \"-\", \":\"})", tags.list());
  }

  @Test
  void singleElementAnnotationUsingEnumValues() {
    Annotation target = Annotation.annotation(Target.class, ElementType.TYPE);
    String t = Target.class.getCanonicalName();
    String et = ElementType.class.getCanonicalName();
    assertEquals("@" + t + "(" + et + ".TYPE)", target.list());
    target.addObject("value", ElementType.PACKAGE);
    assertEquals("@" + t + "({" + et + ".TYPE, " + et + ".PACKAGE})", target.list());
  }

  @Test
  void singleElementNotNamedValue() {
    Annotation a = new Annotation(Name.name("a", "A"));
    a.addMember("a", Annotation.value("zzz"));
    assertEquals("@a.A(a = \"zzz\")", a.list());
    a.addMember("b", Annotation.value(4711));
    assertEquals("@a.A(a = \"zzz\", b = 4711)", a.list());
  }

  @Test
  void value() {
    assertEquals("int.class", Annotation.value(int.class).list());
    assertEquals("Thread.State.class", Annotation.value(Thread.State.class).list());
    assertEquals("Thread.State.NEW", Annotation.value(Thread.State.NEW).list());
    assertEquals("\"a\"", Annotation.value("a").list());
    assertEquals("2.718282F", Annotation.value((float) Math.E).list());
    assertEquals("2.718281828459045", Annotation.value(Math.E).list());
    assertEquals("9223372036854775807L", Annotation.value(Long.MAX_VALUE).list());
    assertEquals("'!'", Annotation.value('!').list());
    assertEquals("null", Annotation.value(null).list());
    assertEquals("0", Annotation.value(BigInteger.ZERO).list());
    assertEquals(" ", Annotation.value(Listable.SPACE).list());
  }

  @Test
  void values() {
    Listable x = listing -> listing.add('x');
    assertSame(Listable.IDENTITY, Annotation.values(List.of()));
    assertEquals("x", Annotation.values(List.of(x)).list());
    assertEquals("{x, x}", Annotation.values(List.of(x, x)).list());
  }
}
