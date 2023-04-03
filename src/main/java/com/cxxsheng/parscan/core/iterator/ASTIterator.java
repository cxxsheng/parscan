package com.cxxsheng.parscan.core.iterator;

import static com.cxxsheng.parscan.core.data.Statement.RETURN_STATEMENT;
import static com.cxxsheng.parscan.core.data.Statement.THROW_STATEMENT;

import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.common.Pair;
import com.cxxsheng.parscan.core.common.Stack;
import com.cxxsheng.parscan.core.data.Block;
import com.cxxsheng.parscan.core.data.ConditionalBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.Statement;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.PointSymbol;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;
import com.cxxsheng.parscan.core.z3.Z3Core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ASTIterator {


  private List<ASTIterator> fathers = new ArrayList<>();

  private final AntlrCore antlrCore;

  private final JavaClass jclass;

  public final FunctionImp imp;

  public final ExpressionOrBlockList methodBody;

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);

  private Stack indexStack;

  private final List<String> traceList;

  private final Graph dataGraph;

  private final Z3Core core;

  //Default mode means ASTIterator construct a data node graph
  //while iterating the ast.
  public final static int DEFAULT_MODE = 0;

  //Execution mode means ASTIterator check the given graph
  //while iterating the ast.
  public final static int EXECUTION_MODE = 1;

  private final int mode;

  private VariableTable variableTable = new VariableTable();

  private VariableTable tmpTable = new VariableTable();

  private void initTraceList(){
    List<FunctionPattern> ps = FunctionPattern.getPatterns();
    for (FunctionPattern p : ps){
      String type = p.getPatternType();
      if ("methodParam".equals(type)){
          Integer index = p.getPatternInt("index");
          if (index != null && index >= 0 ){
            Parameter param = imp.getFunDec().getParams().get(index);
            traceList.add(param.getName());
          }
      }else if ("variableName".equals(type)){

      }
    }
  }

  public ASTIterator(AntlrCore antlrCore, FunctionImp imp){
      this(antlrCore, imp, DEFAULT_MODE, new Graph(), false);
  }

  public ASTIterator(AntlrCore antlrCore, FunctionImp imp, Graph givenGraph){
      this(antlrCore, imp, EXECUTION_MODE, givenGraph, false);
      givenGraph.recovery();
  }

  private ASTIterator(AntlrCore antlrCore, FunctionImp functionImp, int mode, Graph graph, boolean inlined) {
      this.imp = functionImp;
      this.methodBody = functionImp.getBody();
      this.antlrCore = antlrCore;
      // first currentClass
      this.jclass = functionImp.getJavaClass();
      this.mode = mode;
      this.dataGraph = graph;
      traceList = new ArrayList<>();
      if (!inlined)
        initTraceList();
      core = new Z3Core(this) ;
      indexStack = new Stack();
      indexStack.push(0);

  }


  //fixme must use it before it starts
  private boolean isReady(){
      return FunctionPattern.isInit();
  }


  public boolean checkTraceList(String name){
    for (String trace : traceList){
      if (trace.equals(name)){
        return true;
      }
    }
    return false;
  }

  private ExprWithTypeVariable constructConditionByExpression(ExpressionListWithPrevs e){
        return core.mkExpressionListWithPrevs(e, (t) -> core.mkAnd(t, core.EXP_TRUE));
  }

  private ExpressionOrBlock getRecentBlock(){
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>>  blocks = allCurrentBlocks(0);
    ExpressionOrBlock curblock = blocks.get(blocks.size()-2).getLeft();
    return curblock;
  }

  //Compare two mask, if they have the same mask, two nodes are in the same domain
  private static int sizeInSameDomain(int[] mask1, int[] mask2){
    int i = 0;
    if (mask1 !=null && mask2!=null){
      int length = Math.min(mask1.length, mask2.length);
      for (; i < length; i++){
        if (mask1[i]!=mask2[i])
          break;
      }
    }
    return i;
  }


  private ExprWithTypeVariable constructCondition(boolean isPlaceHolder){
      GraphNode last = dataGraph.currentNode();
      int[] last_mark = last.mark();
      int[] current_mark = indexStack.toIntArray();
      int last_len = last_mark.length;
      int same_size =  sizeInSameDomain(last_mark, current_mark);

      //last node need to pop current
      // until they have the same prefix until the last index(exclude the last element)
      // when i < len-1 means they are not in the same domain
      // last node need to pop up until current node and last node
      // are in the same domain
      while (same_size < last_len - 1) {
        // last node = last node's father node
        dataGraph.popCurrent(false);
        last_mark = dataGraph.currentNode().mark();
        last_len = last_mark.length;
      }

      ExprWithTypeVariable cond = core.EXP_TRUE;

      //from same_index to current_node mark index to construct condition
      //reflesh same_index
      int same_index =  sizeInSameDomain(last_mark, current_mark);
      same_index |= 1; // odd num not change, even num self-add

//
//      System.out.println("last_mark " + Arrays.toString(last_mark));
//      System.out.println("current_mark " + Arrays.toString(current_mark));
//
      List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks = allCurrentBlocks(same_index);
      for (int j = 0; j < allCurrentBlocks.size(); j++){
        Pair<ExpressionOrBlock, ExpressionOrBlockList> block = allCurrentBlocks.get(j);
        if (block.getLeft() instanceof ConditionalBlock){

            ExprWithTypeVariable exp;
            //skip the last one is the block... when we construct a placeholder node
            //we are not in the expression area, so that there are some problems to be fixed
            // if last block is the block, we just skip it.
            if (isPlaceHolder && same_index >= indexStack.size() && j==allCurrentBlocks.size()-1)
              break;
            int cond_flag = indexStack.get(same_index);

            if (cond_flag == COND_INDEX_IF){
                ExpressionListWithPrevs e = ((ConditionalBlock)block.getLeft()).getBoolExp();
                exp = constructConditionByExpression(e);
                ((ConditionalBlock) block.getLeft()).setCondSaver(exp);

            }
            else if (cond_flag == COND_INDEX_ELSE)
            {
                exp = core.mkNot(((ConditionalBlock) block.getLeft()).getCondSaver());
            }
            else
            {
              throw new ASTParsingException("expected condition flag -1 or -2 but got " + cond_flag);
            }

            cond = core.mkAnd(cond, exp);
            same_index +=2; //depth of one block in index stack

        }
      }
      return cond;
  }
//
//  private List<Pair<ExprWithTypeVariable, int[]>> constructConditionsByExecutionMode(){
//        List<Pair<ExprWithTypeVariable, int[]>> ret = new ArrayList<>();
//        GraphNode last = dataGraph.preCurrentNode();
//        int[] last_mark = last.mark();
//        int[] current_mark = indexStack.toIntArray();
//        int last_len = last_mark.length;
//        int same_size =  sizeInSameDomain(last_mark, current_mark);
//
//        //last node need to pop current
//        // until they have the same prefix until the last index(exclude the last element)
//        // when i < len-1 means they are not in the same domain
//        // last node need to pop up until current node and last node
//        // are in the same domain
//        while (same_size < last_len - 1) {
//            // last node = last node's father node
//            dataGraph.popCurrent(false);
//            last_mark = dataGraph.currentNode().mark();
//            last_len = last_mark.length;
//        }
//
//        //from same_index to current_node mark index to construct condition
//        //reflesh same_index
//        int same_index =  sizeInSameDomain(last_mark, current_mark);
//        same_index |= 1; // odd num not change, even num self-add
//
////
////      System.out.println("last_mark " + Arrays.toString(last_mark));
////      System.out.println("current_mark " + Arrays.toString(current_mark));
////
//        List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks = allCurrentBlocks(same_index);
//        for (int j = 0; j < allCurrentBlocks.size(); j++){
//            Pair<ExpressionOrBlock, ExpressionOrBlockList> block = allCurrentBlocks.get(j);
//            if (block.getLeft() instanceof ConditionalBlock){
//
//                ExprWithTypeVariable exp;
//                int cond_flag = indexStack.get(same_index);
//                if (cond_flag == COND_INDEX_IF){
//                    ExpressionListWithPrevs e = ((ConditionalBlock)block.getLeft()).getBoolExp();
//                    exp = constructConditionByExpression(e);
//                    ((ConditionalBlock) block.getLeft()).setCondSaver(exp);
//                }
//                else if (cond_flag == COND_INDEX_ELSE)
//                    exp = core.mkNot(((ConditionalBlock) block.getLeft()).getCondSaver());
//                else
//                {
//                    throw new ASTParsingException("expected condition flag -1 or -2 but got " + cond_flag);
//                }
//
//                ret.add(new Pair<>(exp, last_mark));
//                same_index +=2; //depth of one block in index stack
//
//            }
//        }
//        return ret;
//    }

  private void handleStatement(Statement e){

    switch (e.getType()){
      case RETURN_STATEMENT://fixme it is a terminal
        break;
      case THROW_STATEMENT:
        break;
      default:
        break;
    }
    ExpressionListWithPrevs exp = e.getExp();
    List<Expression> exps = exp.toExpressionList();
    for (Expression ee : exps){
        handleExpression(ee);
    }
  }

  //stack handle functions
  private void updateIndex(int i){
    int  len = indexStack.size();
    modifyStackByIndex(len - 1, i);
  }

  private void selfAddIndex(){
    int i = indexStack.peek();
    updateIndex(++i);
  }

  private void selfMinusIndex(){
    int i = indexStack.peek();
    updateIndex(--i);
  }

  private void modifyStackByIndex(int index, int value){
    if (index < 0)
      throw new EmptyStackException();
    indexStack.set(index, value);
  }

  private static final int COND_INDEX_IF = -1;
  private static final int COND_INDEX_ELSE = -2;
  private static final int COND_INDEX_OVERFLOW = -4;

  public int getCondIndex(int index){
    //overflow means we haven't entered this if-else block
    //we just figure it out to enter it
    if (index >= indexStack.size())
      return COND_INDEX_OVERFLOW;
    return indexStack.get(index);
  }



  /**
   * @return from stack index start to the current Expression and the current ExpressionOrBlockList
   * (which contains the current expression) Pair
   * if point < 0 , just return the methodBody handle
   *
   */
  private  List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks(int start){
    ExpressionOrBlock cur = null;
    ExpressionOrBlockList curList = methodBody;
    int size = indexStack.size();
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> rets = new ArrayList<>();
    if (0 >= start)
      rets.add(new Pair<>(cur, curList));
    for (int i =0  ; i < size; ++i){
      int index = indexStack.get(i);

      cur = curList.get(index);
      if (cur instanceof Block){

        if (cur instanceof ConditionalBlock){
          int condIndex = getCondIndex(++i);
          if (condIndex == COND_INDEX_IF)
          {
            curList = ((ConditionalBlock)cur).getContent();
          }
          else if (condIndex == COND_INDEX_ELSE)
          {
            curList = ((ConditionalBlock)cur).getElseBlock();
          }else {
            curList = ((Block)cur).getContent();
          }
        }else {
          curList = ((Block)cur).getContent();
        }
      }

      Pair p =  new Pair<>(cur, curList);
      if (i >= start)
        rets.add(p);
    }
    return rets;
  }

  private Pair<ExpressionOrBlock, ExpressionOrBlockList> indexStackAtCurrentPoint() {
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurBlocks = allCurrentBlocks(0);
    if (allCurBlocks.size()<1)
      throw new ASTParsingException("allCurBlocks size less than 1");
    return allCurBlocks.get(allCurBlocks.size()-1);
  }


  private RuntimeValue handleUnterminalSymbol(Symbol symbol){
    if (symbol instanceof TmpSymbol){
      Expression value = ((TmpSymbol)symbol).getExpression();
      RuntimeValue ret = handleExpression(value);
      tmpTable.addVariable(((TmpSymbol) symbol).getName(), ret);
      return ret;
    }else if(symbol instanceof PointSymbol){
      RuntimeValue ret = null;
      TerminalSymbol left = ((PointSymbol)symbol).getExp();
      if (traceList.contains(left.toString())){
        if (((PointSymbol)symbol).isFunc()){
          CallFunc callFunc = (CallFunc)((PointSymbol)symbol).getV();
          if (mode == DEFAULT_MODE) {
            ParcelDataNode node = ParcelDataNode.parseCallFunc(core, callFunc, indexStack.toIntArray());
            dataGraph.addNewNode(constructCondition(false), node);
            LOG.info("construct " + node.toString());
          }else if (mode == EXECUTION_MODE){
            //need to rebase?
            int[] last_mark = dataGraph.currentNode().mark();
            int[] current_mark = indexStack.toIntArray();
            int last_len = last_mark.length;
            int same_size =  sizeInSameDomain(last_mark, current_mark);

            //last node need to pop current
            // until they have the same prefix until the last index(exclude the last element)
            // when i < len-1 means they are not in the same domain
            // last node need to pop up until current node and last node
            // are in the same domain
            boolean isPop = false;
            while (same_size < last_len - 1) {
              // last node = last node's father node
              dataGraph.popCurrent(true);
              last_mark = dataGraph.currentNode().mark();
              last_len = last_mark.length;
              isPop = true;
            }

            if (isPop)
            {
                List<Edge> childrenEdge = dataGraph.currentNode().getChildren();
                //we need to set choose flag if we have more than one successor
                //If we pop to one node, which means this node must have more than one
                //successor, so that we can explore new state
                if (childrenEdge.size() > 1){
                    dataGraph.setChooseFlag(true);
                }else {
                    throw new ASTParsingException("go to anther passed edge");
                }
            }

            if (dataGraph.needToChooseBranch()){
              List<Pair<ExprWithTypeVariable, int[]>> conds = new ArrayList<>();
              //last_mark may have changed after node pop
              last_mark = dataGraph.currentNode().mark();
              int same_index = sizeInSameDomain(last_mark, current_mark);
              same_index |= 1; // odd num not change, even num self-add
              List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks = allCurrentBlocks(same_index);
              for (int j = 0; j < allCurrentBlocks.size(); j++){
                Pair<ExpressionOrBlock, ExpressionOrBlockList> block = allCurrentBlocks.get(j);
                if (block.getLeft() instanceof ConditionalBlock){

                  ExprWithTypeVariable exp;
                  int cond_flag = indexStack.get(same_index);
                  if (cond_flag == COND_INDEX_IF){
                    ExpressionListWithPrevs e = ((ConditionalBlock)block.getLeft()).getBoolExp();
                    exp = constructConditionByExpression(e);
                    ((ConditionalBlock) block.getLeft()).setCondSaver(exp);
                  }
                  else if (cond_flag == COND_INDEX_ELSE)
                  {
                      exp = core.mkNot(((ConditionalBlock) block.getLeft()).getCondSaver());
                  }
                  else
                  {
                    throw new ASTParsingException("expected condition flag -1 or -2 but got " + cond_flag);
                  }

                  conds.add(new Pair<>(exp, last_mark));
                  same_index +=2; //depth of one block in index stack

                }
              }

              if (conds.size() == 0){
                throw new ParcelMismatchException("expected an one branch node, but got more than one branch" );
              }
              //List<Pair<ExprWithTypeVariable, int[]>> conds = constructConditionsByExecutionMode();
              for (int i = 0; i < conds.size(); i++){
                  boolean islast = i == conds.size() - 1;
                  ExprWithTypeVariable cond = conds.get(i).getLeft();
                  if (islast){
                      dataGraph.currentNode().chooseBranch(core, cond);
                      ParcelDataNode tmpNode = ParcelDataNode.parseCallFunc(core, callFunc, indexStack.toIntArray());
                      GraphNode currentNode = dataGraph.currentNode();
                      if (!(currentNode instanceof ParcelDataNode))
                        throw new ParcelMismatchException("currentNode is not ParcelDataNode");
                      if (!ParcelDataNode.compareTwoNode((ParcelDataNode)currentNode, tmpNode))
                        throw new ParcelMismatchException("Expected  " + (currentNode) + " but got " + tmpNode);
                      currentNode.setMark(tmpNode.mark());
                  }else {
                      dataGraph.currentNode().chooseBranch(core, cond);
                      if (!dataGraph.currentNode().isPlaceholder())
                      {
                          throw new ParcelMismatchException("expected a placeholder but got a " + dataGraph.currentNode());
                      }
                      dataGraph.currentNode().setMark(conds.get(i).getRight());
                  }
              }
              dataGraph.setChooseFlag(false);
               ret = (ParcelDataNode)dataGraph.currentNode();
            }else {
              List<Edge> childrenEdge = dataGraph.currentNode().getChildren();
              while (childrenEdge.size() == 1 &&
                        dataGraph.currentNode().isPlaceholder()) {
                    //only have one path and the node is a placeholder, so that we just go to the next node
                    Edge childEdge = childrenEdge.get(0);
                    GraphNode nextNode = dataGraph.getNodeById(childEdge.getRight());
                    nextNode.setMark(indexStack.toIntArray());
                    dataGraph.updateNodeIndex(nextNode.getIndex(), true);
              }

              ParcelDataNode tmpNode = ParcelDataNode.parseCallFunc(core, callFunc, indexStack.toIntArray());
              GraphNode currentNode = dataGraph.currentNode();
              if (!(currentNode instanceof ParcelDataNode))
                    throw new ParcelMismatchException("currentNode is not ParcelDataNode");
              if (!ParcelDataNode.compareTwoNode((ParcelDataNode) currentNode, tmpNode))
                    throw new ParcelMismatchException("Expected  " + (currentNode) + " but got " + tmpNode);
              ret = (ParcelDataNode)currentNode;
              }
             //set next
              List<Edge> childrenEdge = dataGraph.currentNode().getChildren();
              if (childrenEdge.size() == 1) {
                Edge childEdge = childrenEdge.get(0);
                GraphNode nextNode = dataGraph.getNodeById(childEdge.getRight());
                nextNode.setMark(indexStack.toIntArray());
                dataGraph.updateNodeIndex(nextNode.getIndex(), true);
              }else {
                //wait to next callfunc to compare condition expression
                dataGraph.setChooseFlag(true);
              }
              return ret;
          }
        }
      }
    }else if (symbol instanceof CallFunc){
        CallFunc callFunc = (CallFunc) symbol;
        List<TerminalSymbol> pms = callFunc.getParams();
        for (int i = 0; i < pms.size(); i++){
            TerminalSymbol pm = pms.get(i);
            if(traceList.contains(pm.toString())){
                //need to go to the function!
                FunctionImp imp;
                if (callFunc.isConstructFunc()){
                    JavaClass javaClass = antlrCore.findClassByName(callFunc.getFuncName());
                    imp = javaClass.getFunctionImpByName(callFunc.getFuncName());
                }else {
                    imp = jclass.getFunctionImpByName(callFunc.getFuncName());
                }
                //first need to find defination
                ASTIterator inlineIterator = new ASTIterator(antlrCore, imp, mode, dataGraph, true);
                inlineIterator.traceList.add(imp.getFunDec().getParams().get(i).getName());
                inlineIterator.fathers.add(this);
                inlineIterator.start();
            }
        }
    }
    return new DefaultRuntimeValue();
  }


  private RuntimeValue handleExpression(Expression e){
    if (e.isSymbol()){
      return handleUnterminalSymbol(e.getSymbol());
    }else{
      if (e.isAssign()){
          TerminalSymbol left = e.getLeft();
          TerminalSymbol right = e.getRight();
          RuntimeValue v = null;
          if (right instanceof TmpSymbol){
            v = tmpTable.getVariableByName(((TmpSymbol) right).getName());
            if (v == null){
                throw new ASTParsingException("tmp variable cannot found in tmp table");
            }
          }
          if (v != null){
            variableTable.addVariable(left.toString(), v);
          }
      }
    }
    return new DefaultRuntimeValue();
  }


  public void continueToTaint(){

    while (!indexStack.empty()){

        LOG.info("current index stack is " + indexStack.toString());

        Pair<ExpressionOrBlock, ExpressionOrBlockList> pair = indexStackAtCurrentPoint();
        ExpressionOrBlock cur = pair.getLeft();
        ExpressionOrBlockList curList = pair.getRight();
        if (cur == null){
          //empty block handle
        }
        else if (cur instanceof Expression){
          LOG.info("Handling expression "+ cur.toString());
          handleExpression((Expression)cur);
          selfAddIndex();
        }
        else if (cur instanceof Block){
          //start a new block
          if (cur instanceof ConditionalBlock){
                indexStack.push(COND_INDEX_IF); // mark it is a statement
          }
          //other just change the stack
          indexStack.push(0);
        }
        else if (cur instanceof Statement){
            LOG.info("handling statement "+ cur.toString());
            handleStatement((Statement)cur);
            selfAddIndex();
        }
        else {
          throw new ASTParsingException("unhandled type");
        }
        //get out of current block
        if (indexStack.peek() >= curList.size()){
          indexStackPop();
          if (indexStack.empty())
            break;

          int top = indexStack.peek();
          if (top == COND_INDEX_ELSE){
            indexStackPop();// clear cond index because else executed finished
            if (indexStack.empty())
              break;
          }else if (top == COND_INDEX_IF){
            //go to else statement
            modifyStackByIndex(indexStack.size()-1 , COND_INDEX_ELSE);
            indexStack.push(0);
            if (mode == DEFAULT_MODE){
              ExpressionOrBlock curBlock = getRecentBlock();
              if (curBlock instanceof ConditionalBlock){
                if (indexStack.get(indexStack.size()-2) == COND_INDEX_ELSE)
                  dataGraph.addNewNode(constructCondition(true),
                                       ParcelDataNode.initEmptyInstance(indexStack.toIntArray()));
              }
            }else if (mode == EXECUTION_MODE){

            }

            continue;
          }
          //this block finished, so that father pointer must self-add
          selfAddIndex();
        }
    }
  }


  private void indexStackPop(){

    if (mode == DEFAULT_MODE){
      if (indexStack.size() > 1){
        int cond_index = indexStack.get(indexStack.size()-2);

        if (cond_index == COND_INDEX_IF)
        {
          // the finished index
          dataGraph.addNewNode(core.EXP_TRUE, ParcelDataNode.initEmptyInstance(indexStack.toIntArray()));
        }else if (cond_index == COND_INDEX_ELSE){

          //to find last expression in if statement
          int [] mark = indexStack.toIntArray();
          mark[mark.length-2] = COND_INDEX_IF;
          GraphNode node = dataGraph.findNodeByMark(mark, mark.length-1);
          if (node == null)
            throw new ASTParsingException("Cannot find graph node by mark " + Arrays.toString(mark));
          while (node.getChildren()!=null && node.getChildren().size() > 0){
            Edge childIndexEdge =  node.getChildren().get(0);
            int index = childIndexEdge.getRight();
            node = dataGraph.getNodeById(index);
          }
          if (!node.isPlaceholder())
            throw new ASTParsingException("Expect a placeholder but a "+ node.toString());
          dataGraph.addNewNode(core.EXP_TRUE, node);
        }
      }else {
        //exit
      }
    }else if (mode == EXECUTION_MODE){
        if (indexStack.size()>1){
          int cond_index = indexStack.get(indexStack.size() - 2);
          if (cond_index == COND_INDEX_IF){
              //need to pop?

          }else if (cond_index == COND_INDEX_ELSE){

          }
        }

    }
    indexStack.pop();
  }

  public boolean nextStage(){
    continueToTaint();
    return hasNextStage();
  }

  public boolean hasNextStage(){
    return !indexStack.empty();
  }


  public int getNodeIndexByAttachedName(String name){
    return dataGraph.getIndexByAttachedName(name);
  }

  public Graph getDataGraph() {
    return dataGraph;
  }


  public RuntimeValue getRuntimeValueByName(String name){
      return variableTable.getVariableByName(name);
  }

  public boolean start(){
      while (hasNextStage()){
          System.out.println("nextStage");
          continueToTaint();
      }

      if (mode == EXECUTION_MODE){
         return dataGraph.allEdgePassed();
      }

      //ignore the result
      return false;
  }

    public JavaClass getJavaclass() {
        return jclass;
    }
}
