package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;

public class ConditionalBlock extends Block {

      private final Expression boolExp;

      private ExpressionOrBlockList elseBlock;

      private boolean haveElse = false;

      public ConditionalBlock(Coordinate x, Expression boolExp, ExpressionOrBlockList content) {
        super(x, content);
        this.boolExp = boolExp;

      }


       public  boolean hasElse(){
          return elseBlock != null;
       }

       //fixme may cannot figure out which kind (if/else) of son
       public void initElseBlock(ExpressionOrBlockList elseB){
          elseBlock = elseB;
       }

        public void setHaveElse() {
          this.haveElse = true;
        }
}
