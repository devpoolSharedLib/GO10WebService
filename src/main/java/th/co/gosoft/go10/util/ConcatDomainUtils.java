package th.co.gosoft.go10.util;

import java.util.regex.Pattern;

public class ConcatDomainUtils {
    
    private static String domain = initialDomainImagePath();
    
    public static String deleteDomainImagePath(String content) {
        String result = "";
        if(content.contains(domain)){
            String[] parts = content.split(Pattern.quote(domain));
            for (String subString : parts) {
                result += subString;
            }
        } else {
            result = content;
        }
        
        return result;
    }
    
    public static String concatDomainImagePath(String content) {
        String result = content;
        String regex = "<img src=\"";
        if(result.contains(regex)){
            int fromIndex = 0;
            while(fromIndex<result.length() && fromIndex>=0){
                fromIndex = result.indexOf(regex, fromIndex);
                if(fromIndex != -1){
                    fromIndex = fromIndex + 10;
                    StringBuilder stringBuilder = new StringBuilder(result);
                    stringBuilder.insert(fromIndex, domain);
                    result = stringBuilder.toString();
                }
            }
        }
        
        return result;
    }
    
    private static String initialDomainImagePath(){
        return PropertiesUtils.getProperties("domain_image_path")+"/"+PropertiesUtils.getProperties("folder_name")+"/";
    }
    
}
