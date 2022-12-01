package com.cxxsheng.parscan.core.iterator;

import java.util.Objects;

public class Edge {
  private final int left;
  private final int right;
  private boolean isPassed;

  public Edge(int left, int right) {
      this.left = left;
      this.right = right;
  }

  public int getLeft() {
    return left;
  }

  public int getRight() {
    return right;
  }

  public boolean isPassed() {
    return isPassed;
  }

  public void setPassed(boolean passed) {
    isPassed = passed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Edge edge = (Edge)o;
    return left == edge.left &&
           right == edge.right;
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("");
    sb.append(left);
    sb.append("->").append(right);
    return sb.toString();
  }
}
