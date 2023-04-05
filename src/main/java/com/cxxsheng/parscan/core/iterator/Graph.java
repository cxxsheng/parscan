package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Graph {

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);
  private GraphNode root;

  private GraphNode terminal;

  private final List<GraphNode> allNodes;

  private final List<Edge> edges;


  private volatile int preCurrentNodeIndex = -1;


  private volatile int currentNodeIndex;


  public Graph(GraphNode root) {
    this();
    setRoot(root);
    allNodes.add(root);
    updateNodeIndex(0);
  }

  public Graph() {
    this.edges = new ArrayList<>();
    allNodes = new ArrayList<>();
    int[] d = {-1};
    setRoot(ParcelDataNode.initEmptyInstance(d));
    allNodes.add(root);
    updateNodeIndex(0);
  }

  private void clearNodes() {
    allNodes.clear();
  }

  public GraphNode preCurrentNode() {
    return this.getNodeById(preCurrentNodeIndex);
  }

  public GraphNode currentNode() {
    return this.getNodeById(currentNodeIndex);
  }

  public void updateNodeIndex(int index, boolean isExecutedMode) {
    preCurrentNodeIndex = currentNodeIndex;
    currentNodeIndex = index;

    //if isExecutedMode
    if (isExecutedMode) {
      LOG.info("we are at node $" + index );
      for (Edge edge : edges) {
        if (edge.getLeft() == preCurrentNodeIndex && edge.getRight() == currentNodeIndex) {
          edge.setPassed(true);
          break;
        }
      }
    }
    System.out.println("current index " + index + "/" + currentNode());
  }

  public void updateNodeIndex(int index) {
    updateNodeIndex(index, false);
  }

  public void popCurrent(boolean isExecMode) {
    List<GraphNode> fathers = currentNode().getFathers();
    if (fathers.size() < 1) {
      throw new ASTParsingException("List fathers length cannot less than 0");
    }
    // go to the current father here
    GraphNode father = null;
    if (isExecMode) {
        for (int i = fathers.size() - 1; i >= 0 ; i --){
          GraphNode f = fathers.get(i);
          Edge edge = findEdgeByIndex(f.getIndex(), currentNodeIndex);
          if (edge.isPassed()){
            father = f;
            break;
          }

        }
        if (father == null)
          throw new ASTParsingException("cannot find any father node");
    }else {
       father = currentNode().getFathers().get(fathers.size() - 1);
    }
    int father_index = father.getIndex();
    if (father_index < 0)
      throw new ASTParsingException("father index cannot less than 0");

    if (isExecMode){
      LOG.info("we are now at node $" + father_index);
    }
    currentNodeIndex = father_index;
  }

  private void setRoot(GraphNode root) {
    terminal = ParcelDataNode.initEmptyInstance(null);
    clearNodes();
    this.root = root;
    root.setIndexAtTree(0);
    root.setGraph(this);
  }

  public GraphNode getRoot() {
    return root;
  }

  public int addNewNode(ExprWithTypeVariable condition, GraphNode node) {
    int ret;
    if (allNodes.size() == 0) {
      throw new ASTParsingException("set root node first");
    }
    //already existed just return index
    int index = allNodes.indexOf(node);

    if (index <= 0) {
      index = allNodes.size();
      node.setGraph(this);
      node.setIndexAtTree(index);
      allNodes.add(node);
    }
    currentNode().addChild(condition, index);
    updateNodeIndex(index);
    return index;
  }

  public int addEdge(Edge edge) {
    int index = edges.indexOf(edge);
    if (index >= 0) {
      throw new ASTParsingException("edge [" + edge + "] already existed!");
    }
    else {
      edges.add(edge);
      return edges.size() - 1;
    }
  }

  public int getIndexByAttachedName(String attachedName) {
    if (attachedName == null)
      return -1;
    for (GraphNode node : allNodes) {
      if (node.isPlaceholder())
        continue;

      List<String> names = node.getIdentifier();

      if (names != null)
        if (names.contains(attachedName))
          return node.getIndex();
    }
    return -1;
  }

  public GraphNode getNodeById(int id) {
    if (id < allNodes.size()) {
      return allNodes.get(id);
    }
    return null;
  }


  @Override
  public String toString() {
    return toMermaidString();
  }


  public String toMermaidString() {
    final StringBuilder sb = new StringBuilder("graph TB;\n");
    for (Edge edge : edges) {
      int src = edge.getLeft();
      int dst = edge.getRight();
      GraphNode left = getNodeById(src);
      GraphNode right = getNodeById(dst);
      sb.append("$").append(src);
      if (left instanceof ParcelDataNode){
        sb.append(':');
        if(left.isPlaceholder())
          sb.append("placeholder");
        else
        {
          sb.append(((ParcelDataNode)left).getJtype().getName());
          sb.append(((ParcelDataNode) left).isArray() ? "Array" : "");
        }
      }
      sb.append("--\"").append(edge.getCond());
      sb.append("\"-->");
      sb.append("$").append(dst);
      if (right instanceof ParcelDataNode){
        sb.append(':');
        if (right.isPlaceholder())
          sb.append("placeholder");
        else
        {
          sb.append(((ParcelDataNode)right).getJtype().getName());
          sb.append(((ParcelDataNode)right).isArray() ? "Array" : "");
        }
      }
      sb.append(";\n");
    }
    return sb.toString();
  }

  public static int getTwoNodeSamePrefixIndexAtMark(GraphNode node1, GraphNode node2) {
    int ret = -1;
    int[] mark1 = node1.mark();
    int[] mark2 = node2.mark();
    if (mark1 == mark2)
      return -1;

    if (mark1 != null && mark2 != null && mark1.length == mark2.length) {
      int len = mark1.length;
      //two mark have two the same
      for (int i = 0; i < len; i++) {
        ret++;
        if (mark1[i] != mark2[i])
          return ret;
      }
    }
    return ret;
  }

  public static boolean twoNodeHaveSameMarkPrefix(GraphNode node1, GraphNode node2) {
    int[] mark1 = node1.mark();
    int[] mark2 = node2.mark();
    if (mark1 == mark2)
      return true;
    if (mark1 != null && mark2 != null && mark1.length == mark2.length) {
      int len = mark1.length;
      //two mark have two the same
      for (int i = 0; i < len - 1; i++) {
        if (mark1[i] != mark2[i])
          return false;
      }
      return true;
    }
    return false;
  }

  public GraphNode findNodeByMark(int[] mark, int len) {
    for (GraphNode node : allNodes) {
      int[] node_mark = node.mark();

      if (mark.length != node_mark.length)
        continue;

      if (len > mark.length)
        continue;

      boolean isDiffer = true;
      for (int i = 0; i < len; i++) {
        if (node_mark[i] != mark[i])
        {
          isDiffer = false;
          break;
        }
      }
      if (isDiffer)
        return node;
    }
    return null;
  }

  //recovery current node at root node
  //and clear all node_mark
  public void recovery() {
    int rootIndex = allNodes.indexOf(getRoot());
    preCurrentNodeIndex = rootIndex;
    currentNodeIndex = rootIndex;
    for (GraphNode node : allNodes) {
      if (root != node)
        node.clearMark();
      if (node instanceof ParcelDataNode)
        ((ParcelDataNode)node).clearIdentifier();
    }
  }

  public Edge findEdgeByIndex(int src, int dst) {
    for (Edge edge : edges) {
      if (edge.getLeft() == src && edge.getRight() == dst)
        return edge;
    }
    return null;
  }

  private boolean chooseFlag = false;

  public void setChooseFlag(boolean flag){
    chooseFlag = flag;
  }

  public boolean needToChooseBranch(){
    return chooseFlag;
  }

  public boolean allEdgePassed(){
    for (Edge edge : edges)
    {
      if (!edge.isPassed())
        return false;
    }
    return true;
  }

}