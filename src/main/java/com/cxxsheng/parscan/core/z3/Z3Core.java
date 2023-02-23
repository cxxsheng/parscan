package com.cxxsheng.parscan.core.z3;

import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Primitive;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.BoolSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CharSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.FloatSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IntSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.NullSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.StringSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import com.cxxsheng.parscan.core.iterator.ASTIterator;
import com.cxxsheng.parscan.core.iterator.GraphNode;
import com.cxxsheng.parscan.core.iterator.ParcelDataNode;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import java.util.List;

public class Z3Core {

  public final JavaClass javaClass;
  public final ASTIterator iterator;


  private Context ctx = new Context();

  //private Solver solver = ctx.mkSolver();

  public final ExprWithTypeVariable EXP_TRUE = new ExprWithTypeVariable(ctx.mkTrue());
  public final ExprWithTypeVariable EXP_FALSE = new ExprWithTypeVariable(ctx.mkFalse());

  private final Expr VALUE_EXP = ctx.mkConst("$VALUE", ctx.getIntSort());

  public final ExprWithTypeVariable VALUE = new ExprWithTypeVariable(VALUE_EXP, VALUE_EXP);

  public Z3Core(JavaClass javaClass, ASTIterator iterator) {
    this.javaClass = javaClass;
    this.iterator = iterator;
  }


  private static String float2FractionString(double num) {

    StringBuilder sb = new StringBuilder();
    sb.append(num);
    int i = sb.indexOf(".");
    if (i >= 0) {

      sb.replace(i, i + 1, "");
      sb.append("/");
      sb.append(1);
      int len = sb.length() - 1 - i;
      for (int j = 0; j < len-1 ;j++)
        sb.append(0);

    }
    return sb.toString();
  }

  private static void checkNotNull(){

  }

  public ExprWithTypeVariable mkConst(JavaType type, String name){
    com.microsoft.z3.Symbol symbol = ctx.mkSymbol(name);
    Sort sort;
    if (type.isPrimitive()){
      Primitive p = type.getPrimitive();
      switch (p){
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case CHAR:

          sort = ctx.getIntSort();
          break;
        case FLOAT:
        case DOUBLE:
          sort = ctx.getRealSort();
          break;
        case BOOL:
          sort = ctx.getBoolSort();
          break;
        default:
          throw new Z3ParsingException("Cannot handle unknown JavaType: " + type);
      }

      Expr<Sort> expr = ctx.mkConst(symbol, sort);
      return new ExprWithTypeVariable(expr, expr);
    }
    else {
      throw new Z3ParsingException("Cannot handle object: "+ type);
    }


  }

  public ExprWithTypeVariable handleSymbol(Symbol s){
    if (s == null)
      return null;

    if (s instanceof FloatSymbol)
    {
      StringBuilder sb = new StringBuilder();
      sb.append(((FloatSymbol)s).getValue());
      int i = sb.indexOf(".");
      if (i>=0){
        int denominator = 10^(sb.length()- 1 - i);
        sb.replace(i, i+1, "");
        sb.append("/");
        sb.append(denominator);
      }
      return new ExprWithTypeVariable(ctx.mkReal(sb.toString()));
    }else if (s instanceof IntSymbol){
      return new ExprWithTypeVariable(ctx.mkInt(((IntSymbol)s).getValue()));
    }else if(s instanceof BoolSymbol){
      return new ExprWithTypeVariable(((BoolSymbol)s).getValue() ? ctx.mkTrue() : ctx.mkFalse());
    }else if (s instanceof CharSymbol){
      return new ExprWithTypeVariable(ctx.mkInt(((CharSymbol)s).getValue()));
    }

    else if (s instanceof StringSymbol){
      return new ExprWithTypeVariable(ctx.mkString(((StringSymbol)s).getValue()));

    }else if (s instanceof IdentifierSymbol){

      String name = ((IdentifierSymbol)s).getValue();
      int index = iterator.getNodeIndexByAttachedName(name);
      //find index in dataTree first
      if (index >= 0)
      {
        name = "$" + index;
        //fixme type get may have some problem
        GraphNode node = iterator.getDataGraph().getNodeById(index);
        JavaType javaType = JavaType.parseJavaTypeString("int", false);
        if (node instanceof ParcelDataNode)
          javaType = ((ParcelDataNode)node).getJtype();
        return mkConst(javaType, name);
      }else {
        //check it is a constant, if this var is constant, replace the symbol by the real value
        VarDeclaration d = javaClass.getVarDeclarationByName(((IdentifierSymbol)s).getValue());
        if (d != null && d.getExpressions()!=null) {
          Symbol v = d.getLastExpValue();
          return handleSymbol(v);
        }
        else {
          return mkConst(JavaType.INT, name);
        }
      }

    }else if (s instanceof NullSymbol){
      return new ExprWithTypeVariable(ctx.mkInt(0 ));
    }else if (s instanceof TmpSymbol){
      //fixme fixme
      TmpSymbol tmpSymbol = (TmpSymbol)s;

      //Expression e = tmpSymbol.getExpression();
      ExprWithTypeVariable e = mkExpression(((TmpSymbol)s).getExpression());
      List<Expr> vars = e.getVars();
      if (vars.size() > 0){
        //inherit the value expression's first type (if exist)
        Expr<Sort> v0 = vars.get(0);
        Sort sort = v0.getSort();
        Expr<Sort> v = ctx.mkConst(tmpSymbol.getName(), sort);
        ExprWithTypeVariable name = new ExprWithTypeVariable(v, v);
        //make tmp name equals tmp value
        return mkEq(name, e);
      }else {
        //fixme may throw an exception
        return null;
      }
    }
    else {
      throw new Z3ParsingException("cannot handle " + s );
    }
  }

  public ExprWithTypeVariable mkExpression(Expression e){
    if (e == null)
      return null;
    Expr new_exp;
    Operator op = e.getOp();
    if (op == null)
      throw new Z3ParsingException("unreachable code");

    ExprWithTypeVariable left = handleSymbol(e.getLeft());
    ExprWithTypeVariable right = handleSymbol(e.getRight());
    switch (op){
      case EQ:
        new_exp = ctx.mkEq(left.getExpr(), right.getExpr());
        break;
      case NE:
        new_exp = ctx.mkNot(ctx.mkEq(left.getExpr(), right.getExpr()));
        break;
      case AND2:
        new_exp = ctx.mkAnd(left.getExpr(), right.getExpr());
        break;
      case OR2:
        new_exp = ctx.mkOr(left.getExpr(), right.getExpr());
        break;
      case GT:
        new_exp = ctx.mkGt(left.getExpr(), right.getExpr());
        break;
      case GE:
        new_exp = ctx.mkGe(left.getExpr(), right.getExpr());
        break;
      case LT:
        new_exp = ctx.mkLt(left.getExpr(), right.getExpr());
        break;
      case LE:
        new_exp = ctx.mkLe(left.getExpr(), right.getExpr());
        break;
      default:
        throw new Z3ParsingException("cannot handle such type " + op.getName() + " in exp:" + e.toString());
    }


    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkExpressionListWithPrevs(ExpressionListWithPrevs elp, LastExpressionCallback handle){
    ExprWithTypeVariable ev = null;
    if (elp.hasPreExpression()){
        for (Expression e : elp.getPrevs()){
          if (ev == null)
            ev = mkExpression(e);
          else
            ev = mkAnd(ev, mkExpression(e));
        }
      }
      ExprWithTypeVariable last = mkExpression(elp.getLastExpression());
      if (handle != null)
      {
        last = handle.justify(last);
      }
      return ev == null ? last : mkAnd(ev, last);

  }


  public ExprWithTypeVariable mkNot(ExprWithTypeVariable e){
    e.setExpr(ctx.mkNot(e.getExpr()));
    return e;
  }

  public ExprWithTypeVariable mkAnd(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkAnd(left.getExpr(), right.getExpr());
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkOr(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkOr(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkEq(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkEq(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkLe(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkLe(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkLt(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkLt(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkGt(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkGt(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkGe(ExprWithTypeVariable left, ExprWithTypeVariable right){
    Expr new_exp = ctx.mkGe(left.getExpr(), right.getExpr()).simplify();
    return ExprWithTypeVariable.contact(left, right,new_exp);
  }

  public ExprWithTypeVariable mkInt(long i){
    return new ExprWithTypeVariable(ctx.mkInt(i));
  }

  public Expr mkAll(ExprWithTypeVariable e){
    Expr[] vars = new Expr[e.getVars().size()];
    e.getVars().toArray(vars);
    return ctx.mkForall(vars, e.getExpr(),1, null, null, null, null);
  }

  public Solver mkSolver(){
    return ctx.mkSolver();
  }

}
