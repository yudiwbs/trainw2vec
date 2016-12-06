package edu.upi.cs.cobaw2vec;

import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *   Created by yudiwbs on 02/11/2016.
 *
 *
 *   Proses XML  Wikipedia dan pindahkan menjadi file-file teks yang sudah bersih
 *   satu file teks terdiri atas beberapa artikel
 *
 *   Belum dirapikans
 *
 *
 */

public class XMLtoFile {

    public HandlerSaxWiki handler = null;

    public Prepro preprocess = null; //praproses xml, buang tag dst
    public int maxArtikelDebug=0; //untuk debug, kalau diisi >0 maka akan distop jika sudah mencapai jumlah tsb
    public String namaFileXML;
    public String dirOut; // direktori tempat file-file teks output diletakkan dan log
    public int jumPagePerFile = 1000;  //jumlah artikel per file

    protected File  fLog;
    protected PrintWriter pPage;
    protected PrintWriter pwLog;
    int posFile;

    protected void initFile() {
        posFile = 1;
        String namaFileLog  = dirOut+"\\log.txt";
        File  fLog = new File(namaFileLog);
        PrintWriter pPage = null;
        try {
            pwLog = new PrintWriter(fLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void closeFile() {
        pwLog.close();
        if (pPage!=null) {  pPage.close();}
        System.out.println("==============> Selesai!");
    }

    public void tulisKeFile() {
    //hanya tulis yang ada di data testing
        try {
            int cc = 0;      //menghitung jumlah artikel per file

            for (PageWiki p:handler.alPageWiki) {
                if ((cc % jumPagePerFile) == 0) {
                    if (pPage!=null) {  pPage.close();}
                    String namaFile = "\\wikipage_"+ StringUtils.leftPad(String.valueOf(posFile), 5, "0");
                    pPage = new PrintWriter(new File(dirOut+namaFile));  //buat file baru
                    pwLog.println("file="+namaFile);
                    posFile++;
                    System.out.println("Tulis ke:"+namaFile);
                    pwLog.flush();
                }
                pwLog.println("id= "+ p.getId());
                pwLog.println("title=" + p.getTitle());
                pPage.println(p.getTitle());
                pPage.println(p.getPreproText(preprocess));
                pPage.println();
                cc++;
                System.out.println(cc);
                //title=Elektron
                //id= 579906
                //DEBUG DEBUG!! komentari semua bagian IF jika sudah selesai

                //id= 1600053
                //title=Autism

                /*
                System.out.println(p.getId());
                System.out.println(p.getTitle());
                //id= 27015025
                //title=Abortion

                if ((p.getId()==27015025) && (p.getTitle().equals("Abortion")) ) { //debug
                    pPage.println("-----------------------------------------------");
                    pPage.println(p.getText());
                    pPage.println();
                    pPage.close();
                    closeFile();
                    System.out.println("ABORT DEBUG");
                    System.exit(-1); //abort
                }
                */

            }
            //kosongkan
            handler.alPageWiki.clear();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void proses () {
        try {
            String strNamaFile = namaFileXML;
            File inputFile = new File(strNamaFile);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            //saxParser.xmltofile = this;
            handler.maxPageDebug = maxArtikelDebug;
            handler.xmltofile = this;
            initFile();
            //saxparser juga bisa nulis file


            saxParser.parse(inputFile, handler);  //tulis ke file juga dipanggil disini


            tulisKeFile();  //bereskan sisa
            closeFile();
            /*
            for (PageWiki p:handler.alPageWiki) {
                System.out.println("-----");
                System.out.println(p);
            }
            */

        } catch (Exception e){
            if(e instanceof MySAXTerminatorException){
                //selesai karena dibatasi jumlah artikel (untuk debug)
                //diset di?
                System.out.println("Stop karena melebihi batasan artikel");
                tulisKeFile();
                closeFile();
            } else {
                System.out.println("Ada masalah");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        XMLtoFile PX  = new XMLtoFile();
        PX.handler =  new HandlerSaxWiki();
        PX.preprocess = new PreproEn();
        PX.maxArtikelDebug = 500;
        //PX.namaFileXML = "E:\\corpus\\idwiki-latest-pages-articles.xml\\idwiki-latest-pages-articles.xml";
        //PX.dirOut = "E:\\corpus\\wiki-indo-multifile";
        PX.namaFileXML = "E:\\corpus\\corpus_besar\\enwiki-20161101-pages-articles-multistream.xml\\enwiki-20161101-pages-articles-multistream.xml";
        PX.dirOut = "E:\\corpus\\corpus_besar\\enwiki-multifile-debug";
        PX.proses();
    }
}
