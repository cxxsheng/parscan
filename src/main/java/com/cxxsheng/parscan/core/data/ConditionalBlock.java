package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;
import java.util.List;

public class ConditionalBlock extends Block {
      private final Expression boolExp;

      private ExpressionOrBlockList elseBlock;
      public ConditionalBlock(Expression boolExp, Coordinate coordinate) {
        super(coordinate);
        this.boolExp = boolExp;
      }


       public  boolean hasElse(){
          return elseBlock != null;
       }

       //fixme may cannot figure out which kind (if/else) of son
       public void initElseBlock(ExpressionOrBlockList elseB){
          elseBlock = elseB;
       }
}
