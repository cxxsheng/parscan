package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.exception.JavaMethodExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.ExpressionOrBlock;
import com.cxxsheng.parscan.core.Utils;
import com.cxxsheng.parscan.core.data.ConditionalBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.symbol.*;

import java.util.ArrayList;
import java.util.List;

public class JavaMethodBodyTreeExtractor {

      //trace the param
      private final String traceParamName;

      private final FunctionImp imp ;

      public JavaMethodBodyTreeExtractor(String params, FunctionImp imp) {
          this.traceParamName = params;
          this.imp = imp;
      }


      public Symbol parseIDENTIFIER(String ID){

          Symbol s = new IdentifierSymbol(ID);
          if (ID.equals(traceParamName)){
            s.taint();
          }
          return s;
      }

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

      public Symbol parseLiteral(JavaParser.LiteralContext literalContext){
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
              return new FloatSymbol(Utils.parseFloatString(literalContext.integerLiteral().getText()));
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


          throw new JavaMethodExtractorException("unhandled situation ", literalContext);
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
      public Expression parsePrimary(JavaParser.PrimaryContext primaryContext){
          //cannot handle explicitGenericInvocationSuffix
          if (primaryContext.nonWildcardTypeArguments() != null)
              throw new JavaMethodExtractorException("Cannot handle nonWildcardTypeArguments during primary's parsing ", primaryContext);

          if (primaryContext.literal() != null){
              return parseLiteral(primaryContext.literal()).toExp();
          }
          if (primaryContext.expression() != null)
              return parseExpression(primaryContext.expression());

          return  parseIDENTIFIER(primaryContext.getText()).toExp();
      }




      public CallFunc parseMethodCall(JavaParser.MethodCallContext methodCallContext){
          String funcName="";
          if (methodCallContext.SUPER()!=null)
              funcName = methodCallContext.SUPER().getText();
          if (methodCallContext.THIS()!=null)
              funcName = methodCallContext.THIS().getText();
          if (methodCallContext.IDENTIFIER()!=null)
              funcName = methodCallContext.IDENTIFIER().getText();

          Coordinate coordinate = new Coordinate(methodCallContext.start.getLine(), methodCallContext.start.getCharPositionInLine());
          List<JavaParser.ExpressionContext> params = methodCallContext.expressionList().expression();
          List<Expression> list = new ArrayList<>();

          if (params!=null){
              for (JavaParser.ExpressionContext p : params){
                  list.add(parseExpression(p));
              }
          }
          return new CallFunc(funcName, list, coordinate);
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
      public Expression parseExpression(JavaParser.ExpressionContext expressionContext){
          if (expressionContext.primary() != null){  //primary
              return parsePrimary(expressionContext.primary());
          }

          if (expressionContext.LBRACK()!=null && expressionContext.RBRACK()!=null) //expression '[' expression ']'
          {

              Expression e1 = parseExpression(expressionContext.expression(0));
              Expression e2 = parseExpression(expressionContext.expression(1));
              return new Expression(new ArrayGetSymbol(e1, e2));
          }

           // expression ('<' '<' | '>' '>' '>' | '>' '>') expression
          if (expressionContext.LT() != null){

              int i = expressionContext.LT().size();
              if (i == 2){

                JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
                JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
                return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf("<<"));
              }
            throw  new JavaMethodExtractorException("unreachable syntax during parsing  [expression ('<' '<' | '>' '>' '>' | '>' '>') expression]", expressionContext);

          }

          if (expressionContext.GT() != null){
              int i = expressionContext.LT().size();

              JavaParser.ExpressionContext left_e = expressionContext.expression().get(0);
              JavaParser.ExpressionContext right_e = expressionContext.expression().get(1);
              if (i==2){
                return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf(">>"));
              }
              if (i==3){
                return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf(">>>"));
              }
              throw new JavaMethodExtractorException("unreachable syntax during parsing  [expression ('<' '<' | '>' '>' '>' | '>' '>') expression]", expressionContext);
          }
          //fixme unhandled
          if (expressionContext.creator()!=null){
            throw new JavaMethodExtractorException("unhandled creator", expressionContext);
          }

          if (expressionContext.bop != null){

                if (expressionContext.bop.getText().equals(".")){
                    if (expressionContext.explicitGenericInvocation()!=null)
                        throw new JavaMethodExtractorException("Cannot handle explicitGenericInvocation during point expression's parsing ", expressionContext);

                    if (expressionContext.NEW()!=null)
                        throw new JavaMethodExtractorException("Cannot handle NEW keyword during point expression's parsing", expressionContext);

                    if (expressionContext.SUPER()!=null)
                        throw new JavaMethodExtractorException("Cannot handle SUPER keyword during point expression's parsing", expressionContext);

                    JavaParser.ExpressionContext e = expressionContext.expression(0);
                    Expression expression = parseExpression(e);
                    if (expressionContext.methodCall()!=null){
                        return new PointSymbol(expression, parseMethodCall(expressionContext.methodCall())).toExp();
                    }
                    if (expressionContext.IDENTIFIER()!=null || expressionContext.THIS()!=null){
                        return new PointSymbol(expression, parseIDENTIFIER(expressionContext.IDENTIFIER().getText())).toExp();
                    }
                }

                List<JavaParser.ExpressionContext> expressions = expressionContext.expression();
                if (expressions.size() == 2){
                    JavaParser.ExpressionContext left_e = expressions.get(0);
                    JavaParser.ExpressionContext right_e = expressions.get(1);
                    return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf(expressionContext.bop.getText()));
                }else if (expressions.size()==3){
                    //ConditionalExpression
                    JavaParser.ExpressionContext cond = expressions.get(0);
                    JavaParser.ExpressionContext left =  expressions.get(1);
                    JavaParser.ExpressionContext right =  expressions.get(2);

                    return new Expression(new ConditionalExpression(parseExpression(cond), parseExpression(left), parseExpression(right)));

                }else {
                    throw  new JavaMethodExtractorException("unreachable syntax during paring op", expressionContext);
                }
          }

          if (expressionContext.prefix!=null){
                assert (expressionContext.expression().size()==1); // it is Unitary
                return new Expression(null, parseExpression(expressionContext.expression().get(0)), Operator.nameOf(expressionContext.prefix.getText()), true);
          }

          if (expressionContext.postfix!=null){
              assert (expressionContext.expression().size()==1); // it is Unitary
              return new Expression(parseExpression(expressionContext.expression().get(0)), null , Operator.nameOf(expressionContext.postfix.getText()), true);
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
            throw new JavaMethodExtractorException("cannot handle lambdaExpression ", expressionContext); //fixme
          }
        //  java 8 methodReference fixme unhandled
        if (expressionContext.COLONCOLON()!=null)
            throw new JavaMethodExtractorException("cannot handle java 8 methodReference ", expressionContext); //fixme

        throw new JavaMethodExtractorException("impossible reachable", expressionContext);

      }


      /*****************************************************************

      localTypeDeclaration
        : classOrInterfaceModifier*
        (classDeclaration | interfaceDeclaration)                         X fixme unhandled innerclass declaration or interface
        | ';'
      ;
       *****************************************************************/

      public static ExpressionOrBlockList parseLocalTypeDeclaration(JavaParser.LocalTypeDeclarationContext localTypeDeclaration){
          throw new JavaMethodExtractorException("unhandled innerclass declaration or interface", localTypeDeclaration);
      }



      /*****************************************************************

      localVariableDeclaration
        : variableModifier* typeType variableDeclarators                   √
        ;
       IT WILL IGNORE TYPE AND VAR WITH NO INITIALIZER (eg. int a;)
       *****************************************************************/

      public ExpressionOrBlockList parseLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext localVariableDeclaration){
            //this is useful? WE DO NOT DEED TO RECORD TYPE
            JavaParser.TypeTypeContext typeTypeContext = localVariableDeclaration.typeType();
            List<ExpressionOrBlock> expressions = new ArrayList<>();
            //multi variables init like
            // int a,b,c = 1;
            List<JavaParser.VariableDeclaratorContext> variableDeclaratorContexts = localVariableDeclaration.variableDeclarators().variableDeclarator();
            for (JavaParser.VariableDeclaratorContext variableDeclaratorContext: variableDeclaratorContexts){
                String name =  variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
                Symbol symbol = parseIDENTIFIER(name);
                JavaParser.ArrayInitializerContext arrayInitializerContext = variableDeclaratorContext.variableInitializer().arrayInitializer();
                if (arrayInitializerContext != null){
                    expressions.add(new Expression(symbol, new ArrayInitSymbol(arrayInitializerContext.getText()), Operator.AS));
                }
                JavaParser.ExpressionContext e = variableDeclaratorContext.variableInitializer().expression();//expression
                if (e != null){
                    expressions.add(new Expression(symbol, parseExpression(e), Operator.AS));
                }

            }

            return ExpressionOrBlockList.wrap(ExpressionOrBlockList.PURE_EXPRESSION, expressions);
        }


      /*****************************************************************
      Statement means a block which contains multi-blocks, and also represent
      an statement like if-else statement or while statement.
      statement
        : blockLabel=block                                                                √
        | ASSERT expression (':' expression)? ';'
        | IF parExpression statement (ELSE statement)?
        | FOR '(' forControl ')' statement
        | WHILE parExpression statement
        | DO statement WHILE parExpression ';'
        | TRY block (catchClause+ finallyBlock? | finallyBlock)
        | TRY resourceSpecification block catchClause* finallyBlock?
        | SWITCH parExpression '{' switchBlockStatementGroup* switchLabel* '}'
        | SYNCHRONIZED parExpression block
        | RETURN expression? ';'
        | THROW expression ';'
        | BREAK IDENTIFIER? ';'
        | CONTINUE IDENTIFIER? ';'
        | SEMI
        | statementExpression=expression ';'
        | identifierLabel=IDENTIFIER ':' statement
      ;
      *****************************************************************/

      public ExpressionOrBlockList parseStatement(JavaParser.StatementContext statement){
          if (statement.block()!=null){
                return parseBlock(statement.block());
          }

          if (statement.IF()!=null){
                Expression ce = parseExpression(statement.parExpression().expression());
                Coordinate c = Coordinate.initFromToken(statement.start);
                ConditionalBlock b = new ConditionalBlock(ce, c);
                b.initExpressionOrBlockList(parseStatement(statement.statement(0)));

                if (statement.ELSE() != null){
                    b.initElseBlock(parseStatement(statement.statement(1)));

                }
                return b.wrapToList();
          }


          throw new JavaMethodExtractorException("unreachable code ", statement);




      }

        /*****************************************************************

        blockStatement
          : localVariableDeclaration ';'                                  √
          | statement                                                     √
          | localTypeDeclaration                                          √
        ;
       *****************************************************************/
      public ExpressionOrBlockList parseBlockStatement(JavaParser.BlockStatementContext blockStatement){
          JavaParser.LocalTypeDeclarationContext localVariableDeclaration =  blockStatement.localTypeDeclaration();
          if (localVariableDeclaration != null){

            return parseLocalTypeDeclaration(localVariableDeclaration);
          }

          JavaParser.LocalVariableDeclarationContext localVariableDeclarationContext = blockStatement.localVariableDeclaration();
          if (localVariableDeclarationContext!=null)
          {
              return parseLocalVariableDeclaration(localVariableDeclarationContext);
          }

          //it is son
          JavaParser.StatementContext statement = blockStatement.statement();
          if (statement!=null){
              return parseStatement(statement);

          }

          throw new JavaMethodExtractorException("cannot handle type, unreachable code", blockStatement);
      }

      /*****************************************************************
       block
       : '{' blockStatement* '}'                                        √
       ;
       *****************************************************************/
      public ExpressionOrBlockList parseBlock(JavaParser.BlockContext block){

          ExpressionOrBlockList ebl = new ExpressionOrBlockList();
          List<JavaParser.BlockStatementContext> blockStatements = block.blockStatement();
          for (JavaParser.BlockStatementContext blockStatement: blockStatements){
              ebl.addExpressionList(parseBlockStatement(blockStatement));
          }

          return ebl;
      }


      /*****************************************************************

         methodBody                                                       √
         : block
         | ';'
         ;
       *****************************************************************/
      public void parseMethodBody(JavaParser.MethodBodyContext methodBodyContext){
          JavaParser.BlockContext block = methodBodyContext.block();
          parseBlock(block);
      }



}
