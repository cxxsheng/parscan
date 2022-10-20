package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import java.util.ArrayList;
import java.util.List;

public class Node {

  private int index;
  private Expression condition;
  private Node prev;
  private List<Node> successors = new ArrayList<>();

  public void setCondition(Expression condition) {
    this.condition = condition;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setPrev(Node prev) {
    this.prev = prev;
  }

  public void addSuccessor(Node successor) {
    this.successors.add(successor);
  }
}

