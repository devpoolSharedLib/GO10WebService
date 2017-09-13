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
        String divtag = "<source src=\"";
        
        if(result.contains(regex)){
            int fromIndex = 0;
            while(fromIndex<result.length() && fromIndex>=0){
                fromIndex = result.indexOf(regex, fromIndex);
                if(fromIndex != -1){
                    fromIndex = fromIndex + 10;
                    StringBuilder stringBuilder = new StringBuilder(result);
                    stringBuilder.insert(fromIndex, domain);
                    result = stringBuilder.toString();
                    System.out.println("Result : "+result);
                }
            }
        }
        System.out.println("Contain : "+result.contains(divtag));
        if(result.contains(divtag)){
        	int index = 0;
        	 while(index<result.length() && index>=0){
                 index = result.indexOf(divtag, index);
                 if(index != -1){
                     index = index + 13;
                     StringBuilder stringBuilder = new StringBuilder(result);
                     System.out.println("RResult : "+stringBuilder);
                     stringBuilder.insert(index, domain);
                     result = stringBuilder.toString();
                     System.out.println("RRResult : "+result);
                 }
             }
        }
        return result;
    }
    
    private static String initialDomainImagePath(){
        return PropertiesUtils.getProperties("domain_image_path")+"/"+PropertiesUtils.getProperties("folder_name")+"/";
    }
    
}
