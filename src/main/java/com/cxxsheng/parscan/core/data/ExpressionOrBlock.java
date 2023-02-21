package com.cxxsheng.parscan.core.data;

//wrap exp and block to construct tree
public abstract class ExpressionOrBlock {

  private ExpressionOrBlockList domain;
  public ExpressionOrBlockList wrapToList(){
      return  ExpressionOrBlockList.Init(this);
  }


  protected ExpressionOrBlockList getDomain() {
    return domain;
  }

  protected void setDomain(ExpressionOrBlockList domain) {
    this.domain = domain;
  }


}
