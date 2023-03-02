package com.cxxsheng.parscan.core.iterator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cxxsheng.parscan.core.AntlrCore;
import com.cxxsheng.parscan.core.pattern.FunctionPattern;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ASTIteratorTest {

    @Test
    void continueToTaint() {
      FunctionReaderTest.openWithAntlr();

      try {
        FunctionPattern.initFromFile("./src/test/resources/input/rule.json");
        assertTrue(FunctionPattern.isInit());
        Path cp = Paths.get("src", "test", "resources", "JavaDemo", "GateKeeperResponse.java");
        AntlrCore core = new AntlrCore(cp);
        core.parse();
        ASTIterator iterator = new ASTIterator(core.getJavaClass(), core.getWriteToParcelFunc());

        while (iterator.hasNextStage()){
          System.out.println("nextStage");
          iterator.continueToTaint();
          System.out.println(iterator.getDataGraph());
        }
        Graph graph = iterator.getDataGraph();

        ASTIterator iterator1 = new ASTIterator(core.getJavaClass(), core.getReadFromParcelFunc(), graph);

        while (iterator1.hasNextStage()){
          iterator1.continueToTaint();
        }

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