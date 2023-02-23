package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.antlr.exception.JavaScanException;
import com.cxxsheng.parscan.antlr.parser.JavaLexer;
import com.cxxsheng.parscan.antlr.parser.JavaParser;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.CallFunc;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import com.cxxsheng.parscan.core.extractor.JavaClassExtractor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class AntlrCore {


  private final static Logger LOG = Logger.getLogger(AntlrCore.class.getName());

  private final Path filePath;

  private JavaClass jClass;

  private FunctionImp writeToParcel;

  private FunctionImp readFromParcel;



  public AntlrCore(final Path filePath){
    this.filePath = filePath;
  }

  public AntlrCore(final String fileName){
    this.filePath = Paths.get(fileName);
  }



  public Path getFilePath() {
    return filePath;
  }

  private static FunctionImp getWriteToParcel(JavaClass jClass){
    return  jClass.getFunctionImpByName("void writeToParcel(Parcel dest, int flags)");
  }

  private static FunctionImp getReadFromParcel(JavaClass jClass){
      VarDeclaration v = jClass.getVarDeclarationByName("CREATOR");
      if (v != null){
        Symbol e =  v.getLastExpValue();
        if (e instanceof TmpSymbol){
            e = ((TmpSymbol)e).getExpression().getSymbol();
        }
        if (e  instanceof  CallFunc)
        {
            JavaClass nullClass = ((CallFunc)e).extraClass();
            return nullClass.getFunctionImpByName("GateKeeperResponse createFromParcel(Parcel source)");
        }
      }
      return null;
  }




  public static boolean compareTwoFunction(FunctionImp writeToParcel, FunctionImp readFromParcel){
      // trace writeToParcel's first param
      Parameter p = writeToParcel.getFunDec().getParams().get(0);
      assert p.getType().toString().equals("Parcel");
      String name = p.getName();
      if (writeToParcel.getBody().isEmpty())
        return false;
      return false;
  }
  //Start parse given java file
  public void parse() throws IOException {
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

      if (ts.size() != 1){
        throw new JavaScanException("Warning! Unsupported multi-class in single java file");
      }
      for (JavaParser.TypeDeclarationContext t: ts){
        if (t.classDeclaration()!=null){
          JavaClassExtractor extractor = new JavaClassExtractor();
          jClass = extractor.parseClass(t.classDeclaration());
          writeToParcel = getWriteToParcel(jClass);
          readFromParcel = getReadFromParcel(jClass);
          //System.out.println(writeToParcel);
          //System.out.println(readFromParcel);
          //compareTwoFunction(writeToParcel, readFromParcel);
          break;
        }
      }


  }


  public JavaClass getJavaClass() {
    return jClass;
  }

  public FunctionImp getReadFromParcelFunc() {
    return readFromParcel;
  }

  public FunctionImp getWriteToParcelFunc() {
    return writeToParcel;
  }
}
