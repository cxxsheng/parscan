package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.antlr.exception.JavaASTExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.Utils;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionList;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.Primitive;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.ArrayGetSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.ArrayInitSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.BoolSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.CharSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.ConditionalExpression;
import com.cxxsheng.parscan.core.data.unit.symbol.Creator;
import com.cxxsheng.parscan.core.data.unit.symbol.FloatSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.IntSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.NullSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.PointSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.StringSymbol;
import com.cxxsheng.parscan.core.extractor.callback.BinaryCreator;
import com.cxxsheng.parscan.core.extractor.callback.ListCreator;
import com.cxxsheng.parscan.core.extractor.callback.TernaryCreator;
import com.cxxsheng.parscan.core.extractor.callback.UnaryCreator;
import java.util.ArrayList;
import java.util.List;

public class CommonExtractor {


  /*****************************************************************

   literal
   : integerLiteral                                                √
   | floatLiteral                                                  X fixme this unfinished，goto see Utils.parseFloatString 's imp
   | CHAR_LITERAL                                                  √
   | STRING_LITERAL                                                √
   | BOOL_LITERAL                                                  √
   | NULL_LITERAL                                                  √
   ;
   *****************************************************************/

  public static Symbol parseLiteral(JavaParser.LiteralContext literalContext){
    if(literalContext.integerLiteral() != null){
      int mode = 10;
      if (literalContext.integerLiteral().DECIMAL_LITERAL()!=null){
        mode = 10;
      }else if (literalContext.integerLiteral().HEX_LITERAL() != null){
        mode = 16;
      }else if (literalContext.integerLiteral().OCT_LITERAL() != null){
        mode = 8;
      }else if (literalContext.integerLiteral().BINARY_LITERAL() != null){
        mode = 2;
      }
      return new IntSymbol(Utils.parseIntString(literalContext.integerLiteral().getText(), mode));
    }
    if (literalContext.floatLiteral() != null){
      return new FloatSymbol((literalContext.integerLiteral().getText()));
    }
    if (literalContext.BOOL_LITERAL() != null){
      if ("true".equals(literalContext.BOOL_LITERAL().getText()))
        return new BoolSymbol(true);
      else
        return new BoolSymbol(false);
    }

    if(literalContext.CHAR_LITERAL() != null){
      return new CharSymbol(literalContext.CHAR_LITERAL().getText().charAt(0));
    }
    if(literalContext.STRING_LITERAL() != null){
      return new StringSymbol(literalContext.STRING_LITERAL().getText());
    }

    if (literalContext.NULL_LITERAL() != null)
      return NullSymbol.Init();


    throw new JavaASTExtractorException("unhandled situation ", literalContext);
  }



  //    primary
  //    : '(' expression ')'
  //            | THIS
  //    | SUPER
  //    | literal
  //    | IDENTIFIER
  //    | typeTypeOrVoid '.' CLASS
  //    | nonWildcardTypeArguments (explicitGenericInvocationSuffix | THIS arguments)
  //
  public static ExpressionList parsePrimary(JavaParser.PrimaryContext primaryContext){
    //cannot handle explicitGenericInvocationSuffix
    if (primaryContext.nonWildcardTypeArguments() != null)
      throw new JavaASTExtractorException("Cannot handle nonWildcardTypeArguments during primary's parsing ", primaryContext);

    if (primaryContext.literal() != null){
      return parseLiteral(primaryContext.literal()).toExp().wrapToList();
    }
    if (primaryContext.expression() != null)
      return parseExpression(primaryContext.expression());
    return parseIDENTIFIER(primaryContext.getText()).toExp().wrapToList();
  }



  public static List<ExpressionList> parseExpressionList(JavaParser.ExpressionListContext params){
    List<ExpressionList> list = null;
    if (params!=null){
      List<JavaParser.ExpressionContext> ps = params.expression();
      for (JavaParser.ExpressionContext p : ps){
        if (list==null)
          list = new ArrayList<>();
        list.add(parseExpression(p));
      }
    }
    return list;
  }


  public static ExpressionList parseMethodCall(JavaParser.MethodCallContext methodCallContext){
      String funcName="";
      if (methodCallContext.SUPER()!=null)
        funcName = methodCallContext.SUPER().getText();
      if (methodCallContext.THIS()!=null)
        funcName = methodCallContext.THIS().getText();
      if (methodCallContext.IDENTIFIER()!=null)
        funcName = methodCallContext.IDENTIFIER().getText();

      Coordinate x = new Coordinate(methodCallContext.start.getLine(), methodCallContext.start.getCharPositionInLine());
      List<ExpressionList> list = parseExpressionList(methodCallContext.expressionList());

      return new CallFunc(x, funcName, list);
  }

  public static Symbol parseIDENTIFIER(String ID){
      Symbol s = new IdentifierSymbol(ID);
      return s;
  }


  private static TerminalSymbol createTmpSymbolIfNeed(ExpressionList e){
     Expression last = e.getLastExpression();
     if (last.isSymbol()){
       Symbol s = last.getSymbol();
       if (s instanceof TerminalSymbol)
       {
         return (TerminalSymbol)s;
       }
     }
     return TmpSymbolManager.createNewTmpSymbol(last);
  }

  public static ExpressionList createExpressionListUnary(ExpressionList e, UnaryCreator callback){
    TerminalSymbol t = createTmpSymbolIfNeed(e);
    Expression last = callback.create(t);
    ExpressionList el = new ExpressionList(last);
    if (e.hasPreExpression())
      el.addPrevs(e.getPrevs());
    if (t instanceof TmpSymbol)
      el.addPrev(t.toExp());
    return el;
  }

  public static ExpressionList createExpressionListBinary(ExpressionList e1, ExpressionList e2, BinaryCreator callback){
    TerminalSymbol t1 = createTmpSymbolIfNeed(e1);
    TerminalSymbol t2 = createTmpSymbolIfNeed(e2);
    Expression last = callback.create(t1, t2);
    ExpressionList el = new ExpressionList(last);
    if (e1.hasPreExpression())
      el.addPrevs(e1.getPrevs());
    if (e2.hasPreExpression())
      el.addPrevs(e2.getPrevs());
    if (t1 instanceof TmpSymbol)
      el.addPrev(t1.toExp());
    if (t2 instanceof TmpSymbol)
      el.addPrev(t2.toExp());
    return el;
  }

  public static ExpressionList createExpressionListTernary(ExpressionList e1, ExpressionList e2,ExpressionList e3, TernaryCreator callback){
    TerminalSymbol t1 = createTmpSymbolIfNeed(e1);
    TerminalSymbol t2 = createTmpSymbolIfNeed(e2);
    TerminalSymbol t3 = createTmpSymbolIfNeed(e3);

    Expression last = callback.create(t1, t2, t3);
    ExpressionList el = new ExpressionList(last);
    if (e1.hasPreExpression())
      el.addPrevs(e1.getPrevs());
    if (e2.hasPreExpression())
      el.addPrevs(e2.getPrevs());
    if (e3.hasPreExpression())
      el.addPrevs(e3.getPrevs());
    if (t1 instanceof TmpSymbol)
      el.addPrev(t1.toExp());
    if (t2 instanceof TmpSymbol)
      el.addPrev(t2.toExp());
    if (t3 instanceof TmpSymbol)
      el.addPrev(t3.toExp());
    return el;
  }

  public static ExpressionList createExpressionListList(List<ExpressionList> ell, ListCreator callback){
    List<TerminalSymbol> terminalList = new ArrayList<>();
    for (ExpressionList el : ell){
      TerminalSymbol t = createTmpSymbolIfNeed(el);
      terminalList.add(t);
    }
    Expression last = callback.create(terminalList);
    ExpressionList retEl = new ExpressionList(last);
    for (ExpressionList el : ell){
          if (el.hasPreExpression())
            retEl.addPrevs(el.getPrevs());
    }
    for (TerminalSymbol ts : terminalList){
      if (ts instanceof TmpSymbol)
        retEl.addPrev(ts.toExp());
    }
    return retEl;
  }

  /*****************************************************************
   *   X  means unfinished
   *   √  means finished
   expression
   : primary                                                                                               √
   | expression bop='.'                                                                                    X fixme unfinished
   ( IDENTIFIER
   | methodCall
   | THIS
   | NEW nonWildcardTypeArguments? innerCreator  //fixme unhandled will throw exception
   | SUPER superSuffix                           //fixme unhandled will throw exception
   | explicitGenericInvocation                   //fixme unhandled will throw exception
   )
   | expression '[' expression ']'                                                                     √
   | methodCall                                                                                            √
   | NEW creator                                                                                           X fixme unhandled
   | '(' annotation* typeType ('&' typeType)* ')' expression                                               X fixme unfinished
   | expression postfix=('++' | '--')                                                                      √
   | prefix=('+'|'-'|'++'|'--') expression                                            √
   | expression bop=('*'|'/'|'%') expression                                                               √
   | expression bop=('+'|'-') expression                                                                   √
   | expression ('<' '<' | '>' '>' '>' | '>' '>') expression                                               √
   | expression bop=('<=' | '>=' | '>' | '<') expression                                                   √
   | expression bop=INSTANCEOF typeType                                                                    √
   | expression bop=('==' | '!=') expression                                                               √
   | expression bop='&' expression                                                                         √
   | expression bop='^' expression                                                                         √
   | expression bop='|' expression                                                                         √
   | expression bop='&&' expression                                                                        √
   | expression bop='||' expression                                                                        √
   | <assoc=right> expression bop='?' expression ':' expression                                            √
   | <assoc=right> expression
   bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
   expression                                                                                                √
   | lambdaExpression // Java8                                                                             X fixme unhandled

   // Java 8 methodReference
   | expression '::' typeArguments? IDENTIFIER                                                             X fixme unhandled
   | typeType '::' (typeArguments? IDENTIFIER | NEW)                                                       X fixme unhandled
   | classType '::' typeArguments? NEW                                                                     X fixme unhandled
   ;
   *****************************************************************/
  public static ExpressionList parseExpression(JavaParser.ExpressionContext expressionContext){

    Coordinate x = Coordinate.createFromCtx(expressionContext);
    if (expressionContext.primary() != null){  //primary
      return parsePrimary(expressionContext.primary());
    }

    if (expressionContext.LBRACK()!=null && expressionContext.RBRACK()!=null) //expression '[' expression ']'
    {

      ExpressionList e1 = parseExpression(expressionContext.expression(0));
      ExpressionList e2 = parseExpression(expressionContext.expression(1));
      return createExpressionListBinary(e1, e2, ((t1, t2) -> new ArrayGetSymbol(t1, t2).toExp()));
    }

    // expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    if (expressionContext.LT().size() != 0){

      int i = expressionContext.LT().size();
      if (i == 2){

        JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
        JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
        ExpressionList e1 = parseExpression(left_e);
        ExpressionList e2 = parseExpression(right_e);

        return createExpressionListBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf("<<"), t1, t2)));
      }
      throw new JavaASTExtractorException("unreachable syntax during parsing  [expression ('<' '<' | '>' '>' '>' | '>' '>') expression]", expressionContext);

    }

    if (expressionContext.GT().size() >= 2){
      int i = expressionContext.LT().size();

      JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
      JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
      ExpressionList e1 = parseExpression(left_e);
      ExpressionList e2 = parseExpression(right_e);

      if (i==2){
        return createExpressionListBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf(">>"), t1, t2)));
      }
      if (i==3){
        return createExpressionListBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf(">>>"), t1, t2)));
      }
      throw new JavaASTExtractorException("unreachable syntax during parsing  [expression ('<' '<' | '>' '>' '>' | '>' '>') expression]", expressionContext);
    }
    //fixme unhandled
    //creator
    //    : nonWildcardTypeArguments createdName classCreatorRest
    //    | createdName (arrayCreatorRest | classCreatorRest)
    //    ;
    if (expressionContext.creator()!=null){
      JavaParser.CreatorContext creator = expressionContext.creator();

      //type
      String name = creator.createdName().getText();

      //arrayCreatorRest
      //    : '[' (']' ('[' ']')* arrayInitializer | expression ']' ('[' expression ']')* ('[' ']')*)
      //    ;
      if (creator.arrayCreatorRest()!=null){
        //
        ExpressionList size1 = parseExpression(creator.arrayCreatorRest().expression(0));
        JavaType javaType = new JavaType(Primitive.nameOf(name), true);
        return createExpressionListUnary(size1, t -> new Creator(t, javaType).toExp());
      }


      if (creator.classCreatorRest()!=null)
      {
        //
        //classCreatorRest
        //: arguments classBody?
        //;
        JavaParser.ClassCreatorRestContext ccr = creator.classCreatorRest();
         JavaParser.ExpressionListContext es = ccr.arguments().expressionList();
         List<ExpressionList> ps = parseExpressionList(es);

         return createExpressionListList(ps, tle -> {
           CallFunc callFunc = new CallFunc(x, name, tle, true);

           if (ccr.classBody()!=null){
             JavaClassExtractor ext = new JavaClassExtractor();
             JavaClass javaClass = ext.parseAnonymousClass(ccr.classBody());
             callFunc.setExtraClass(javaClass);
           }
           return callFunc.toExp();
         });

      }

      throw new JavaASTExtractorException("unhandled creator", expressionContext);
    }

    if (expressionContext.bop != null){

      if (expressionContext.bop.getText().equals(".")){
        if (expressionContext.explicitGenericInvocation()!=null)
          throw new JavaASTExtractorException("Cannot handle explicitGenericInvocation during point expression's parsing ", expressionContext);

        if (expressionContext.NEW()!=null)
          throw new JavaASTExtractorException("Cannot handle NEW keyword during point expression's parsing", expressionContext);

        if (expressionContext.SUPER()!=null)
          throw new JavaASTExtractorException("Cannot handle SUPER keyword during point expression's parsing", expressionContext);

        JavaParser.ExpressionContext e = expressionContext.expression(0);
        ExpressionList expression = parseExpression(e);
        if (expressionContext.methodCall()!=null){
          return createExpressionListUnary(expression,
                                           (t-> new PointSymbol(t, parseMethodCall(expressionContext.methodCall())).toExp()));
        }
        if (expressionContext.IDENTIFIER()!=null || expressionContext.THIS()!=null){
          return createExpressionListUnary(expression, t -> new PointSymbol(t, parseIDENTIFIER(expressionContext.IDENTIFIER().getText())).toExp());
        }
      }

      List<JavaParser.ExpressionContext> expressions = expressionContext.expression();
      if (expressions.size() == 2){
        JavaParser.ExpressionContext left_e = expressions.get(0);
        JavaParser.ExpressionContext right_e = expressions.get(1);
        ExpressionList e1 = parseExpression(left_e);
        ExpressionList e2 = parseExpression(right_e);
        Operator op = Operator.nameOf(expressionContext.bop.getText());
        return createExpressionListBinary(e1, e2, (t1, t2) -> new Expression(op, t1, t2));
      }else if (expressions.size()==3){
        //ConditionalExpression
        JavaParser.ExpressionContext cond = expressions.get(0);
        JavaParser.ExpressionContext left =  expressions.get(1);
        JavaParser.ExpressionContext right =  expressions.get(2);

        ExpressionList e1 = parseExpression(cond);
        ExpressionList e2 = parseExpression(left);
        ExpressionList e3 = parseExpression(right);
        return createExpressionListTernary(e1, e2, e3, ((t1, t2, t3) -> new ConditionalExpression(t1, t2, t3).toExp()));

      }else {
        throw  new JavaASTExtractorException("unreachable syntax during paring op", expressionContext);
      }
    }

    if (expressionContext.prefix!=null){
      assert (expressionContext.expression().size()==1); // it is Unitary
      ExpressionList e = parseExpression(expressionContext.expression().get(0));
      return createExpressionListUnary(e, (t) -> new Expression(Operator.nameOf(expressionContext.prefix.getText()), null, t, true));
    }

    if (expressionContext.postfix!=null){
      assert (expressionContext.expression().size()==1); // it is Unitary
      ExpressionList e = parseExpression(expressionContext.expression().get(0));
      return createExpressionListUnary(e, (t) -> new Expression(Operator.nameOf(expressionContext.postfix.getText()), null, t, true));
    }


    if (expressionContext.methodCall() != null){ //methodCall
      return parseMethodCall(expressionContext.methodCall()).toExp();
    }


    //'(' annotation* typeType ('&' typeType)* ')' expression fixme unfinished
    if (expressionContext.expression() != null && expressionContext.expression().size() == 1){
      return parseExpression(expressionContext.expression(0));
    }
    // lambdaExpression fixme unhandled
    if (expressionContext.lambdaExpression()!=null){
      throw new JavaASTExtractorException("cannot handle lambdaExpression ", expressionContext); //fixme
    }
    //  java 8 methodReference fixme unhandled
    if (expressionContext.COLONCOLON()!=null)
      throw new JavaASTExtractorException("cannot handle java 8 methodReference ", expressionContext); //fixme

    throw new JavaASTExtractorException("impossible reachable", expressionContext);

  }

  //typeType
    //  : annotation* (classOrInterfaceType | primitiveType) (annotation* '[' ']')*
    //;
    public static JavaType parseJavaType(JavaParser.TypeTypeContext ctx){
          //ignore annotations
          if (ctx.classOrInterfaceType()!=null)
                return new JavaType( ctx.classOrInterfaceType().getText(),false);
          else
          {
                Primitive p = Primitive.nameOf(ctx.primitiveType().getText());
                return new JavaType(p, false);
          }
    }


    public static JavaType parseJavaTypeOrVoid(JavaParser.TypeTypeOrVoidContext ctx){
        if (ctx.VOID()!=null)
          return JavaType.getVOID();
        else
          return parseJavaType(ctx.typeType());
    }


    public static List<Parameter> parseParamListFromFormalParameterList(JavaParser.FormalParameterListContext c){
      List<Parameter> params = null;
      if (c == null)
        return params;

      for (JavaParser.FormalParameterContext p : c.formalParameter()){
        if(params==null)
          params = new ArrayList<>();
        JavaType type  = parseJavaType(p.typeType());
        Parameter param = new Parameter(type, p.variableDeclaratorId().getText());
        params.add(param);
      }
      return params;
    }


    //variableDeclarators
    //    : variableDeclarator (',' variableDeclarator)*
    //    ;
    public static ExpressionOrBlockList parseVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {

      ExpressionOrBlockList ret = ExpressionOrBlockList.InitEmptyInstance();
      //multi variables init like
      // int a,b,c = 1;
      List<JavaParser.VariableDeclaratorContext> variableDeclaratorContexts = ctx.variableDeclarator();
      for (JavaParser.VariableDeclaratorContext variableDeclaratorContext : variableDeclaratorContexts) {

        String name = variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
        Symbol symbol = parseIDENTIFIER(name);
        JavaParser.VariableInitializerContext vInitializerContext = variableDeclaratorContext.variableInitializer();
        if (vInitializerContext != null) {
          JavaParser.ArrayInitializerContext arrayInit = vInitializerContext.arrayInitializer();
          if (arrayInit != null)
            ret = ret.addOne(new Expression(symbol, new ArrayInitSymbol(arrayInit.getText()), Operator.AS));

            JavaParser.ExpressionContext e = vInitializerContext.expression();//expression
          if (e != null)
            ret = ret.addOne(new Expression(symbol, parseExpression(e), Operator.AS));
        }else {
            ret = ret.addOne(symbol.toExp());
        }
      }
      return ret;
    }

}
