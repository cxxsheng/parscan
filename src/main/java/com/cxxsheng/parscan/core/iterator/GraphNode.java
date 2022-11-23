package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import com.microsoft.z3.Expr;
import java.util.List;

public interface GraphNode {


  Pair<Expr, Integer> getChildIndex(int i);

  List<GraphNode> getFathers();

  void addChild(Expr cond, int nodeIndex);

  List<Pair<Expr, Integer>> getChildren();

  void addFather(GraphNode node);

  void setGraph(Graph graph);

  void setIndexAtTree(int index);

  boolean isRoot();

  String getIdentifier();

  boolean isPlaceholder();

  void setCond(Expr condition);

  Expr getCond();

  int getIndex();

  int[] mark();
}
