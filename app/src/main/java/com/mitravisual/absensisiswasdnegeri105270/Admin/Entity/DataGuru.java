package com.mitravisual.absensisiswasdnegeri105270.Admin.Entity;

public class DataGuru {

    private String NIP, Nama, Password, Email, GuruKelas, terdafar;

    public DataGuru(String NIP, String nama, String password, String email, String guruKelas, String terdafar) {
        this.NIP = NIP;
        this.Nama = nama;
        this.Password = password;
        this.Email = email;
        this.GuruKelas = guruKelas;
        this.terdafar = terdafar;
    }

    public DataGuru() {

    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGuruKelas() {
        return GuruKelas;
    }

    public void setGuruKelas(String guruKelas) {
        GuruKelas = guruKelas;
    }

    public String getTerdafar() {
        return terdafar;
    }

    public void setTerdafar(String terdafar) {
        this.terdafar = terdafar;
    }
}
