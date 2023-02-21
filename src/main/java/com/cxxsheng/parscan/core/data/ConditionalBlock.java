package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;

public class ConditionalBlock extends Block {

      private final ExpressionListWithPrevs boolExp;

      private ExpressionOrBlockList elseBlock = ExpressionOrBlockList.InitEmptyInstance();


      public ConditionalBlock(Coordinate x, ExpressionListWithPrevs boolExp, ExpressionOrBlockList content) {
        super(x, content);
        this.boolExp = boolExp;

      }


       public boolean hasElse(){
          return !elseBlock.isEmpty();
       }

       public void initElseBlock(ExpressionOrBlockList elseB){
          elseBlock = elseB;
          if (!elseB.isEmpty())
            elseBlock.setOwner(this);
       }


      public ExpressionListWithPrevs getBoolExp() {
        return boolExp;
      }

      public ExpressionOrBlockList getElseBlock() {
          return elseBlock;
        }

        @Override
        public String toString() {
              final StringBuffer sb = new StringBuffer("");
              sb.append("if(").append(boolExp).append(")");
              sb.append("{\n").append(getContent()).append("}\n");
              if (hasElse()){
                sb.append("else\n {\n").append(elseBlock).append("\n}\n");
              }
              return sb.toString();
        }
}
