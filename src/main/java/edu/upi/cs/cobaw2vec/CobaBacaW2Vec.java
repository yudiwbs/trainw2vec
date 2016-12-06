package edu.upi.cs.cobaw2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by yudiwbs on 01/11/2016.
 */
public class CobaBacaW2Vec {
    public static void main(String[] args) {
        Word2Vec vec = null;
        try {
            //File f = new File("D:\\corpus\\wordvec_cobavec2.txt");
            //File f = new File("D:\\corpus\\wordvec_cobavec_multifile.txt");
            //File f = new File("E:\\corpus\\wiki-indo-multifile\\wordvec.txt");
            //File f = new File("E:\\corpus\\wiki-indo-multifile\\wordvec_sedang.txt");
            File f = new File("E:\\corpus\\wiki-indo-multifile\\word2vec_wiki_id");
            vec = WordVectorSerializer.readWord2Vec(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collection<String> lst = vec.wordsNearest("jakarta", 5);
        System.out.println(lst);

        lst = vec.wordsNearest("bandung",5);
        System.out.println(lst);

        lst = vec.wordsNearest("presiden",5);
        System.out.println(lst);

        lst = vec.wordsNearest("bakso",5);
        System.out.println(lst);


        Collection<String> relList;
        //Collection<String> kingList = vec.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 10);
        relList = vec.wordsNearest(Arrays.asList("raja", "perempuan"), Arrays.asList("ratu"), 10);
        relList = vec.wordsNearest(Arrays.asList("raja", "perempuan"), Arrays.asList("ratu"), 10);
        System.out.println(relList);

        relList = vec.wordsNearest(Arrays.asList("jakarta", "thailand"), Arrays.asList("bangkok"), 10);
        System.out.println(relList);

        relList = vec.wordsNearest(Arrays.asList("makan", "air"), Arrays.asList("minum"), 10);
        System.out.println(relList);

        //Collection<String> kingList = vec.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 10);
    }
}
