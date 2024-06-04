package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.*;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.ConditionalExpression;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;
import com.cxxsheng.parscan.core.z3.Z3Core;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cxxsheng.parscan.core.iterator.ASTIterator.DEFAULT_MODE;
import static com.cxxsheng.parscan.core.iterator.ASTIterator.EXECUTION_MODE;

public class ParcelDataNode implements GraphNode ,RuntimeValue{

    private static List<String> whileList = Arrays.asList("unmarshall");
    private Graph graph;

    private List<Edge> children = new ArrayList<>();

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
    public List<Edge> getChildren() {
      return children;
    }

    @Override
    public Edge getChildByIndex(int i) {
      return children.get(i);
    }

    @Override
    public void addChild(ExprWithTypeVariable cond, int dstIndex) {

          if (dstIndex == index){
              return;
          }

//          if (dstIndex < index)
//          {
//              throw new ASTParsingException("cannot support loop state");
//          }
          Edge edge = new Edge(cond, index, dstIndex);
          children.add(edge);

          //set father
          GraphNode node = graph.getNodeById(dstIndex);
          node.addFather(this);

          if (index < 0)
            throw new ASTParsingException("index must be more than -1");
          graph.addEdge(edge);
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

    public static ParcelDataNode parseCallFunc(Z3Core core, CallFunc func, int[] marks, int mode){
          int type = 0;
          String funcName = func.getFuncName();
          JavaType jType = null;
          boolean isArray = false;
          boolean isOneOrZero = false;

          FunctionDeclaration d = FunctionReader.findDeclarationByName(funcName);
          ExprWithTypeVariable value = null;

          String attachedSymbolName = null;

          if (funcName.startsWith("read")){
            if (mode != EXECUTION_MODE)
                throw new ParcelMismatchException("oh no that is ridiculous");
            type = FUNC_TYPE_READ;
            jType = d.getReturnType();
            //fixme we need to record the return value
            if ("T".equals(jType.getObjectName())){
                String newFuncName = funcName.substring(4);
                if (newFuncName.equals("Parcelable"))
                    jType.setName(newFuncName);
            }
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
              if (mode != DEFAULT_MODE)
                    throw new ParcelMismatchException("that is ridiculous ");
              type = FUNC_TYPE_WRITE;
            if (d.hasParameter()){
              jType  = d.getParameterByIndex(0).getType();
              TerminalSymbol ts = func.getParams().get(0);
              if (ts instanceof IdentifierSymbol){
                attachedSymbolName = ts.toString();
              }else if(ts instanceof TmpSymbol) {
                Expression e = ((TmpSymbol) ts).getExpression();
                if (e.isAssign())
                {
                   if (e.getRight() instanceof TmpSymbol)
                       e = ((TmpSymbol) e.getRight()).getExpression();
                }
                Symbol symbol = e.getSymbol();
                if (symbol instanceof ConditionalExpression){
                        isOneOrZero = true;
                }
                if (e.toString().contains("1:0"))
                    isOneOrZero = true;
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
              if (mode != EXECUTION_MODE)
                  throw new ParcelMismatchException("that is ridiculous");
            type = FUNC_TYPE_CREATE;
            jType = d.getReturnType();
          }else if (whileList.contains(funcName)){
                return null;
          }


          if (jType == null || jType.isVoid()){
            throw new ASTParsingException("cannot handle this call-func: " + func.toString());
          }
          ParcelDataNode ret = new ParcelDataNode(attachedSymbolName, jType, type, marks);
          ret.setValue(value);
          ret.isOneZeroValue = isOneOrZero;
          return ret;
    }

    public JavaType getJtype() {
      return jtype;
    }

    public boolean isArray(){
        return jtype.isArray();
    }

    private boolean isOneZeroValue = false;

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
      for (Edge childEdge :  children){
        Solver solver = core.mkSolver();

        ExprWithTypeVariable childCond = childEdge.getCond();
        ExprWithTypeVariable eq = core.mkEq(curCond, childCond);
//        if (eq.getVars()!=null && eq.getVars().size() > 0){
//            Expr all_eq = core.mkAll(eq);
//            solver.add(core.getCtx().mkNot(all_eq));
//        }
//        else {
        solver.add(core.getCtx().mkNot(eq.getExpr()));
//        }
        Status status = solver.check();
        if (status == Status.UNSATISFIABLE)
        {
          Graph graph = getGraph();
          graph.updateNodeIndex(childEdge.getRight(), true);

          Edge edge = graph.findEdgeByIndex(index, childEdge.getRight());
          if (edge == null)
            throw new ParcelMismatchException("cannot find edge ["+index +"->" + childEdge.getRight() + "]");
          edge.setPassed(true);
          return;
        }
      }
      throw new ParcelMismatchException("cannot find any node");
    }


    public static final List<String> mapStings = Arrays.asList("Map", "HashMap");
    public static List<String> listStrings = Arrays.asList("List", "ArrayList");
    public static List<String> IBinberStrings = Arrays.asList("IBinder","IInterface");
    public static List<List<String>> allStrings = Arrays.asList(mapStings, listStrings, IBinberStrings);
    public static boolean compareTwoNode(ParcelDataNode node, ParcelDataNode currentNode){

       if(node.isPlaceHolder ^ currentNode.isPlaceHolder)
        return false;

       for (List<String> strings : allStrings)
       {
           for (String start : strings){
               if (node.getJtype().toString().startsWith(start))
               {
                   for (String bstart : strings){
                       if (currentNode.getJtype().toString().startsWith(bstart))
                           return currentNode.getJtype().isArray() == node.getJtype().isArray();
                   }
                   return false;
               }
           }
       }

//       if (node.toString().startsWith("List") && (currentNode.toString().startsWith("List") || ))
//           return true;
       if (!node.getJtype().equals(currentNode.getJtype()))
       {
           if ("T".equals(currentNode.getJtype().getObjectName()))
               return node.getJtype().getObjectName()!=null;
           if ("T".equals(node.getJtype().getObjectName()))
               return currentNode.getJtype().getObjectName()!=null;
           return false;
       }
       //maybe check value
       return true;

    }

    public boolean isOneZeroValue() {
        return isOneZeroValue;
    }
}
