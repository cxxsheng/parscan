package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.antlr.parser.JavaParserBaseListener;
import org.antlr.v4.runtime.ParserRuleContext;

public class JavaClassScanListener extends JavaParserBaseListener {

  @Override
  public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
    super.enterClassDeclaration(ctx);
  }

  @Override
  public void enterClassBody(JavaParser.ClassBodyContext ctx) {
    super.enterClassBody(ctx);
    System.out.println(ctx.getText());
  }

  @Override
  public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
    super.enterMethodDeclaration(ctx);
    System.out.println(ctx.getText());

  }


}
