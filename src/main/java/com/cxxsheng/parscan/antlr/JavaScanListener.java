package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.exception.JavaScanException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.antlr.parser.JavaParserBaseListener;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.parcelale.AndroidParcelableFuncImpPair;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import javafx.util.Pair;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.log4j.Logger;

import java.util.*;


//THIS IS IMPORTANT: Before we use antlr listener we must filter out some seeming parcelable java file by
//using regexp or string matching firstly so that we do not need to parse all AOSP file
//(that is a huge workload) and increase the scanning efficiency.
public class JavaScanListener extends JavaParserBaseListener {

  private final static Logger LOG = Logger.getLogger(JavaScanListener.class);

  // May have multi-classes in one java file so that
  // we need to include all above possible parcelable classes.
  // We choose k-v(Class Name/Parcelable Class) map for get class
  // during scanning, because we could enter another class and
  // we do not finish scanning one class yet if we have multi-classes.
  // only save parcelable classes.
  private final Map<String, AndroidParcelableFuncImpPair> parImps = new HashMap<>();

  private String packageName = "unk";


  //Not like parImps, class stack push/pop ALL kinds(include not parcelable class) classes in this java file.
  private final Stack<Pair<String,Boolean>> classStack = new Stack<>();

  // Like classStack, condition stack push/pop ALL kinds conditions stack during scanning.


  private volatile boolean currentConditionNeedPar = false;


  public static final int METHOD_EXITED = -1;
  public static final int UNKNOWN_METHOD_ENTERED = 0;
  public static final int METHOD_WRITE_TO_PARCEL_ENTERED = 1;
  public static final int METHOD_CREATE_FROM_PARCEL_ENTERED = 2;

  private volatile int currentMethodStatus = METHOD_EXITED;
  // method parameter list
  //private volatile List<Parameter> paramList = new ArrayList<>();

  public JavaScanListener(){

  }

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
        parImps.put(className, new AndroidParcelableFuncImpPair(packageName, className));
        break;
      }
    }


    //If top class is Parcelable, walker will start extract info.
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




  private List<Parameter> parseParamListFromMethodDeclare(JavaParser.MethodDeclarationContext ctx){
    List<Parameter> params = new ArrayList<>();
    JavaParser.FormalParameterListContext c = ctx.formalParameters().formalParameterList();
    for (JavaParser.FormalParameterContext p : c.formalParameter()){
        Parameter param = new Parameter(p.typeType().getText(), p.variableDeclaratorId().getText());
        params.add(param);
    }
    return params;
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
          List<Parameter> params = parseParamListFromMethodDeclare(ctx);

          Coordinate c = new Coordinate(ctx.start.getLine(), ctx.start.getCharPositionInLine());
          AndroidParcelableFuncImpPair imp = getCurrentParcelableClass();
          if (imp == null){
            throw new JavaScanException("cannot find current parcelable class");
          }
          imp.initDeSerFunc(imp.getClassName(), ctx.IDENTIFIER().getText(), params, c);
          JavaMethodBodyTreeExtractor extractor = new JavaMethodBodyTreeExtractor(imp.getSerializeFuncKeyParamName(), imp.getDeserializeFunc());
          extractor.parseMethodBody(ctx.methodBody());
          currentMethodStatus = METHOD_CREATE_FROM_PARCEL_ENTERED;

      }else if ("writeToParcel".equals(ctx.IDENTIFIER().getText())){

          assert ("void".equals(ctx.typeTypeOrVoid().getText()));
          LOG.info("Found " + ctx.IDENTIFIER());
          List<Parameter> params = parseParamListFromMethodDeclare(ctx);
          Coordinate c = new Coordinate(ctx.start.getLine(), ctx.start.getCharPositionInLine());
          AndroidParcelableFuncImpPair imp = getCurrentParcelableClass();
          if (imp == null){
            throw new JavaScanException("cannot find current parcelable class");
          }
          imp.initSerFunc("void", ctx.IDENTIFIER().getText(), params, c);

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
  }

  @Override
  public void enterStatement(JavaParser.StatementContext ctx) {
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
        break;
      case METHOD_CREATE_FROM_PARCEL_ENTERED:
        break;
      default:
        LOG.debug("Nothing to do");
        break;
    }
  }

  @Override
  public void enterEveryRule(ParserRuleContext ctx) {
    super.enterEveryRule(ctx);
  }

  private AndroidParcelableFuncImpPair getCurrentParcelableClass(){
    try {
      String className = classStack.peek().getKey();
      return parImps.getOrDefault(className, null);
    }catch (EmptyStackException e){
      return null;
    }
  }



}
