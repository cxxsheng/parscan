package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;


public class Block implements ExpressionOrBlock {

      protected boolean isTaint = false;
      protected Block father;
      private final Coordinate coordinate;
      private ExpressionOrBlockList content;

      public Block(Coordinate coordinate, ExpressionOrBlockList content) {
        this.content = content;
        this.coordinate = coordinate;

        //if content exits, must have taint expression
        if (!content.isEmpty())
          taint();
      }





      public void addExpressionOrBlock(ExpressionOrBlock eb){
          if (eb.isTaint())
            content = content.addOne(eb);
      }

        //public void addExpression(Expression exp){
        //    content.add(exp);
        //}
        //
        //public void addBlock(Block block){
        //  block.father = this;
        //  content.add(block);
        //}

        public ExpressionOrBlockList getContent() {
          return content;
        }

        @Override
        public void taint() {
          isTaint = true;
        }

        public boolean isTaint() {
          return isTaint;
        }

        public void Taint(){
          isTaint = true;
        }

        public Block getFather() {
          return father;
        }
}
