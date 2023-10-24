package com.mitravisual.absensisiswasdnegeri105270.Admin.Entity;

public class DataSiswa {

    private String NISN, nama, kelas, alamat, key, url;

    public DataSiswa(String NISN, String nama, String kelas, String alamat, String url) {
        this.NISN = NISN;
        this.nama = nama;
        this.kelas = kelas;
        this.alamat = alamat;
        this.url = url;
    }

    public DataSiswa() {

    }

    public String getNISN() {
        return NISN;
    }

    public void setNISN(String NISN) {
        this.NISN = NISN;
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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
