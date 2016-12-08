package edu.upi.cs.cobaw2vec;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *   Created by yudiwbs on 02/11/2016.
 *   proses XML, data disimpan di PageWiki
 *
 *   Dipanggil oleh XMLtofile
 */


public class HandlerSaxWikiEntailment extends HandlerSaxWiki {

    public String fileDataTest   = "";
    public String fileStopWords  = "";
    private ArrayList<String> alDataTest = new ArrayList<>();
    private ArrayList<String> alStopWords = new ArrayList<>();
    private PreproEn pp = new  PreproEn();
    private int jumketemu = 0;
    private int jumTolak = 0;

    public void init() {
        jumketemu = 0;
        jumTolak = 0;
        //load data test entailment
        Scanner s = null;
        try {
            //ambil stopwords
            s = new Scanner(new File(fileStopWords));
            while (s.hasNext()){
                String kata = s.next();
                alStopWords.add(kata);
            }
            s.close();

            //ambil kata di data test
            s = new Scanner(new File(fileDataTest));
            while (s.hasNext()){
                String kata = s.next();
                kata = kata.replaceAll("[“”\",;()\\?:\\*–\\[\\]\\-\\#]", " ");
                //jangan buang titik!! sentence berguna
                //kata = kata.replaceAll("\\.(?!\\d)"," "); //buang titik tapi yang tidak diikuti oleh digit (angka biarkan)
                kata = kata.trim();
                //buang stopwords
                if (kata.length()<=1) {continue;}
                if (!alStopWords.contains(kata.toLowerCase())) {
                    if (!alDataTest.contains(kata)) {  //buang duplikasi
                        alDataTest.add(" "+kata+" "); //tambah spasi supaya pencarian berdasarkan kata (whole word)
                    }
                }
            }
            s.close();

            //for (String sTest:alDataTest) {
                //System.out.println(sTest);
               // if (sTest.trim().equals("is")) {
                //    System.out.println("ada is !!!!");
                //}
            //}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("page")) {

            //loop untuk semua kata di data test
            //harusnya page sudah terisi
            //hanya jika kata di datatest ada maka akan masuk
            String artikel = page.getPreproText(pp);
            boolean isFound =false;
            for (String sTest:alDataTest) {
                if (artikel.contains(sTest)) {
                    //debug
                    System.out.println("ketemu:"+sTest+" p.id:"+page.getId()+ " p.title:"+page.getTitle());
                    isFound = true; //ada
                    jumketemu++;
                    break;
                }
            }
            if (isFound) {
                cc++;
                alPageWiki.add(page);
            } else {
                jumTolak++;
                page = null; //hapus
            }
            //tambah untuk stop
            if (cc>=maxPageDebug &&  maxPageDebug>0) {
                //stop setelah mencapai batasan terentu , untuk debug
                throw new MySAXTerminatorException();
            }

            //setelah mencapai besaran tertentu, untuk menghemat memori, maka disimpan ke file
            // dan lalu objeknya dihapus
            if ( (cc>0) &&  ( (cc % maxPageTulis) == 0 ))  {
                System.out.println("Jum ketemu:"+jumketemu);
                System.out.println("Jum tdk ketemu:"+jumTolak);
                System.out.println("=====> tulis ke file, clear memori");
                xmltofile.tulisKeFile();
            }
        } else
        if (qName.equalsIgnoreCase("title")) {
            page.setTitle(sbTitle.toString());
            bTitle = false;
        } else
        if (qName.equalsIgnoreCase("text")) {
            page.setText(sbText.toString());
            bText = false;
        }
    }
}
