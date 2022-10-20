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

public class Pattern {


  private final String patternType;
  private final Map<String, String> v;

  private static List<Pattern> patterns = new ArrayList<>();


  public String getPatternType() {
    return patternType;
  }

  public Map<String, String> getV() {
    return v;
  }

  public static List<Pattern> getPatterns() {
    return patterns;
  }

  private static void addPattern(Pattern pattern){
    patterns.add(pattern);
  }

  private Pattern(String patternType, Map<String, String> v){
    this.patternType = patternType;
    this.v = v;
  }


  public static void initFromString(String str){

    JSONObject jb = JSONObject.parseObject(str);
    if(jb.containsKey("parscan")){
      handleParscan(jb.getJSONArray("parscan"));
    }
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
          Pattern p = new Pattern(type, map);
          addPattern(p);
        }
    }

  }


  public static void initFromFile(String fileName) throws IOException {

    String json = FileUtils.readFileToString(new File(fileName));
    initFromString(json);

  }
}
