package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.antlr.exception.JavaASTExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.Utils;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
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
      return NullSymbol.INIT();
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
  public static ExpressionListWithPrevs parsePrimary(JavaParser.PrimaryContext primaryContext){
    //cannot handle explicitGenericInvocationSuffix
    if (primaryContext.nonWildcardTypeArguments() != null)
      throw new JavaASTExtractorException("Cannot handle nonWildcardTypeArguments during primary's parsing ", primaryContext);

    if (primaryContext.literal() != null){
      return parseLiteral(primaryContext.literal()).toExp().wrapToPrevList();
    }
    if (primaryContext.expression() != null)
      return parseExpression(primaryContext.expression());
    return parseIDENTIFIER(primaryContext.getText()).toExp().wrapToPrevList();
  }



  public static List<ExpressionListWithPrevs> parseExpressionListWithPrevs(JavaParser.ExpressionListContext params){
    List<ExpressionListWithPrevs> list = new ArrayList<>();
    if (params!=null){
      List<JavaParser.ExpressionContext> ps = params.expression();
      for (JavaParser.ExpressionContext p : ps){
        list.add(parseExpression(p));
      }
    }
    return list;
  }


  public static ExpressionListWithPrevs parseMethodCall(JavaParser.MethodCallContext methodCallContext){
      String funcName="";
      if (methodCallContext.SUPER()!=null)
        funcName = methodCallContext.SUPER().getText();
      if (methodCallContext.THIS()!=null)
        funcName = methodCallContext.THIS().getText();
      if (methodCallContext.IDENTIFIER()!=null)
        funcName = methodCallContext.IDENTIFIER().getText();

      Coordinate x = new Coordinate(methodCallContext.start.getLine(), methodCallContext.start.getCharPositionInLine());
      List<ExpressionListWithPrevs> list = parseExpressionListWithPrevs(methodCallContext.expressionList());
      String finalFuncName = funcName;
      return createExpressionListWithPrevsList(list,
                                      tle -> new CallFunc(x, finalFuncName, tle).toExp());
  }

  public static IdentifierSymbol parseIDENTIFIER(String ID){
      IdentifierSymbol s = new IdentifierSymbol(ID);
      return s;
  }


  private static TerminalSymbol createTmpSymbolIfNeed(ExpressionListWithPrevs e){
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

  public static ExpressionListWithPrevs createExpressionListWithPrevsUnary(ExpressionListWithPrevs e, UnaryCreator callback){
    TerminalSymbol t = createTmpSymbolIfNeed(e);
    Expression last = callback.create(t);
    ExpressionListWithPrevs el = new ExpressionListWithPrevs(last);
    if (e.hasPreExpression())
      el.addPrevs(e.getPrevs());
    if (t instanceof TmpSymbol)
      el.addPrev(t.toExp());
    return el;
  }

  public static ExpressionListWithPrevs createExpressionListWithPrevsBinary(ExpressionListWithPrevs e1, ExpressionListWithPrevs e2, BinaryCreator callback){
    TerminalSymbol t1 = createTmpSymbolIfNeed(e1);
    TerminalSymbol t2 = createTmpSymbolIfNeed(e2);
    Expression last = callback.create(t1, t2);
    ExpressionListWithPrevs el = new ExpressionListWithPrevs(last);
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

  public static ExpressionListWithPrevs createExpressionListWithPrevsTernary(ExpressionListWithPrevs e1, ExpressionListWithPrevs e2, ExpressionListWithPrevs e3, TernaryCreator callback){
    TerminalSymbol t1 = createTmpSymbolIfNeed(e1);
    TerminalSymbol t2 = createTmpSymbolIfNeed(e2);
    TerminalSymbol t3 = createTmpSymbolIfNeed(e3);

    Expression last = callback.create(t1, t2, t3);
    ExpressionListWithPrevs el = new ExpressionListWithPrevs(last);
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

  public static ExpressionListWithPrevs createExpressionListWithPrevsList(List<ExpressionListWithPrevs> ell, ListCreator callback){
    List<TerminalSymbol> terminalList = new ArrayList<>();
    for (ExpressionListWithPrevs el : ell){
      TerminalSymbol t = createTmpSymbolIfNeed(el);
      terminalList.add(t);
    }
    Expression last = callback.create(terminalList);
    ExpressionListWithPrevs retEl = new ExpressionListWithPrevs(last);
    for (ExpressionListWithPrevs el : ell){
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
  public static ExpressionListWithPrevs parseExpression(JavaParser.ExpressionContext expressionContext){

    Coordinate x = Coordinate.createFromCtx(expressionContext);
    if (expressionContext.primary() != null){  //primary
      return parsePrimary(expressionContext.primary());
    }

    if (expressionContext.LBRACK()!=null && expressionContext.RBRACK()!=null) //expression '[' expression ']'
    {

      ExpressionListWithPrevs e1 = parseExpression(expressionContext.expression(0));
      ExpressionListWithPrevs e2 = parseExpression(expressionContext.expression(1));
      return createExpressionListWithPrevsBinary(e1, e2, ((t1, t2) -> new ArrayGetSymbol(t1, t2).toExp()));
    }

    // expression ('<' '<' | '>' '>' '>' | '>' '>') expression
    if (expressionContext.LT().size() != 0){

      int i = expressionContext.LT().size();
      if (i == 2){

        JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
        JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
        ExpressionListWithPrevs e1 = parseExpression(left_e);
        ExpressionListWithPrevs e2 = parseExpression(right_e);

        return createExpressionListWithPrevsBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf("<<"), t1, t2)));
      }
      throw new JavaASTExtractorException("unreachable syntax during parsing  [expression ('<' '<' | '>' '>' '>' | '>' '>') expression]", expressionContext);

    }

    if (expressionContext.GT().size() >= 2){
      int i = expressionContext.LT().size();

      JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
      JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
      ExpressionListWithPrevs e1 = parseExpression(left_e);
      ExpressionListWithPrevs e2 = parseExpression(right_e);

      if (i==2){
        return createExpressionListWithPrevsBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf(">>"), t1, t2)));
      }
      if (i==3){
        return createExpressionListWithPrevsBinary(e1, e2, ((t1, t2) -> new Expression(Operator.nameOf(">>>"), t1, t2)));
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
        ExpressionListWithPrevs size1 = parseExpression(creator.arrayCreatorRest().expression(0));
        JavaType javaType = new JavaType(Primitive.nameOf(name), true);
        return createExpressionListWithPrevsUnary(size1, t -> new Creator(t, javaType).toExp());
      }


      if (creator.classCreatorRest()!=null)
      {
        //
        //classCreatorRest
        //: arguments classBody?
        //;
        JavaParser.ClassCreatorRestContext ccr = creator.classCreatorRest();
        JavaParser.ExpressionListContext es = ccr.arguments().expressionList();
        List<ExpressionListWithPrevs> ps = parseExpressionListWithPrevs(es);

        return createExpressionListWithPrevsList(ps, tle -> {
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
        ExpressionListWithPrevs expression = parseExpression(e);
        if (expressionContext.methodCall()!=null){
          ExpressionListWithPrevs methodCall = parseMethodCall(expressionContext.methodCall());
          ExpressionListWithPrevs retEL = createExpressionListWithPrevsUnary(expression, t -> new PointSymbol(t, methodCall.getLastExpression().getSymbol()).toExp());
          if (methodCall.hasPreExpression())
            retEL.addPrevs(methodCall.getPrevs());
          return retEL;
        }
        if (expressionContext.IDENTIFIER()!=null || expressionContext.THIS()!=null || expressionContext.SUPER()!=null){
          String id;
          if (expressionContext.IDENTIFIER()!= null)
             id = expressionContext.IDENTIFIER().getText();
          else if (expressionContext.THIS() != null )
            id = "this";
          else
            id = "super";
          String finalId = id;
          return createExpressionListWithPrevsUnary(expression, t -> new PointSymbol(t, parseIDENTIFIER(finalId)).toExp());
        }
      }

      List<JavaParser.ExpressionContext> expressions = expressionContext.expression();
      if (expressions.size() == 2){
        JavaParser.ExpressionContext left_e = expressions.get(0);
        JavaParser.ExpressionContext right_e = expressions.get(1);
        ExpressionListWithPrevs e1 = parseExpression(left_e);
        ExpressionListWithPrevs e2 = parseExpression(right_e);
        Operator op = Operator.nameOf(expressionContext.bop.getText());
        return createExpressionListWithPrevsBinary(e1, e2, (t1, t2) -> new Expression(op, t1, t2));
      }else if (expressions.size()==3){
        //ConditionalExpression
        JavaParser.ExpressionContext cond = expressions.get(0);
        JavaParser.ExpressionContext left =  expressions.get(1);
        JavaParser.ExpressionContext right =  expressions.get(2);

        ExpressionListWithPrevs e1 = parseExpression(cond);
        ExpressionListWithPrevs e2 = parseExpression(left);
        ExpressionListWithPrevs e3 = parseExpression(right);
        return createExpressionListWithPrevsTernary(e1, e2, e3, ((t1, t2, t3) -> new ConditionalExpression(t1, t2, t3).toExp()));

      }else {
        throw  new JavaASTExtractorException("unreachable syntax during paring op", expressionContext);
      }
    }

    if (expressionContext.prefix!=null){
      assert (expressionContext.expression().size()==1); // it is Unitary
      ExpressionListWithPrevs e = parseExpression(expressionContext.expression().get(0));
      return createExpressionListWithPrevsUnary(e, (t) -> new Expression(Operator.nameOf(expressionContext.prefix.getText()), null, t, true));
    }

    if (expressionContext.postfix!=null){
      assert (expressionContext.expression().size()==1); // it is Unitary
      ExpressionListWithPrevs e = parseExpression(expressionContext.expression().get(0));
      return createExpressionListWithPrevsUnary(e, (t) -> new Expression(Operator.nameOf(expressionContext.postfix.getText()), null, t, true));
    }


    if (expressionContext.methodCall() != null){ //methodCall
      return parseMethodCall(expressionContext.methodCall());
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
          boolean isArray = false;
          if (ctx.LBRACK() != null && ctx.LBRACK().size() > 0
                  && ctx.RBRACK()!=null && ctx.RBRACK().size() > 0)
            isArray = true;
          //ignore annotations
          if (ctx.classOrInterfaceType()!=null)
                return new JavaType( ctx.classOrInterfaceType().getText(),isArray);
          else
          {
                Primitive p = Primitive.nameOf(ctx.primitiveType().getText());
                return new JavaType(p, isArray);
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
    public static List<ExpressionListWithPrevs> parseVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {


        //multi variables init like
        // int a,b,c = 1;
        List<ExpressionListWithPrevs> elwp = new ArrayList<>();
        List<JavaParser.VariableDeclaratorContext> variableDeclaratorContexts = ctx.variableDeclarator();
        for (JavaParser.VariableDeclaratorContext variableDeclaratorContext : variableDeclaratorContexts) {

        String name = variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
        IdentifierSymbol is = parseIDENTIFIER(name);
        JavaParser.VariableInitializerContext vInitializerContext = variableDeclaratorContext.variableInitializer();
        if (vInitializerContext != null) {
          JavaParser.ArrayInitializerContext arrayInit = vInitializerContext.arrayInitializer();
          if (arrayInit != null)
          {
            Expression e = new Expression(Operator.AS, is, new ArrayInitSymbol(arrayInit.getText()));
            elwp.add(new ExpressionListWithPrevs(e));
          }

          JavaParser.ExpressionContext e = vInitializerContext.expression();//expression
          if (e != null){
            ExpressionListWithPrevs el_ = parseExpression(e);
            ExpressionListWithPrevs el =  createExpressionListWithPrevsUnary(el_, (t -> new Expression(Operator.AS, is, t)));
            elwp.add(el);
          }
        }else {
            //just a symbol has nothing to do maybe;
           elwp.add(new ExpressionListWithPrevs(is.toExp()));
        }
      }
      return elwp;
    }

}
