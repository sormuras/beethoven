package com.github.sormuras.beethoven.unit;

import com.github.sormuras.beethoven.Listable;
import com.github.sormuras.beethoven.Listing;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A block is a sequence of statements, local class declarations and local variable declaration
 * statements within braces.
 *
 * <p>A local class is a nested class that is not a member of any class and that has a name.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-14.html#jls-14.2">JLS
 *     14.2</a>
 */
public class Block implements Listable {

  // { LocalVariableDeclarationStatement, Statement, ClassDeclaration }
  private List<Listable> sequence = new ArrayList<>();

  public Block add(Listable listable) {
    sequence.add(listable);
    return this;
  }

  public Block add(String... lines) {
    for (String line : lines) {
      sequence.add(l -> l.add(line).newline());
    }
    return this;
  }

  public Block eval(String source, Object... args) {
    return add(listing -> listing.eval(source, args));
  }

  @Override
  public Listing apply(Listing listing) {
    listing.add('{').newline().indent(1);
    getSequence().forEach(listing::add);
    listing.indent(-1).add('}').newline();
    return listing;
  }

  /**
   * Declare an enum that is not a member of any class and that has a name.
   *
   * @param name the simple local enum name
   * @return enum declaration instance
   */
  public EnumDeclaration declareLocalEnum(String name) {
    return declareLocal(EnumDeclaration::new, name);
  }

  /**
   * Declare a normal local class that is not a member of any class and that has a name.
   *
   * @param name the simple local class name
   * @return normal class declaration instance
   */
  public NormalClassDeclaration declareLocalClass(String name) {
    return declareLocal(NormalClassDeclaration::new, name);
  }

  public <C extends ClassDeclaration> C declareLocal(Supplier<C> supplier, String name) {
    C declaration = supplier.get();
    declaration.setLocal(true);
    declaration.setName(name);
    getSequence().add(declaration);
    return declaration;
  }

  public List<Listable> getSequence() {
    return sequence;
  }
}
