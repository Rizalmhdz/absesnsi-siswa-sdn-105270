package com.mitravisual.absensisiswasdnegeri105270.Guru.Entity;

import java.util.ArrayList;

public class DataQrRekap {

    private String nama;
    private int no;
    private ArrayList<Integer> sakit, izin, alpha;

    public DataQrRekap(String nama, int no, ArrayList<Integer> sakit, ArrayList<Integer> izin, ArrayList<Integer> alpha) {
        this.nama = nama;
        this.no = no;
        this.sakit = sakit;
        this.izin = izin;
        this.alpha = alpha;
    }

    public DataQrRekap() {

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public ArrayList<Integer> getSakit() {
        return sakit;
    }

    public void setSakit(ArrayList<Integer> sakit) {
        this.sakit = sakit;
    }

    public ArrayList<Integer> getIzin() {
        return izin;
    }

    public void setIzin(ArrayList<Integer> izin) {
        this.izin = izin;
    }

    public ArrayList<Integer> getAlpha() {
        return alpha;
    }

    public void setAlpha(ArrayList<Integer> alpha) {
        this.alpha = alpha;
    }
}
