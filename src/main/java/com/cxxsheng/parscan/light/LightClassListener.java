package com.cxxsheng.parscan.light;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.antlr.parser.JavaParserBaseListener;

import java.util.Stack;

public class LightClassListener extends JavaParserBaseListener {
    private static Stack<String> classStack = new Stack();
    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        super.enterClassDeclaration(ctx);


            for (JavaParser.TypeTypeContext interfaze : ctx.typeList().typeType()){
                if ("Parcelable".equals(interfaze.getText())){
                        System.out.println("enterring " + ctx.IDENTIFIER().getText());
                        classStack.push(ctx.IDENTIFIER().getText());
                }

        }
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        super.exitClassDeclaration(ctx);
        classStack.pop();
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        super.enterMethodDeclaration(ctx);
        String methodName = ctx.IDENTIFIER().getText();
        if ("createFromParcel".equals(methodName)){
            if (ctx.methodBody().getText().contains("write")){
                System.out.println("FBIWARNING!");
            }
        }

    }

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        super.enterConstructorDeclaration(ctx);
        String methodName = ctx.IDENTIFIER().getText();

        if (classStack.peek().equals(methodName)){
            if (ctx.block().getText().contains("write")){
                System.out.println("FBIWARNING!");
            }
        }
    }
}
