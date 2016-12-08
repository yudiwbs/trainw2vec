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

    private ArrayList<String> alTagDalamBuang;  //tag dan isi diantaranya buang
    private ArrayList<String> alBuangTagSaja;  //hanya tagnya saja

    public PostProcEn() {
        alTagDalamBuang = new ArrayList<>();
        alTagDalamBuang.add("body");
        alTagDalamBuang.add("html");
        alTagDalamBuang.add("nowiki");
        alTagDalamBuang.add("imagemap");
        alTagDalamBuang.add("pre");
        alTagDalamBuang.add("style");
        alTagDalamBuang.add("blockquote");
        alTagDalamBuang.add("script");
        alTagDalamBuang.add("timeline");
        alTagDalamBuang.add("ce");
        alTagDalamBuang.add("math");
        alTagDalamBuang.add("code");  //<code> </code> dst
        alTagDalamBuang.add("gallery");
        alTagDalamBuang.add("span");
        alTagDalamBuang.add("font");
        alTagDalamBuang.add("includeonly");
        alTagDalamBuang.add("onlyinclude");
        alTagDalamBuang.add("noinclude");
        alTagDalamBuang.add("hiero");
        alTagDalamBuang.add("score");

        alBuangTagSaja = new ArrayList<>();
        alBuangTagSaja.add("title");
        alBuangTagSaja.add("small");
        alBuangTagSaja.add("ol");
        alBuangTagSaja.add("li");
        alBuangTagSaja.add("il");
        alBuangTagSaja.add("ul");
        alBuangTagSaja.add("s");
        alBuangTagSaja.add("u");
        alBuangTagSaja.add("b");
        alBuangTagSaja.add("i");
        alBuangTagSaja.add("p");
        alBuangTagSaja.add("em");
        alBuangTagSaja.add("td");
        alBuangTagSaja.add("tr");
        alBuangTagSaja.add("hr");
        alBuangTagSaja.add("tt");
        alBuangTagSaja.add("big");
        alBuangTagSaja.add("center");
        alBuangTagSaja.add("br");
        alBuangTagSaja.add("strike");
        alBuangTagSaja.add("source");
        alBuangTagSaja.add("cite");
        alBuangTagSaja.add("syntaxhighlight");
        alBuangTagSaja.add("poem");
        alBuangTagSaja.add("var");
        alBuangTagSaja.add("sub");
        alBuangTagSaja.add("sup");
        alBuangTagSaja.add("ins");
        alBuangTagSaja.add("div");
        alBuangTagSaja.add("kbd");
        alBuangTagSaja.add("h");
        alBuangTagSaja.add("abbr");
    }



    public boolean   prosesTagDibuang(Scanner sc,String kata) {
        //rekursif, secara default akan membuang semua isi tag kecuali
        //yang berada di alTagDalamTdkBuang
        boolean isStop=false;  //tag pembuka, karena di dlm tag bisa panjang < asdfasdfasdf asdfasdfasdf asdfsdf >
        boolean isStop2=false;
        String tag="";
        StringBuilder sbBuang = new StringBuilder();


        //bersihkan end tag
        if (kata.contains("</")) {   //end tag, entah karena ada endtag tanpa awal atau kasus tag yg diabaikan
            kata = kata.replaceAll("<","< ");
            kata = kata.replaceAll(">"," >");

           // System.out.println("End tag:"+kata);

            //cuma satu </div>
            if (kata.contains(">")) {
                isStop = true;
            }

            //banyak </bla bla bla>  <-- eh tapi gak mungkin ya
            while (sc.hasNext() && !isStop) {   //kalau didalam <> banyak elemen
                String kata2 = sc.next();
                if (kata2.contains(">")) {
                    isStop = true;
                }
            }

        }
        else
        //ketemu tag, ini yg penting
        if ( kata.contains("<") ) {
            //tambah spasi biar gampang diparsing
            kata = kata.toLowerCase();
            kata = kata.replaceAll("<","< ");
            kata = kata.replaceAll(">"," >");
            kata = kata.replaceAll("[^a-z<> ]", "");
            //ambil nama tagnya
            String kataTemp = kata.trim();
            String[] arrKata = kataTemp.split(" ");
            //kedua berisi tag
            if (arrKata.length>=2) {
                tag  = arrKata[1].trim();
                if (!alTagDalamBuang.contains(tag) && !alBuangTagSaja.contains(tag)) { //tag tidak dikenal, tdk diproses
                    //System.out.println("tag tidak dikenal, abort:"+tag);
                    return false; //TIDAK DIPROSES KARENA TAG TIDAK DIKENAL.. KENAPA PAKE HURUF BEASAR?
                } else
                if (alBuangTagSaja.contains(tag)) { //tag dibuang tapi isinya tidak
                    isStop2 = true; //stop pembuangan isi
                }
            } else  {
                //tag kosong? < >
                return false;
            }

            if (kata.contains(">")) {
                isStop = true;
            }

            //ada yg langsung selesai spt: <br / >
            //bisa bahaya juga kalau aslinya ada spasi (jadi 2 spasi)
            if (kata.contains("/ >")) {
                isStop2 = true;
                isStop = true;
            }

            //int jarak=0;
            //StringBuilder sbTemp = new StringBuilder();  //DEBUG, nanti dimatikan
            //int batasJarak = 50;
            while (sc.hasNext() && !isStop) {   //kalau didalam <> banyak elemen
                String kata2 = sc.next();
                if (kata2.contains(">")) {
                    isStop = true;
                }
                //jarak++;
                //sbTemp.append(kata2);
                //sbTemp.append(" ");
            }
            /*
            if (jarak>batasJarak) {
                System.out.println("Jarak lebih dari batas, kemungkinan bukan tag");
                System.out.println("tag yg aneh:"+tag);
                System.out.println("yg dibuang di dalam <>:"+sbTemp.toString());
                isStop2 = true; //kelemahannya, ada kata yang terbuang
            }
            */


            //loop untuk cari tag tutup
            //100
            int jumBuang=0;  //pengaman kalau2 tdk ada penutup
            while (sc.hasNext() && !isStop2 && jumBuang<1000) {
                String kata1 = sc.next();
                if (kata1.toLowerCase().contains("</" + tag)) {
                    isStop2 = true;
                } else if (kata1.contains("<")) {
                    //tag dalam tag
                    //rekusif
                    //System.out.println("rekursif:"+kata1);
                    prosesTagDibuang(sc,kata1);
                } else {
                    sbBuang.append(kata1);
                    sbBuang.append(" ");
                    jumBuang++;
                }
            }
        }
        return true;
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
            boolean isDibuang = false;
            if (kata.contains("<") ) {
                isDibuang = prosesTagDibuang(sc, kata);
                //if (sc.hasNext()) kata = sc.next();  //lanjut
            }
            if (!isDibuang) {
                sb.append(kata);
                sb.append(" ");
                //System.out.print(kata+" ");
            }


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
        //pp.namaDirTujuan = "E:\\corpus\\corpus_besar\\output-coba-postproses\\" ;

        pp.namaDir = "E:\\corpus\\corpus_besar\\enwiki-multifile-katatrain4";
        pp.namaDirTujuan = "E:\\corpus\\corpus_besar\\enwiki-multifile-katatrain-bersih\\";

        pp.proses();
        /*
        String coba  = "<font>";
        coba = coba.replaceAll("<","< ");
        coba = coba.replaceAll(">"," >");
        String[] arrKata = coba.split(" ");
        System.out.println(arrKata[1]);*/
    }
}
