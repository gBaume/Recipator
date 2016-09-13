package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Blume Till on 09.09.2016.
 */
public class Utils {
    public static final String FINAL_OBJECT = "<Finish>";

    /**
     *
     * @param map
     * @param restrictions
     * @return
     */
    public static String selectRandom(HashMap<String, Double> map, String... restrictions){
        double random = Math.random();
        double tmp = 0.0;
        if(map == null || map.isEmpty())
            return null;
        for(Map.Entry<String, Double> entry : map.entrySet()){
            tmp += entry.getValue();
            if(random <= tmp)
                return entry.getKey();
        }
        return null;
    }


    public static String readFile(String filename){
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String s = null;
            while ((s = in.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }
}
