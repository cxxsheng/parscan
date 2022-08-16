package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.parcelale.ParcelableFuncImp;
import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Operator;
import com.cxxsheng.parscan.core.unit.Symbol;
import com.cxxsheng.parscan.core.unit.symbol.ArrayInitSymbol;
import com.cxxsheng.parscan.core.unit.symbol.IdentifierSymbol;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JavaTreeExtractor {

    public static Symbol parseLiteral(JavaParser.LiteralContext literalContext){
        if(literalContext.integerLiteral()!=null){

        }
        literalContext.BOOL_LITERAL();
        literalContext.CHAR_LITERAL();
        literalContext.floatLiteral();
        literalContext.NULL_LITERAL();
        literalContext.STRING_LITERAL();
    }

    public static Expression parsePrimary(JavaParser.PrimaryContext primaryContext){
        if (primaryContext.literal() != null){
            return new Expression(parseLiteral(primaryContext.literal()), (Expression) null,null);
        }
        if (primaryContext.expression() != null)
            return parseExpression(primaryContext.expression());
        throw new RuntimeException("??"); //fixme
    }

    public static Expression parseExpression(JavaParser.ExpressionContext expressionContext){
        if (expressionContext.primary() != null){
            return parsePrimary(expressionContext.primary());
        }
        throw new RuntimeException("??"); //fixme
    }

    public static void parseLocalTypeDeclaration(ParcelableFuncImp imp, JavaParser.LocalTypeDeclarationContext localTypeDeclaration){

    }


    public static List<Expression> parseLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext localVariableDeclaration){
        //this is useful?
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
    public static void parseStatement(ParcelableFuncImp imp, JavaParser.StatementContext statement){
    }

    public static void parseMethodBody(ParcelableFuncImp imp, JavaParser.MethodBodyContext methodBodyContext){
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
