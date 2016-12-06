package edu.upi.cs.cobaw2vec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yudiwbs on 01/11/2016.
 *
 * memproses multi file
 *
 */

public class MultiFileLineSentenceIterator extends BaseSentenceIterator {

    private InputStream file;
    private LineIterator iter;
    private File f;
    private LinkedList<File> files;
    private int posFile=0;

    //return null kalau sudah habis
    private File getNextFile() {
        File f = null;
        if (posFile<files.size())  {
            f = files.get(posFile);
            posFile++;
        }

        return f;
    }

    public MultiFileLineSentenceIterator(File dir) {
        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException("Please specify an existing directory");
        try {

            files = (LinkedList<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            //for (File file : files) {
            //        System.out.println("file: " + file.getCanonicalPath());
            //}
            this.f = getNextFile();
            System.out.println("Proses file:"+this.f.getName()); //file pertama
            this.file = new BufferedInputStream(new FileInputStream(f));
            iter = IOUtils.lineIterator(this.file,"UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String nextSentence() {
        String line = iter.nextLine();
        if (preProcessor != null) {
            line = preProcessor.preProcess(line);
        }
        //System.out.println(line);
        return line;
    }

    //next sentence
    @Override
    public boolean hasNext() {
        boolean ret=false;
        if (!iter.hasNext()) {   //habis satu file, ambil file berikutnya
            this.f = getNextFile();
            if (this.f==null) {
                ret = false;  //habis
            } else {
                System.out.println("Proses file:"+this.f.getName());  //file kedua dan berikutnya
                try {
                    this.file = new BufferedInputStream(new FileInputStream(f));
                    iter = IOUtils.lineIterator(this.file, "UTF-8");
                    ret =  iter.hasNext();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ret = true;
        }
        return ret;
    }

    @Override
    public void reset() {
        System.out.println("=========================> reset");
        try {
            if(file != null)
                file.close();
            if(iter != null)
                iter.close();
            posFile=0; //reset
            this.f =  getNextFile();
            System.out.println("Proses file:"+this.f.getName());
            this.file = new BufferedInputStream(new FileInputStream(f));
            iter = IOUtils.lineIterator(this.file,"UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
