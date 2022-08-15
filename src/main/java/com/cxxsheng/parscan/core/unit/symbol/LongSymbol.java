package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class LongSymbol extends NoValueSymbol {

    /**
     * @param type symbol type
     * @param name symbol name
     * @param value symbol value
     */
    public LongSymbol(String type, String name, long value) {
        super(type, name);
        this.value = value;
    }

    private long value;

    public long getValue() {
        return value;
    }

}
