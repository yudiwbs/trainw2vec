package edu.upi.cs.cobaw2vec;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by yudiwbs on 31/10/2016.
 */
public class CobaTrainW2Vec {


    public static void main(String[] args) throws Exception {
        String filePath = null;

        SentenceIterator iter = new LineSentenceIterator(new File("D:\\corpus\\coba.txt"));

        iter.setPreProcessor(new SentencePreProcessor() {
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });

        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        vec.fit();
        //error utk full
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\wordvec_cobavec.txt");
        WordVectorSerializer.writeWord2Vec(vec,"D:\\corpus\\wordvec_cobavec2.txt");
        //WordVectorSerializer.writeWordVectors(vec, "D:\\corpus\\cobavec.txt");

        //Word2Vec word2Vec = WordVectorSerializer.loadFullModel("pathToSaveModel.txt");

        Collection<String> lst = vec.wordsNearest("brimob", 10);
        System.out.println(lst);

    }
}
