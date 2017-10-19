package radar.radar.Services;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kenneth on 17/10/17.
 */

public class TimeFormatService {
    // TODO locale
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static String parseTimeString(String timeString, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(timeString);
            System.out.println(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());

            Integer day = calendar.get(Calendar.DAY_OF_YEAR);
            Integer year = calendar.get(Calendar.YEAR);

            Calendar today = Calendar.getInstance();
            Integer todayDay = today.get(Calendar.DAY_OF_YEAR);
            Integer todayYear = today.get(Calendar.YEAR);

            boolean pastOneDayBefore = false;
            if (year < todayYear) {
                pastOneDayBefore = true;
            } else if (day < todayDay) {
                pastOneDayBefore = true;
            }

            if (pastOneDayBefore) {
                Integer dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                Integer month = calendar.get(Calendar.MONTH);
                int monthIndex = month - 1;

                return MONTHS[monthIndex] + " " + dayOfMonth;
            } else {
                Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
                Integer minute = calendar.get(Calendar.MINUTE);

                String hourString, minuteString;

                if (hour < 10) {
                    hourString = "0" + hour.toString();
                } else {
                    hourString = hour.toString();
                }

                if (minute < 10) {
                    minuteString = "0" + minute.toString();
                } else {
                    minuteString = minute.toString();
                }

                if (!DateFormat.is24HourFormat(context)) {
                    return hourString + ":" + minuteString;
                } else {
                    // set am or pm
                    int hourOfDay = hour;
                    if (hourOfDay >= 12) {
                        return hourString + ":" + minuteString + " PM";
                    } else {
                        return hourString + ":" + minuteString + " AM";
                    }

                }
            }




        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
