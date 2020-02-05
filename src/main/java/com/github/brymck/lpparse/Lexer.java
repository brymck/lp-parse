package com.github.brymck.lpparse;

import java.io.InputStream;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

class Lexer implements Iterable<Token> {
  private final @NotNull TokenIterator tokenIterator;

  public Lexer(@NotNull InputStream inputStream) {
    tokenIterator = new TokenIterator(inputStream);
  }

  @Override
  public @NotNull Iterator<@NotNull Token> iterator() {
    return tokenIterator;
  }
}
