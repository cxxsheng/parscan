package com.cxxsheng.parscan.core.extractor;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.TmpSymbol;
import java.util.ArrayList;
import java.util.List;

public class TmpSymbolManager {
  public static int id = 0;

  private static List<TmpSymbol> tmpSyms = new ArrayList<>();

  private static int nextInt(){
    return id++;
  }

  public static TmpSymbol createNewTmpSymbol( Expression value){
    TmpSymbol tmpSymbol = new TmpSymbol(nextInt(), value);
    tmpSyms.add(tmpSymbol);
    return tmpSymbol;
  }




}
