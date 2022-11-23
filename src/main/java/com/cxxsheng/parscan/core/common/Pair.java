package com.cxxsheng.parscan.core.common;

import java.util.Objects;

public class Pair<L,R> {

  private L left;
  private R right;

  public Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pair<?, ?> pair = (Pair<?, ?>)o;
    return Objects.equals(left, pair.left) &&
           Objects.equals(right, pair.right);
  }

  @Override
  public int hashCode() {
    return Objects.hash(left, right);
  }

  public L getLeft() {
    return left;
  }

  public void setLeft(L left) {
    this.left = left;
  }

  public void setRight(R right) {
    this.right = right;
  }

  public R getRight() {
    return right;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Pair{");
    sb.append(left);
    sb.append("/").append(right).append("}");
    return sb.toString();
  }
}
