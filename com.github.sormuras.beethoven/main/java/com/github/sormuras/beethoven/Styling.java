package com.github.sormuras.beethoven;

import java.util.function.Function;

/** Determines the correct name emission style for the current context. */
public interface Styling extends Function<Name, Style> {}
