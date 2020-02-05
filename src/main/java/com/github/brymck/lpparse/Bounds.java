package com.github.brymck.lpparse;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Bounds {
  private @NotNull List<@NotNull String> items;

  public Bounds(@NotNull List<@NotNull String> items) {
    this.items = items;
  }

  public @NotNull List<@NotNull String> getItems() {
    return items;
  }

  protected void addItem(@NotNull String item) {
    items.add(item);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (!(obj instanceof Bounds)) {
      return super.equals(obj);
    } else {
      Bounds other = (Bounds) obj;
      return items.equals(other.getItems());
    }
  }

  @Override
  public String toString() {
    return "Bounds(items=" + items.toString() + ")";
  }
}
