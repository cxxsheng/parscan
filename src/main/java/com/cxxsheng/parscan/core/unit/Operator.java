package com.cxxsheng.parscan.core.unit;

public enum Operator {
    NONE(""),
    //Arithmetic operator
    ADD("+"), SUB("-"),
    MUL("*"), DIV("/"),
    REM("%"),
    INC("++"), DEC("--"),
    //Relational operator
    GT(">"), GE(">="),
    LT("<"), LE("<="),
    EQ("=="), NE("!="),
    IO("instanceof"),
    //Logical operator
    AND("&"), OR("|"),
    XOR("^"), NOT("!"),
    AND2("&&"),OR2("||"),

    //Bit operator
    SHL("<<"), SHR(">>"),
    URS(">>>"),NEG("~"),

    //Ternary operator
    COL(":"),QUE("?"),

    //assign operator
    // bop=('=' | '+=' | '-=' | '*=' | '/=' | '&=' | '|=' | '^=' | '>>=' | '>>>=' | '<<=' | '%=')
    AS("="), AE("+="),
    PE("-="), ME("*="),
    DE("/="), ANDE("&="),
    ORE("|="), XOREQ("^="),
    SHLE("<<="), SHRE(">>="),
    URSE(">>>="), RE("%=");

  ;
    private String name;
    Operator(String name){
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public boolean isAssign(){
        return this.ordinal() >= AS.ordinal();
    }


    public static Operator
    nameOf(String name){
      for (Operator op : Operator.values()){
        if(op.getName().equals(name))
          return op;
      }
      return Operator.NONE;
    }

}

