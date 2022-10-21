package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ASTIteratorTest {

    @Test
    void continueToTaint() {


      try {
        FunctionPattern.initFromFile("./src/test/resources/input/rule.json");
        assertTrue(FunctionPattern.isInit());
        Path cp = Paths.get("src", "test", "resources", "JavaDemo", "test.java");
        AntlrCore core = new AntlrCore(cp);
        core.parse();
        ASTIterator iterator = new ASTIterator(core.getJavaClass(), core.getReadFromParcelFunc());
        iterator.continueToTaint();
      }
      catch (IOException e) {

      }
    }

    @Test
    void nextStage() {
    }

    @Test
    void hasNextStage() {
    }
}