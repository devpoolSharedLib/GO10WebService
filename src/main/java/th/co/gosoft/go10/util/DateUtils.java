package th.co.gosoft.go10.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import th.co.gosoft.go10.model.LastTopicModel;

public class DateUtils {

    private static DateFormat dbFormat = createSimpleDateFormat("yyyy/MM/dd HH:mm:ss", "GMT+7");
    private static DateFormat clientFormat = createSimpleDateFormat("dd/MM/yyyy HH:mm:ss", "GMT+7");
    
    public static List<LastTopicModel> formatDBDateToClientDate(List<LastTopicModel> lastTopicModelList) {
        List<LastTopicModel> resultList = new ArrayList<LastTopicModel>();
        for (LastTopicModel lastTopicModel : lastTopicModelList) {
            LastTopicModel resultModel = lastTopicModel;
            resultModel.setDate(clientFormat.format(parseStringToDate(lastTopicModel.getDate())));
            resultList.add(resultModel);
        }
        return resultList;
    }
    
    private static Date parseStringToDate(String dateString){
        try {
            return dbFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private static DateFormat createSimpleDateFormat(String formatString, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat;
    }
}
