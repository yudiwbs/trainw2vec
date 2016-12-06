package edu.upi.cs.cobaw2vec;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 *   Created by yudiwbs on 02/11/2016.
 *   proses XML, data disimpan di PageWiki
 *
 *   Dipanggil oleh XMLtofile
 */


public class HandlerSaxWiki extends DefaultHandler {

    public XMLtoFile xmltofile = null; //dipanggil untuk menulis ke file dalam periode tertentu (masalah memori)
    //public XMLtoFileEntailment xmltofile = null; //dipanggil untuk menulis ke file dalam periode tertentu (masalah memori)
    public ArrayList<PageWiki> alPageWiki = new ArrayList<PageWiki>();
    public int maxPageDebug = 0;
    // kondisi stop jika mencapai maxpage, cocok
    // untuk debug. kalau diisi==0 artinya tidak dibatasi, tidak akan stop sampe

    protected int maxPageTulis = 100000; //jika mencapai ini, penulisan ke file dipanggil
    protected PageWiki page;
    protected boolean bId;
    protected boolean bText;
    protected boolean bTitle;
    protected int cc=0;
    protected StringBuilder sbText;
    protected StringBuilder sbTitle;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("page")) {
            page = new PageWiki();
        } else if (qName.equalsIgnoreCase("id")) {
            bId   = true;
        } else if (qName.equalsIgnoreCase("text")) {
            bText = true;
            sbText = new StringBuilder();
        } else if (qName.equalsIgnoreCase("title")) {
            bTitle = true;
            sbTitle = new StringBuilder();
    }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("page")) {
            //perlu ada mekanisme kalau putus ditengah jalan
            cc++;
            alPageWiki.add(page);

            //tambah untuk stop
            if (cc>=maxPageDebug &&  maxPageDebug>0) {
                //stop setelah mencapai batasan terentu , untuk debug
                throw new MySAXTerminatorException();
            }

            //setelah mencapai besaran tertentu, untuk menghemat memori, maka disimpan ke file
            // dan lalu objeknya dihapus

            if ( (cc>0) &&  ( (cc % maxPageTulis) == 0 ))  {
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

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (bId) {
            page.setId(Integer.parseInt(new String(ch, start, length)));
            bId = false;
        } else if (bText) {
            sbText.append(new String(ch, start, length));
        } else if (bTitle) {
            sbTitle.append(new String(ch, start, length));
        }
    }
}
