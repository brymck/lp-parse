package com.github.brymck.lpparse;

import org.jetbrains.annotations.NotNull;

public class Objective {
  private @NotNull Sense sense;
  private @NotNull Statement statement;

  public Objective(@NotNull Sense sense, @NotNull Statement statement) {
    this.sense = sense;
    this.statement = statement;
  }

  public Sense getSense() {
    return sense;
  }

  public Statement getStatement() {
    return statement;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Objective)) {
      return super.equals(obj);
    } else {
      Objective other = (Objective) obj;
      return statement.equals(other.statement);
    }
  }

  @Override
  public @NotNull String toString() {
    return "Objective(sense=" + sense.toString() + ", statement=" + statement.toString() + ")";
  }
}
