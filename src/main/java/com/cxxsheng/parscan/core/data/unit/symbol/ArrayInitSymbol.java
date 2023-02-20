package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;
import java.util.Objects;


// like {1,2,3,4} while initializing array
public class ArrayInitSymbol extends Symbol {
    private final String value;

    public ArrayInitSymbol(String value) {
        this.value = value;
    }


    @Override
    //Not very precise because element in {} can be expression instead of constant
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ArrayInitSymbol that = (ArrayInitSymbol)o;
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
