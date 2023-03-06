package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;
import com.cxxsheng.parscan.core.z3.Z3Core;
import java.util.List;

public interface GraphNode {


  Edge getChildByIndex(int i);

  List<GraphNode> getFathers();

  void addChild(ExprWithTypeVariable cond, int nodeIndex);

  List<Edge> getChildren();

  void addFather(GraphNode node);

  void setGraph(Graph graph);

  void setIndexAtTree(int index);

  boolean isRoot();

  List<String> getIdentifier();

  boolean isPlaceholder();

  int getIndex();

  int[] mark();

  void clearMark();

  void setMark(int[] mark);

  void chooseBranch(Z3Core core, ExprWithTypeVariable exp);
}

