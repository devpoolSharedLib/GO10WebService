package th.co.gosoft.go10.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static List<String> parseStringToList(String string) {
        List<String> result;
        if ("all".equals(string)) {
            result = new ArrayList<>(); 
            result.add("all");
        } else {
            result = Arrays.asList(string.split("\\s*,\\s*"));
        }
        return result;
    }
    
    public static String parseListToString(List<String> list) {
        String result = "[";
        String delimiter = "";
        for (String string : list) {
            result += delimiter;
            result += "\""+string+"\"";
            delimiter = ", ";
        }
        result += "]";
        return result;
    }

}
