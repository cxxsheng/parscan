package com.cxxsheng.parscan.core.iterator;


import com.cxxsheng.parscan.core.common.Pair;

public class ASTIteratorManager {
  private static Pair<ASTIterator,ASTIterator> itPair;

  private static transient boolean isStarted = false;

  public static void initializeManager(ASTIterator aIt, ASTIterator bIt){
    itPair = new Pair<>(aIt, bIt);
  }


  public static boolean compareTwoStage(ASTIterator aIt, ASTIterator bIt){
    return false;
  }



  public static boolean startSync(){
    ASTIterator aIt = itPair.getLeft();
    ASTIterator bIt = itPair.getRight();



    while (aIt.nextStage() && bIt.nextStage()){
        if(!compareTwoStage(aIt, bIt))
            return false;
    }
    return aIt.hasNextStage() == aIt.hasNextStage();
  }




}
