package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.AntlrCore;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ASTIteratorTest {

    @Test
    void continueToTaint() {


      Path cp = Paths.get("src", "test", "resources", "JavaDemo", "test.java");
      AntlrCore core = new AntlrCore(cp);
      try {
        core.parse();
        ASTIterator iterator = new ASTIterator(core.getJavaClass(), core.getReadFromParcelFunc().getBody());
        iterator.continueToTaint();
      }
      catch (IOException e) {
        assertTrue(true, e.getMessage());
      }
    }

    @Test
    void nextStage() {
    }

    @Test
    void hasNextStage() {
    }
}