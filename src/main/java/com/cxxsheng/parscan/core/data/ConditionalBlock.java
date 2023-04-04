package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.z3.ExprWithTypeVariable;

import java.util.List;

public class ConditionalBlock extends Block {

      private final ExpressionListWithPrevs boolExp;

      private ExpressionOrBlockList elseBlock = ExpressionOrBlockList.InitEmptyInstance();

      private final List<ExpressionListWithPrevs> someLocalEquals;

      public ConditionalBlock(Coordinate x, ExpressionListWithPrevs boolExp, ExpressionOrBlockList content) {
            this(x, boolExp, content, null);
      }

      public ConditionalBlock(Coordinate x, ExpressionListWithPrevs boolExp, ExpressionOrBlockList content,  List<ExpressionListWithPrevs> localEquals) {
        super(x, content);
        this.boolExp = boolExp;
        this.someLocalEquals = localEquals;
        boolExp.setBlock(this);
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

      private ExprWithTypeVariable condSaver;


      public ExprWithTypeVariable getCondSaver() {
          return condSaver;
      }


      public void setCondSaver(ExprWithTypeVariable condSaver) {
          this.condSaver = condSaver;
      }

      public boolean needCombineCondition(){
          return false;
      }

      public List<ExpressionListWithPrevs> getSomeLocalEquals() {
        return someLocalEquals;
      }
}
