import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.iterator.ASTIterator;
import com.cxxsheng.parscan.core.iterator.FunctionReader;
import com.cxxsheng.parscan.core.iterator.Graph;
import com.cxxsheng.parscan.core.iterator.ParcelMismatchException;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import org.apache.commons.io.FileUtils;
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


  private static boolean handleOneFile(Path cp){
    AntlrCore core = new AntlrCore(cp);
    try {
      core.parse(null);
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    ASTIterator iterator1 = new ASTIterator(core, core.getWriteToParcelFunc());
    iterator1.start();
    Graph graph = iterator1.getDataGraph();
    LOG.info(graph.toMermaidString());
    ASTIterator iterator2 = new ASTIterator(core, core.getReadFromParcelFunc(), graph);
    return iterator2.start();
  }


  private static void handleTest(){
    Path dir = Paths.get("src", "test", "resources", "JavaDemo", "cves");
    File cveDir=  dir.toFile();
    if (!cveDir.isDirectory()){
      return;
    }
    File[] files = cveDir.listFiles();
    for (File file : files){
      LOG.info("Handing " + file.getPath());
      boolean result = handleOneFile(Paths.get(file.getPath()));
      LOG.info("finished "+ file.getPath() + " result is " +result);
    }
    try {

    }
    catch (ParcelMismatchException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    init();
   // handleTest();
    if (args.length <=0 ){
      LOG.error("please give an input file");
    }

    String giveJsonFile = args[0];
    String json = "";
    try {
      json = FileUtils.readFileToString(new File(giveJsonFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    JSONObject obj = JSON.parseObject(json);

    for(String key :obj.keySet()){
      LOG.info("handing " + key + " ...");
      JSONArray path_array = obj.getJSONArray(key);
      for (Object path : path_array){
        LOG.info("parsing " + path + " ...");
        boolean result =  handleOneFile(Paths.get((String) path));
        LOG.info("finished "+path + " result is " + result);

      }
    }

  }


}
