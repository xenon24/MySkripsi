package com.appjombang.myskripsi.Grafik;

public class TampData {
    private String suhu;
    private String kelembaban;
    private String waktu;

    public TampData(String suhu, String kelembaban, String waktu) {
        this.suhu = suhu;
        this.kelembaban = kelembaban;
        this.waktu = waktu;
    }

    public String getSuhu() {
        return suhu;
    }

    public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    public String getKelembaban() {
        return kelembaban;
    }

    public void setKelembaban(String kelembaban) {
        this.kelembaban = kelembaban;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}
