package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;


public class Block implements ExpressionOrBlock {

      private final Coordinate coordinate;
      private ExpressionOrBlockList content;


      public Block(Coordinate x, ExpressionOrBlockList content) {
        this.content = content;
        this.coordinate = x;

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


}
