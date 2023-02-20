package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import java.util.Objects;

public class BoolSymbol extends TerminalSymbol {
    private final boolean value;

    public BoolSymbol(boolean value) {
        this.value = value;
    }

    @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoolSymbol that = (BoolSymbol)o;
        return value == that.value;
      }

    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
      return ""+value;
    }


    public boolean getValue(){
      return value;
    }
}
