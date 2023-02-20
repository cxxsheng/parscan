package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class ConditionalExpression extends Symbol {

    private final TerminalSymbol cond;
    private final TerminalSymbol left;
    private final TerminalSymbol right;


    public ConditionalExpression(TerminalSymbol cond, TerminalSymbol left, TerminalSymbol right) {
        this.cond = cond;
        this.left = left;
        this.right = right;

    }

    public TerminalSymbol getCond() {
        return cond;
    }

    public TerminalSymbol getLeft() {
        return left;
    }

    public TerminalSymbol getRight() {
        return right;
    }

  @Override
    public String toString() {
      return ""+ cond +
             "?" + left +
             ":" + right;
    }
}
