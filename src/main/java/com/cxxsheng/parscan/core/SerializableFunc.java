package com.cxxsheng.parscan.core;


import com.cxxsheng.parscan.core.FuncStatement;

//core class to collect parcelable function
public interface SerializableFunc {

    FuncStatement getSerializeFunc();

    FuncStatement getDeserializeFunc();

    boolean CompareMatchOrNot();

}
