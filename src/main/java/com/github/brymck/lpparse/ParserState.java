package com.github.brymck.lpparse;

enum ParserState {
  START,
  OBJECTIVE,
  OBJECTIVE_STATEMENT,
  BOUNDS,
  BOUNDS_STATEMENT,
  BOUNDS_STATEMENT_NEW_LINE,
  SUBJECT_TO,
  SUBJECT_TO_STATEMENT,
  GENERALS,
  SEMI_CONTINUOUS,
  END
}
