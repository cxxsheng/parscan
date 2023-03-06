package com.cxxsheng.parscan.core.z3;

import com.microsoft.z3.Expr;
import java.util.ArrayList;
import java.util.List;

//wrap the z3 expr so that use it
public class ExprWithTypeVariable {
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

  public void addVarsAll(List<Expr> vars){
    this.vars.addAll(vars);
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
