package com.cxxsheng.parscan.core.unit;

public abstract class Symbol {
    private final String type;
    private final String name;

    /**
     * @param type symbol type
     * @param name symbol name
     */
    public Symbol(String type, final String name){
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
