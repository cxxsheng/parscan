package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import com.microsoft.z3.Expr;

public interface TreeNode {


  Pair<Expr, Integer> getChildIndex(int i);

  TreeNode getFather();

  void addChild(Expr cond, int nodeIndex);

  void setFather(TreeNode node);

  void setTree(Tree tree);

  void setIndexAtTree(int index);

  boolean isRoot();

  String getIdentifier();

  boolean isPlaceholder();

  void setCond(Expr condition);

  Expr getCond();

  int getIndex();

  int[] mark();
}
