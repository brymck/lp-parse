package com.github.brymck.lpparse;

import org.jetbrains.annotations.NotNull;

final class Token {
  private @NotNull String value;
  private @NotNull TokenType type;

  public Token(@NotNull String value, @NotNull TokenType type) {
    this.value = value;
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public TokenType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Token(value=" + value + ", type=" + type.toString() + ")";
  }
}
