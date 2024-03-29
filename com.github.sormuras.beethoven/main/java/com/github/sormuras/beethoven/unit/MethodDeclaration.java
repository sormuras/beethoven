package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.type.ClassType;
import com.github.sormuras.beethoven.type.ReferenceType;
import com.github.sormuras.beethoven.type.Type;
import com.github.sormuras.beethoven.type.TypeVariable;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Method declaration.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.4">JLS 8.4</a>
 */
public class MethodDeclaration extends ClassMember {

  private Block body = null;
  private List<Listable> bodyStatements = new ArrayList<>();
  private List<MethodParameter> parameters = new ArrayList<>();
  private Type returnType = Type.type(void.class);
  private List<ReferenceType> throwables = new ArrayList<>();
  private List<TypeParameter> typeParameters = new ArrayList<>();

  public MethodParameter declareParameter(Class<?> type, String name) {
    return declareParameter(Type.type(type), name);
  }

  public MethodParameter declareParameter(Type type, String name) {
    MethodParameter parameter = new MethodParameter();
    parameter.setType(type);
    parameter.setName(name);
    addParameter(parameter);
    return parameter;
  }

  public void addParameter(MethodParameter declaration) {
    declaration.setMethodDeclaration(this);
    getParameters().add(declaration);
  }

  public void addStatement(Listable listable) {
    bodyStatements.add(listing -> listable.apply(listing).add(';'));
  }

  public void addStatement(String line) {
    bodyStatements.add(l -> l.add(line).add(';'));
  }

  public void addStatement(String source, Object... args) {
    bodyStatements.add(l -> l.eval(source, args).add(';'));
  }

  public void addThrows(Class<?> type) {
    addThrows((ClassType) Type.type(type));
  }

  public void addThrows(ClassType type) {
    getThrows().add(type);
  }

  public void addThrows(TypeVariable type) {
    getThrows().add(type);
  }

  public void addTypeParameter(TypeParameter typeParameter) {
    getTypeParameters().add(typeParameter);
  }

  @Override
  public Listing apply(Listing listing) {
    listing.newline();
    applyAnnotations(listing);
    applyModifiers(listing);
    if (!getTypeParameters().isEmpty()) {
      listing.add('<');
      listing.addAll(getTypeParameters(), ", ");
      listing.add("> ");
    }
    if (isConstructor()) {
      if (getEnclosingDeclaration() != null) {
        listing.add(getEnclosingDeclaration().getName());
      } else {
        listing.add("<init>");
      }
    } else {
      listing.add(getReturnType());
      listing.add(' ');
      listing.add(getName());
    }
    listing.add('(');
    listing.addAll(getParameters(), ", ");
    listing.add(')');
    if (!getThrows().isEmpty()) {
      listing.add(" throws ");
      listing.addAll(getThrows(), ", ");
    }
    Optional<Block> body = getBody();
    if (body.isPresent()) {
      listing.add(' ');
      listing.add(body.get());
    } else if (!bodyStatements.isEmpty()) {
      listing.add(" {").newline().indent(1);
      listing.addAll(bodyStatements, Listable.NEWLINE);
      listing.newline();
      listing.indent(-1).add('}');
      listing.newline();
    } else {
      listing.add(';');
      listing.newline();
    }
    return listing;
  }

  @Override
  public ElementType getAnnotationsTarget() {
    return ElementType.METHOD;
  }

  public Optional<Block> getBody() {
    return Optional.ofNullable(body);
  }

  public List<MethodParameter> getParameters() {
    return parameters;
  }

  public Type getReturnType() {
    return returnType;
  }

  public List<ReferenceType> getThrows() {
    return throwables;
  }

  public List<TypeParameter> getTypeParameters() {
    return typeParameters;
  }

  public boolean isConstructor() {
    return "<init>".equals(getName());
  }

  public boolean isVarArgs() {
    if (getParameters().isEmpty()) {
      return false;
    }
    return getParameters().get(getParameters().size() - 1).isVariable();
  }

  public void setBody(Block body) {
    this.body = body;
  }

  public void setReturnType(java.lang.reflect.Type type) {
    setReturnType(Type.type(type));
  }

  public void setReturnType(Type type) {
    this.returnType = type;
  }

  public void setVarArgs(boolean variable) {
    if (getParameters().isEmpty()) {
      throw new IllegalStateException("no parameters defined");
    }
    getParameters().get(getParameters().size() - 1).setVariable(variable);
  }

  public Listing applyCall(Listing listing) {
    return applyCall(listing, MethodParameter::getName);
  }

  public Listing applyCall(Listing listing, String... names) {
    return applyCall(listing, parameter -> names[getParameters().indexOf(parameter)]);
  }

  public Listing applyCall(Listing listing, Function<MethodParameter, String> function) {
    listing.add(getName());
    listing.add('(');
    if (!getParameters().isEmpty()) {
      boolean first = true;
      for (MethodParameter parameter : getParameters()) {
        if (first) {
          first = false;
        } else {
          listing.add(", ");
        }
        String name = function.apply(parameter);
        listing.add(name);
      }
    }
    return listing.add(')');
  }
}
