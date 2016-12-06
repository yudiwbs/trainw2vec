package edu.upi.cs.cobaw2vec;

/**
 * Created by yudiwbs on 16/11/2016.
 */
public class Coba {
    public static void main(String[] args) {
        String out = "kadal hari ri ingg 23.24 pada hari. Statpi topa";
        //out = out.replaceAll("\\.(?!\\d)"," "); //buang titik tapi yang tidak diikuti oleh digit (angka biarkan)
        PreproEn pe = new PreproEn();
        out = pe.proses(out);
        if (out.contains(" kadald ")) {
            System.out.println("ada");
        } else
            System.out.println("tidak");
    }
}
