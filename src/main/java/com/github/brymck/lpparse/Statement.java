package com.github.brymck.lpparse;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Statement {
  private @NotNull String label;
  private @NotNull List<@NotNull String> items;

  public Statement(@NotNull String label, @NotNull List<@NotNull String> items) {
    this.label = label;
    this.items = items;
  }

  public @NotNull String getLabel() {
    return label;
  }

  protected void setLabel(@NotNull String label) {
    this.label = label;
  }

  public @NotNull List<@NotNull String> getItems() {
    return items;
  }

  protected void addItem(@NotNull String item) {
    this.items.add(item);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Statement)) {
      return super.equals(obj);
    } else {
      Statement other = (Statement) obj;
      return label.equals(other.label) && items.equals(other.items);
    }
  }

  @Override
  public @NotNull String toString() {
    return "Statement(label=" + label + ", items=" + items.toString() + ")";
  }
}
