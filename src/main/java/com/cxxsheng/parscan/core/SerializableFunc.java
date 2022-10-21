package com.cxxsheng.parscan.core;


import com.cxxsheng.parscan.core.data.FunctionImp;

//core class to collect parcelable function
public interface SerializableFunc {

    FunctionImp getSerializeFunc();

    FunctionImp getDeserializeFunc();

    boolean CompareMatchOrNot();

}
