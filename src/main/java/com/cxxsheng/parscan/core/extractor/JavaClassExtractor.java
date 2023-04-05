package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaClassExtractor {


   private final Path path;
   private JavaClass javaClass;

   private final CommonExtractor extractor;

    public JavaClassExtractor(Path path) {
        this.path = path;
        this.extractor = new CommonExtractor(path);
    }
    //memberDeclaration
    //  : methodDeclaration                   √
    //  | genericMethodDeclaration            X  ignored generic
    //  | fieldDeclaration                    √
    //  | constructorDeclaration              √
    //  | genericConstructorDeclaration       X  ignored generic
    //  | interfaceDeclaration                X  useless
    //  | annotationTypeDeclaration           X  useless
    //  | classDeclaration                    √
    //  | enumDeclaration                     X
    //;

    private void parseMemberDeclaration(JavaParser.MemberDeclarationContext ctx){
        //methodDeclaration & genericMethodDeclaration
        if (ctx.genericMethodDeclaration() != null || ctx.methodDeclaration() != null){
           JavaParser.MethodDeclarationContext md = ctx.methodDeclaration();
            if (ctx.genericMethodDeclaration() != null )
                md = ctx.genericMethodDeclaration().methodDeclaration();

            List<Parameter> ps = extractor.parseParamListFromFormalParameterList(md.formalParameters().formalParameterList());
            Coordinate x = Coordinate.createFromCtx(ctx);
            JavaType retType = extractor.parseJavaTypeOrVoid(md.typeTypeOrVoid());
            FunctionDeclaration fd = new FunctionDeclaration(retType ,md.IDENTIFIER().getText(), ps, x);
            FunctionImp imp = new FunctionImp(fd);
            JavaMethodBodyTreeExtractor bodyExtractor = new JavaMethodBodyTreeExtractor(path, this.extractor);
            ExpressionOrBlockList body = bodyExtractor.parseMethodBody(md.methodBody());
            imp.setBody(body);
            javaClass.addMethod(imp);
        }

        //fieldDeclaration
        //    : typeType variableDeclarators ';'
        //    ;
        if (ctx.fieldDeclaration() != null){
            JavaType type = extractor.parseJavaType(ctx.fieldDeclaration().typeType());
            extractor.parseVariableDeclarators(ctx.fieldDeclaration().variableDeclarators());

            List<ExpressionListWithPrevs> exps = extractor.parseVariableDeclarators(ctx.fieldDeclaration().variableDeclarators());
            for (ExpressionListWithPrevs exp : exps){
                Expression e = exp.getLastExpression();
                VarDeclaration var;
                if (e.isSymbol())
                  var = new VarDeclaration((IdentifierSymbol)e.getSymbol(), type);
                else {
                  //fixme need to adjust
                  var = new VarDeclaration((IdentifierSymbol)e.getLeft(), type, exp);
                }
                javaClass.addClassVariable(var);
            }
        }

        //
        //genericConstructorDeclaration
        //: typeParameters constructorDeclaration
        //  ;
        //
        //constructorDeclaration
        //: IDENTIFIER formalParameters (THROWS qualifiedNameList)? constructorBody=block
        //;
        if (ctx.constructorDeclaration()!=null || ctx.genericConstructorDeclaration()!=null){
          JavaParser.ConstructorDeclarationContext cd = ctx.constructorDeclaration();
              if (ctx.genericConstructorDeclaration()!=null){
                cd = ctx.genericConstructorDeclaration().constructorDeclaration();
              }

              String name = cd.IDENTIFIER().getText();
              List<Parameter> ps = extractor.parseParamListFromFormalParameterList(cd.formalParameters().formalParameterList());
              Coordinate x = Coordinate.createFromCtx(ctx);
              FunctionDeclaration fd = new FunctionDeclaration(cd.IDENTIFIER().getText(), ps, x);
              FunctionImp imp = new FunctionImp(fd);
              JavaMethodBodyTreeExtractor e = new JavaMethodBodyTreeExtractor(path, extractor);
              ExpressionOrBlockList body = e.parseBlock(cd.block());
              imp.setBody(body);
              javaClass.addMethod(imp);
        }

        if (ctx.classDeclaration()!=null)
        {
            JavaClassExtractor innerExtractor = new JavaClassExtractor(path);
            JavaClass innerClass = innerExtractor.parseClass(ctx.classDeclaration());
            javaClass.addInnerClass(innerClass);
        }

    }

    //classBodyDeclaration
    //  : ';'
    //    | STATIC? block
    //  | modifier* memberDeclaration
    //;
   public void parseClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext c){
          if (c.block()!=null){
              JavaMethodBodyTreeExtractor e = new JavaMethodBodyTreeExtractor(path, extractor);
              ExpressionOrBlockList staticBody = e.parseBlock(c.block());
              javaClass.setStaticDomain(staticBody);
          }

          if (c.memberDeclaration()!=null){
              parseMemberDeclaration(c.memberDeclaration());
          }
   }

    //classDeclaration
    //    : CLASS IDENTIFIER typeParameters?
    //      (EXTENDS typeType)?
    //      (IMPLEMENTS typeList)?
    //      classBody
    //    ;
    public JavaClass parseClass(JavaParser.ClassDeclarationContext context){
          String name = context.IDENTIFIER().getText();
          String superName = null;
          if (context.typeType()!=null){
              superName = context.typeType().getText();
          }

          List<String> interfaceList = new ArrayList<>();
          if (context.typeList()!=null){
            for (JavaParser.TypeTypeContext t : context.typeList().typeType()){
                interfaceList.add(t.getText());
            }
          }


          javaClass = new JavaClass(name, interfaceList, superName, path);
          //classBody
          //: '{' classBodyDeclaration* '}'
          //;

          JavaParser.ClassBodyContext classBodyContext = context.classBody();
          parseAnonymousClass(classBodyContext);
          return javaClass;
    }



    public JavaClass parseAnonymousClass(JavaParser.ClassBodyContext ctx) {
      if (javaClass == null){
        javaClass = new JavaClass(path);
      }
      if (ctx!=null){
        for (JavaParser.ClassBodyDeclarationContext c : ctx.classBodyDeclaration())
        {
          parseClassBodyDeclaration(c);
        }
      }
      return javaClass;
    }
  }
