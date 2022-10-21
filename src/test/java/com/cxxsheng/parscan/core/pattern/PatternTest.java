package com.cxxsheng.parscan.core.pattern;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

class PatternTest {

    @Test
    void compileFromFile() {

      try {
        Pattern.initFromFile("./src/test/resources/input/rule.json");
        StringBuilder sb = new StringBuilder();
        for (Pattern p : Pattern.getPatterns()){

          sb.append(p.getPatternType()).append(": \n");
          Map<String,String> vv = p.getV();
          for (String key : vv.keySet()){
            String vvvv = vv.get(key);
            sb.append(key).append(": ").append(vvvv).append("\n");
          }

          sb.append("\n");
        }
        System.out.println(sb.toString());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

  @Test
  void test() {

    try {
      Pattern.initFromFile("1");
      StringBuilder sb = new StringBuilder();
      for (Pattern p : Pattern.getPatterns()){

        sb.append(p.getPatternType()).append(": \n");
        Map<String,String> vv = p.getV();
        for (String key : vv.keySet()){
          String vvvv = vv.get(key);
          sb.append(key).append(": ").append(vvvv).append("\n");
        }

        sb.append("\n");
      }
      System.out.println(sb.toString());
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
  }
}