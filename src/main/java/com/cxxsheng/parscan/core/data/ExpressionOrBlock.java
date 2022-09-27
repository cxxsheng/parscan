package com.cxxsheng.parscan.core.data;

import java.util.ArrayList;
import java.util.List;

//wrap exp and block to construct tree
public interface ExpressionOrBlock {

  default ExpressionOrBlockList wrapToList(){
      return  ExpressionOrBlockList.Init(this);
  }
}
