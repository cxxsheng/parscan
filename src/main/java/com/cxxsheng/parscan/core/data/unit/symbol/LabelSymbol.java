package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

public class LabelSymbol extends Symbol {

  public LabelSymbol(String name) {this.name = name;}

  @Override
  public void taint() {

  }

  private final String name;

}
