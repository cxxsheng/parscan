package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.antlr.exception.JavaASTExtractorException;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.ExpressionOrBlock;
import com.cxxsheng.parscan.core.data.ExpressionOrBlockList;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import java.util.ArrayList;
import java.util.List;

import static com.cxxsheng.parscan.core.extractor.CommonExtractor.*;

public class JavaClassExtractor {



   private JavaClass javaClass;
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

    public void parseMemberDeclaration(JavaParser.MemberDeclarationContext ctx){
        //methodDeclaration & genericMethodDeclaration
        if (ctx.genericMethodDeclaration() != null || ctx.methodDeclaration() != null){
           JavaParser.MethodDeclarationContext md = ctx.methodDeclaration();
            if (ctx.genericMethodDeclaration() != null )
                md = ctx.genericMethodDeclaration().methodDeclaration();

            List<Parameter> ps = parseParamListFromFormalParameterList(md.formalParameters().formalParameterList());
            Coordinate x = Coordinate.createFromCtx(ctx);
            JavaType retType = parseJavaTypeOrVoid(md.typeTypeOrVoid());
            FunctionDeclaration fd = new FunctionDeclaration(retType ,md.IDENTIFIER().getText(), ps, x);
            FunctionImp imp = new FunctionImp(fd);
            JavaMethodBodyTreeExtractor extractor = new JavaMethodBodyTreeExtractor();
            ExpressionOrBlockList body = extractor.parseMethodBody(md.methodBody());
            imp.setBody(body);
            javaClass.addMethod(imp);
        }

        //fieldDeclaration
        //    : typeType variableDeclarators ';'
        //    ;
        if (ctx.fieldDeclaration() != null){
            JavaType type = parseJavaType(ctx.fieldDeclaration().typeType());
            parseVariableDeclarators(ctx.fieldDeclaration().variableDeclarators());

            ExpressionOrBlockList exps = parseVariableDeclarators(ctx.fieldDeclaration().variableDeclarators());
            for (ExpressionOrBlock e: exps.getContent()){
                Expression ee = (Expression)e;
                VarDeclaration var;
                if (ee.isTerminal()){
                  IdentifierSymbol name = (IdentifierSymbol)ee.getSymbol();
                  var = new VarDeclaration(name, type);
                }else {
                  IdentifierSymbol name = (IdentifierSymbol)ee.getL().getSymbol();
                  var = new VarDeclaration(name, type, ee.getR());
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
              List<Parameter> ps = CommonExtractor.parseParamListFromFormalParameterList(cd.formalParameters().formalParameterList());
              Coordinate x = Coordinate.createFromCtx(ctx);
              FunctionDeclaration fd = new FunctionDeclaration(cd.IDENTIFIER().getText(), ps, x);
              FunctionImp imp = new FunctionImp(fd);
              JavaMethodBodyTreeExtractor e = new JavaMethodBodyTreeExtractor();
              ExpressionOrBlockList body = e.parseBlock(cd.block());
              imp.setBody(body);
              javaClass.addMethod(imp);
        }


    }

    //classBodyDeclaration
    //  : ';'
    //    | STATIC? block
    //  | modifier* memberDeclaration
    //;
   public void parseClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext c){
          if (c.block()!=null){
              JavaMethodBodyTreeExtractor e = new JavaMethodBodyTreeExtractor();
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

          List<String> interfaceList = null;
          if (context.typeList()!=null){
            interfaceList = new ArrayList<>();
            for (JavaParser.TypeTypeContext t : context.typeList().typeType()){
                interfaceList.add(t.getText());
            }
          }


          javaClass = new JavaClass(name, interfaceList, superName);
          //classBody
          //: '{' classBodyDeclaration* '}'
          //;

          JavaParser.ClassBodyContext classBodyContext = context.classBody();
          parseAnonymousClass(classBodyContext);
          return javaClass;
    }



    public JavaClass parseAnonymousClass(JavaParser.ClassBodyContext ctx) {
      if (javaClass == null){
        javaClass = new JavaClass();
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
