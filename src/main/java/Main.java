import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.iterator.ASTIterator;
import com.cxxsheng.parscan.core.iterator.FunctionReader;
import com.cxxsheng.parscan.core.iterator.Graph;
import com.cxxsheng.parscan.core.iterator.ParcelMismatchException;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  private final static Logger LOG = LoggerFactory.getLogger(Main.class);

  private static void init(){
    FunctionReader.openWithAntlr("src/main/resources/Parcel_.java");
    FunctionReader.readFunctionList();
    try {
      FunctionPattern.initFromFile("./src/test/resources/input/rule.json");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static void handleOneFile(Path cp){
    AntlrCore core = new AntlrCore(cp);
    try {
      core.parse(null);
    }
    catch (IOException e) {
      e.printStackTrace();
      return;
    }
    ASTIterator iterator = new ASTIterator(core, core.getWriteToParcelFunc());
    while (iterator.hasNextStage()){
      System.out.println("nextStage");
      iterator.continueToTaint();
      System.out.println(iterator.getDataGraph());
    }
    Graph graph = iterator.getDataGraph();
    ASTIterator iterator1 = new ASTIterator(core, core.getReadFromParcelFunc(), graph);

    while (iterator1.hasNextStage()){
      iterator1.continueToTaint();
    }
  }


  public static void main(String[] args) {
    init();
    Path dir = Paths.get("src", "test", "resources", "JavaDemo", "cves");

    File cveDir=  dir.toFile();
    if (!cveDir.isDirectory()){
      return;
    }
    File[] files = cveDir.listFiles();
    for (File file : files){
      LOG.info("Handing " + file.getPath());
      handleOneFile(Paths.get(file.getPath()));
    }
    try {

    }
    catch (ParcelMismatchException e) {
        e.printStackTrace();
    }

  }
}
