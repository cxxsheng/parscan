package com.cxxsheng.parscan.antlr;

import com.cxxsheng.parscan.antlr.parser.JavaLexer;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.extractor.JavaClassExtractor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class AntlrCore {

  private final static Logger LOG = Logger.getLogger(AntlrCore.class.getName());

  private final Path filePath;

  public AntlrCore(final Path filePath){
    this.filePath = filePath;
  }

  public AntlrCore(final String fileName){
    this.filePath = Paths.get(fileName);
  }



  public Path getFilePath() {
    return filePath;
  }


  //Start parse given java file
  public void  parse() throws IOException {
      JavaLexer lexer = new JavaLexer(CharStreams.fromPath(filePath));
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      JavaParser parser = new JavaParser(tokens);

      JavaParser.CompilationUnitContext tree = parser.compilationUnit();

      List<JavaParser.TypeDeclarationContext> ts = tree.typeDeclaration();

      //typeDeclaration
      //: classOrInterfaceModifier*
      //  (classDeclaration | enumDeclaration | interfaceDeclaration | annotationTypeDeclaration)
      //  | ';'
      //;
      for (JavaParser.TypeDeclarationContext t: ts){
        if (t.classDeclaration()!=null){
          JavaClassExtractor extractor = new JavaClassExtractor();
          JavaClass jClass = extractor.parseClass(t.classDeclaration());
          System.out.println();
        }
      }
  }




}
