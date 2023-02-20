package com.cxxsheng.parscan.core.extractor.callback;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import java.util.List;

public interface ListCreator {
  Expression create(List <TerminalSymbol> tle);

}
