package pumpkinbox.time;

/**
 * Created by ramiawar on 4/1/17.
 */

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Interval;

import java.time.format.DateTimeFormatter;


/**
 * @author Rami Awar
 * This Time class is used to get current date and time in an easy way, and operate on dateTime objects easily.
 */
public class Time {

    /**
     * Function simply returns current timestamp in default joda.time format.
     * @return
     */
    public static String getTimeStamp(){

        DateTime dateTime = new DateTime();
//
//        int month = dateTime.getMonthOfYear();
//        int year = dateTime.getYear();
//
//        Instant x = new Instant();

        return dateTime.toString();
    }

    /**
     * Function returns a prettied version of the timestamp, in a string formed with D, M D xxxx at xx:xx
     * @return
     */
    public static String prettyTimeStamp(){
        DateTime dateTime = new DateTime();
        String month = dateTime.monthOfYear().getAsText();
        String day = dateTime.dayOfWeek().getAsText();
        int digday = dateTime.getDayOfMonth();
        int year = dateTime.getYear();
        String hour = dateTime.hourOfDay().getAsText();
        String minute = dateTime.minuteOfHour().getAsText();

        return day + ", " + month + " " + Integer.toString(digday) + " " + Integer.toString(year) + " at " + hour + ":" + minute;

    }

    /**
     * Function returns current timestamp plus 1 hour added to current time.
     * @return
     */
    public static String getTimeStampPlus(){
        DateTime dateTime = new DateTime();
        return dateTime.plusHours(1).toString();
    }

    /**
     * Function checks if a timestamp is expired or still in the future.
     * @param timeString
     * @return
     */
    public static boolean checkTimestamp(String timeString){

        DateTime dateTimeNow = new DateTime();
        DateTime dateTimeExp = new DateTime(timeString);

        return dateTimeExp.compareTo(dateTimeNow) >= 0;
    }

    //TODO: Make a test class
//    public static void main(String[] args) {
//
//        DateTime time = new DateTime().plusHours(2);
//
//        System.out.println(Integer.parseInt(time.hourOfDay().getAsText())%12);
//        System.out.println(time.getHourOfDay());
//        System.out.println(time.toDate());
//    }
//


    /**
     * Function returns duration of interval between two instants.
     * @param x1
     * @param x2
     * @return
     */
    public static String getDuration(Instant x1, Instant x2){
        return (new Interval(x1, x2)).toDuration().toString();
    }

    /**
     * Function returns duration of interval formed by one instant and current time (current instant)).
     * @param x
     * @return
     */
    public static String getDurationNow(Instant x){
        return (new Interval(x, new Instant())).toDuration().toString();
    }




}
