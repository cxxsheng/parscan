package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.data.FunctionImp;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class FunctionReader {

  private static Map<String, FunctionDeclaration> functionMaps = new HashMap<>();

  public static void open(String path){

    File file = new File(path);
    LineIterator it = null;
    try {
      it = FileUtils.lineIterator(file, "UTF-8");

      while (it.hasNext()) {
        String line = it.nextLine();
        String[] strs = line.split("->");
        if (strs.length != 2)
          continue;

        String retValue = strs[0];
        String function = strs[1];
        //System.out.println(retValue+" -> "+function);
      }
    }catch (IOException e){
        e.printStackTrace();
    }
    finally {
      LineIterator.closeQuietly(it);
    }
  }

  public static void openWithAntlr(String path){
    AntlrCore core = new AntlrCore(path);
    try {
      JavaClass clazz = core.parse(null);
      for (FunctionImp imp : clazz.getMethods()){
          FunctionDeclaration d = imp.getFunDec();
          functionMaps.put(d.getName(), d);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, FunctionDeclaration> readFunctionList(){
    return functionMaps;
  }

  public static FunctionDeclaration findDeclarationByName(String name){
     return functionMaps.get(name);
  }

}
