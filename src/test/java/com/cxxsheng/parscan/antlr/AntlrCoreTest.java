package com.cxxsheng.parscan.antlr;
import com.microsoft.z3.*;


import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


class AntlrCoreTest {

    @org.junit.jupiter.api.Test
    void getFileName() {
      Path a =  Paths.get("");
      assertEquals(a, new AntlrCore(a).getFilePath());
    }

    @org.junit.jupiter.api.Test
    void parseEx() {
      Path a =  Paths.get("");
      AntlrCore core = new AntlrCore(a);
      assertThrows(IOException.class, core::parse);
    }

    @org.junit.jupiter.api.Test
    void parse() {

      Path cp = Paths.get("src", "test", "resources", "JavaDemo", "test.java");
      AntlrCore core = new AntlrCore(cp);
      try {
        core.parse();
      }
      catch (IOException e) {
        assertTrue(true, e.getMessage());
      }
    }


}