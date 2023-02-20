package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Primitive;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class TypeIDSymbol extends TerminalSymbol {

    private final Primitive type;

    private final IdentifierSymbol id;

    private final boolean isArray;

    public TypeIDSymbol(Primitive type, IdentifierSymbol id, boolean isArray) {
      this.type = type;
      this.id = id;
      this.isArray = isArray;
    }

}
