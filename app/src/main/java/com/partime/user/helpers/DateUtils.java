package com.partime.user.helpers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class DateUtils {


    // convert and set time coming from server
   /* public static boolean isDaysAgo(String time, TextView tvDate, Context context) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date createdDate = sdf.parse(time);
            String currentTime = sdf.format(new Date(System.currentTimeMillis()));
            Date currentDate = sdf.parse(currentTime);
            Calendar createdCalendar = Calendar.getInstance();
            createdCalendar.setTime(createdDate);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);
            long createdMilisecond = createdCalendar.getTimeInMillis();
            long currentMiliSecond = currentCalendar.getTimeInMillis();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(currentMiliSecond - createdMilisecond);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(currentMiliSecond - createdMilisecond);
            long hours = TimeUnit.MILLISECONDS.toHours(currentMiliSecond - createdMilisecond);
            long days = TimeUnit.MILLISECONDS.toDays(currentMiliSecond - createdMilisecond);
            if (seconds < 60) {
                if (seconds < 10) {
                    tvDate.setText(context.getResources().getString(R.string.just_now));
                    return true;
                } else {
                    tvDate.setText(seconds + " " + context.getResources().getString(R.string.seconds_ago));
                    return true;
                }

            } else if (minutes < 60) {
                tvDate.setText(minutes + " " + context.getResources().getString(R.string.minutes_ago));
                return true;

            } else if (hours < notifications_icon) {
                tvDate.setText(hours + " " + context.getResources().getString(R.string.hours_ago));
                return true;

            } else if(isDateYesterday(time)){
                tvDate.setText(context.getResources().getString(R.string.yesterday));
                return true;
            }
            else {
                if(days<=30) {
                    tvDate.setText(days + " " + context.getResources().getString(R.string.days_ago));
                    return true;
                }else
                {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }*/


    public static String formatDateWithTimeZone(String inputDateStr, String srcFormat, String destFormat, String timeZone) {
        String outputDateStr = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat(srcFormat, Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
            Date inputDate = inputFormat.parse(inputDateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat(destFormat, Locale.ENGLISH);
            outputFormat.setTimeZone(TimeZone.getDefault());
            outputDateStr = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return outputDateStr;
    }

    public static String formatDate(String inputDateStr, String srcFormat, String destFormat) {
        String outputDateStr = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat(srcFormat, Locale.ENGLISH);
            Date inputDate = inputFormat.parse(inputDateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat(destFormat, Locale.ENGLISH);
            outputDateStr = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return outputDateStr;
    }


    //2016-11-18
    public static String formatUtcDateTimeToLocalDate(String dateStr) {
        String localDateTime = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputFormat.setTimeZone(TimeZone.getDefault());
            localDateTime = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return localDateTime;
    }


    //2016-11-18
    public static String formatUtcDateToLocalDate(String dateStr) {
        String localDateTime = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputFormat.setTimeZone(TimeZone.getDefault());
            localDateTime = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return localDateTime;
    }

    //7/20
    public static String formatUtcDateToLocalMonthDate(String dateStr) {
        String dateMonth = null;

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d");
            dateMonth = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dateMonth;
    }


    // 7/20
    public static String formatUtcDateTimeToLocalMonthDate(String dateStr) {
        String dateMonth = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d");
            outputFormat.setTimeZone(TimeZone.getDefault());
            dateMonth = outputFormat.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dateMonth;
    }


    // Sat. 11/25
    public static String formatUtcDateTimeToLocalDayNameMonthDate(String dateStr) {

        String dayOfTheWeek = null;
        String dateMonth = null;

        try {
            DateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date inputDate = inputFormat1.parse(dateStr);

            SimpleDateFormat outputFormat1 = new SimpleDateFormat("EE");
            outputFormat1.setTimeZone(TimeZone.getDefault());
            dayOfTheWeek = outputFormat1.format(inputDate);

            SimpleDateFormat outputFormat2 = new SimpleDateFormat("M/d");
            outputFormat2.setTimeZone(TimeZone.getDefault());
            dateMonth = outputFormat2.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dayOfTheWeek + ". " + dateMonth;
    }

    // Sat. 11/25
    public static String formatUtcDateToLocalDayNameMonthDate(String dateStr) {

        String dayOfTheWeek = null;
        String dateMonth = null;

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat1 = new SimpleDateFormat("EE");
            dayOfTheWeek = outputFormat1.format(inputDate);

            SimpleDateFormat outputFormat2 = new SimpleDateFormat("M/d");
            outputFormat2.setTimeZone(TimeZone.getDefault());
            dateMonth = outputFormat2.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dayOfTheWeek + ". " + dateMonth;
    }

    // 24th August
    public static String formatDateToDateMonth(String dateStr) {

        String month = null;
        String dateMonth = null;

        try {
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat1 = new SimpleDateFormat("MMMM");
            month = outputFormat1.format(inputDate);

            SimpleDateFormat outputFormat2 = new SimpleDateFormat("d");
            dateMonth = outputFormat2.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dateMonth + "th\n" + month;
    }

    //notifications_icon
    public static String formatDateToDate(String dateStr) {

        String dateMonth = null;

        try {
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat2 = new SimpleDateFormat("d");
            dateMonth = outputFormat2.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dateMonth;
    }

    //August
    public static String formatDateToMonth(String dateStr) {

        String month = null;
        try {
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat1 = new SimpleDateFormat("MMMM");
            month = outputFormat1.format(inputDate);


        } catch (Exception e) {
            return "";
        }

        return month;
    }

    // 24th, August
    public static String formatDateToDateMonth2(String dateStr) {

        String month = null;
        String dateMonth = null;

        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            Date inputDate = inputFormat.parse(dateStr);

            SimpleDateFormat outputFormat1 = new SimpleDateFormat("MMMM");
            month = outputFormat1.format(inputDate);

            SimpleDateFormat outputFormat2 = new SimpleDateFormat("d");
            dateMonth = outputFormat2.format(inputDate);

        } catch (Exception e) {
            return "";
        }

        return dateMonth + "th, " + month;
    }


    public static String getDate(Context context, String timestamp_in_string) {
        long dv = (Long.valueOf(timestamp_in_string)) * 1000;// its need to be in milisecond
        Date df = new Date(dv);
        String vv = new SimpleDateFormat("MMM dd/yyyy,hh:mma", Locale.ENGLISH).format(df);

        return vv;
    }

    public static String getDateWithFormat(Context context, String timestamp_in_string, String format) {
        long dv = (Long.valueOf(timestamp_in_string)) * 1000;// its need to be in milisecond
        Date df = new Date(dv);
        String vv = new SimpleDateFormat(format).format(df);

        return vv;
    }

    public static String getTime(String timestamp_in_string) {
        long dv = Long.valueOf(timestamp_in_string) * 1000;// its need to be in milisecond
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(dv);
        String date = android.text.format.DateFormat.format("hh:mm:ss aa", cal).toString();
        return date;

    }

    public static String getTimeWithFormat(String timestamp_in_string, String format) {
        long dv = Long.valueOf(timestamp_in_string) * 1000;// its need to be in milisecond
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(dv);
        String date = android.text.format.DateFormat.format(format, cal).toString();
        return date;

    }

    /*public static boolean isDateToday(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Date getDate = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());
       // calendar.set(Calendar.HOUR_OF_DAY, 0);
        //calendar.set(Calendar.MINUTE, 0);
       // calendar.set(Calendar.SECOND, 0);

        Date startDate = calendar1.getTime();

        if(getDate.compareTo(startDate)>0)
        {
            return false;
        }else if(getDate.compareTo(startDate)==-1)
        {
            return true;
        }
        return false;

    }*/


    public static boolean isDateToday(String dateTime) {
        String pattern1 = "yyyy-MM-dd";
        String currentTime = getCurrentTime();

        SimpleDateFormat sdf = new SimpleDateFormat(pattern1);

        try {
            Date currentDTime = sdf.parse(currentTime);
            Date eventsTime = sdf.parse(dateTime);

            return currentDTime.equals(eventsTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDateYesterday(String dateTime) {
        String pattern1 = "yyyy-MM-dd";
        String currentTime = getCurrentTime();

        SimpleDateFormat sdf = new SimpleDateFormat(pattern1);

        try {
            Date currentDTime = sdf.parse(currentTime);
            Date eventsTime = sdf.parse(dateTime);
            Date yesterday = new Date(currentDTime.getTime() - (1000 * 60 * 60 * 24));

            return yesterday.equals(eventsTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String convertUTCtoLocalTime(String p_UTCDateTime) throws Exception {

        String lv_dateFormateInLocalTimeZone = "";//Will hold the final converted date
        Date lv_localDate = null;
        SimpleDateFormat lv_formatter;
        SimpleDateFormat lv_parser;

        //Temp for testing(mapping of cities and timezones will eventually be in a properties file
        TimeZone.getDefault();

        //create a new Date object using the UTC timezone
        lv_parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        lv_parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        lv_localDate = lv_parser.parse(p_UTCDateTime);

        //Set output format - // prints "2007/10/25  18:35:07 EDT(-0400)"
        lv_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("convertUTCtoLocalTime " + ": " + "The Date in the UTC time zone(UTC) " + lv_formatter.format(lv_localDate));

        //Convert the UTC date to Local timezone
        lv_formatter.setTimeZone(TimeZone.getDefault());
        lv_dateFormateInLocalTimeZone = lv_formatter.format(lv_localDate);
        System.out.println("convertUTCtoLocalTime: " + ": " + "The Date in the LocalTime Zone time zone " + lv_formatter.format(lv_localDate));

        return lv_dateFormateInLocalTimeZone;
    }

    public static String convertUTCtoLocalTime2(String p_UTCDateTime, String format) throws Exception {

        String lv_dateFormateInLocalTimeZone = "";//Will hold the final converted date
        Date lv_localDate = null;
        SimpleDateFormat lv_formatter;
        SimpleDateFormat lv_parser;

        //Temp for testing(mapping of cities and timezones will eventually be in a properties file
        TimeZone.getDefault();

        //create a new Date object using the UTC timezone
        lv_parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        lv_parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        lv_localDate = lv_parser.parse(p_UTCDateTime);

        //Set output format - // prints "2007/10/25  18:35:07 EDT(-0400)"
        lv_formatter = new SimpleDateFormat(format);

        //Convert the UTC date to Local timezone
        lv_formatter.setTimeZone(TimeZone.getDefault());
        lv_dateFormateInLocalTimeZone = lv_formatter.format(lv_localDate);

        return lv_dateFormateInLocalTimeZone;
    }


    public static String getCurrentTime() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getCurrentDate() {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    public static String getCurrentDate(String format) {
        //date output format
        DateFormat dateFormat = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    public static void openDatePicker(Context context, int day, int month, int year, final GetDob listeners) {
        // Get Current Date
        int mYear, mMonth, mDay;
        if (year == 0) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } else {
            mYear = year;
            mMonth = month;
            mDay = day;
        }

        //launch datepicker modal

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        listeners.getDateSuccess(formatDate("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth, "yyyy-MM-dd", "yyyy-MM-dd"));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Date of birth");
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - (((long) 3.78692e+11)));
        // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();


    }

    public static String getDaysDiff(String fromDate, String toDate) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date createdDate = sdf.parse(fromDate);
            Date currentDate = sdf.parse(toDate);
            Calendar createdCalendar = Calendar.getInstance();
            createdCalendar.setTime(createdDate);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);
            long createdMilisecond = createdCalendar.getTimeInMillis();
            long currentMiliSecond = currentCalendar.getTimeInMillis();

            long days = TimeUnit.MILLISECONDS.toDays(currentMiliSecond - createdMilisecond) + 1;

            return "" + days;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "" + 0;

    }

    public static void openTimePicker(Context context, final GetDob listeners, int hr, int min) {
        int hour, minute;
        if (hr == 0) {
            //if not selected
            Calendar mcurrentTime = Calendar.getInstance();
            hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            minute = mcurrentTime.get(Calendar.MINUTE);
        } else {
            //if already selected
            hour = hr;
            minute = min;
        }
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                listeners.getTimeSuccess(selectedHour, selectedMinute);
            }
        }, hour, minute, false);//Yes notifications_icon hour time
        mTimePicker.setTitle("Select Time");

        mTimePicker.show();
    }

    public static String getUTCDate(String OurDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getDefault());

            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //MM-dd-yyyy HH:mm this format changeable
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            OurDate = dateFormatter.format(value);

            //Log.d("OurDate", OurDate);
        } catch (Exception e) {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }

    public static String getLocalDate(String OurDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm"); //MM-dd-yyyy HH:mm this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            //Log.d("OurDate", OurDate);
        } catch (Exception e) {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }

    public interface GetDob {
        void getTimeSuccess(int hours, int minutes);

        void getDateSuccess(String dob);

    }

}


