package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.antlr.JavaScanListener;
import com.cxxsheng.parscan.core.unit.Symbol;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SymbolManager {
    private final static org.apache.log4j.Logger LOG = Logger.getLogger(SymbolManager.class);
    public static Symbol parseSymbol(String type, String name, String value){
        if (value==null){
            return new NoValueSymbol(type,name);
        }
        if ("int".equals(type)){
            try {
                long v = Long.parseLong(value);
                return new LongSymbol(type, name, v);
            }catch (NumberFormatException e){
                LOG.warn("invalid int format" + value + "\n"+ e.getMessage());
            }
        }if ("long".equals(type)){
            try {
                long v = Long.parseLong(value);
                return new LongSymbol(type, name, v);
            }catch (NumberFormatException e){
                LOG.warn("invalid long format" + value + "\n"+ e.getMessage());
            }
        }if ("String".equals(type)){
            return new StringSymbol(type,name,value);
        }
        return new UnkownSymbol(type, name, value); //maybe object assign or other var assign
    }

    private static final List<Symbol> globalSymbolList = new ArrayList<>();

    public static List<Symbol> getGlobalSymbolList() {
        return globalSymbolList;
    }

    public static void addSymbol2GlobalList(Symbol symbol){
        globalSymbolList.add(symbol);
    }
}
