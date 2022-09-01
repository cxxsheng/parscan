package com.cxxsheng.parscan.core.unit;

import java.util.Objects;

public interface Symbol {
    default Expression toExp(){
        return new Expression(this);
    }
}
