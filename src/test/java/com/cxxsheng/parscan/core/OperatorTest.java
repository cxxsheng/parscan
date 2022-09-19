package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.data.unit.Operator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    void getName() {
      assertEquals(Operator.DE.getName(), "/=");
      assertEquals(Operator.EQ, Operator.nameOf("=="));
    }

    @Test
    void isAssign() {
      assertTrue(Operator.DE.isAssign());
      assertFalse(Operator.ADD.isAssign());
    }
}