package com.partime.user.helpers;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Naresh on 10/7/2019.
 */

public class Utilities {
    public static String LOG_TAG = "eventLog";
    public static String[] CARD_TYPE = {"^4[0-9]{12}(?:[0-9]{3})?$", "^(?:5[1-5]|2(?!2([01]|20)|7(2[1-9]|3))[2-7])\\d{14}$"
            , "^3[47][0-9]{13}$", "^6(?:011|[45][0-9]{2})[0-9]{12}$", "^(?:2131|1800|35\\d{3})\\d{11}$", "^62[0-9]{14,17}$"};

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int dipToPixels(Context context, float f) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f,
                metrics);
    }

    public static String getImageAsString(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        byte[] encoded = Base64.encode(imageBytes, 0);
        String encodedString = new String(encoded);
        return encodedString;
    }



   /* @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.bg_statusbar);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }
*/


    public static void showToastShort(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String msg) {
        if (context != null)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void hideKeyboardONDialog(Context context, EditText editTxt) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editTxt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    public static boolean isLastActivityInTheStack(Context context, String className) {
        ActivityManager mngr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if (taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(className)) {
            return true;
        } else {

        }
        for (ActivityManager.RunningTaskInfo info : taskList) {
            //Log.i(Utilities.MY_LOG, "T  info.getClass().getName():" + info.getClass().getName());

        }
        return false;
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo("com.winchat.app", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("keyhash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("keyhash", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("keyhash", "printHashKey()", e);
        }
    }

    public static void pickDate(final Context context, final TextView editText) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                if (validateNextDate(context, dayOfMonth, monthOfYear, year)) {
                    String dd, yy, mm;

                    if ((monthOfYear + 1) < 10)
                        mm = "0" + (monthOfYear);
                    else
                        mm = String.valueOf(monthOfYear);
                    if (dayOfMonth < 10)
                        dd = "0" + dayOfMonth;
                    else
                        dd = dayOfMonth + "";
                    if (editText != null)
                        //editText.setText(year + "-" + mm + "-" + dd);
                        editText.setText(formatDate(year, Integer.parseInt(mm), Integer.parseInt(dd)));
                }
            }
        }, mYear, mMonth, mDay);

        //dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();

    }

    public static void getPickedDate(final Context context, final DatePickCallBack datePickCallBack) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                if (validateNextDate(context, dayOfMonth, monthOfYear, year)) {
                    String dd, yy, mm;

                    if ((monthOfYear + 1) < 10)
                        mm = "0" + (monthOfYear + 1);
                    else
                        mm = String.valueOf(monthOfYear + 1);
                    if (dayOfMonth < 10)
                        dd = "0" + dayOfMonth;
                    else
                        dd = dayOfMonth + "";

                    datePickCallBack.onDateSet(year + "-" + mm + "-" + dd, formatDate(year, Integer.parseInt(mm), Integer.parseInt(dd)));
                }
            }
        }, mYear - 18, mMonth, mDay);

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear - 18, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                datePickCallBack.onCancel();
            }
        });
        dpd.show();

    }

    public interface DatePickCallBack {
        void onDateSet(String date, String formattedDate);

        void onCancel();
    }


    public static void pickDate2(final Context context, final TextView editText) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                if (validatePastDate(context, dayOfMonth, monthOfYear, year)) {
                    String dd, yy, mm;
                    if ((monthOfYear + 1) < 10)
                        mm = "0" + (monthOfYear + 1);
                    else
                        mm = String.valueOf(monthOfYear + 1);
                    if (dayOfMonth < 10)
                        dd = "0" + dayOfMonth;
                    else
                        dd = dayOfMonth + "";
                    if (editText != null)
                        editText.setText(year + "-" + mm + "-" + dd);
                }
            }
        }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        dpd.show();

    }

    public static String formatDate(int year, int month, int day) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date newDate = null;
            newDate = format.parse(year + "-" + month + "-" + day);
            format = new SimpleDateFormat("dd-MMM-yyyy");
            String date = format.format(newDate);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String formatDateTime(String dateStamp, String inputFormat, String outputFormat) {

        try {
            SimpleDateFormat format = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
            Date newDate = null;
            newDate = format.parse(dateStamp);
            format = new SimpleDateFormat(outputFormat);
            String date = format.format(newDate);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static boolean validatePastDate(Context mContext, int day, int month, int year) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        if (day < currentDay /*&& year == currentYear && month == currentMonth*/) {
            Toast.makeText(mContext, "Please select a valid date", Toast.LENGTH_LONG).show();
            return false;
        }/* else if (month < currentMonth && year == currentYear) {
            Toast.makeText(mContext, "Please select valid month", Toast.LENGTH_LONG).show();
            return false;
        } else if (year < currentYear) {
            Toast.makeText(mContext, "Please select valid year", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }

    public static boolean validateNextDate(Context mContext, int day, int month, int year) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        if (day > currentDay & year == currentYear & month == currentMonth) {
            Toast.makeText(mContext, "Please select a valid date", Toast.LENGTH_LONG).show();
            return false;
        } else if (month > currentMonth & year == currentYear) {
            Toast.makeText(mContext, "Please select valid month", Toast.LENGTH_LONG).show();
            return false;
        } else if (year > currentYear) {
            Toast.makeText(mContext, "Please select valid year", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public static void pickTime(Context context, final TextView editText) {

        final Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String AM_PM = " AM";
                        String mm_precede = "";
                        if (hourOfDay >= 12) {
                            AM_PM = " PM";
                            if (hourOfDay >= 13 && hourOfDay < 24) {
                                hourOfDay -= 12;
                            } else {
                                hourOfDay = 12;
                            }
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if (minute < 10) {
                            mm_precede = "0";
                        }

                        if (editText != null)
                            editText.setText(hourOfDay + ":" + mm_precede + minute + AM_PM);
                        //editText.setText(hrs + ":" + mins);
                    }
                }, mHour, mMinute, false);

        tpd.show();

    }


    public static boolean validateTime(String time, String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

            Date now = new Date(System.currentTimeMillis());
            int result = now.compareTo(dateFormatter.parse(date));
            if (result < 0) {
                return true;
            }

            Date EndTime = dateFormat.parse(time);

            Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

            if (CurrentTime.after(EndTime)) {
                System.out.println("timeeee end ");
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getAge(int _year, int _month, int _day) {

        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        return a;
    }


    public static Bitmap getBitMapFromImageURl(String imagepath, Activity activity) {

        Bitmap bitmapFromMapActivity = null;
        Bitmap bitmapImage = null;
        try {

            File file = new File(imagepath);
            // We need to recyle unused bitmaps
            if (bitmapImage != null) {
                bitmapImage.recycle();
            }
            bitmapImage = reduceImageSize(file, activity);
            int exifOrientation = 0;
            try {
                ExifInterface exif = new ExifInterface(imagepath);
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            if (rotate != 0) {
                int w = bitmapImage.getWidth();
                int h = bitmapImage.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap & convert to ARGB_8888, required by
                // tess

                Bitmap myBitmap = Bitmap.createBitmap(bitmapImage, 0, 0, w, h,
                        mtx, false);
                bitmapFromMapActivity = myBitmap;
            } else {
                int SCALED_PHOTO_WIDTH = 150;
                int SCALED_PHOTO_HIGHT = 200;
                Bitmap myBitmap = Bitmap.createScaledBitmap(bitmapImage,
                        SCALED_PHOTO_WIDTH, SCALED_PHOTO_HIGHT, true);
                bitmapFromMapActivity = myBitmap;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bitmapFromMapActivity;

    }

    public static Bitmap reduceImageSize(File f, Context context) {

        Bitmap m = null;
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE = 150;

            int width_tmp = o.outWidth, height_tmp = o.outHeight;

            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
            m = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            // Toast.makeText(context,
            // "Image File not found in your phone. Please select another image.",
            // Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
        return m;
    }


    public static boolean isValidJson(String responseStr) {

        try {
            new JSONObject(responseStr);
            return true;
        } catch (JSONException ex) {
            try {
                new JSONArray(responseStr);
                return true;
            } catch (JSONException ex1) {
                return false;
            }
        }
    }

    public int convertDpToPixel(Context context, int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }


    public static void SendEmail(Activity context, String to, String subject) {
        String[] TO = {to};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("signUpMessage/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        PackageManager manager = context.getPackageManager();
        List infos = manager.queryIntentActivities(emailIntent, 0);
        if (infos.size() > 0) {
            context.startActivity(emailIntent);
        } else {
            showToastShort(context, "No Email application installed on phone");
        }
    }

    public static String getTimeStamp() {

        long timestamp = (System.currentTimeMillis() / 1000L);
        String tsTemp = "" + timestamp;
        return "" + tsTemp;

    }

    public static long getDateDiffInDays(String dateStamp, String inputFormat) {
        Date newDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(inputFormat, Locale.ENGLISH);

            newDate = format.parse(dateStamp);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return getDateDiffInDays(newDate, Calendar.getInstance(Locale.ENGLISH).getTime());
    }

    public static long getDateDiffInDays(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return days;
    }

    public static long getDateDiffInHours(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return hours;
    }

    public static long getDateDiffInMinutes(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        return minutes;
    }

    public static long getDateDiffInSeconds(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        long seconds = diff / 1000;
        return seconds;
    }

    public static long getDateDiffInMilliSeconds(Date date1, Date date2) {

        long diff = date1.getTime() - date2.getTime();
        return diff;
    }

    public static Bitmap decodeSampledBitmapFromFile(String pic_Path, int reqWidth, int reqHeight) {
        try {
            File f = new File(pic_Path);
            Matrix mat = getOrientatationFromFile(f.getPath());// postRotate(angle);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pic_Path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            // return BitmapFactory.decodeFile(pic_Path, options);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            Bitmap correctBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            return correctBmp;
        } catch (Exception e) {
            return null;
        }
    }

    static Matrix getOrientatationFromFile(String pic_Path) {
        Matrix mat = new Matrix();
        try {

            ExifInterface exif = new ExifInterface(pic_Path);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            mat.postRotate(angle);
            return mat;
        } catch (Exception e) {
            return mat;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static class DateTimeDifference {
        private String days = "";
        private String hours = "";
        private String mins = "";
        private String secs = "";

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getHours() {
            return hours;
        }

        public void setHours(String hours) {
            this.hours = hours;
        }

        public String getMins() {
            return mins;
        }

        public void setMins(String mins) {
            this.mins = mins;
        }

        public String getSecs() {
            return secs;
        }

        public void setSecs(String secs) {
            this.secs = secs;
        }
    }


    public static Bitmap rotateBitmap(String filePath, Bitmap bitmap) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 300 || rotatedHeight > 300) {
            float widthRatio = ((float) rotatedWidth) / ((float) 300);
            float heightRatio = ((float) rotatedHeight) / ((float) 300);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

    }

    private static Bitmap getScaledBitmap(String pathOfInputImage, int dstWidth, int dstHeight) {

        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(pathOfInputImage);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = new FileInputStream(pathOfInputImage);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / dstWidth, inHeight / dstHeight);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, dstWidth, dstHeight);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);

            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            return resizedBitmap;
           /* // save image
            try
            {
                FileOutputStream out = new FileOutputStream(pathOfOutputImage);
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            catch (Exception e)
            {
                Log.e("Image", e.getSignUpMessage(), e);
            }*/
        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
        }

        return null;
    }

    public static void clearAllgoToActivity(Context context, Class<?> act) {
        Intent i = new Intent(context, act);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    public static String[] getCounterTime(long different) {
        String[] counttimearray = null;
        if (different > 0) {
            // Calendar calender = Calendar.getInstance();
            counttimearray = printDifference(different);
        } else {
            counttimearray = new String[]{"00", "00", "00", "00"};
        }
        return counttimearray;

    }


    public static String[] printDifference(long different) {
        String[] counttime = new String[4];

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        counttime[0] = String.valueOf(elapsedDays);
        counttime[1] = String.valueOf(elapsedHours);
        counttime[2] = String.valueOf(elapsedMinutes);
        counttime[3] = String.valueOf(elapsedSeconds);

        return counttime;
    }


    public static String getDifferenceSeconds(long different) {
        String diffSecond = null;
        if (different > 0) {
            long secondsInMilli = 1000;
            long elapsedSeconds = different / secondsInMilli;

            diffSecond = elapsedSeconds + "";


        } else {
            diffSecond = "";
        }
        return diffSecond;

    }


    public static String getCurrentDate() {

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        String date = dateFormatGmt.format(new Date());

        return date;


    }

    public static String getCurrTimeUTC() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateAsString = dateFormat.format(date);
        return dateAsString;
    }

    public static String getActivityName(Activity activity) {

        if (activity != null) {
            return activity.getClass().getSimpleName();
        } else {
            return "";
        }

    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    public static String checkCardType(String cardNo) {

        for (int i = 0; i < CARD_TYPE.length; i++) {
            if (cardNo.matches(CARD_TYPE[i])) {
                if (i == 0) {
                    return "VISA";
                } else if (i == 1) {
                    return "MASTERCARD";

                } else if (i == 2) {
                    return "AMERICAN EXPRESS";

                } else if (i == 3) {
                    return "DISCOVER";

                } else if (i == 4) {
                    return "JCB";

                } else if (i == 5) {
                    return "CHINA UNION PAY";

                }
            }

        }
        return "UNKNOWN";
    }

    public static void setImagePicasso(Context context, ImageView view, String path) {
        if (path != null) {
            if (path.length() != 0) {
                Picasso.get()
                        .load(path)
                        .resize(900, 600)
                        .onlyScaleDown()
                        //  .placeholder(R.drawable.profile_male)
                        //.fit()
                        // .centerCrop()
                        .into(view);
            } else {
                //  Picasso.with(context)
                //.load(R.drawable.profile_male)
                //.fit()
                //.centerCrop()
                //    .into(view);
            }
        }
    }

    public static void setImageCar(Context context, ImageView view, String path) {
        if (path != null) {
            if (path.length() != 0) {
                Picasso.get()
                        .load(path)
                        .resize(900, 600)
                        .onlyScaleDown()
                        //  .placeholder(R.drawable.placeholder)
                        //.fit()
                        // .centerCrop()
                        .into(view);
            } else {
                // Picasso.with(context)
                //  .load(R.drawable.placeholder)
                //.fit()
                //.centerCrop()
                //      .into(view);
            }
        }
    }

    @SuppressLint("NewApi")
    public static int[] getScreenSpec(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width, height};
    }

    public static void expandOrCollapse(final View v, String exp_or_colpse) {
        TranslateAnimation anim = null;
        if (exp_or_colpse.equals("expand")) {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        } else {
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);
        }

        // To Collapse
        //

        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }


    /*SLL ING TRUST*/
    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
