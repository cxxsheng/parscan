package com.cxxsheng.parscan.core.z3;

import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Primitive;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.BoolSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CharSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.FloatSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IntSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.StringSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import com.cxxsheng.parscan.core.iterator.ASTIterator;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

public class Z3Core {

  public final JavaClass javaClass;
  public final ASTIterator iterator;


  private Context ctx = new Context();

  public final Expr EXP_TRUE = ctx.mkTrue();
  public final Expr EXP_FALSE = ctx.mkFalse();


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




  public Expr mkConst(JavaType type, String name){
    if (type.isPrimitive()){
      Primitive p = type.getPrimitive();
      switch (p){

        case BYTE:
        case SHORT:
        case INT:
        case LONG:
        case CHAR:
          return ctx.mkIntConst(name);
        case FLOAT:
        case DOUBLE:
          return ctx.mkRealConst(name);
        case BOOL:
          return ctx.mkBoolConst(name);
        default:
          throw new Z3ParsingException("Cannot handle unknown JavaType: " + type);
      }
    }
    else {
      throw new Z3ParsingException("Cannot handle object: "+ type);
    }


  }

  public Expr mkExpression(Expression e){
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
        }else if(s instanceof BoolSymbol){
          return ((BoolSymbol)s).getValue() ? ctx.mkTrue() : ctx.mkFalse();
        }else if (s instanceof CharSymbol){
          return ctx.mkInt(((CharSymbol)s).getValue());
        }

        else if (s instanceof StringSymbol){
          return  ctx.mkString(((StringSymbol)s).getValue());

        }else if (s instanceof IdentifierSymbol){
            VarDeclaration d = javaClass.getVarDeclarationByName(((IdentifierSymbol)s).getValue());
            Expr result;
            if (d != null){
              result = mkExpression(d.getValue());
            }else {

              String name = ((IdentifierSymbol)s).getValue();
              int index = iterator.getNodeIndexByAttachedName(name);

              if (index >= 0)
                name = "$" + index;
              //fixme typeget
              result = mkConst("", name);

            }


          return result;

        }

    }

    Operator op = e.getOp();
    if (op == null)
      throw new Z3ParsingException("unreachable code");

    left =  mkExpression(e.getL());
    right = mkExpression(e.getR());
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
