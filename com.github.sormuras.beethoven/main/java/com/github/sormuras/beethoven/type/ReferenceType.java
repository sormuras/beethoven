package com.github.sormuras.beethoven.type;

import com.github.sormuras.beethoven.Annotation;
import java.util.List;

/**
 * There are four kinds of reference types: class types (§8.1), interface types (§9.1), type
 * variables (§4.4), and array types (§10.1).
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.3">JLS 4.3</a>
 */
public abstract class ReferenceType extends Type {

  ReferenceType(List<Annotation> annotations) {
    super(annotations);
  }
}
