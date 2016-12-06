package edu.upi.cs.cobaw2vec;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Created by yudiwbs on 04/11/2016.
 *
 *  preprocessing teks XML, misal membuang tag  HTML, ref dsb
 *  untuk wiki bhs Indonesia
 *
 *  bug:
 *  tag <math > </>
 *
 *  <ref> didalam [[berkas:
 *
 *  buang titik di akhir kalimat tapi bukan di angka
 *
 *  lihat preproEn saja
 */

public class PreproId extends  Prepro {

    public  String proses(String s) {
        StringBuilder sb = new StringBuilder();


        boolean isBerkas=false;

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
                if (kata.contains("</ref"))  {
                    isStop = true;
                }
                while (sc.hasNext() && !isStop) {
                    String kata2 = sc.next();
                    if (kata2.contains("</ref"))  {
                        isStop = true;
                    }
                }
            } else
            //todo penanganan ada ref didalam berkas
            if (kata.contains("[[Berkas:")) {
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

        //buang url
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pUrl = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher mUrl = pUrl.matcher(out);
        while (mUrl.find()) {
            try {
                String sU = mUrl.group();
                out = out.replaceAll(sU, "");
            } catch(Exception ex) {
                //System.out.println("); skip aja kalau ada yg error
            }
        }

        //buang karakter diluar alphanumerik, spasi dan tanda sambung
        Pattern pt = Pattern.compile("[^a-zA-Z0-9 \\-]");
        Matcher match= pt.matcher(out);
        while(match.find())
        {
            String sg= match.group();
            out=out.replaceAll("\\"+sg, " ");
        }
        //System.out.println(c);

        //nanti buang stopwords

        return out;
    }

    public static void main(String[] args) {
        //test
        String s = "[[Berkas:DNA Structure+Key+Labelled.pn NoBB.png|thumb|right|340px|Struktur [[heliks ganda]] DNA. [[Atom]]-atom pada struktur tersebut diwarnai sesuai dengan [[unsur kimia]]nya dan struktur detail dua pasangan basa ditunjukkan oleh gambar kanan bawah]]\n" +
                "[[Berkas:ADN animation.gif|thumb|Gambaran tiga dimensi DNA]]\n" +
                "'''Asam deoksiribonukleat''', lebih dikenal dengan singkatan '''DNA''' ([[bahasa Inggris]]: '''''d'''eoxyribo'''n'''ucleic '''a'''cid''), adalah sejenis biomolekul yang menyimpan dan menyandi instruksi-instruksi [[genetika]] setiap [[organisme]] dan banyak jenis [[virus]]. Instruksi-instruksi genetika ini berperan penting dalam pertumbuhan, perkembangan, dan fungsi organisme dan virus. DNA merupakan [[asam nukleat]]; bersamaan dengan [[protein]] dan [[karbohidrat]], asam nukleat adalah [[makromolekul]] esensial bagi seluruh [[makhluk hidup]] yang diketahui. Kebanyakan molekul DNA terdiri dari dua unting [[biopolimer]] yang berpilin satu sama lainnya membentuk [[heliks ganda]]. Dua unting DNA ini dikenal sebagai [[polinukleotida]] karena keduanya terdiri dari [[monomer|satuan]]-satuan molekul yang disebut [[nukleotida]]. Tiap-tiap nukleotida terdiri atas salah satu jenis [[basa nitrogen]] ([[guanina]] (G), [[adenina]] (A), [[timina]] (T), atau [[sitosina]] (C)), gula [[monosakarida]] yang disebut [[deoksiribosa]], dan gugus [[fosfat]]. Nukleotida-nukelotida ini kemudian tersambung dalam satu rantai [[ikatan kovalen]] antara gula satu nukleotida dengan fosfat nukelotida lainnya. Hasilnya adalah rantai punggung gula-fosfat yang berselang-seling. Menurut kaidah [[pasangan basa]] (A dengan T dan C dengan G), [[ikatan hidrogen]] mengikat basa-basa dari kedua unting polinukleotida membentuk DNA unting ganda\n" +
                "\n" +
                "Dua unting DNA bersifat anti-paralel, yang  berarti bahwa keduanya berpasangan secara berlawanan. Pada setiap gugus gula, terikat salah satu dari empat jenis nukleobasa. [[Urutan asam nukleat|Urutan-urutan]] empat nukleobasa di sepanjang rantai punggung DNA inilah yang menyimpan kode informasi biologis. Melalui proses biokimia yang disebut [[transkripsi]], unting DNA digunakan sebagai templat untuk membuat unting [[RNA]]. Unting RNA ini kemudian ditranslasikan untuk menentukan urutan [[asam amino]] protein yang dibangun.\n" +
                "\n" +
                "Struktur kimia DNA yang ada membuatnya sangat cocok untuk menyimpan [[informasi]] biologis setiap makhluk hidup. Rantai punggung DNA resisten terhadap pembelahan kimia, dan kedua-dua unting dalam struktur unting ganda DNA menyimpan informasi biologis yang sama. Karenanya, informasi biologis ini akan direplikasi ketika dua unting DNA dipisahkan. Sebagian besar DNA (lebih dari 98% pada manusia) bersifat non-kode, yang berarti bagian ini tidak berfungsi menyandikan protein.\n" +
                "\n" +
                "Dalam sel, DNA tersusun dalam [[kromosom]]. Semasa [[pembelahan sel]], kromosom-kromosom ini diduplikasi dalam proses yang disebut [[replikasi DNA]]. [[Organisme eukariotik]] ([[hewan]], [[tumbuhan]], [[fungi]], dan [[protista]]) menyimpan kebanyakan DNA-nya dalam [[inti sel]] dan sebagian kecil sisanya dalam [[organel]] seperti [[mitokondria]] ataupun [[kloroplas]].<ref>{{cite book|last = Russell|first = Peter|title = iGenetics|publisher = Benjamin Cummings|location = New York|year = 2001|isbn = 0-8053-4553-1 }}</ref> Sebaliknya [[organisme prokariotik]] ([[bakteri]] dan [[arkaea]]) menyimpan DNA-nya hanya dalam [[sitoplasma]]. Dalam kromosom, protein [[kromatin]] seperti [[histon]] berperan dalam penyusunan DNA menjadi struktur kompak. Struktur kompak inilah yang kemudian berinteraksi antara DNA dengan protein lainnya, sehingga membantu kontrol bagian-bagian DNA mana sajakah yang dapat ditranskripsikan.\n" +
                "\n" +
                "Para ilmuwan menggunakan DNA sebagai alat molekuler untuk menyingkap teori-teori dan hukum-hukum fisika, seperti misalnya [[teorema ergodik]] dan teori [[elastisitas]]. Sifat-sifat materi DNA yang khas membuatnya sangat menarik untuk diteliti bagi ilmuwan dan insinyur yang bekerja di bidang mikrofabrikasi dan nanofabrikasi material. Beberapa kemajuan di bidang material ini misalnya [[origami DNA]] dan material hibrida berbasi DNA.<ref>{{cite journal |author=Mashaghi A, Katan A |title=A physicist's view of DNA |journal=De Physicus|volume=24e |issue=3 |pages=59–61 |year=2013 | arxiv= 1311.2545v1 |bibcode=2013arXiv1311.2545M }}</ref>\n" +
                "\n" +
                "== Sifat-sifat DNA ==\n" +
                "[[Berkas:DNA chemical structure_id.svg|thumb|300px|Struktur kimia DNA; [[ikatan hidrogen]] ditunjukkan oleh garis putus-putus]]\n" +
                "DNA merupakan sebuah [[polimer]] yang terdiri dari satuan-satuan berulang yang disebut [[nukleotida]].<ref>{{cite book|last = Saenger|first = Wolfram|title = Principles of Nucleic Acid Structure|publisher = Springer-Verlag|location = New York|year = 1984|isbn = 0-387-90762-9 }}</ref><ref name=Alberts>{{cite book|last=Alberts|first=Bruce|author2=Johnson, Alexander|author3=Lewis, Julian|author4=Raff, Martin|author5=Roberts, Keith|author6=Walters, Peter|title=Molecular Biology of the Cell; Fourth Edition|publisher=Garland Science|year=2002|location=New York and London|isbn=0-8153-3218-1|oclc=145080076 48122761 57023651 69932405}}</ref><ref name=Butler>{{cite book|author=Butler, John M.|year=2001|title=Forensic DNA Typing|publisher= Elsevier|isbn=978-0-12-147951-0|oclc=223032110 45406517}} pp. 14–15.</ref> Tiap-tiap nukleotida terdiri dari tiga komponen utama, yakni [[gugus fungsional|gugus]] [[fosfat]], gula deoksiribosa, dan basa nitrogen ([[nukleobasa]])<ref>{{en}}{{cite web\n" +
                "| url          = http://www.ncbi.nlm.nih.gov/bookshelf/br.fcgi?book=mboc4&part=A2\n" +
                "| title        = All Cells Replicate Their Hereditary Information by Templated Polymerization\n" +
                "| accessdate   = 2010-03-19\n" +
                "| work         = Bruce Alberts, et al.\n" +
                "}}</ref>. Pada DNA, nukleobasa yang ditemukan adalah [[Adenina]] (A), [[Guanina]] (G), [[Sitosina]] (C) dan [[Timina]] (T). Nukleobasa yang terhubung dengan sebuah gugus gula disebut sebagai [[nukleosida]], dan nukleosida yang terhubung dengan satu atau lebih gugus fosfat disebut sebagai [[nukleotida]]. Polimer yang terdiri dari nukleotida yang saling terhubung menjadi satu rantai disebut sebagai [[polinukleotida]].<ref name=\"IUPAC\">[http://www.chem.qmul.ac.uk/iupac/misc/naabb.html Abbreviations and Symbols for Nucleic Acids, Polynucleotides and their Constituents] IUPAC-IUB Commission on Biochemical Nomenclature (CBN). Retrieved 3 January 2006.</ref> Sehingga DNA termasuk pula ke dalam polinukleotida.";
        PreproId pp = new PreproId();
        String out = pp.proses(s);
        System.out.println();
        System.out.println();
        System.out.println(out);
    }
}
