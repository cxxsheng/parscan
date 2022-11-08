package com.cxxsheng.parscan.core.z3;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.FloatSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IntSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.StringSymbol;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

public class Z3Core {

  private Context ctx = new Context();



  private static void checkNotNull(){

  }

  public Expr mkExpression(Expression e, Operator op){
    Expr left = null;
    Expr right = null;
    if (e.isTerminalSymbol()){
       Symbol s = e.getSymbol();
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
          return ctx.mkReal(sb.toString());
        }else if (s instanceof IntSymbol){
          return ctx.mkInt(((IntSymbol)s).getValue());
        }else if (s instanceof StringSymbol){
          throw new Z3ParsingException("cannot handle string type");
        }else if (s instanceof IdentifierSymbol){



        }

    }

    if (op == null)
      throw new Z3ParsingException("unreachable code");


    switch (op){
      case EQ:
        return ctx.mkEq(left, right);

      case NE:
        return ctx.mkNot(right);

      case AND2:

        return ctx.mkAnd(left, right);

      case OR2:
        return ctx.mkOr(left, right);

      case GT:
        return ctx.mkGt(left, right);

      case GE:
        return ctx.mkGe(left, right);

      case LT:
        return ctx.mkLt(left, right);
      case LE:
        return ctx.mkLe(left, right);




      default:
        throw new Z3ParsingException("cannot handle such type " + op.getName());

    }

  }



}
