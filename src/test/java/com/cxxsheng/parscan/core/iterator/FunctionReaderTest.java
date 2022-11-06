package com.cxxsheng.parscan.core.iterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionReaderTest {

    @Test
    void open() {
      FunctionReader.open("src/main/resources/Parcel.txt");
    }

    @Test
    public static void openWithAntlr() {
      FunctionReader.openWithAntlr("src/main/resources/Parcel_.java");
      FunctionReader.readFunctionList();
    }


}