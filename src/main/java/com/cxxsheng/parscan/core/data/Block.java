package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;

public class Block implements ExpressionOrBlock {

      protected boolean isTaint = false;
      protected Block father;
      private final Coordinate coordinate;
      private final ExpressionOrBlockList content;

      public Block( Coordinate coordinate, ExpressionOrBlockList content) {
        this.content = content;
        this.coordinate = coordinate;}





      public void addExpressionOrBlock(ExpressionOrBlock eb){
          content.addOne(eb);
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
