package com.github.brymck.lpparse;

import org.jetbrains.annotations.NotNull;

public class Variable {
  private @NotNull String name;

  public Variable(@NotNull String name) {
    this.name = name;
  }

  public @NotNull String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Variable)) {
      return super.equals(obj);
    } else {
      Variable other = (Variable) obj;
      return name.equals(other.name);
    }
  }

  @Override
  public @NotNull String toString() {
    return "Variable(name=" + name + ")";
  }
}
