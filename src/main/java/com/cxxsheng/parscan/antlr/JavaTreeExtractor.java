package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.parcelale.ParcelableFuncImp;

import java.util.List;

public class JavaTreeExtractor {


    public static void parseLocalTypeDeclaration(ParcelableFuncImp imp, JavaParser.LocalTypeDeclarationContext localTypeDeclaration){

    }

    public static void parseLocalVariableDeclaration(ParcelableFuncImp imp, JavaParser.LocalVariableDeclarationContext localVariableDeclaration){

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
                parseLocalVariableDeclaration(imp, localVariableDeclarationContext);
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
