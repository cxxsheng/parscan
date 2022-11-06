package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import java.util.ArrayList;
import java.util.List;

public class Tree {

  private TreeNode root;

  private final List<TreeNode> allNodes;

  private final List<Pair<Integer,Integer>> edges;


  public Tree(TreeNode root) {
    this();
    this.root = root;
  }

  public Tree() {
    this.edges = new ArrayList<>();
    allNodes = new ArrayList<>();
  }

  private void setRoot(TreeNode root) {
    this.root = root;
  }

  public TreeNode getRoot() {
    return root;
  }

  public int addNewNode(Condition condition, TreeNode node){
      int ret;
      if (allNodes.size() == 0)
      {
        setRoot(node);

        ret =  0;
      }

      int index = allNodes.indexOf(node);

      if (index >=0)
          ret = index;
      else {

          allNodes.add(node);
          node.setTree(this);
          node.setIndexAtTree(allNodes.size() - 1);
          ret = allNodes.size() - 1;
      }
      return  ret;
  }

  public int addEdges(int src, int dst){
      Pair<Integer, Integer> edge = new Pair<>(src, dst);
      int index = edges.indexOf(edge);

      if (index >= 0){
          throw new ASTParsingException("edge [" + edge +"] already existed!");
      }
      else {
          edges.add(edge);
          return edges.size() - 1;
      }
  }


}
