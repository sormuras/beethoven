package com.github.sormuras.beethoven;

import java.util.function.Function;

public class Omitting extends Listing {

  @Override
  public Function<Name, NameMode> getNameModeFunction() {
    return name -> name.isJavaLangPackage() ? NameMode.SIMPLE : NameMode.CANONICAL;
  }
}
