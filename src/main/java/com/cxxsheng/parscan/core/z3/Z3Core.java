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
import com.cxxsheng.parscan.core.data.unit.symbol.NullSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.StringSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import com.cxxsheng.parscan.core.iterator.ASTIterator;
import com.cxxsheng.parscan.core.iterator.ParcelDataNode;
import com.cxxsheng.parscan.core.iterator.TreeNode;
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
    if (e==null)
      return null;
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

          String name = ((IdentifierSymbol)s).getValue();
          int index = iterator.getNodeIndexByAttachedName(name);
          //find index in dataTree first
          if (index >= 0)
          {
            name = "$" + index;
            //fixme type get may have some problem
            TreeNode node = iterator.getDataTree().getNodeById(index);
            JavaType javaType = JavaType.parseJavaTypeString("int", false);
            if (node instanceof ParcelDataNode)
              javaType = ((ParcelDataNode)node).getJtype();
            return mkConst(javaType, name);
          }else {
            //check it is a constant, if this var is constant, replace the symbol by the real value
            VarDeclaration d = javaClass.getVarDeclarationByName(((IdentifierSymbol)s).getValue());
            if (d != null && d.getValue()!=null) {
              return mkExpression(d.getValue());
            }
            else {
              return mkConst(JavaType.INT, name);
            }
          }

        }else if (s instanceof NullSymbol){
          return ctx.mkInt(0 );
        }
        else {
          throw new Z3ParsingException("cannot handle " + e );
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
        return ctx.mkNot(ctx.mkEq(left, right));

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

  public Expr mkNot(Expr e){
    return ctx.mkNot(e).simplify();
  }

  public Expr mkAnd(Expr left, Expr right){
    return ctx.mkAnd(left, right).simplify();
  }

  public Expr mkOr(Expr left, Expr right){
    return ctx.mkOr(left, right).simplify();
  }
}
