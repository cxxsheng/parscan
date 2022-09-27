package com.cxxsheng.parscan.antlr.exception;

import com.cxxsheng.parscan.core.Coordinate;
import org.antlr.v4.runtime.ParserRuleContext;

public class JavaASTExtractorException extends RuntimeException {

    public JavaASTExtractorException(String msg, ParserRuleContext c){
        super(msg + " at " + Coordinate.createFromCtx(c));
    }

   public JavaASTExtractorException(String msg){
     super(msg);
   }
}
