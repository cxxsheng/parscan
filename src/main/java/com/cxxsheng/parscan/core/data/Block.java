package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;


public class Block implements ExpressionOrBlock {

      protected boolean isTaint = false;
      private final Coordinate coordinate;
      private ExpressionOrBlockList content;


      public Block(Coordinate x, ExpressionOrBlockList content) {
        this.content = content;
        this.coordinate = x;

        //if content exits, must have taint expression
        if (!content.isEmpty())
          taint();
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

}
