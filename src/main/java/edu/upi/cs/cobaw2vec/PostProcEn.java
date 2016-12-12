package edu.upi.cs.cobaw2vec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *  Created by yudiwbs on 17/11/2016.
 *
 *  harusnya tidak perlu kalau prepro sudah bagus :(
 *  Membersihkan berbagai bug yg tersisa
 *
 */
public class PostProcEn {
    //buang diantara <code>  </code>
    //buang diantara <gallery> File Lufthansa b737 500 d abjf arp jpg Boeing 737 500  Aalen  File ICE Aalen jpg ICE  Aalen  at Aalen station </gallery>
    //buang: valign  top      valign  top
    //valign  top       style  text align center
    //buang kurung kurawal  { dan }
    //buang REDIRECT


    //buang semua < bla bla > tapi hati-hati ada yg bukan tag spt misalnya <10 ppm (cek apakah angka)

    //&lt   &gt  &ndash


    public String namaDir;
    public String namaDirTujuan;

    private ArrayList<String> alTagDalamTdkBuang;

    public PostProcEn() {
        alTagDalamTdkBuang = new ArrayList<>();
        /*
        alTagDalamBuang.add("code");  //<code> </code> dst
        alTagDalamBuang.add("gallery");
        alTagDalamBuang.add("span");
        alTagDalamBuang.add("font");
        alTagDalamBuang.add("includeonly");
        alTagDalamBuang.add("noinclude");
        */
    }



    public void  prosesTagDibuang(Scanner sc,String kata) {
        //rekursif, secara default akan membuang semua isi tag kecuali
        //yang berada di alTagDalamTdkBuang
        boolean isStop=false;  //tag pembuka, karena di dlm tag bisa panjang < asdfasdfasdf asdfasdfasdf asdfsdf >
        boolean isStop2=false;
        String tag="";
        //tambah spasi biar gampang diparsing
        kata = kata.replaceAll("<","< ");
        kata = kata.replaceAll(">"," >");
        if (kata.contains("<")) {
            //ambil nama tagnya
            String kataTemp = kata.trim();
            String[] arrKata = kataTemp.split("<");
            //harusnya dapat dua
            if (arrKata.length>=2) {
                tag  = arrKata[1];
            } else {
                //harus maju satu
            }

            if (kata.contains(">")) {
                isStop = true;
            }
            //ada yg langsung selesai spt: <br />
            if (kata.contains("/>")) {
                isStop2 = true;
                isStop = true;
            }



            while (sc.hasNext() && !isStop) {   //kalau didalam <> banyak elemen
                String kata2 = sc.next();
                if (kata2.contains(">")) {
                    isStop = true;
                }
            }



            //loop untuk cari tag tutup
            while (sc.hasNext() && !isStop2) {
                String kata1 = sc.next();
                if (kata1.contains("</"+tag)) {
                    isStop2 = true;
                }
            }
        }
    }



    public void  oldProsesTagDibuang(Scanner sc,String kata) {
        //tidak digunakan
        //belum menangani tag dalam tag (perlu rekursif)
        //loop untuk semua tag

        for (String tag: alTagDalamTdkBuang) {
            boolean isStop = false;    //stop tag pertama
            boolean isStop2 = false;   //stop selesai tag

            //loop untuk semua tag
            if (kata.contains("<"+tag)) {
                if (kata.contains(">")) {
                    isStop = true;
                }
                //ada yg langsung selesai spt: <br />
                if (kata.contains("//>")) {
                    isStop2 = true;
                    isStop = true;
                }

                while (sc.hasNext() && !isStop) {   //kalau didalam <> banyak elemen
                    String kata2 = sc.next();
                    if (kata2.contains(">")) {
                        isStop = true;
                    }
                }

                //loop untuk cari tag tutup
                while (sc.hasNext() && !isStop2) {
                    String kata1 = sc.next();
                    if (kata1.contains("</"+tag)) {
                        isStop2 = true;
                    }
                }
            }
            if (isStop2) {
                break; // cukup proses satu
            }
            //</code>
            //<span id  Affirmed > Affirmed </span>
        }
    }

    /*

     */
    private void prosesFile(File f) {
        StringBuilder sb = new StringBuilder();
        //agar pencarian berd kata bisa dilakukan "saya mau makan", kalau cari "saya" bisa ketemu
        sb.append(" ");
        Scanner sc = null;
        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNext()) {
            String kata = sc.next();
            if (kata.contains("<")) {
                prosesTagDibuang(sc, kata);
                if (sc.hasNext()) kata = sc.next();  //lanjut
            }
            sb.append(kata);
            sb.append(" ");
        }

        //simpan ke file
        String fileTujuan = namaDirTujuan+f.getName();
        System.out.println(fileTujuan);
        try {
            PrintWriter pw = new PrintWriter(fileTujuan);
            pw.append(sb.toString().trim());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void proses() {

        File dir = new File(namaDir);
        LinkedList<File> files;

        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException("Please specify an existing directory");
        try {
            //loop semua file dalam direktori
            files = (LinkedList<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File file : files) {
                System.out.println("Proses file: " + file.getCanonicalPath());
                //proses file
                prosesFile(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        //input: E:\corpus\corpus_besar\coba-postproses
        //output: E:\corpus\corpus_besar\output-coba-postproses
        PostProcEn pp = new PostProcEn();
        //pp.namaDir = "E:\\corpus\\corpus_besar\\coba-postproses";
        //harus ada backslash diujung
        pp.namaDir = "C:\\yudiwbs\\eksperimen\\enwiki-vocabtest-multifile-dgntitik-bag2";
        pp.namaDirTujuan = "C:\\yudiwbs\\eksperimen\\enwiki-vocabtest-multifile-dgntitik-postpro-bag2\\";
        pp.proses();
        //String coba  = "< font >";
        //String[] arrKata = coba.split("<");
        //System.out.println(arrKata[1]);
    }
}
