package com.cxxsheng.parscan.core.iterator;

import static com.cxxsheng.parscan.core.data.Statement.RETURN_STATEMENT;
import static com.cxxsheng.parscan.core.data.Statement.THROW_STATEMENT;
import static com.cxxsheng.parscan.core.iterator.ExpressionHandler.TAG_BINARY_EXP;
import static com.cxxsheng.parscan.core.iterator.ExpressionHandler.TAG_POINT_SYMBOL;
import static com.cxxsheng.parscan.core.iterator.ExpressionHandler.TAG_UNIVERSAL;

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
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
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


  private final JavaClass javaClass;

  public final FunctionImp imp;

  public final ExpressionOrBlockList methodBody;

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);

  private Stack indexStack;

  private final List<String> traceList;

  private final Graph dataGraph;

  private final ExpressionHandler H;

  private final ExpressionHandler condH;

  private ExprWithTypeVariable current_condition = null;

  private final Z3Core core;

  private final VariableTable vt = new VariableTable();

  //Default mode means ASTIterator construct a data node graph
  //while iterating the ast.
  public final static int DEFAULT_MODE = 0;

  //Execution mode means ASTIterator check the given graph
  //while iterating the ast.
  public final static int EXECUTION_MODE = 1;

  private final int mode;

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

  public ASTIterator(JavaClass javaClass, FunctionImp imp){
      this(javaClass, imp, DEFAULT_MODE, new Graph());
  }

  public ASTIterator(JavaClass javaClass, FunctionImp imp, Graph givenGraph){
      this(javaClass, imp, EXECUTION_MODE, givenGraph);
      givenGraph.recovery();
  }

  public ASTIterator(JavaClass javaClass, FunctionImp functionImp, int mode, Graph graph) {
      this.imp = functionImp;
      this.methodBody = functionImp.getBody();
      this.javaClass = javaClass;
      this.mode = mode;
      this.dataGraph = graph;
      traceList = new ArrayList<>();
      initTraceList();

      core = new Z3Core(javaClass, this) ;

      indexStack = new Stack();
      indexStack.push(0);


      H = new ExpressionHandler();
      H.addCallback(new ExpressionHandlerCallback() {
        @Override
        public RuntimeValue handleSymbol(Symbol s, boolean isHit) {
          if (isHit){
            //prefix is tainted
            if(!(s instanceof CallFunc)){
              throw new ASTParsingException("Cannot handle such symbol " + s + "and expect CallFunc.");
            }

            if (mode == DEFAULT_MODE) {
                ParcelDataNode node = ParcelDataNode.parseCallFunc(core, (CallFunc)s, indexStack.toIntArray());
                dataGraph.addNewNode(constructCondition(false), node);
                LOG.info("construct " + node.toString());
            }else if (mode == EXECUTION_MODE){
              List<Pair<ExprWithTypeVariable, Integer>> childrenPair  = dataGraph.currentNode().getChildren();
              while (childrenPair.size() == 1 && dataGraph.getNodeById(childrenPair.get(0).getRight()).isPlaceholder()){
                //only have one path and it is a placeholder
                GraphNode nextNode = dataGraph.getNodeById(childrenPair.get(0).getRight());
                //broadcast the mark
                nextNode.setMark(dataGraph.currentNode().mark());
                dataGraph.updateNodeIndex(nextNode.getIndex(), true);
              }

              GraphNode cur = dataGraph.currentNode();

              if (cur instanceof ParcelDataNode){

                ExprWithTypeVariable variable =  constructCondition(false);
              }


              //if (node instanceof ParcelDataNode){
              //      System.out.println("current conditoin " + variable);
              //      ParcelDataNode tmpNode = ParcelDataNode.parseCallFunc(core, (CallFunc)s, indexStack.toIntArray());
              //      System.out.println("comparing " + tmpNode.toString() +" /:/ "+node.toString());
              //      if (!((ParcelDataNode)node).getJtype().equals(tmpNode.getJtype()))
              //        throw new ParcelMismatchException("java type mismatch!");
              //
              //      node.setMark(indexStack.toIntArray());
              //  }

            }
          }
          return null;
        }
        @Override
        public RuntimeValue handleExpression(Expression e, boolean isHit) {
          return null;
        }

        @Override
        public List<RuntimeValue> handleBinExpression(List<RuntimeValue> left,
                                                      Operator op,
                                                      List<RuntimeValue> right,
                                                      boolean isHit) {
          return null;
        }

        @Override
        public boolean broadcastHit(Symbol terminalSymbol) {
          if (!terminalSymbol.isTerminal())
            throw new ASTParsingException("Cannot handle such symbol " + terminalSymbol + "and expect a terminal symbol.");
          return checkTraceList(terminalSymbol.toString());
        }

        @Override
        public String getTag() {
          return TAG_POINT_SYMBOL;
        }
      });

      H.addCallback(new ExpressionHandlerCallback() {
        @Override
        public RuntimeValue handleSymbol(Symbol s, boolean isHit) {
          return null;
        }

        @Override
        public RuntimeValue handleExpression(Expression e, boolean isHit) {
          return null;
        }

        @Override
        public List<RuntimeValue> handleBinExpression(List<RuntimeValue> left, Operator op, List<RuntimeValue> right, boolean isHit) {
          if (isHit){
            if (op == Operator.AS){
              if (left.size() > 1){
                throw new ASTParsingException("assign expression's left identifier more than 1");
              }
              RuntimeValue left_ = left.get(0);
              //if (right)
              for (int i =0; i < right.size(); i++){
                RuntimeValue v = right.get(i);
                if (v instanceof ParcelDataNode){
                  ((ParcelDataNode)v).addIdentifier(left_.toString());
                }
              }
            }

          }
          return null;
        }

        @Override
        public boolean broadcastHit(Symbol terminalSymbol) {
          return false;
        }

        @Override
        public String getTag() {
          return TAG_BINARY_EXP;
        }
      });

      condH = new ExpressionHandler();
      condH.addCallback(new ExpressionHandlerCallback() {
        @Override
        public RuntimeValue handleSymbol(Symbol s, boolean isHit) {
          return null;
        }

        @Override
        public RuntimeValue handleExpression(Expression e, boolean isHit) {
              current_condition = core.mkExpression(e);
              return null;
        }

        @Override
        public List<RuntimeValue> handleBinExpression(List<RuntimeValue> left,
                                                      Operator op,
                                                      List<RuntimeValue> right,
                                                      boolean isHit) {
          return null;
        }

          @Override
        public boolean broadcastHit(Symbol terminalSymbol) {
          return false;
        }

        @Override
        public String getTag() {
          return TAG_UNIVERSAL;
        }
      });


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

  private ExprWithTypeVariable constructConditionByExpression(Expression e){
      condH.handleExpression(e);
      if (current_condition == null )
        throw new ASTParsingException("cannot access condition at " + e);
      ExprWithTypeVariable tmp = current_condition;
      current_condition = null; //Clear it is very important for checking constructCondition successful or not
      return tmp;
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
      while (same_size < last_len-1) {
        // last node = last node's father node
        dataGraph.popCurrent();
        last_mark = dataGraph.currentNode().mark();
        last_len = last_mark.length;
      }

      ExprWithTypeVariable cond = core.EXP_TRUE;

      //from same_index to current_node mark index to construct condition
      //reflesh same_index
      int same_index =  sizeInSameDomain(last_mark, current_mark);
      same_index |= 1; // odd num not change, even num self-add


      System.out.println("last_mark " + Arrays.toString(last_mark));
      System.out.println("current_mark " + Arrays.toString(current_mark));

      List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks = allCurrentBlocks(same_index);
      for (int j = 0; j < allCurrentBlocks.size(); j++){
        Pair<ExpressionOrBlock, ExpressionOrBlockList> block = allCurrentBlocks.get(j);
        if (block.getLeft() instanceof ConditionalBlock){

            Expression e = ((ConditionalBlock)block.getLeft()).getBoolExp();
            ExprWithTypeVariable exp = constructConditionByExpression(e);


            //skip the last one is the block... when we construct a placeholder node
            //we are not in the expression area, so that there are some problems to be fixed
            // if last block is the block, we just skip it.
            if (isPlaceHolder && same_index >= indexStack.size() && j==allCurrentBlocks.size()-1)
              break;

            int cond_flag = indexStack.get(same_index);
            if (cond_flag == COND_INDEX_IF)
              ;
            else if (cond_flag == COND_INDEX_ELSE)
              exp = core.mkNot(exp);
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

  private void handleStatement(Statement e){

    switch (e.getType()){
      case RETURN_STATEMENT://fixme it is a terminal
        break;
      case THROW_STATEMENT:
        break;
      default:
        break;
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
          LOG.info("handling expression "+ cur.toString());

          H.handleExpression((Expression)cur);
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
                ExprWithTypeVariable expr =  constructConditionByExpression(((ConditionalBlock)curBlock).getBoolExp());
                System.out.println(expr);
                if (indexStack.get(indexStack.size()-2) == COND_INDEX_ELSE)
                  dataGraph.addNewNode(constructCondition(true), ParcelDataNode.initEmptyInstance(indexStack.toIntArray()));
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


  private final void indexStackPop(){

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
            Pair<ExprWithTypeVariable, Integer> childIndexEdge =  node.getChildren().get(0);
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


  public final int getNodeIndexByAttachedName(String name){
    return dataGraph.getIndexByAttachedName(name);
  }

  public Graph getDataGraph() {
    return dataGraph;
  }


  public void putRuntimeValue(String name , RuntimeValue value){
    RuntimeVariable var = new RuntimeVariable(indexStack.toIntArray(), name);
    vt.putVariable(var, value);
  }

}
