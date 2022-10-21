package com.cxxsheng.parscan.core.pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionPattern {


  private final String patternType;
  private final Map<String, String> v;

  private static List<FunctionPattern> patterns = new ArrayList<>();


  public String getPatternType() {
    return patternType;
  }

  public Map<String, String> getV() {
    return v;
  }

  public static List<FunctionPattern> getPatterns() {
    return patterns;
  }

  private static void addPattern(FunctionPattern pattern){
    patterns.add(pattern);
  }

  public static final int NO_PAUSE = 0;

  public static final int PAUSE_AT_FUNC = 1;

  public static final int PAUSE_AT_CONDITION = 2;

  private static int PAUSE_AT = NO_PAUSE;

  private static boolean isInit = false;

  private FunctionPattern(String patternType, Map<String, String> v){
    this.patternType = patternType;
    this.v = v;
  }


  public static void initFromString(String str){

    JSONObject jb = JSONObject.parseObject(str);
    if(jb.containsKey("trace")){
      handleParscan(jb.getJSONArray("trace"));
    }
    String pauseAt = jb.getString("pauseAt");
    if ("callFunc".equals(pauseAt)){
      PAUSE_AT &= PAUSE_AT_FUNC;
    }
    isInit = true;
  }

  private static void handleParscan(JSONArray ja) {

    for (int i =0 ; i < ja.size(); i++){
        JSONObject obj = ja.getJSONObject(i);
        String type = obj.getString("type");
        if (type != null){

          Map<String,String> map = new HashMap<>();
          for (Map.Entry<String, Object> entry : obj.entrySet()) {
              map.put(entry.getKey(), entry.getValue().toString());
          }
          FunctionPattern p = new FunctionPattern(type, map);
          addPattern(p);
        }
    }

  }


  public static void initFromFile(String fileName) throws IOException {

    String json = FileUtils.readFileToString(new File(fileName));
    initFromString(json);

  }

  public static boolean isInit(){
      return isInit;
  }

}
