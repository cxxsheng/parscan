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
        Path cp = Paths.get("src", "test", "resources", "JavaDemo","patched", "GateKeeperResponse.java");
        AntlrCore core = new AntlrCore(cp);
        core.parse();
        ASTIterator iterator = new ASTIterator(core.getJavaClass(), core.getWriteToParcelFunc());
        iterator.start();
        Graph graph = iterator.getDataGraph();
        System.out.println(graph);
        ASTIterator iterator1 = new ASTIterator(core.getJavaClass(), core.getReadFromParcelFunc(), graph);
        if (iterator1.start())
            System.out.println("okokokokokokok");
      }
      catch (IOException e) {
            e.printStackTrace();
      }
    }

    @Test
    void nextStage() {
    }

    @Test
    void hasNextStage() {
    }
}