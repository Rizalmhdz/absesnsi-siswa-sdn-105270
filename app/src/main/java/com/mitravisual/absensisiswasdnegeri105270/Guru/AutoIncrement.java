package com.mitravisual.absensisiswasdnegeri105270.Guru;

public class AutoIncrement {
    private int no = 0;

    private int counter;

    public AutoIncrement() {
        counter = 0;
    }

    public int getCounter() {
        return counter;
    }

    public void increment() {
        counter++;
    }

    public int getNextNo(){
        no++;
        return no;
    }

    public int getTotal() {
        return no;
    }

    public void resetCounter() {
        no = 0;
    }
}
