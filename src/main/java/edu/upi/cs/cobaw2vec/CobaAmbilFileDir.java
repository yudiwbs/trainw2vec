package edu.upi.cs.cobaw2vec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by yudiwbs on 01/11/2016.
 */
public class CobaAmbilFileDir {


    public static void main(String[] args) {

        File dir = new File("D:\\corpus\\testmultifile\\");

        try {
            System.out.println("Getting all files in " + dir.getCanonicalPath() + " including those in subdirectories");
            List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File file : files) {
                System.out.println("file: " + file.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
