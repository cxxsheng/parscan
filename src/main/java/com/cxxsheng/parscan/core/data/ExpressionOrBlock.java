package com.cxxsheng.parscan.core.data;

//wrap exp and block to construct tree
public abstract class ExpressionOrBlock {

  private ExpressionOrBlock previous = null;
  private ExpressionOrBlock next = null;
  private ExpressionOrBlockList domain;
  public ExpressionOrBlockList wrapToList(){
      return  ExpressionOrBlockList.Init(this);
  }

  public ExpressionOrBlock previous(){
    return previous;
  }
  public ExpressionOrBlock next(){
    return next;
  }
  void setPreviousNode(ExpressionOrBlock node){
    previous = node;
  }
  void setNextNode(ExpressionOrBlock node){
    next = node;
  }

  public boolean hasNext(){
    return next != null;
  }
  protected ExpressionOrBlockList getDomain() {
    return domain;
  }

  protected void setDomain(ExpressionOrBlockList domain) {
    this.domain = domain;
  }


}
