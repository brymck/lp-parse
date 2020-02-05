package com.github.brymck.lpparse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum Keyword {
  MAXIMIZE,
  MINIMIZE,
  BOUNDS,
  SUBJECT_TO,
  SEMI_CONTINUOUS,
  GENERALS,
  END;

  public static @Nullable Keyword tryParseString(@NotNull String s) {
    switch (s.trim().toLowerCase()) {
      case "bounds":
        return BOUNDS;
      case "generals":
        return GENERALS;
      case "max":
      case "maximize":
        return MAXIMIZE;
      case "min":
      case "minimize":
        return MINIMIZE;
      case "semi-continuous":
        return SEMI_CONTINUOUS;
      case "st":
      case "subject to":
        return SUBJECT_TO;
      case "end":
        return END;
      default:
        return null;
    }
  }
}
