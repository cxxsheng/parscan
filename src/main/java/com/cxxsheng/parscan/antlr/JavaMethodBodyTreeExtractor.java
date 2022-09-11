package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.exception.JavaMethodExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.Utils;
import com.cxxsheng.parscan.core.parcelale.ParcelableFuncImp;
import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Operator;
import com.cxxsheng.parscan.core.unit.Symbol;
import com.cxxsheng.parscan.core.unit.symbol.*;

import java.util.ArrayList;
import java.util.List;

public class JavaMethodBodyTreeExtractor {

    //trace the param
    private final String traceParamName;

    public JavaMethodBodyTreeExtractor(String params) {
        this.traceParamName = params;
    }


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
            return new FloatSymbol(Utils.parseFloatString(literalContext.integerLiteral().getText()));
        }
        if (literalContext.BOOL_LITERAL() != null){
            if ("true".equals(literalContext.BOOL_LITERAL().getText()))
                return new BoolSymbol(true);
            else
                return new BoolSymbol(false);
        }

        if(literalContext.CHAR_LITERAL()!=null){
            return new CharSymbol(literalContext.CHAR_LITERAL().getText().charAt(0));
        }
        if(literalContext.STRING_LITERAL()!=null){
            return new StringSymbol(literalContext.STRING_LITERAL().getText());
        }
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
            return new Expression(parseLiteral(primaryContext.literal()), (Expression) null,null);
        }
        if (primaryContext.expression() != null)
            return parseExpression(primaryContext.expression());

        return new IdentifierSymbol(primaryContext.getText()).toExp();
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


        if (expressionContext.bop != null){

//                 expression bop='.'
//                  ( IDENTIFIER
//                    | methodCall
//                    | THIS
//                    | NEW nonWildcardTypeArguments? innerCreator fixme here
//                    | SUPER superSuffix fixme here
//                    | explicitGenericInvocation fixme here
//                  )
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
                    return new PointSymbol(expression, new IdentifierSymbol(expressionContext.IDENTIFIER().getText())).toExp();
                }
            }

            List<JavaParser.ExpressionContext> expressions = expressionContext.expression();
            if (expressions.size() == 2){
                JavaParser.ExpressionContext left_e = expressions.get(0);
                JavaParser.ExpressionContext right_e = expressions.get(1);
                return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf(expressionContext.bop.getText()));
            }else if (expressions.size()==3){
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
            return new Expression(null, parseExpression(expressionContext.expression().get(0)), Operator.nameOf(expressionContext.bop.getText()), true);
        }

        if (expressionContext.postfix!=null){
            assert (expressionContext.expression().size()==1); // it is Unitary
            return new Expression(parseExpression(expressionContext.expression().get(0)), null , Operator.nameOf(expressionContext.bop.getText()), true);
        }


        if (expressionContext.methodCall() != null){ //methodCall
          return parseMethodCall(expressionContext.methodCall()).toExp();
        }


        //'(' annotation* typeType ('&' typeType)* ')' expression
        if (expressionContext.expression() != null && expressionContext.expression().size() == 1){
            return parseExpression(expressionContext.expression(0));
        }
        throw new RuntimeException("??"); //fixme
    }

    public static void parseLocalTypeDeclaration(ParcelableFuncImp imp, JavaParser.LocalTypeDeclarationContext localTypeDeclaration){

    }


    public List<Expression> parseLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext localVariableDeclaration){
        //this is useful? WE DO NOT DEED TO RECORD TYPE
        JavaParser.TypeTypeContext typeTypeContext = localVariableDeclaration.typeType();
        List<Expression> expressions = new ArrayList<>();
        List<JavaParser.VariableDeclaratorContext> variableDeclaratorContexts = localVariableDeclaration.variableDeclarators().variableDeclarator();
        for (JavaParser.VariableDeclaratorContext variableDeclaratorContext: variableDeclaratorContexts){
            String name =  variableDeclaratorContext.variableDeclaratorId().IDENTIFIER().getText();
            Symbol symbol = new IdentifierSymbol(name);
            JavaParser.ArrayInitializerContext arrayInitializerContext = variableDeclaratorContext.variableInitializer().arrayInitializer();
            if (arrayInitializerContext!=null){
                expressions.add(new Expression(symbol, new ArrayInitSymbol(arrayInitializerContext.getText()), Operator.AS));
            }
            JavaParser.ExpressionContext e = variableDeclaratorContext.variableInitializer().expression();//expression
            if (e!=null){
                expressions.add(new Expression(symbol, parseExpression(e), Operator.AS));
            }
        }

        return expressions;
    }
    public void parseStatement(ParcelableFuncImp imp, JavaParser.StatementContext statement){
    }

    public void parseMethodBody(ParcelableFuncImp imp, JavaParser.MethodBodyContext methodBodyContext){
        JavaParser.BlockContext block = methodBodyContext.block();
        List<JavaParser.BlockStatementContext> blockStatements = block.blockStatement();
        for (JavaParser.BlockStatementContext blockStatement: blockStatements){
            JavaParser.LocalTypeDeclarationContext localVariableDeclaration =  blockStatement.localTypeDeclaration();
            if (localVariableDeclaration != null){

                parseLocalTypeDeclaration(imp, localVariableDeclaration);
                continue;
            }

            JavaParser.LocalVariableDeclarationContext localVariableDeclarationContext = blockStatement.localVariableDeclaration();
            if (localVariableDeclarationContext!=null)
            {
                parseLocalVariableDeclaration(localVariableDeclarationContext);
                continue;
            }

            JavaParser.StatementContext statement = blockStatement.statement();
            if (statement!=null){
                parseStatement(imp,statement);
                continue;
            }
        }
    }



}
