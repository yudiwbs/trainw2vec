package edu.upi.cs.cobaw2vec;

import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by yudiwbs on 13/11/2016.
 */
public class TrainDirW2Vec {
    //String path = "E:\\corpus\\wiki-indo-multifile\\test_kecil\\";
    //String path = "E:\\corpus\\wiki-indo-multifile\\semua";
    //String fileOut = "E:\\corpus\\wiki-indo-multifile\\word2vec_full.txt";

    String path = "C:\\yudiwbs\\eksperimen\\enwiki-vocabtest-multifile-dgntitik-postpro-all";
    String fileOut = "C:\\yudiwbs\\eksperimen\\word2vec_allvocab_wikien_minword5_layer100_windowsize10.txt";

    //argh, gagal training wikipedia semua vocab!!! habis memori
    //String path = "C:\\yudiwbs\\eksperimen\\enwiki-vocaball-multifile-dgntitik-postpro-all";
    //String fileOut = "C:\\yudiwbs\\eksperimen\\word2vec_allvocab_wikien_minword5_layer100_windowsize10.txt";


    public int tipe = 0; //0 skip gram, 1: cbow default adalah skiprgram


    public void proses() {
        long startTime = System.nanoTime();


        //yang penting itu class MultiFileLineSentenceIterator
        SentenceIterator iter = new MultiFileLineSentenceIterator(new File(path));
        iter.setPreProcessor(new SentencePreProcessor() {
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        Word2Vec vec;
        if (tipe==0) { //skipgram
            System.out.println("Skip Gram");
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(10)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();
        } else {  //cbow
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .elementsLearningAlgorithm(new CBOW())
                    .build();
        }

        vec.fit();
        //error utk full
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\wordvec_cobavec.txt");
        WordVectorSerializer.writeWord2Vec(vec,fileOut);
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\cobavec.txt");

        //Word2Vec word2Vec = WordVectorSerializer.loadFullModel("pathToSaveModel.txt");

        long endTime = System.nanoTime();
        double duration = (double) (endTime - startTime)/1000000000;  //divide by 1000000 to get milliseconds.

        duration = (double) duration / 60 ; //menit
        System.out.println("Waktu (menit):"+duration);


        Collection<String> lst = vec.wordsNearest("Bush", 10);
        System.out.println(lst);


    }

    public static void main(String[] args) {
        TrainDirW2Vec tdw = new TrainDirW2Vec();
        tdw.tipe = 1; //cbow
        tdw.proses();
    }
}
