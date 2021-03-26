package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Annotation;
import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * An annotation type declaration specifies a new annotation type, a special kind of interface type.
 * To distinguish an annotation type declaration from a normal interface declaration, the keyword
 * {@code interface} is preceded by an at-sign <code>@</code>.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.6">JLS 9.6</a>
 */
public class AnnotationDeclaration extends TypeDeclaration {

  private final List<ConstantDeclaration> constants = new ArrayList<>();
  private final List<AnnotationElement> elements = new ArrayList<>();

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyAnnotations(listing);
    applyModifiers(listing);
    listing.add("@interface").add(' ').add(getName());
    listing.add(' ').add('{').newline();
    listing.indent(1);
    if (!isDeclarationsEmpty()) {
      getDeclarations().forEach(listing::add);
    }
    getConstants().forEach(listing::add);
    getElements().forEach(listing::add);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  /** Add new annotation field. */
  public ConstantDeclaration declareConstant(Type type, String name, Listable initializer) {
    ConstantDeclaration constant = new ConstantDeclaration();
    constant.setEnclosingDeclaration(this);
    constant.setCompilationUnit(getCompilationUnit());
    constant.setName(name);
    constant.setType(type);
    constant.setInitializer(initializer);
    getConstants().add(constant);
    return constant;
  }

  /** Add new annotation field. */
  public ConstantDeclaration declareConstant(Type type, String name, Object value) {
    return declareConstant(type, name, Annotation.value(value));
  }

  /** Add new annotation method w/o default value. */
  public AnnotationElement declareElement(Type returnType, String name) {
    return declareElement(returnType, name, null);
  }

  /** Add new annotation method with default value. */
  public AnnotationElement declareElement(Type returnType, String name, Listable defaultValue) {
    AnnotationElement element = new AnnotationElement();
    element.setEnclosingDeclaration(this);
    element.setCompilationUnit(getCompilationUnit());
    element.setName(name);
    element.setReturnType(returnType);
    element.setDefaultValue(defaultValue);
    getElements().add(element);
    return element;
  }

  /** Add new annotation method with default value. */
  public AnnotationElement declareElement(Type returnType, String name, Object defaultValue) {
    return declareElement(returnType, name, Annotation.value(defaultValue));
  }

  public List<ConstantDeclaration> getConstants() {
    return constants;
  }

  public List<AnnotationElement> getElements() {
    return elements;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && getElements().isEmpty() && getConstants().isEmpty();
  }
}
