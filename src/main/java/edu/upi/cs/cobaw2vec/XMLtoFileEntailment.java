package edu.upi.cs.cobaw2vec;

import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 *   Created by yudiwbs on 02/11/2016.
 *
 *   sebenarnya tidak perlu jadi class baru...
 *   lihat xmlToFile, ini utk coba saja,
 *
 *
 */

public class XMLtoFileEntailment extends  XMLtoFile {


    public static void main(String[] args) {
        XMLtoFileEntailment PX  = new XMLtoFileEntailment();
        PX.preprocess = new PreproEn();
        //PX.maxArtikelDebug = 500;
        HandlerSaxWikiEntailment h = new HandlerSaxWikiEntailment();
        h.fileDataTest = "E:\\corpus\\corpus_besar\\data_test_gold_rte3.txt";
        h.fileStopWords = "E:\\corpus\\corpus_besar\\en_stopwords.txt";
        h.init();
        PX.handler = h;
        PX.namaFileXML = "E:\\corpus\\corpus_besar\\enwiki-20161101-pages-articles-multistream.xml\\enwiki-20161101-pages-articles-multistream.xml";
        PX.dirOut      = "E:\\corpus\\corpus_besar\\enwiki-multifile-debug";
        PX.jumPagePerFile = 5000;  //jumlah artikel per file
        PX.initFile();
        PX.proses();
    }
}
