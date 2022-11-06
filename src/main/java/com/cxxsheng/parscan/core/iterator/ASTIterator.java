package com.cxxsheng.parscan.core.iterator;

import static com.cxxsheng.parscan.core.data.Statement.RETURN_STATEMENT;
import static com.cxxsheng.parscan.core.data.Statement.THROW_STATEMENT;
import static com.cxxsheng.parscan.core.iterator.ExpressionHandler.TAG_POINT_SYMBOL;
import static com.cxxsheng.parscan.core.iterator.ExpressionHandler.TAG_UNIVERSAL;

import com.cxxsheng.parscan.core.common.Pair;
import com.cxxsheng.parscan.core.data.Block;
import com.cxxsheng.parscan.core.data.ConditionalBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.Statement;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ASTIterator {


  private final JavaClass javaClass;

  public final FunctionImp imp;

  public final ExpressionOrBlockList methodBody;

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);

  private Stack<Integer> indexStack;

  private final List<String> traceList;

  private final Tree dataTree = new Tree();

  private final ExpressionHandler H;

  private final ExpressionHandler condH;

  private Condition current_condition = null;

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


  public ASTIterator(JavaClass javaClass, FunctionImp functionImp) {
      this.imp = functionImp;
      this.methodBody = functionImp.getBody();
      this.javaClass = javaClass;
      traceList = new ArrayList<>();
      initTraceList();
      indexStack = new Stack<>();
      indexStack.push(0);


      H = new ExpressionHandler();
      H.addCallback(new ExpressionHandlerCallback() {
        @Override
        public void handleSymbol(Symbol s, boolean isHit) {
          if (isHit){
            //prefix is tainted
            if(!(s instanceof CallFunc)){
              throw new ASTParsingException("Cannot handle such symbol " + s + "and expect CallFunc.");
            }
            ParcelDataNode node = ParcelDataNode.parseCallFunc((CallFunc)s);
            //ParcelDataNode node = ParcelDataNode
            if (indexStack.size()==1){
              dataTree.addNewNode(Condition.TRUE, node);
            }
            else {
              dataTree.addNewNode(constructCondition(), node);
            }
            LOG.info("construct "+ node.toString());
          }
        }
        @Override
        public void handleExpression(Expression e, boolean isHit) {
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

        condH = new ExpressionHandler();
        condH.addCallback(new ExpressionHandlerCallback() {
        @Override
        public void handleSymbol(Symbol s, boolean isHit) {

        }

        @Override
        public void handleExpression(Expression e, boolean isHit) {

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

  private Condition constructConditionByExpression(Expression e){
      condH.handleExpression(e);
      if (current_condition == null )
        throw new ASTParsingException("cannot access condition at " + e);
      Condition tmp = current_condition;
      current_condition = null; //Clear it is very important for checking constructCondition sucessful or not
      return tmp;
  }

  private Condition constructCondition(){
      int i = indexStack.peek();
      Pair<ExpressionOrBlock, ExpressionOrBlockList> pair = indexStackAtPoint(indexStack.size()-1);
      ExpressionOrBlock block = pair.getLeft();

      if (block instanceof ConditionalBlock){

        Expression e = ((ConditionalBlock)block).getBoolExp();
        return constructConditionByExpression(e);
      }
      return Condition.TRUE;
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
   * @return index the i-th stack to the current Expression and ExpressionOrBlockList(content) Pair
   */
  private Pair<ExpressionOrBlock, ExpressionOrBlockList> indexStackAtPoint(int point){
    ExpressionOrBlock cur = null;
    ExpressionOrBlockList curList = methodBody;
    int size = indexStack.size();

    if (point>0 && point < indexStack.size())
      size = point;

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
          continue;
        }

        curList = ((Block)cur).getContent();
      }
    }
    return new Pair<>(cur, curList);
  }

  private Pair<ExpressionOrBlock, ExpressionOrBlockList> indexStackAtCurrentPoint() {
    return indexStackAtPoint(-1);
  }


  public void continueToTaint(){

    while (!indexStack.empty()){

        System.out.println(indexStack);

        Pair<ExpressionOrBlock, ExpressionOrBlockList> pair = indexStackAtCurrentPoint();
        ExpressionOrBlock cur = pair.getLeft();
        ExpressionOrBlockList curList = pair.getRight();


        if (cur == null){
          //empty block handle

        }

        else if (cur instanceof Expression){
          LOG.info("handling expression "+ cur.toString());

          boolean hitTaint = H.handleExpression((Expression)cur);
          selfAddIndex();

          if (hitTaint)
            return;
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
          indexStack.pop();
          if (indexStack.empty())
            break;

          int top = indexStack.peek();
          if (top == COND_INDEX_ELSE){
            indexStack.pop();// clear cond index because else executed finished

            if (indexStack.empty())
              break;

          }else if (top == COND_INDEX_IF){
            //go to else statement
            modifyStackByIndex(indexStack.size()-1 , COND_INDEX_ELSE);
            indexStack.push(0);
            continue;
          }
          //this block finished, so that father pointer must self-add
          selfAddIndex();
        }
    }
  }



  public boolean nextStage(){
    continueToTaint();
    return hasNextStage();
  }

  public boolean hasNextStage(){
    return !indexStack.empty();
  }


}
