package com.cxxsheng.parscan.core.iterator;

public interface TreeNode {


  TreeNode getChild(int i);

  TreeNode getFather();

  void addChild(TreeNode node);

  void setFather(TreeNode node);

  void setTree(Tree tree);

  void setIndexAtTree(int index);

  boolean isRoot();

  String getIdentifier();
}
