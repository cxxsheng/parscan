package com.cxxsheng.parscan.core;



//core class to collect parcelable function
public interface SerializableFunc {

    FunctionImp getSerializeFunc();

    FunctionImp getDeserializeFunc();

    boolean CompareMatchOrNot();

}
