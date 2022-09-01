package com.cxxsheng.parscan.core.parcelale;

import com.cxxsheng.parscan.antlr.exception.JavaScanException;
import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.SerializableFunc;
import com.cxxsheng.parscan.core.unit.Parameter;
import com.cxxsheng.parscan.core.FuncStatement;
import com.cxxsheng.parscan.core.unit.Symbol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParcelableFuncImp implements SerializableFunc {
    private FuncStatement serFunc = new FuncStatement();     //writeToParcel function
    private FuncStatement deserFuc = new FuncStatement();    //createFromParcel function

    //symbolList in this file
    private Map<String, Symbol> symbolMap = new HashMap<>();

    //packageName
    private  final String packageName;

    //class name
    private final String className;

    @Override
    public FuncStatement getSerializeFunc() {
        return serFunc;
    }

    @Override
    public FuncStatement getDeserializeFunc() {
        return deserFuc;
    }


    // use z3 to compare two above func statement
    @Override
    public boolean CompareMatchOrNot() {
        return false;
    }


    public ParcelableFuncImp(String packageName, String className){
        this.packageName = packageName;
        this.className = className;
    }


    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    // if two parcelable classes have the same package name and class name,
    // which means they are the same class.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParcelableFuncImp that = (ParcelableFuncImp) o;
        return Objects.equals(packageName, that.packageName) && Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className);
    }


    //init writeToParcel function
    public void initSerFunc(String retValueType, String name, List<Parameter> params, Coordinate c){
        serFunc.initFunctionDeclaration(retValueType, name, params, c);
    }

    //init createFromParcel function
    public void initDeSerFunc(String retValueType, String name, List<Parameter> params, Coordinate c){
        deserFuc.initFunctionDeclaration(retValueType, name, params, c);
    }

    //writeFromParcel's first param name. must be Parcel type
    public String getSerializeFuncKeyParamName(){
        Parameter p = serFunc.getFunDec().getParams().get(0);
        if (p==null || !"Parcel".equals(p.getType()))
            throw new JavaScanException("Invalid serialization function because of parameter at "+ serFunc.getPosition());
        return p.getName();
    }


    //createFromParcel's first param name. must be Parcel type
    public String getDeSerializeFuncKeyParamName(){
        Parameter p = serFunc.getFunDec().getParams().get(0);
        if (p==null || !"Parcel".equals(p.getType()))
            throw new JavaScanException("Invalid deserialization function because of parameter at "+ serFunc.getPosition());
        return p.getName();
    }

    public void addLocalSymbol(){

    }

}
