package com.cxxsheng.parscan.core.extractor.callback;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public interface BinaryCreator {
  Expression create(TerminalSymbol t1, TerminalSymbol t2);
}
