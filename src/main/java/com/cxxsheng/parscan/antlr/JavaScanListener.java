package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.antlr.parser.JavaParserBaseListener;
import com.cxxsheng.parscan.core.Condition;
import com.cxxsheng.parscan.core.Coordinate;
import com.oracle.tools.packager.Log;
import javafx.util.Pair;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;


//Before we use antlr listener we must filter out some seeming parcelable java file by
//using regexp or string matching first so that we do not need to parse all AOSP file
//(that is a huge workload) and increase the scanning efficiency.
public class JavaScanListener extends JavaParserBaseListener {

  private final static Logger LOG = Logger.getLogger(JavaScanListener.class);



  private String packageName = "unk";


  //className/isParcelable
  private Stack<Pair<String,Boolean>> classStack = new Stack<>();

  private Stack<Pair<Condition, Integer>> conditionStack = new Stack<>();


  private volatile boolean currentConditionNeedPar = false;


  public static final int METHOD_EXITED = -1;
  public static final int UNKNOWN_METHOD_ENTERED = 0;
  public static final int METHOD_WRITE_TO_PARCEL_ENTERED = 1;
  public static final int METHOD_CREATE_FROM_PARCEL_ENTERED = 2;

  private volatile int currentMethodStatus = METHOD_EXITED;
  // method parameter list
  private volatile List<Parameter> paramList = new ArrayList<>();

  @Override
  public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    super.enterPackageDeclaration(ctx);
    packageName = ctx.getText();
  }

  @Override
  public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
    super.enterClassDeclaration(ctx);

    //get class name
    String className = ctx.IDENTIFIER().getText();
    //get interface list
    JavaParser.TypeListContext interfaceList = ctx.typeList();
    boolean isParcelable = false;

    LOG.debug("Enter class: " + className);
    // this class implements Parcelable
    for(JavaParser.TypeTypeContext interfaceType : interfaceList.typeType()){
      if (interfaceType.getText().equals("Parcelable")){
        LOG.info("class: " + className + " implements interface Parcelable");
        isParcelable = true;
        break;
      }
    }


    //classStack must be Parcelable
    classStack.push(new Pair<>(className, isParcelable));

  }



  @Override
  public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
    super.exitClassDeclaration(ctx);
    String className = ctx.IDENTIFIER().getText();
    LOG.debug("Exit class: " + className);
    assert (classStack.peek().getKey().equals(className));
    classStack.pop();


  }

  private void parseParamListFromMethodDeclare(JavaParser.MethodDeclarationContext ctx){
    assert(paramList.isEmpty());
    JavaParser.FormalParameterListContext c = ctx.formalParameters().formalParameterList();
    for (JavaParser.FormalParameterContext p : c.formalParameter()){
        Parameter param = new Parameter(p.typeType().getText(), p.variableDeclaratorId().getText());
        paramList.add(param);
    }
  }

  @Override
  public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
    super.enterMethodDeclaration(ctx);

    LOG.debug("Entered method " + ctx.IDENTIFIER());
    assert (currentMethodStatus == METHOD_EXITED);



    currentMethodStatus = UNKNOWN_METHOD_ENTERED;
    if (classStack.peek().getValue()){

      if("createFromParcel".equals(ctx.IDENTIFIER().getText())){

          assert (classStack.peek().getKey().equals(ctx.typeTypeOrVoid().getText()));
          LOG.info("Found " + ctx.IDENTIFIER());
          parseParamListFromMethodDeclare(ctx);

          currentMethodStatus = METHOD_CREATE_FROM_PARCEL_ENTERED;

      }else if ("writeToParcel".equals(ctx.IDENTIFIER().getText())){


          assert ("void".equals(ctx.typeTypeOrVoid().getText()));
          LOG.info("Found " + ctx.IDENTIFIER());
          parseParamListFromMethodDeclare(ctx);

          currentMethodStatus = METHOD_WRITE_TO_PARCEL_ENTERED;

      }
    }
  }


  @Override
  public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
    super.exitMethodDeclaration(ctx);
    LOG.debug("Exited method " + ctx.IDENTIFIER());
    assert (currentMethodStatus != METHOD_EXITED);
    currentMethodStatus = METHOD_EXITED;
    paramList.clear();
  }

  @Override
  public void enterStatement(JavaParser.StatementContext ctx) {
    switch (currentMethodStatus){
      case METHOD_WRITE_TO_PARCEL_ENTERED:
      case METHOD_CREATE_FROM_PARCEL_ENTERED:

        if (ctx.IF() != null){
            //start of a new IF statement
           // System.out.println("if"+ ctx.getText());
            Token IF_TOKEN = ctx.IF().getSymbol();
            Coordinate coord = new Coordinate(IF_TOKEN.getLine(), IF_TOKEN.getCharPositionInLine());
            //fixme
            Condition condition = new Condition(coord, null);
            //System.out.println(coord);
            Pair<Condition, Integer> pair = new Pair<>(condition,1);
            conditionStack.push(pair);
            currentConditionNeedPar = true;
        }
    }
    super.enterStatement(ctx);
  }


  @Override
  public void exitStatement(JavaParser.StatementContext ctx) {

    super.exitStatement(ctx);
  }


  @Override
  public void enterParExpression(JavaParser.ParExpressionContext ctx) {
    super.enterParExpression(ctx);
  }


  @Override
  public void exitParExpression(JavaParser.ParExpressionContext ctx) {
    super.exitParExpression(ctx);
    if (currentConditionNeedPar)
    {
      System.out.println("\nend");
      currentConditionNeedPar = false;
    }
  }
  //
  @Override
  public void visitTerminal(TerminalNode node) {
    super.visitTerminal(node);
    if (currentConditionNeedPar){
      System.out.print(node.getText()+" ,");

    }
  }


  @Override
  public void enterExpression(JavaParser.ExpressionContext ctx) {
    super.enterExpression(ctx);
  }






  @Override
  public void enterMethodBody(JavaParser.MethodBodyContext ctx) {
    super.enterMethodBody(ctx);

    switch (currentMethodStatus){

      case METHOD_WRITE_TO_PARCEL_ENTERED:
        LOG.debug("param List is " + paramList);
        break;
      case METHOD_CREATE_FROM_PARCEL_ENTERED:
        LOG.debug("param List is " + paramList);
        break;
      default:
        LOG.debug("Nothing to do");
        break;


    }



  }


}
