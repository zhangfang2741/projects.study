package com.zhangfang.structure.hash;

public class Main{
    public static void main(String[] args){
        HashLinearDetection hs=new HashLinearDetection(17);
        hs.insert(1);
        hs.insert(20);
        hs.insert(7);
        hs.insert(8);
        hs.insert(23);
        hs.insert(21);
        hs.insert(3);
        hs.showHash();

    }
}