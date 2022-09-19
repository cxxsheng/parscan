package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import java.util.ArrayList;
import java.util.List;

public class Block implements ExpressionOrBlock {

      protected boolean isTaint = false;
      protected Block father;
      private ExpressionOrBlockList content;
      private final Coordinate coordinate;

      public Block(Coordinate coordinate) {this.coordinate = coordinate;}



      public void initExpressionOrBlockList(ExpressionOrBlockList content){
          this.content = content;
      }


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
