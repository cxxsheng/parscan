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

  private volatile int preCurrentNodeIndex = -1;

  private volatile int currentNodeIndex;


  public Tree(TreeNode root) {
    this();
    setRoot(root);
    allNodes.add(root);
    updateNodeIndex(0);
  }

  public Tree() {
    this.edges = new ArrayList<>();
    allNodes = new ArrayList<>();
    setRoot (ParcelDataNode.initEmptyInstance());
    allNodes.add(root);
    updateNodeIndex(0);
  }

  private void clearNodes(){
    allNodes.clear();
  }


  public TreeNode preCurrentNode(){
    return this.getNodeById(preCurrentNodeIndex);
  }


  public TreeNode currentNode(){
    return this.getNodeById(currentNodeIndex);
  }

  public void updateNodeIndex(int index){
      currentNodeIndex = index;
      preCurrentNodeIndex = currentNodeIndex;
      System.out.println("current index " + index  + "/" + currentNode());

  }

  public void popCurrent(){
      TreeNode father = currentNode().getFather();
      System.out.println(currentNode());
      if (father == null){
        System.out.println();
      }
      int father_index = father.getIndex();
      if (father_index < 0)
        throw new ASTParsingException("father index cannot less than 0");
      currentNodeIndex = father_index;
  }

  private void setRoot(TreeNode root) {
    clearNodes();
    this.root = root;
    root.setIndexAtTree(0);
    root.setTree(this);
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
      //already existed just return index
      int index = allNodes.indexOf(node);

      if (index <= 0)
       {
          index = allNodes.size();
          node.setTree(this);
          node.setIndexAtTree(index);
          node.setCond(condition);
          attachedName2TreeNodeIndex.put(node.getIdentifier(), index) ;
          allNodes.add(node);
      }


      currentNode().addChild(condition, index);
      updateNodeIndex(index);
      return index;
  }

  public int addEdge(int src, int dst){
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

  public TreeNode getNodeById(int id){
    if (id< allNodes.size()){
      return allNodes.get(id);
    }

    return null;
  }


  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Tree{");
    for (Pair<Integer, Integer> edge :edges){
      int src = edge.getLeft();
      int dst = edge.getRight();

      sb.append(getNodeById(dst).getCond());
      sb.append(getNodeById(src));
      sb.append("->");
      sb.append(getNodeById(dst));
      sb.append('\n');
    }
    sb.append('}');
    return sb.toString();
  }
}
