package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.antlr.exception.JavaScanException;
import com.cxxsheng.parscan.core.data.Block;
import com.cxxsheng.parscan.core.data.ConditionalBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.Statement;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.PointSymbol;
import com.cxxsheng.parscan.core.pattern.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import static com.cxxsheng.parscan.core.data.Statement.*;


public class ASTIterator {

  private final List<Pattern> pattern;

  private final JavaClass javaClass;

  public final ExpressionOrBlockList content;

  private final static Logger LOG = LoggerFactory.getLogger(ASTIterator.class);

  private Stack<Integer> indexStack;



  public ASTIterator(JavaClass javaClass, ExpressionOrBlockList content) {
    this.content = content;
    this.javaClass = javaClass;
    pattern = Pattern.getPatterns();
    indexStack = new Stack<>();
    indexStack.push(0);
  }


  //fixme must use it before it starts
  private boolean isReady(){
    return pattern != null && pattern.size() > 0;
  }




  private boolean handleExpression(Expression e){


    if (e.isTerminal()){
      Symbol s = e.getSymbol();
      if (s instanceof PointSymbol){
            if (((PointSymbol)s).getExp().toString().contains("source")){
              return false;
            }
      }


      return false;

    }

    if (e.isAssign()){
        return false;
    }


    if(e.hasLeft())
       return handleExpression(e.getL());
    if (e.hasRight())
       return handleExpression(e.getR());

    throw new JavaScanException("unreachable expression");
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

  private void updateIndex(int i){
    int  len = indexStack.size();
    modifyStackByIndex(len-1, i);
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

          LOG.info("handling statement "+ cur.toString());
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
          throw new JavaScanException("unhandeld type");
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
    return false;
  }

  public boolean hasNextStage(){
    return false;
  }


}
