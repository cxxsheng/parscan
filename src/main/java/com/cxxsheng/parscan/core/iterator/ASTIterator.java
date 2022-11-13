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
import com.cxxsheng.parscan.core.z3.Z3Core;
import com.microsoft.z3.Expr;
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

  private Expr current_condition = null;

  private final Z3Core core;

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

      core = new Z3Core(javaClass, this) ;

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

            if (indexStack.size()==1){
              dataTree.addNewNode(core.EXP_TRUE, node);
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
              current_condition = core.mkExpression(e);
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

  private Expr constructConditionByExpression(Expression e){
      condH.handleExpression(e);
      if (current_condition == null )
        throw new ASTParsingException("cannot access condition at " + e);
      Expr tmp = current_condition;
      current_condition = null; //Clear it is very important for checking constructCondition sucessful or not
      return tmp;
  }

  private ExpressionOrBlock getRecentBlock(){
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>>  blocks = allCurrentBlocks();
    ExpressionOrBlock curblock = blocks.get(blocks.size()-2).getLeft();
    return curblock;
  }

  private int lastSize;
  private Expr constructCondition(){
      int i = indexStack.peek();
      ExpressionOrBlock curblock = getRecentBlock();


      if (curblock instanceof ConditionalBlock){


        Expression e = ((ConditionalBlock)curblock).getBoolExp();
        if (indexStack.get(indexStack.size()-2) == COND_INDEX_IF){
          LOG.info("construct if condition by " + e);
          return constructConditionByExpression(e);
        }
      }
      return core.EXP_TRUE;
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
   * if point < 0 , just return the methodBody handle
   */
  private  List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurrentBlocks(){
    ExpressionOrBlock cur = null;
    ExpressionOrBlockList curList = methodBody;
    int size = indexStack.size();
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> rets = new ArrayList<>();
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
      rets.add(p);
    }
    return rets;
  }

  private Pair<ExpressionOrBlock, ExpressionOrBlockList> indexStackAtCurrentPoint() {
    List<Pair<ExpressionOrBlock, ExpressionOrBlockList>> allCurBlocks = allCurrentBlocks();
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

            ExpressionOrBlock curBlock = getRecentBlock();
            if (curBlock instanceof ConditionalBlock){
              constructConditionByExpression(((ConditionalBlock)curBlock).getBoolExp());
              if (indexStack.get(indexStack.size()-2) == COND_INDEX_ELSE)
                dataTree.addNewNode(constructCondition(), ParcelDataNode.initEmptyInstance());
            }
            continue;
          }
          //this block finished, so that father pointer must self-add
          selfAddIndex();
        }
    }
  }


  private final void indexStackPop(){
    indexStack.pop();
   // getDataTree().popCurrent();
  }

  public boolean nextStage(){
    continueToTaint();
    return hasNextStage();
  }

  public boolean hasNextStage(){
    return !indexStack.empty();
  }


  public final int getNodeIndexByAttachedName(String name){
    return dataTree.getIndexByAttachedName(name);
  }

  public Tree getDataTree() {
    return dataTree;
  }
}
