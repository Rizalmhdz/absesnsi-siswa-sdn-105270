package com.mitravisual.absensisiswasdnegeri105270.Guru.Entity;

public class DataQr {

    private String code, nama, kelas, tanggal, jam, keterangan;
    private int no;

    public DataQr(int no,String code, String nama, String kelas, String tanggal, String jam, String keterangan) {
        this.no = no;
        this.code = code;
        this.nama = nama;
        this.kelas = kelas;
        this.tanggal = tanggal;
        this.jam = jam;
        this.keterangan = keterangan;
    }

    public DataQr() {

    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
