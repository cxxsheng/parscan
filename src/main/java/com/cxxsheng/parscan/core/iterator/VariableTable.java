package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.common.Pair;
import java.util.ArrayList;
import java.util.List;

public class VariableTable {

  public List<Pair<RuntimeVariable, RuntimeValue>> vs = new ArrayList<>();


  public static boolean inDomain(RuntimeVariable v, int[] currentMark){
    if (v.mark().length <= currentMark.length){
      int i;
      for (i = 0; i < v.mark().length; i++){
        if (v.mark()[i] != currentMark[i])
          break;
      }
      if (i == v.mark().length-2 && v.mark()[i] <= currentMark[i])
        return true;
    }
    return false;
  }

  public Pair<RuntimeVariable, RuntimeValue> findValueByName(String name, int[] currentMask){
      if(name == null)
        return null;

      Pair<RuntimeVariable, RuntimeValue> ret = null;
      for (Pair<RuntimeVariable, RuntimeValue>  vPair : vs){
          RuntimeVariable v = vPair.getLeft();
          if (inDomain(v, currentMask)){
            if (name.equals(v.getName())){
                //make sure the lastest variable
                ret = vPair;
            }
          }
      }
      return ret;
  }

  public void putVariable(RuntimeVariable variable, RuntimeValue value){
      Pair<RuntimeVariable, RuntimeValue> pair = new Pair<>(variable, value);
      vs.add(pair);
  }
}
