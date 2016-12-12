package edu.upi.cs.cobaw2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.util.Collection;

/**
 *    Created by yudiwbs on 12/9/2016.
 *    train
 *
 */



public class TrainDirGlove {
    String path    = "C:\\yudiwbs\\eksperimen\\enwiki-vocabtest-multifile-dgntitik-postpro-all";
    String fileOut = "C:\\yudiwbs\\eksperimen\\glove_enwiki-vocabtest_alpha075_learning01_" +
            "ep25_xmax100_batch1000_shuffle_symmetric_window5_layer200.txt";

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

        Glove glove = new Glove.Builder()
                .iterate(iter)
                .tokenizerFactory(t)
                .windowSize(10)
                .layerSize(200)

                .alpha(0.75)
                .learningRate(0.1)

                // number of epochs for training
                .epochs(25)

                // cutoff for weighting function
                .xMax(100)

                // training is done in batches taken from training corpus
                .batchSize(1000)

                // if set to true, batches will be shuffled before training
                .shuffle(true)

                // if set to true word pairs will be built in both directions, LTR and RTL
                .symmetric(true)
                .build();

        glove.fit();





        /*Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(3)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        */
        //error utk full
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\wordvec_cobavec.txt");
        //WordVectorSerializer.writeWord2Vec(vec,fileOut);
        WordVectorSerializer.writeWordVectors(glove,fileOut);
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\cobavec.txt");

        //Word2Vec word2Vec = WordVectorSerializer.loadFullModel("pathToSaveModel.txt");

        long endTime = System.nanoTime();
        double duration = (double) (endTime - startTime)/1000000000;  //divide by 1000000 to get milliseconds.

        duration = (double) duration / 60 ; //menit
        System.out.println("Waktu (menit):"+duration);


        //test
        Collection<String> lst = glove.wordsNearest("bush", 10);
        System.out.println(lst);


    }

    public static void main(String[] args) {
        TrainDirGlove tdg = new TrainDirGlove();
        tdg.proses();
    }

}
