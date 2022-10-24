package com.cxxsheng.parscan.core.iterator;

import static com.cxxsheng.parscan.core.data.Statement.RETURN_STATEMENT;
import static com.cxxsheng.parscan.core.data.Statement.THROW_STATEMENT;

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
import com.cxxsheng.parscan.core.data.unit.symbol.PointSymbol;
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

  public final ExpressionOrBlockList content;

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);

  private Stack<Integer> indexStack;

  private final List<String> traceList;


  private void initTraceList(){
    List<FunctionPattern> ps = FunctionPattern.getPatterns();
    for (FunctionPattern p : ps){
      String type = p.getPatternType();
      if ("methodParam".equals(type)){
          Integer index = p.getPatternInt("index");
          if (index!=null && index >=0 ){
            Parameter param = imp.getFunDec().getParams().get(index);
            traceList.add(param.getName());
          }
      }else if ("variableName".equals(type)){

      }
    }
  }


  public ASTIterator(JavaClass javaClass, FunctionImp functionImp) {
    this.imp = functionImp;
    this.content = functionImp.getBody();
    this.javaClass = javaClass;
    traceList = new ArrayList<>();
    initTraceList();
    indexStack = new Stack<>();
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


  /**
   * @param e the expression we need to handle
   * @return true when we hit some traceList so we block
   * the iterator and wait to evaluate the state between
   * two functions.
   */

  private boolean handleExpression(Expression e){
    if (e.isTerminal()){
        Symbol s = e.getSymbol();
        if (s instanceof PointSymbol){

              boolean prefixExp = false;
              boolean surfixSymbol = false;
              Expression exp = ((PointSymbol)s).getExp();
              if (!exp.isTerminalSymbol()){
                    // this expression can be decomposed
                prefixExp = handleExpression(e);
              }else {
                    //only has one id
                    Symbol terminal_sym = exp.getSymbol();
                    prefixExp = checkTraceList(terminal_sym.toString());
              }


              //this is a function
              if (((PointSymbol)s).isFunc()){

                  //handling params first
                  CallFunc callFunc = (CallFunc)((PointSymbol)s).getV();
                  List<Expression> params = callFunc.getParams();
                  if (params!=null)
                    for(Expression p : callFunc.getParams()){
                      if (!p.isTerminalSymbol())
                        surfixSymbol |= handleExpression(p);
                    }
                  if (prefixExp){
                      //prefix is tainted
                      LOG.info(s.toString());
                  }

              }else {
                   //identifier

              }

              return prefixExp | surfixSymbol;
        }
        else if (s instanceof CallFunc){
          boolean ret = false;
          List<Expression> params = ((CallFunc)s).getParams();
          if (params != null)
            for(Expression p : ((CallFunc)s).getParams()){
              ret |= handleExpression(p);
            }
          return ret;
        }
        return false;
    }else{

        boolean left = false;
        boolean right  = false;

        if (e.leftCanDecompose())
        {
           left = handleExpression(e.getL());
        }
        if (e.rightCanDecompose()){
           right = handleExpression(e.getR());
        }
        return left | right;
    }

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



  public void continueToTaint(){
    while (!indexStack.empty()){

        System.out.println(indexStack);

        ExpressionOrBlock cur = null;
        ExpressionOrBlockList curList = content;


        for (int i =0  ; i < indexStack.size(); ++i){
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

        if (cur == null){
          //empty block handle

        }

        else if (cur instanceof Expression){

          LOG.info("handling expression "+ cur.toString());
          boolean hitTaint = handleExpression((Expression)cur);
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
