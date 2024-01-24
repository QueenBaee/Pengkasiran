/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Robby
 */
public class Kasir {
    String kodeBarang,namaBarang;
    int jumlahBarang,Harga,Total,dibayar,kembalian;
    int Harga(){
        return Harga;
    }
    int Jumlah(){
        return jumlahBarang;
    }
    int Total(){
        return Harga * jumlahBarang;
    }
    int dibayar(){
        return dibayar;
    }
    int kembalian(){
        return dibayar - Total;
    }
    int dataKembalian(){
        return kembalian();
    }
    void dataKodeBarang(String kodeBarang){
        this.kodeBarang = kodeBarang;
    }
    void namaBarang(String namaBarang){
        this.namaBarang = namaBarang;
    }
    int cetakHarga(){
        return Harga;
    }
    int cetakJumlahBarang(){
        return jumlahBarang;
    }
    int cetakBayar(){
        return dibayar;
    }
   String cetakNamaBarang(){
      return namaBarang;
   }
   String cetakKodeBarang(){
       return kodeBarang;
   }
}