package com.github.sormuras.beethoven;

import java.util.function.Function;

public class Importing extends Listing {

  private final Function<Name, NameMode> function;

  public Importing() {
    this(NameMode.SIMPLE);
  }

  public Importing(NameMode mode) {
    this.function = mode.function();
  }

  @Override
  public Function<Name, NameMode> getNameModeFunction() {
    return function;
  }
}
