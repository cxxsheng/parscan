package com.cxxsheng.parscan.core.iterator;

import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.List;

public class VariableTable {

   private HashMap<String, RuntimeValue> vs;
   public VariableTable(){
       vs = new HashMap<>();
   }

   public void addVariable(String name, RuntimeValue value){
       vs.put(name, value);
   }

   public RuntimeValue getVariableByName(String name){
       return vs.get(name);
   }
}
