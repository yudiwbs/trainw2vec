package edu.upi.cs.cobaw2vec;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yudiwbs on 15/11/2016.
 * untuk wiki bhs Inggris
 *
 * todo bug:

 todo: HATI-HATI JANGAN MEMBUANG TANDA TITIK pemisah kalimat!!
 todo: Banyak URL yang masih belum bisa dibuang !


 semua yang berada di <> harus dibuang
 coba lihat daftar bug di PostProcEn

 koma untuk angka jangan dibuang 1,000 = seribu
 atau dijadikan 1000 (jangan terpisah)

 <span style  font family Arial color  2F4D92 >
 <font color  red >

 *
 *
 */
public class PreproEn extends  Prepro {

    /*
        menghasilkkan string yg diprepro

     */

    public String proses(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(" "); //agar pencarian berd kata bisa dilakukan "saya mau makan", kalau cari "saya" bisa ketemu


        boolean isBerkas=false;


        //lebih simpel
        s = s.replaceAll("http.*?\\s", "");

        /*
        //buang url
        //pake regex malah banyak yg kelewat
        String urlPattern  = "(((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?)";
        Pattern pUrl = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher mUrl = pUrl.matcher(s);
        while (mUrl.find()) {
            try {
                String sU = mUrl.group();
                s = s.replaceAll(sU, "");
            } catch(Exception ex) {
                //System.out.println("); skip aja kalau ada yg error
            }
        }
        */

        //tambah spasi biar parsingnya gampang
        s = s.replaceAll("<", " <");
        s = s.replaceAll(">", "> ");
        s = s.replaceAll("\\[\\[", " [[");
        s = s.replaceAll("\\]\\]", "]] ");
        s = s.replaceAll("\\{\\{", " {{");
        s = s.replaceAll("\\}\\}", "}} ");
        s = s.replaceAll("&nbsp", " ");
        //System.out.println(s);

        Scanner sc = new Scanner(s);
        while (sc.hasNext()) {
            String kata = sc.next();
            //todo: dibuat generik
            if (kata.contains("<div")) {
                boolean isStop = false;
                if (kata.contains(">")) {
                    isStop = true;
                }
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains(">")) {
                        isStop = true;
                    }
                }
            }
            else
            if (kata.contains("{|")) {
                boolean isStop = false;
                if (kata.contains("}}")) {  //jika satu kata mengandung buka dan tutup {{x}}
                    isStop = true;
                }
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("|}")) {
                        isStop = true;
                    }
                }
            }
            else
            if (kata.contains("<!--")) {
                boolean isStop = false;
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("-->"))  {
                        isStop = true;
                    }
                }
            } else
            if (kata.contains("<br")) {
                boolean isStop = false;
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("/>"))  {
                        isStop = true;
                    }
                }
            } else
            if (kata.contains("<sub")) {
                boolean isStop = false;
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("</sub>"))  {
                        isStop = true;
                    }
                }
            }
            else
            if (kata.contains("<sup>")) {
                boolean isStop = false;
                if (kata.contains("</sup>")) {
                    isStop = true;
                }
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("</sup>"))  {
                        isStop = true;
                    }
                }
            }
            else
            if (kata.contains("{{")) {
                boolean isStop = false;
                if (kata.contains("}}"))  {  //jika satu kata mengandung buka dan tutup {{x}}
                    isStop = true;
                }

                int jumKurDobelBuka = 1;
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("{{"))
                        jumKurDobelBuka++;  //karena bisa didalam siku ada  siku lagi
                    if (kata2.contains("}}")) {  //jangan pake else karena dalam satu kata bisa mengandung [[ dan ]]
                        jumKurDobelBuka--;
                        if (jumKurDobelBuka== 0) {
                            isStop = true;
                        }
                    }
                /*
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("}}"))  {
                        isStop = true;
                    }
                 */
                }
            } else
            if (kata.contains("<ref")) { //buang semua yang berada di antara ref
                boolean isStop = false;
                if (kata.contains("</ref")) {
                    isStop = true;
                }
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("</ref")) {
                        isStop = true;
                    }
                }
            }
            else
            if (kata.contains("<math")) { //buang semua yang berada di antara ref
                    boolean isStop = false;
                    if (kata.contains("</math"))  {
                        isStop = true;
                    }
                    while (sc.hasNext() && !isStop) {
                        String kata2 = sc.next();
                        if (kata2.contains("</math"))  {
                            isStop = true;
                        }
                    }
            } else
                //todo penanganan ada ref didalam berkas
                if (kata.contains("[[File:")) {
                    //buang sampai ketemu | yang terakhir
                    //[[Berkas:DNA Structure+Key+Labelled.pn NoBB.png|thumb|right|340px|Struktur [[heliks ganda]] DNA.
                    StringBuilder sb2 = new StringBuilder();
                    isBerkas = true;
                    //skip sampai ketemu ujung
                    int jumSikuBuka = 1;
                    boolean isStop = false;
                    boolean isStartAmbil = false;
                    while (sc.hasNext() && !isStop) {
                        String kata2 = sc.next();

                        //nanti dirapikan
                        if (kata2.contains("<ref")) { //buang semua yang berada di antara ref
                            boolean isStop2 = false;
                            if (kata2.contains("</ref")) {
                                isStop2 = true;
                            }
                            while (sc.hasNext() && !isStop) {
                                String kata3 = sc.next();
                                if (kata3.contains("</ref")) {
                                    isStop = true;
                                }
                            }
                        }


                        if (kata2.contains("[["))
                            jumSikuBuka++;  //karena bisa didalam siku ada  siku lagi
                        if (kata2.contains("]]")) {  //jangan pake else karena dalam satu kata bisa mengandung [[ dan ]]
                            jumSikuBuka--;
                        } else if (kata2.contains("|")) {
                            //NoBB.png|thumb|right|340px|Struktur
                            //ambil kata terakhir setelah | yaitu struktur
                            int pos = kata2.lastIndexOf("|");
                            kata2 = kata2.substring(pos+1);
                            isStartAmbil = true;
                        }
                        if (isStartAmbil) {
                            sb2.append(kata2);
                            sb2.append(" ");
                        }
                        if (jumSikuBuka == 0) {
                            isStop = true;
                            sb.append(sb2.toString());
                            sb.append(" ");
                        }
                    }
                }  //pemrosesan Berkas:  (mungkin nanti yg lain?)
                else {
                    sb.append(kata);
                    sb.append(" ");
                }
        }
        String out = sb.toString();

        //nanti buang stopwords?
        out = out.replaceAll("\\[\\[", " ");
        out = out.replaceAll("\\]\\]", " ");
        out = out.replaceAll("['''|''|==]", " ");
        //“acceptance  not cures ”
        out = out.replaceAll("[“”\",;()\\?:\\*–\\[\\]\\-\\#]", " ");

        //titik jangan dibuang!! sentence penting!
        //out = out.replaceAll("\\.(?!\\d)"," "); //buang titik tapi yang tidak diikuti oleh digit (angka biarkan)
        out = out.replaceAll("<br />|<br>|<br/>"," ");
        return out;
    }

    public static void main(String[] args) {
        //test
        //http://www.usgs.gov/state/state.asp
        String s = "kalo ini https https://www.eumetsat.ce ini url yg susah  http://www.eumetsat.int/Home/Main/Access_to_Data/Meteosat_Meteorological_Products/Product_List/SP_1125489019643?l=123  ini lebih gampapng https://www.usgs.gov/state/state.asp harusnya dibuang. Betul nggak ";
        PreproEn pp = new PreproEn();
        String out = pp.proses(s);
        System.out.println("hasil="+out);






    }
}
