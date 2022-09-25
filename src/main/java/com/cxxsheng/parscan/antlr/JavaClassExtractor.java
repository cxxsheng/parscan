package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.data.JavaClass;
import java.util.ArrayList;
import java.util.List;

public class JavaClassExtractor {

    //classBodyDeclaration
    //  : ';'
    //    | STATIC? block
    //  | modifier* memberDeclaration
    //;
   public void parseClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext c){
          if (c.block()!=null){

          }

          if (c.memberDeclaration()!=null){

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
        //classBody
        //: '{' classBodyDeclaration* '}'
        //;

        JavaParser.ClassBodyContext classBodyContext = context.classBody();
        if (classBodyContext!=null){
            for (JavaParser.ClassBodyDeclarationContext c : classBodyContext.classBodyDeclaration())
            {
                parseClassBodyDeclaration(c);
            }
        }
        return new JavaClass(name, interfaceList, superName);
    }






}
