package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.Utils;
import com.cxxsheng.parscan.core.parcelale.ParcelableFuncImp;
import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Operator;
import com.cxxsheng.parscan.core.unit.Symbol;
import com.cxxsheng.parscan.core.unit.symbol.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JavaTreeExtractor {

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
        throw new RuntimeException("??"); //fixme
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
        if (expressionContext.bop != null){


            if (expressionContext.bop.getText().equals(".")){



            }

            List<JavaParser.ExpressionContext> expressions = expressionContext.expression();
            if (expressions.size() == 2){
                JavaParser.ExpressionContext left_e = expressions.get(0);
                JavaParser.ExpressionContext right_e = expressions.get(1);
                return new Expression(parseExpression(left_e), parseExpression(right_e), Operator.nameOf(expressionContext.bop.getText()));
            }else if (expressionContext.expression().size()==3){
                //unhandle
            }else {
                throw  new RuntimeException("??");//fixme
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

        if (expressionContext.methodCall() != null){
            String funcName="";
            if (expressionContext.methodCall().SUPER()!=null)
                funcName = expressionContext.methodCall().SUPER().getText();
            if (expressionContext.methodCall().THIS()!=null)
                funcName = expressionContext.methodCall().THIS().getText();
            if (expressionContext.methodCall().IDENTIFIER()!=null)
                funcName = expressionContext.methodCall().IDENTIFIER().getText();


            assert  !funcName.equals("");
            Coordinate coordinate = new Coordinate(expressionContext.methodCall().start.getLine(), expressionContext.methodCall().start.getCharPositionInLine());
            List<JavaParser.ExpressionContext> params = expressionContext.methodCall().expressionList().expression();
            List<Expression> list = new ArrayList<>();

            if (params!=null){
                for (JavaParser.ExpressionContext p : params){
                    list.add(parseExpression(p));
                }
            }
            return new Expression(new CallFunc(funcName, list, coordinate));
        }

        if (expressionContext.expression() != null){
            // a?b:c cannot handle
            // a[b] cannot handle
            //

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
