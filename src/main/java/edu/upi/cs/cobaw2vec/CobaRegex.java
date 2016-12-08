package edu.upi.cs.cobaw2vec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yudiwbs on 08/12/2016.
 */
public class CobaRegex {
    public static void main(String[] args) {
        String s = "ini url yg susah  http://www.eumetsat.int/Home/Main/Access_to_Data/Meteosat_Meteorological_Products/Product_List/SP_1125489019643?l=123  ini lebih gampapng https://www.usgs.gov/state/state.asp harusnya dibuang. Betul nggak ";
        String urlPattern  = "(((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?)";
        //String urlPattern = "\\b(https?|ftp|file)://[-A-Z0-9+&@#/%?=~_|!:,.;]*[-A-Z0-9+&@#/%=~_|]";
        //String urlPattern  =  "^((http[s]?|ftp):\\/)?\\/?([^:\\/\\s]+)((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(.*)?(#[\\w\\-]+)?$";


        Pattern pUrl = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher mUrl = pUrl.matcher(s);
        while (mUrl.find()) {
            try {
                String sU = mUrl.group();
                s = s.replaceAll(sU, "");
            } catch(Exception ex) {
                //System.out.println("); skip aja kalau ada yg error
            }
        }
        System.out.println(s);
    }
}
