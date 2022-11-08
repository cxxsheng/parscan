package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.PointSymbol;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ExpressionHandler.class);

  private List<ExpressionHandlerCallback> callbacks;

  public static final String TAG_UNIVERSAL = "Universal";
  public static final String TAG_POINT_SYMBOL = "PointSymbol";


  public ExpressionHandler(){
    callbacks = new ArrayList<>();
  }

  public void addCallback(ExpressionHandlerCallback callback){
    callbacks.add(callback);
  }

  private ExpressionHandlerCallback getCallbackByName(String name){
    for (ExpressionHandlerCallback callback : callbacks){
      if (callback.getTag()!=null && callback.getTag().equals(name))
        return callback;
    }
    return null;
  }

  /**
   * @param e the expression we need to handle
   * @return true when we hit some traceList so we block
   * the iterator and wait to evaluate the state between
   * two functions.
   */

  public void handleExpression(Expression e){
    ExpressionHandlerCallback universalCallback = getCallbackByName(TAG_UNIVERSAL);
    if (e.isTerminal()){
      Symbol s = e.getSymbol();

      if (universalCallback!=null)
        universalCallback.handleSymbol(s, true);


      if (s instanceof PointSymbol){
        ExpressionHandlerCallback callback = getCallbackByName(TAG_POINT_SYMBOL);
        Expression exp = ((PointSymbol)s).getExp();

        boolean hitFlag = false;
        if (!exp.isTerminalSymbol()){
          // this expression can be decomposed
          handleExpression(e);
        }else {
          //only has one identifier and cannot be decomposed
          Symbol terminal_sym = exp.getSymbol();
          if (callback != null)
            hitFlag = callback.broadcastHit(terminal_sym);
            //prefixExp = iterator.checkTraceList(terminal_sym.toString());
        }

        //this is a point function
        if (((PointSymbol)s).isFunc()){
          //handling params first
          CallFunc callFunc = (CallFunc)((PointSymbol)s).getV();
          List<Expression> params = callFunc.getParams();
          if (params!=null)
            for(Expression p : callFunc.getParams()){
              if (!p.isTerminalSymbol())
                 handleExpression(p);
            }

          if (callback!=null){
            callback.handleSymbol(callFunc, hitFlag);
          }

        }else {
          //identifier


        }

        return;
      }
      //pure function call instead of point call func
      else if (s instanceof CallFunc){
        boolean ret = false;
        List<Expression> params = ((CallFunc)s).getParams();
        if (params != null)
          for(Expression p : ((CallFunc)s).getParams()){
             handleExpression(p);
          }
        return;
      }


      return ;
    }else{

      boolean left = false;
      boolean right  = false;

      if (e.leftCanDecompose())
      {
        handleExpression(e.getL());
      }
      if (e.rightCanDecompose()){
        handleExpression(e.getR());
      }

      if (universalCallback!=null)
        universalCallback.handleExpression(e, true);

      return;
    }

  }



}
