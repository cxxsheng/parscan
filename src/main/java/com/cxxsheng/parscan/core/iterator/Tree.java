package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import com.microsoft.z3.Expr;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {

  private TreeNode root;

  private final List<TreeNode> allNodes;

  private final List<Pair<Integer,Integer>> edges;

  private final Map<String, Integer> attachedName2TreeNodeIndex = new HashMap();

  public Tree(TreeNode root) {
    this();
    this.root = root;
  }

  public Tree() {
    this.edges = new ArrayList<>();
    allNodes = new ArrayList<>();
    setRoot (ParcelDataNode.initEmptyInstance());
  }

  private void setRoot(TreeNode root) {
    this.root = root;
    allNodes.clear();
    allNodes.add(root);
  }

  public TreeNode getRoot() {
    return root;
  }

  public int addNewNode(Expr condition, TreeNode node){
      int ret;
      if (allNodes.size() == 0)
      {
        throw new ASTParsingException("set root node first");
      }

      int index = allNodes.indexOf(node);

      if (index >= 0)
          ret = index;
      else {
          allNodes.add(node);
          node.setTree(this);
          node.setIndexAtTree(allNodes.size() - 1);
          ret = allNodes.size() - 1;
        attachedName2TreeNodeIndex.put(node.getIdentifier(), ret) ;
      }
      return ret;
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

  public int getIndexByAttachedName(String attachedName){
    Integer i =  attachedName2TreeNodeIndex.get(attachedName);
    if (i == null)
      return -1;
    else
      return i;
  }


}
