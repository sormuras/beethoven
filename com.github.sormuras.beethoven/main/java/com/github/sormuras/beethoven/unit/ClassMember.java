package com.github.sormuras.beethoven.unit;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.lang.model.element.Modifier;

/**
 * Class <b>member</b> declaration adds modifier support.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.6">JLS 8</a>
 */
public abstract class ClassMember extends NamedMember implements Modifiable {

  private Set<Modifier> modifiers = Collections.emptySet();

  @Override
  public Set<Modifier> getModifiers() {
    if (modifiers == Collections.EMPTY_SET) {
      modifiers = EnumSet.noneOf(Modifier.class);
    }
    return modifiers;
  }

  @Override
  public boolean isModified() {
    return !modifiers.isEmpty();
  }
}
