package com.cxxsheng.parscan.core.pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class FunctionPattern {


  private final String patternType;
  private final JSONObject v;

  private static List<FunctionPattern> patterns = new ArrayList<>();


  public String getPatternType() {
    return patternType;
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

  private FunctionPattern(String patternType, JSONObject v){
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


          FunctionPattern p = new FunctionPattern(type, obj);
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

  public String getPatternString(String name){
    return v.getString(name);
  }

  public Integer getPatternInt(String name){
    return v.getInteger(name);
  }

  public JSONObject getV() {
    return v;
  }
}
