package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;
import java.util.Objects;

public class StringSymbol extends Symbol {
    private final String value;

    public StringSymbol(String value) {
        this.value = value;
    }

    // constant cannot be tainted
    @Override
    public void taint() {

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
}
