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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class AntlrCore {


  private final static Logger LOG = Logger.getLogger(AntlrCore.class.getName());

  private final Path filePath;

  private List<JavaClass> jClasses = new ArrayList<>();

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

  public FunctionImp getWriteToParcelFunc(){
    JavaClass jClass = jClasses.get(0);
    return  jClass.getFunctionImpByFullName("writeToParcel",
                                            new String[]{"Parcel","int"});
  }

  public FunctionImp getReadFromParcelFunc(){
      JavaClass jClass = jClasses.get(0);

      VarDeclaration v = jClass.getVarDeclarationByName("CREATOR");
      if (v != null){
        Symbol e =  v.getLastExpValue();
        if (e instanceof TmpSymbol){
            e = ((TmpSymbol)e).getExpression().getSymbol();
        }
        if (e  instanceof  CallFunc)
        {
            String funcName  = ((CallFunc) e).getFuncName();
            JavaClass nullClass = jClass.findInnerClassByName(funcName);

            if (nullClass == null){
                //fixme need to look up another calss

                //to replace funcname
                Path absPath =  this.filePath.toAbsolutePath();
                String newFileName = funcName.replace('.','/');
                newFileName += ".java";
                Path newRelativePath = Paths.get(newFileName);
                Path newPath = absPath.resolveSibling(newRelativePath);
                try {
                    nullClass = parse(newPath);
                }catch (IOException ex){
                    ex.printStackTrace();
                }

            }
            return nullClass.getFunctionImpByFullName("createFromParcel",
                                                     new String[]{"Parcel"});
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
  public JavaClass parse(Path filePath) throws IOException {
      //if file patg == null start from root filePath
      if (filePath == null)
        filePath = this.filePath;

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
          JavaClassExtractor extractor = new JavaClassExtractor(filePath);
          JavaClass javaClass = extractor.parseClass(t.classDeclaration());
          jClasses.add(javaClass);
          return javaClass;
          //writeToParcel = getWriteToParcel(jClass);
          //readFromParcel = getReadFromParcel(jClass);
          //System.out.println(writeToParcel);
          //System.out.println(readFromParcel);
          //compareTwoFunction(writeToParcel, readFromParcel);
        }
      }
        return null;
  }

    public List<JavaClass> getJavaClasses() {
        return jClasses;
    }

    public JavaClass findClassByName(String name){
        for (JavaClass javaClass: jClasses){
            if (name.equals(javaClass.getName()))
                return javaClass;
        }
        return null;
    }

}
