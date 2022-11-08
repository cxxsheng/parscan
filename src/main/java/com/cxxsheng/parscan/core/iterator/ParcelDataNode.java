package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import java.util.List;

public class ParcelDataNode implements TreeNode {

    private Tree tree;

    private List<TreeNode> children = null;

    private TreeNode father = null;

    private final String attachedSymbolName;

    //FUNC_TYPE_READ when read function (etc. readInt, readString)
    //FUNC_TYPE_READ when write function (etc. writeInt, writeString)
    //FUNC_TYPE_CREATE when create function (etc. createIntArray)

    public final static int FUNC_TYPE_READ = 1;
    public final static int FUNC_TYPE_WRITE = 2;
    public final static int FUNC_TYPE_CREATE = 3;

    private final int func_type;

    private final JavaType jtype;


    private int index = -1;

    private boolean isEmpty = false;

    private ParcelDataNode(){
      func_type = 0;
      jtype = JavaType.getVOID();
      isEmpty = true;
      attachedSymbolName = null;
    }

    public static ParcelDataNode initEmptyInstance(){
       return new ParcelDataNode();
    }

    //return true means this node is
    //a placeholder node
    public boolean isEmpty(){
      return isEmpty;
    }

    public ParcelDataNode(String attachedSymbolName, JavaType jtype, int func_type){
      this.attachedSymbolName = attachedSymbolName;
      this.jtype = jtype;
      this.func_type =  func_type;
    }


    @Override
    public TreeNode getChild(int i) {
      return children.get(i);
    }

    @Override
    public void addChild(TreeNode node) {
        //if (children == null)
        //    children = new ArrayList<>();
        //
        //children.add(node);
        //
        //
        //int dst = tree.addNewNode(node);
        //if (index < 0)
        //  throw new ASTParsingException("index must be more than -1");
        //tree.addEdges(index, dst);
    }


    @Override
    public TreeNode getFather() {
        return father;
    }

    @Override
    public void setFather(TreeNode node) {
        this.father = father;
    }


    public Tree getTree() {
        return tree;
    }

    @Override
    public void setTree(Tree tree) {
        this.tree = tree;
    }


    public void setIndexAtTree(int index) {
        this.index = index;
    }

    @Override
    public boolean isRoot() {
      return tree.getRoot() == this;
    }

    @Override
    public String getIdentifier() {
      return attachedSymbolName;
    }

    public static ParcelDataNode parseCallFunc(CallFunc func){
          int type = 0;
          String funcName = func.getFuncName();
          JavaType jType = null;
          boolean isArray = false;
          FunctionDeclaration d = FunctionReader.findDeclarationByName(funcName);
          String attachedSymbolName = null;
          if (funcName.startsWith("read")){
            type = FUNC_TYPE_READ;
            jType = d.getReturnType();
            //fixme we need to record the return value
            if (jType.isVoid() && d.hasParameter()){
              jType = d.getParameterByIndex(0).getType();
              Expression e = func.getParams().get(0);
              if (e.isTerminalSymbol()){
                attachedSymbolName = e.getSymbol().toString();
              }
            }
          }else if (funcName.startsWith("write")){
            type = FUNC_TYPE_WRITE;
            if (d.hasParameter()){
              jType  = d.getParameterByIndex(0).getType();
              Expression e = func.getParams().get(0);
              if (e.isTerminalSymbol()){
                attachedSymbolName = e.getSymbol().toString();
              }
            }
          }else if (funcName.startsWith("create")){
            type = FUNC_TYPE_CREATE;
            jType = d.getReturnType();
          }

          if (jType == null || jType.isVoid()){
            throw new ASTParsingException("cannot handle this call-func: " + func.toString());
          }
          return new ParcelDataNode(attachedSymbolName, jType, type);
      }

      public boolean isArray(){
        return jtype.isArray();
      }

      @Override
      public String toString() {
        final StringBuffer sb = new StringBuffer("ParcelDataNode{");
        sb.append("func_type=").append(func_type);
        sb.append(", jtype=").append(jtype);
        sb.append(", attachedSymbolName=").append(attachedSymbolName);
        sb.append('}');
        return sb.toString();
      }
}
