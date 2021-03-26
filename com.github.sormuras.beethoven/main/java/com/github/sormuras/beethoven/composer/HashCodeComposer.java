package com.github.sormuras.beethoven.composer;

import com.github.sormuras.beethoven.Listing;
import com.github.sormuras.beethoven.Name;
import com.github.sormuras.beethoven.unit.ClassDeclaration;
import com.github.sormuras.beethoven.unit.FieldDeclaration;
import com.github.sormuras.beethoven.unit.MethodDeclaration;
import com.github.sormuras.beethoven.unit.NamedMember;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public class HashCodeComposer implements Function<ClassDeclaration, MethodDeclaration> {

  @Override
  public MethodDeclaration apply(ClassDeclaration declaration) {
    MethodDeclaration method = declaration.declareMethod(int.class, "hashCode");
    method.addAnnotation(Override.class);
    method.setModifiers(Modifier.PUBLIC);
    if (declaration.getFields().isEmpty()) {
      method.addStatement(this::applyNoField);
      return method;
    }
    if (declaration.getFields().size() == 1) {
      method.addStatement(listing -> applySingleField(listing, declaration.getFields().get(0)));
      return method;
    }
    method.addStatement(listing -> applyForFields(listing, declaration.getFields()));
    return method;
  }

  public Listing applyNoField(Listing listing) {
    listing.add("return ");
    listing.add(Name.reflect(Objects.class, "hashCode"));
    listing.add("(this)");
    return listing;
  }

  public Listing applySingleField(Listing listing, FieldDeclaration field) {
    listing.add("return ");
    listing.add(Name.reflect(Objects.class, "hashCode"));
    listing.add('(');
    listing.add(field.getName());
    listing.add(')');
    return listing;
  }

  public Listing applyForFields(Listing listing, List<FieldDeclaration> fields) {
    List<String> names = fields.stream().map(NamedMember::getName).collect(Collectors.toList());
    listing.add("return ");
    listing.add(Name.reflect(Objects.class, "hash"));
    listing.add('(');
    listing.add(String.join(", ", names));
    listing.add(')');
    return listing;
  }
}
