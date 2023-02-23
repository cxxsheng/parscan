package com.cxxsheng.parscan.core.iterator;

public class ExpressionHandler {
  //
  //private static final Logger LOG = LoggerFactory.getLogger(ExpressionHandler.class);
  //
  //private List<ExpressionHandlerCallback> callbacks;
  //
  //public static final String TAG_UNIVERSAL = "Universal";
  //public static final String TAG_POINT_SYMBOL = "PointSymbol";
  //public static final String TAG_ASSIGN_EXP = "AssignExpression";
  //public static final String TAG_EXP = "Expression";
  //
  //public ExpressionHandler(){
  //  callbacks = new ArrayList<>();
  //}
  //
  //public void addCallback(ExpressionHandlerCallback callback){
  //  callbacks.add(callback);
  //}
  //
  //private ExpressionHandlerCallback getCallbackByName(String name){
  //  for (ExpressionHandlerCallback callback : callbacks){
  //    if (callback.getTag()!=null && callback.getTag().equals(name))
  //      return callback;
  //  }
  //  return null;
  //}
  //
  ///**
  // * @param e the expression we need to handle
  // * @return true when we hit some traceList so we block
  // * the iterator and wait to evaluate the state between
  // * two functions.
  // */
  //
  //public RuntimeValue handleExpression(Expression e){
  //  ExpressionHandlerCallback universalCallback = getCallbackByName(TAG_UNIVERSAL);
  //  if (e.isTerminal()){
  //    Symbol s = e.getSymbol();
  //
  //
  //
  //
  //    if (s instanceof PointSymbol){
  //      ExpressionHandlerCallback callback = getCallbackByName(TAG_POINT_SYMBOL);
  //      Expression exp = ((PointSymbol)s).getExp();
  //
  //      boolean hitFlag = false;
  //      if (!exp.isTerminalSymbol()){
  //        // this expression can be decomposed
  //        //fixme cannot handle
  //        return null;
  //
  //      }else {
  //        //only has one identifier and cannot be decomposed
  //        Symbol terminal_sym = exp.getSymbol();
  //        if (callback != null)
  //          hitFlag = callback.broadcastHit(terminal_sym);
  //          //prefixExp = iterator.checkTraceList(terminal_sym.toString());
  //      }
  //
  //      //this is a point function
  //      if (((PointSymbol)s).isFunc()){
  //        //handling params first
  //
  //        CallFunc callFunc = (CallFunc)((PointSymbol)s).getV();
  //        if (callback!=null){
  //          return callback.handleSymbol(callFunc, hitFlag);
  //        }
  //
  //        List<Expression> params = callFunc.getParams();
  //        List<Expression>
  //        if (params != null)
  //          for(Expression p : callFunc.getParams()){
  //            if (!p.isTerminalSymbol())
  //                handleExpression(p);
  //          }
  //
  //
  //      }else {
  //        //identifier
  //        if (callback!=null)
  //           callback.handleExpression(((PointSymbol)s).getExp(),false);
  //      }
  //
  //    }
  //    //pure function call instead of point call func
  //    else if (s instanceof CallFunc){
  //      List<Expression> params = ((CallFunc)s).getParams();
  //      RuntimeFunction rf = new RuntimeFunction(((CallFunc)s).getFuncName());
  //      if (params != null)
  //        for(Expression p : ((CallFunc)s).getParams()){
  //           rf.addParam(handleExpression(p));
  //        }
  //      return rf;
  //    }
  //
  //    if (universalCallback!=null)
  //      return universalCallback.handleSymbol(s, true);
  //
  //    return null;
  //
  //  }else{
  //
  //
  //
  //    ExpressionHandlerCallback callback = getCallbackByName(TAG_ASSIGN_EXP);
  //
  //    if (e.getOp() == Operator.AS){
  //
  //      RuntimeValue right = handleExpression(e.getR());
  //      if(callback !=null)
  //        return callback.handleAssignExpression(e.getL().toString(), right, true);
  //      return right;
  //    }
  //
  //    if (e.getOp() != null){
  //      callback = getCallbackByName(TAG_EXP);
  //      if (callback!=null)
  //        return callback.handleExpression(e,true);
  //    }
  //
  //  }
  //
  //  throw new ASTParsingException("cannot handing " +e.toString()+ "during expression handler ");
  //}



}
