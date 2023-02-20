package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class LabelSymbol extends TerminalSymbol {

  private final String name;

  public LabelSymbol(String name) {this.name = name;}



  public String getName() {
    return name;
  }
}
