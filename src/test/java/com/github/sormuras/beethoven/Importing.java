package com.github.sormuras.beethoven;

import java.util.function.Function;

public class Importing extends Omitting {

  private final NameMode mode;

  public Importing() {
    this(NameMode.SIMPLE);
  }

  public Importing(NameMode mode) {
    this.mode = mode;
  }

  @Override
  public Function<Name, NameMode> getNameModeFunction() {
    return name -> mode;
  }
}
