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

/**
 * Name emission style.
 *
 * <p>{@code Style#CANONICAL}: Canonical, like:
 *
 * <pre>
 *    "java.util.Objects"
 *    "java.lang.Thread.State.NEW"
 *    "java.util.Collections.sort"
 * </pre>
 *
 * {@code Style#LAST}: Single (type and static) imports only emit last name, like:
 *
 * <pre>
 *    import java.util.Objects                 - "Objects"
 *    import static java.util.Collections.sort - "sort"
 * </pre>
 *
 * {@code Style#LAST}: On-demand _static_ import, like:
 *
 * <pre>
 *   import static java.lang.Thread.State.*
 *     name("java.lang.Thread.State.BLOCKED")   - "BLOCKED"
 *     name("java.lang.Thread.State.NEW")       - "NEW"
 *     name("java.lang.Thread.State.WAITING")   - "WAITING"
 * </pre>
 *
 * {@code Style#SIMPLE} On-demand _package_ imports only emit all simple names, like:
 *
 * <pre>
 *   import java.lang.*
 *     name("java.lang.Object")                 - "Object"
 *     name("java.lang.Thread")                 - "Thread"
 *     name("java.lang.Thread.State")           - "Thread.State"
 *     name("java.lang.Thread.State.RUNNABLE")  - "Thread.State.RUNNABLE"
 * </pre>
 */
public enum Style {
  /** Emit canonical name, like entire {@code java.lang.Thread.State.BLOCKED}. */
  CANONICAL,

  /** Emit last name only, like {@code BLOCKED} for {@code java.lang.Thread.State.BLOCKED}. */
  LAST,

  /** Emit all simples names, like {@code Thread.State.BLOCKED} without the package name. */
  SIMPLE;

  /** Convenient for {@code auto("", name)} passing unnamed package as the current one. */
  public static Style auto(Name name) {
    return auto("", name);
  }

  /**
   * A compilation unit automatically has access to all types declared in its package and also
   * automatically imports all of the public types declared in the predefined package {@code
   * java.lang}.
   *
   * @return {@code #SIMPLE} if the package names match or of the name points to a type declared in
   *     {@code java.lang} package. Else, {@code #CANONICAL} is returned.
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html">JLS 7</a>
   */
  public static Style auto(String contextPackageName, Name name) {
    if (name.packageName().equals(contextPackageName)) {
      return Style.SIMPLE;
    }
    return name.isJavaLangPackage() ? Style.SIMPLE : Style.CANONICAL;
  }

  /** Return this style for all names. */
  public Styling styling() {
    return name -> this;
  }
}
