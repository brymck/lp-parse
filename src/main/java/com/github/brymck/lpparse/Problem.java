package com.github.brymck.lpparse;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class Problem {
  private @NotNull Objective objective;
  private @NotNull List<@NotNull Bounds> bounds;
  private @NotNull List<@NotNull Variable> generals;
  private @NotNull List<@NotNull Variable> semiContinuous;
  private @NotNull List<@NotNull Statement> subjectTo;

  public Problem(
      @NotNull Objective objective,
      @NotNull List<@NotNull Bounds> bounds,
      @NotNull List<@NotNull Variable> generals,
      @NotNull List<@NotNull Variable> semiContinuous,
      @NotNull List<@NotNull Statement> subjectTo) {
    this.objective = objective;
    this.bounds = bounds;
    this.generals = generals;
    this.semiContinuous = semiContinuous;
    this.subjectTo = subjectTo;
  }

  public @NotNull Objective getObjective() {
    return objective;
  }

  protected void setObjective(@NotNull Objective objective) {
    this.objective = objective;
  }

  public @NotNull List<@NotNull Bounds> getBounds() {
    return unmodifiableList(bounds);
  }

  protected void addBounds(@NotNull Bounds bounds) {
    this.bounds.add(bounds);
  }

  protected void addItemToLatestBounds(@NotNull String item) {
    Bounds bounds = this.bounds.get(this.bounds.size() - 1);
    bounds.addItem(item);
  }

  public @NotNull List<@NotNull Statement> getSubjectTo() {
    return unmodifiableList(subjectTo);
  }

  protected void addSubjectTo(@NotNull Statement statement) {
    this.subjectTo.add(statement);
  }

  public @NotNull List<@NotNull Variable> getSemiContinuous() {
    return unmodifiableList(semiContinuous);
  }

  protected void setSemiContinuous(@NotNull List<@NotNull Variable> variables) {
    this.semiContinuous = variables;
  }

  public @NotNull List<@NotNull Variable> getGenerals() {
    return unmodifiableList(generals);
  }

  protected void setGenerals(@NotNull List<@NotNull Variable> variables) {
    this.generals = variables;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Problem)) {
      return super.equals(obj);
    } else {
      Problem other = (Problem) obj;
      return (objective.equals(other.objective)
          && subjectTo.equals(other.subjectTo)
          && bounds.equals(other.bounds)
          && generals.equals(other.generals)
          && semiContinuous.equals(other.semiContinuous));
    }
  }

  @Override
  public @NotNull String toString() {
    return ("Problem("
        + "objective="
        + objective.toString()
        + ", bounds="
        + bounds.toString()
        + ", subjectTo="
        + subjectTo
        + ", generals="
        + generals
        + ", semiContinuous="
        + semiContinuous
        + ")");
  }

  public static @NotNull Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private @NotNull Objective objective;
    private @NotNull List<@NotNull Bounds> bounds;
    private @NotNull List<@NotNull Variable> generals;
    private @NotNull List<@NotNull Variable> semiContinuous;
    private @NotNull List<@NotNull Statement> subjectTo;

    private Builder() {
      objective = new Objective(Sense.MINIMIZE, new Statement("", new ArrayList<>()));
      bounds = new ArrayList<>();
      generals = new ArrayList<>();
      semiContinuous = new ArrayList<>();
      subjectTo = new ArrayList<>();
    }

    public Builder withObjective(@NotNull Objective objective) {
      this.objective = objective;
      return this;
    }

    public Builder withBounds(@NotNull List<@NotNull Bounds> bounds) {
      this.bounds = bounds;
      return this;
    }

    public Builder withGenerals(@NotNull List<@NotNull Variable> generals) {
      this.generals = generals;
      return this;
    }

    public Builder withSemiContinuous(@NotNull List<@NotNull Variable> semiContinuous) {
      this.semiContinuous = semiContinuous;
      return this;
    }

    public Builder withSubjectTo(@NotNull List<@NotNull Statement> subjectTo) {
      this.subjectTo = subjectTo;
      return this;
    }

    public @NotNull Problem build() {
      return new Problem(objective, bounds, generals, semiContinuous, subjectTo);
    }
  }
}
