package com.github.sormuras.beethoven;

import java.util.function.Function;

public class Importing extends Omitting {

  @Override
  public Function<Name, NameMode> getNameModeFunction() {
    return name -> NameMode.LAST;
  }
}
