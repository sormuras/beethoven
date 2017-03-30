/*
 * Copyright 2017 Christian Stein
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.sormuras.beethoven;

import java.beans.Transient;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Native;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface All {

  byte a() default 5;

  short b() default 6;

  int c() default 7;

  long d() default 8;

  float e() default 9.0f;

  double f() default 10.0;

  char[] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

  boolean h() default true;

  Thread.State i() default Thread.State.BLOCKED;

  Documented j() default @Documented;

  String k() default "kk";

  Class<? extends java.lang.annotation.Annotation> l() default Native.class;

  int[] m() default {1, 2, 3};

  ElementType[] n() default {ElementType.FIELD, ElementType.METHOD};

  Target o();

  int p();

  Transient q() default @Transient(value = false);

  Class<? extends Number>[] r() default {Byte.class, Short.class, Integer.class, Long.class};
}
