package com.cxxsheng.parscan.core.pattern;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class PatternTest {

    @Test
    void compileFromFile() {

      try {
        FunctionPattern.initFromFile("./src/test/resources/input/rule.json");
        StringBuilder sb = new StringBuilder();
        for (FunctionPattern p : FunctionPattern.getPatterns()){

          sb.append(p.getPatternType()).append(": \n");
          sb.append("index: ").append(p.getPatternInt("index")).append('\n');
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
      FunctionPattern.initFromFile("1");
      StringBuilder sb = new StringBuilder();

      for (FunctionPattern p : FunctionPattern.getPatterns()){

        sb.append(p.getPatternType()).append(": \n");
        sb.append("index: ").append(p.getPatternInt("index")).append('\n');
        sb.append("\n");
      }
      System.out.println(sb.toString());
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
  }
}