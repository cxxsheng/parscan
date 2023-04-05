import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.iterator.*;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

  private final static Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void init(){
    FunctionReader.openWithAntlr("src/main/resources/Parcel_.java");
    FunctionReader.readFunctionList();
    try {
      FunctionPattern.initFromFile("./src/test/resources/input/rule.json");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static boolean handleOneFile(Path cp){
    AntlrCore core = new AntlrCore(cp);
    try {
      core.parse(null);
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    JavaClass jclass = core.getJavaClasses().get(0);
    if (!jclass.containsInterfaceName("Parcelable")){
      throw new ASTParsingException("expected an parcelable but got nothing");
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

  private static boolean checkIfInDebugMode(){
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    for (String arg : runtimeMXBean.getInputArguments()){
      if (arg.startsWith("-agentlib:jdwp"))
        return true;
    }
    return false;
  }

  private static final List<String> blackList = Arrays.asList(
          //"_1.1.6_Apkpure.apk"
  );
  public static void main(String[] args) {
    init();
   // handleTest();
    if (args.length <=1 ){
      LOG.error("please give an input file");
    }

    String giveJsonFile = args[0];
    String outputFile = args[1];
    OutputStream fos = null;
    try {
      fos = Files.newOutputStream(Paths.get(outputFile));
      String json = FileUtils.readFileToString(new File(giveJsonFile));
      JSONObject obj = JSON.parseObject(json);
      for(String key :obj.keySet()){
        LOG.info("handing " + key + " ...");
        fos.write(("handing " + key + " ...\n").getBytes());
        if (blackList.contains(key)){
          fos.write("skip blaklist \n".getBytes());
          continue;
        }
        JSONArray path_array = obj.getJSONArray(key);
        for (int i = 0; i < path_array.size(); i ++){
          //coveint for debug
          if (checkIfInDebugMode()){
            int passedIndex = 1;
            if (i < passedIndex)
              continue;
          }
          String path = path_array.getString(i);
          StringBuilder sb = new StringBuilder("parsing " + path + " ...");
          LOG.info(sb.toString());
          fos.write((sb.toString()+"\n").getBytes());
          try {
            boolean result =  handleOneFile(Paths.get(path));
            sb = new StringBuilder("finished "+path + " result is " + result);
            fos.write((sb.toString()+"\n").getBytes());
            LOG.info(sb.toString());
          }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            fos.write((sw.toString()+"\n").getBytes());
          }

        }
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }






  }


}
