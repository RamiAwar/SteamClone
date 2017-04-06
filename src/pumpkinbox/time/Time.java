package pumpkinbox.time;

/**
 * Created by ramiawar on 4/1/17.
 */

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;


/**
 * Created by ramiawar on 3/31/17.
 */
public class Time {

    public static String getTimeStamp(){

        DateTime dateTime = new DateTime();
//
//        int month = dateTime.getMonthOfYear();
//        int year = dateTime.getYear();
//
//        Instant x = new Instant();

        return dateTime.toString();
    }

    public static String getTimeStampPlus(){
        DateTime dateTime = new DateTime();
        return dateTime.plusHours(1).toString();
    }

    public static boolean checkTimestamp(String timeString){

        DateTime dateTimeNow = new DateTime();
        DateTime dateTimeExp = new DateTime(timeString);

        return dateTimeExp.compareTo(dateTimeNow) >= 0;
    }

    public static void main(String[] args) {

        DateTime time = new DateTime().plusHours(4);

        System.out.println(checkTimestamp(time.toString()));
    }

    public static String getDuration(Instant x1, Instant x2){
        return (new Interval(x1, x2)).toDuration().toString();
    }

    public static String getDurationNow(Instant x){
        return (new Interval(x, new Instant())).toDuration().toString();
    }




}
