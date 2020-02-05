package com.github.brymck.lpparse;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

final class StateMachine {
  private @NotNull ParserState state;
  private @NotNull Problem problem;
  private @NotNull Bounds bounds;
  private @NotNull Statement statement;
  private @NotNull List<@NotNull Variable> variables;

  public StateMachine() {
    this.state = ParserState.START;
    this.problem = Problem.builder().build();
    this.bounds = new Bounds(emptyList());
    this.statement = new Statement("", emptyList());
    this.variables = emptyList();
  }

  public @NotNull ParserState getState() {
    return state;
  }

  public void setState(@NotNull ParserState state) {
    this.state = state;
  }

  public @NotNull Problem getProblem() {
    return problem;
  }

  public @NotNull Statement getStatement() {
    return statement;
  }

  public @NotNull Statement newStatement() {
    statement = new Statement("", new ArrayList<>());
    return statement;
  }

  public @NotNull Bounds newBounds() {
    bounds = new Bounds(new ArrayList<>());
    return bounds;
  }

  public @NotNull List<@NotNull Variable> getVariables() {
    return variables;
  }

  public @NotNull List<@NotNull Variable> newVariables() {
    variables = new ArrayList<>();
    return variables;
  }
}
