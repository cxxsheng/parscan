package com.cxxsheng.parscan.core;

public class Utils {

    /**
     * @param num number string to be parsed
     * @param mode the radix to be used while parsing num
     * @return long value of the number string
     */
    public static long parseIntString(String num, int mode){
        num = num.replace("_","");
        if (num.endsWith("l")|| num.endsWith("L"))
            num = num.substring(0, num.length()-1);
        if (mode==10){
        }else if (mode==16){
            if (num.startsWith("0x")|| num.startsWith("0X"))
                num = num.substring(2);
        }else if (mode==8)
        {

        }else if (mode==2){
            if (num.startsWith("0b") || num.startsWith("0B"))
                num = num.substring(2);
        }else {
            throw new RuntimeException("??");//fixme
        }
        return Long.parseLong(num,mode);
    }


    //fixme neet to match other forms of float number (such as start with 0x)
    //so that may have some problem
    public static double parseFloatString(String num){
        if (num.startsWith("0x")|| num.startsWith("0X") || num.contains("_")){
            throw new RuntimeException("??");//fixme
        }
       return Double.parseDouble(num);
    }
}
