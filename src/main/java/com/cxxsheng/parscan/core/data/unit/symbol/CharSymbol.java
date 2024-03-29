package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import java.util.Objects;

public class CharSymbol extends TerminalSymbol {
    private final  char value;

    public CharSymbol(char value) {
        this.value = value;
    }


  @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CharSymbol that = (CharSymbol)o;
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

    public char getValue() {
      return value;
    }
}
