package com.cxxsheng.parscan.core.unit.symbol;


//this symbol has value
public class IntSymbol extends NoValueSymbol {

    /**
     * @param type symbol type
     * @param name symbol name
     * @param value symbol value
     */
    public IntSymbol(String type, String name, int value) {
        super(type, name);
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
