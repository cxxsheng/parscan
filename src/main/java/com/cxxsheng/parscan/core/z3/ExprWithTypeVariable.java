package com.cxxsheng.parscan.core.z3;

import com.cxxsheng.parscan.core.iterator.RuntimeValue;
import com.microsoft.z3.Expr;
import java.util.ArrayList;
import java.util.List;

public class ExprWithTypeVariable extends RuntimeValue {
  private final List<Expr> vars = new ArrayList<>();

  private Expr expr;

  public ExprWithTypeVariable(Expr expr, Expr var){
    this.expr = expr;
    vars.add(var);
  }

  public ExprWithTypeVariable(Expr expr){
    this.expr = expr;
  }

  public Expr getExpr() {
    return expr;
  }

  public void setExpr(Expr expr) {
    this.expr = expr;
  }

  public void addVarsAll(List<Expr> var){
    this.vars.addAll(var);
  }


  public List<Expr> getVars() {
    return vars;
  }

  @Override
  public String toString() {
    return (expr!=null ? expr.toString() : "null");
  }


  public static ExprWithTypeVariable contact(ExprWithTypeVariable e1, ExprWithTypeVariable e2, Expr new_exp){
    ExprWithTypeVariable ret = new ExprWithTypeVariable(new_exp);
    ret.addVarsAll(e1.getVars());
    ret.addVarsAll(e2.getVars());
    return ret;
  }
}
