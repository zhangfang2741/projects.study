package com.zhangfang.structure.hash;

/**
 * 线性探测
 */
public class HashLinearDetection {
    public int[] hArray;
    public int s;

    public HashLinearDetection(int size) {
        hArray = new int[size];
        s = size;
        for (int i = 0; i < s; i++) {
            hArray[i] = 0;
        }
    }

    public void insert(int n) {
        int index = n % s;
        while (hArray[index] != -1 && hArray[index] != 0) {
            index = (index + 1) % s;
        }
        hArray[index] = n;//这里会有问题，如果没有空间了，则原来的值会被替换掉。
    }

    public int find(int key) {
        int i = key % s;//先寻址
        while (true) {
            if (hArray[i] == key)//通过key来解决hash碰撞后的数据对比问题
                return i;
            else if (hArray[i] == 0)
                return -1;
            else
                i = (i + 1) % s;//寻找下一个位置
        }
    }

    public boolean delete(int key) {
        int i = key % s;
        while (true) {
            if (hArray[i] == key) {
                hArray[i] = -1;
                return true;
            } else if (hArray[i] == 0)
                return false;
            else
                i = (i + 1) % s;
        }
    }

    public void showHash() {
        for (int i = 0; i < s; i++) {
            if (hArray[i] == 0 || hArray[i] == -1)
                continue;
            else
                System.out.print(hArray[i] + " ");
        }
    }
}
