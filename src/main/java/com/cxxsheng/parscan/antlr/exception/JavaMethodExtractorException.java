package com.cxxsheng.parscan.antlr.exception;

import com.cxxsheng.parscan.antlr.JavaMethodBodyTreeExtractor;
import com.cxxsheng.parscan.core.Coordinate;
import org.antlr.v4.runtime.ParserRuleContext;

public class JavaMethodExtractorException extends RuntimeException {

    public JavaMethodExtractorException(String msg, ParserRuleContext c){
        super(msg + " at " + Coordinate.createFromCtx(c));
    }
}
