package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import java.util.Objects;

public class StringSymbol extends TerminalSymbol {
    private final String value;

    public StringSymbol(String value) {
        this.value = value;
    }



    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      StringSymbol that = (StringSymbol)o;
      return Objects.equals(value, that.value);
    }


    @Override
    public int hashCode() {
      return Objects.hash(value);
    }

    @Override
    public String toString() {
       return value;
    }

    public String getValue() {
      return value;
    }
}
