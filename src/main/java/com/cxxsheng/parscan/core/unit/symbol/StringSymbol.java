package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class StringSymbol extends NoValueSymbol {
    private final String value;


    /**
     * @param type  symbol type
     * @param name  symbol name
     * @param value symbol value
     */
    public StringSymbol(String type, String name, String value) {
        super(type, name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
