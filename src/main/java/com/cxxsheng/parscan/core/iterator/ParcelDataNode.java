package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;
import com.cxxsheng.parscan.core.z3.Z3Core;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParcelDataNode implements GraphNode ,RuntimeValue{

    private Graph graph;

    private List<Pair<ExprWithTypeVariable, Integer>> children = null;

    private List<GraphNode> fathers = new ArrayList<>();

    private ExprWithTypeVariable value;

    private List<String> attachedSymbolName;

    private final boolean isPlaceHolder;

    //FUNC_TYPE_READ when read function (etc. readInt, readString)
    //FUNC_TYPE_READ when write function (etc. writeInt, writeString)
    //FUNC_TYPE_CREATE when create function (etc. createIntArray)

    public final static int FUNC_TYPE_READ = 1;
    public final static int FUNC_TYPE_WRITE = 2;
    public final static int FUNC_TYPE_CREATE = 3;

    private final int func_type;

    private final JavaType jtype;

    private int[] mark;

    private int index = -1;

    private ParcelDataNode(int[] mark){
      func_type = 0;
      jtype = JavaType.getVOID();
      attachedSymbolName = null;
      isPlaceHolder = true;
      this.mark = mark;
    }

    public static ParcelDataNode initEmptyInstance(int[] mark){
       return new ParcelDataNode(mark);
    }


    public ParcelDataNode(String attachedSymbolName, JavaType jtype, int func_type, int[] mark){
      this.attachedSymbolName = new ArrayList<>();
      this.attachedSymbolName.add(attachedSymbolName);
      this.jtype = jtype;
      this.func_type =  func_type;
      isPlaceHolder = false;
      this.mark = mark;
    }

    @Override
    public List<Pair<ExprWithTypeVariable, Integer>> getChildren() {
      return children;
    }

    @Override
    public Pair<ExprWithTypeVariable, Integer> getChildIndex(int i) {
      return children.get(i);
    }

    @Override
    public void addChild(ExprWithTypeVariable cond, int dstIndex) {
          if (children == null)
              children = new ArrayList<>();

          children.add(new Pair<>(cond, dstIndex));

          //set father
          GraphNode node = graph.getNodeById(dstIndex);
          node.addFather(this);

          if (index < 0)
            throw new ASTParsingException("index must be more than -1");
          graph.addEdge(cond, index, dstIndex);
      }


    @Override
    public List<GraphNode> getFathers() {
        return fathers;
    }

    @Override
    public void addFather(GraphNode node) {
      fathers.add(node);
    }


    public Graph getGraph() {
        return graph;
    }

    @Override
    public void setGraph(Graph graph) {
        this.graph = graph;
    }


    public void setIndexAtTree(int index) {
        this.index = index;
    }

    @Override
    public boolean isRoot() {
      return graph.getRoot() == this;
    }

    public void clearIdentifier(){
      if (attachedSymbolName!=null)
        attachedSymbolName.clear();
    }

    public void addIdentifier(String name){
      if (attachedSymbolName!=null)
        attachedSymbolName.add(name);
    }


    @Override
    public List<String> getIdentifier() {
      return attachedSymbolName;
    }

    @Override
    public boolean isPlaceholder() {
      return isPlaceHolder;
    }

    public static ParcelDataNode parseCallFunc(Z3Core core, CallFunc func, int[] marks){
          int type = 0;
          String funcName = func.getFuncName();
          JavaType jType = null;
          boolean isArray = false;
          FunctionDeclaration d = FunctionReader.findDeclarationByName(funcName);
          ExprWithTypeVariable value = null;

          String attachedSymbolName = null;
          if (funcName.startsWith("read")){
            type = FUNC_TYPE_READ;
            jType = d.getReturnType();
            //fixme we need to record the return value
            if (jType.isVoid() && d.hasParameter()){
              jType = d.getParameterByIndex(0).getType();
              TerminalSymbol e = func.getParams().get(0);
              if (e instanceof IdentifierSymbol){
                attachedSymbolName = e.toString();
              }else if (e instanceof TmpSymbol){
                //if (e.getSymbol() instanceof ConditionalExpression){
                //  Expression L = ((ConditionalExpression)e.getSymbol()).getLeft();
                //  Expression R = ((ConditionalExpression)e.getSymbol()).getRight();
                //
                //  ExprWithTypeVariable LL = core.mkEq(core.VALUE, core.mkExpression(L));
                //  ExprWithTypeVariable RR = core.mkEq(core.VALUE, core.mkExpression(R));
                //  value = core.mkOr(LL, RR);
                //
                //}else if (e.getSymbol() instanceof PointSymbol){
                //  if (e.getSymbol().toString().endsWith(".length"))
                //    value = core.mkGe(core.VALUE, core.mkInt(1));
                //}
                //else
                //  value = core.mkEq(core.VALUE, core.mkExpression(e));
              }
            }
          }else if (funcName.startsWith("write")){
            type = FUNC_TYPE_WRITE;
            if (d.hasParameter()){
              jType  = d.getParameterByIndex(0).getType();
              TerminalSymbol ts = func.getParams().get(0);
              if (ts instanceof IdentifierSymbol){
                attachedSymbolName = ts.toString();
              }else if(ts instanceof TmpSymbol) {
                //if (e instanceof ConditionalExpression){
                //  Expression L = ((ConditionalExpression)e.getSymbol()).getLeft();
                //  Expression R = ((ConditionalExpression)e.getSymbol()).getRight();
                //
                //  ExprWithTypeVariable LL = core.mkEq(core.VALUE, core.mkExpression(L));
                //  ExprWithTypeVariable RR = core.mkEq(core.VALUE, core.mkExpression(R));
                //  value = core.mkOr(LL, RR);
                //
                //}else if (e.getSymbol() instanceof PointSymbol){
                //  if (e.getSymbol().toString().endsWith(".length"))
                //    value = core.mkGe(core.VALUE, core.mkInt(1));
                //}
                //else
                //  value = core.mkEq(core.VALUE, core.mkExpression(e));
              }
            }
          }else if (funcName.startsWith("create")){
            type = FUNC_TYPE_CREATE;
            jType = d.getReturnType();
          }

          if (jType == null || jType.isVoid()){
            throw new ASTParsingException("cannot handle this call-func: " + func.toString());
          }
          ParcelDataNode ret = new ParcelDataNode(attachedSymbolName, jType, type, marks);
          ret.setValue(value);
          return ret;
    }

    public JavaType getJtype() {
      return jtype;
    }

    public boolean isArray(){
        return jtype.isArray();
    }

    @Override
    public String toString() {
        if (isPlaceHolder)
          return "PlaceHolderNode";
        final StringBuffer sb = new StringBuffer("ParcelDataNode{");
        sb.append(Arrays.toString(mark));
        sb.append("func_type=").append(func_type);
        sb.append(", jtype=").append(jtype);
        sb.append(", attachedSymbolName=").append(attachedSymbolName);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int getIndex() {
      return index;
    }

    @Override
    public int[] mark() {
      if (mark == null)
        throw new ASTParsingException("Cannot get a cleared mark data");
      return mark;
    }


    public void setValue(ExprWithTypeVariable exp){
        value = exp;
    }

    @Override
    public void clearMark(){
      mark = null;
    }

    @Override
    public void setMark(int[] mark) {
      this.mark = mark;
    }

    @Override
    public void chooseBranch(Z3Core core, ExprWithTypeVariable curCond) {
      for (Pair<ExprWithTypeVariable, Integer> childPair :  children){
        Solver solver = core.mkSolver();

        ExprWithTypeVariable childCond = childPair.getLeft();
        Status status = solver.check();
        ExprWithTypeVariable eq = core.mkEq(curCond, childCond);
        Expr all_eq = core.mkAll(eq);
        solver.add(all_eq);
        solver.check();
        if (status == Status.SATISFIABLE)
        {
          Graph graph = getGraph();
          graph.updateNodeIndex(childPair.getRight());

          Edge edge = graph.findEdgeByIndex(index, childPair.getRight());
          if (edge == null)
            throw new ParcelMismatchException("cannot find edge ["+index +"->" + childPair.getRight() + "]");
          edge.setPassed(true);
          break;
        }
      }
    }


  public static boolean compareTwoNode(ParcelDataNode node, ParcelDataNode currentNode){

      if(node.isPlaceHolder ^ currentNode.isPlaceHolder)
        return false;

       if (!node.getJtype().equals(currentNode.getJtype()))
        return false;
       //maybe check value
       return true;

    }
}
