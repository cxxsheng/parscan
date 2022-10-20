package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;


public class Block extends ExpressionOrBlock {

      private final Coordinate coordinate;
      private ExpressionOrBlockList content;

      private ExpressionOrBlock previous;
      private ExpressionOrBlock next;

      public Block(Coordinate x, ExpressionOrBlockList content) {
        this.content = content;
        this.coordinate = x;
        content.setOwner(this);
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
        public ExpressionOrBlock previous() {
          return previous;
        }

        @Override
        public ExpressionOrBlock next() {
          return next;
        }

        public void setPrevious(ExpressionOrBlock previous) {
          this.previous = previous;
        }

        public void setNext(ExpressionOrBlock next) {
          this.next = next;
        }

        protected void updateContent(ExpressionOrBlockList content) {
          this.content = content;
        }


}
