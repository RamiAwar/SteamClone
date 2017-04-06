package pumpkinbox.time;

/**
 * Created by ramiawar on 4/1/17.
 */

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.time.format.DateTimeFormatter;


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

    public static String prettyTimeStamp(){
        DateTime dateTime = new DateTime();
        String month = dateTime.monthOfYear().getAsText();
        String day = dateTime.dayOfWeek().getAsText();
        int year = dateTime.getYear();
        String hour = dateTime.hourOfDay().getAsText();
        String minute = dateTime.minuteOfHour().getAsText();

        return day + ", " + month + " " + day + " " + Integer.toString(year) + " at " + hour + ":" + minute;

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

        DateTime time = new DateTime().plusHours(2);

        System.out.println(Integer.parseInt(time.hourOfDay().getAsText())%12);
        System.out.println(time.getHourOfDay());
        System.out.println(time.toDate());
    }

    public static String getDuration(Instant x1, Instant x2){
        return (new Interval(x1, x2)).toDuration().toString();
    }

    public static String getDurationNow(Instant x){
        return (new Interval(x, new Instant())).toDuration().toString();
    }




}
