package com.cxxsheng.parscan.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void parseIntString() {
        long a = 0x123;
        String aa = "0x123";
        assertEquals(a,   Utils.parseIntString(aa, 16));

        a = 0X123;
        aa = "0X123";
        assertEquals(a,   Utils.parseIntString(aa, 16));

        a = 0_12_3;
        aa = "0_12_3";
        assertEquals(a,   Utils.parseIntString(aa, 8));

        a = 0b10111;
        aa = "0b10111";
        assertEquals(a,   Utils.parseIntString(aa, 2));


        a = 0B11111;
        aa = "0B11111";
        assertEquals(a,   Utils.parseIntString(aa, 2));

        a = 12399_9_2_2_1_1;
        aa = "12399_9_2_2_1_1";
        assertEquals(a,   Utils.parseIntString(aa, 10));

    }

    @Test
    void parseFloatString() {
        double a = 123.1E1;
        String aa = "123.1E1";
        assertEquals(a, Utils.parseFloatString(aa));

    }
}