package edu.upi.cs.cobaw2vec;

/**
 *   Created by yudiwbs on 02/11/2016.
 *   Reprentasi satu artikel wiki
 *
 */

public class PageWiki {
        private int id;
        private String title;
        private String text;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public String getPreproText(Prepro pp) {
            return pp.proses(text);
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "id="+this.id + "\n" + "Title=" +  this.title +  "\n" +" Teks=" + this.text;
        }

}

