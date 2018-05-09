package com.example.cropmyimage2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 */

public class Constant {
    public static String PICKUP_LATITUDE = "";
    public static String PICKUP_LONGITUDE = "";
    public static Object SEL_ADDRESS;
    Context mContext;
    AlertDialog.Builder alertDialog;
    DatePickerDialog dialog;

    public Constant(AppController appController) {
        this.mContext = appController;
    }

    /**
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::::Used to hide the Keyboard ForceFully::::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static void hideKeyboardForceFully(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::::Used to show the Keyboard :::::::::::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static void showKeyboardForceFully(Activity activity, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * ::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::  Check Value is Present  :::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static boolean isFieldEmpty(EditText editText) {
        String ed_text = editText.getText().toString().trim();
        if (ed_text.isEmpty() || ed_text.length() == 0 || ed_text.equals("") || ed_text == null) {
            return false;
        } else {
            return true;
        }
    }

    /******
     * ::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::::  Email Validation  ::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::
     *****/

    public static boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    /****
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::Detect whether internet is connected or not:::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     *****/
    public static boolean checkInternetConnect(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /***
     * :::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::::: Runtime Multiple Permission ::::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static boolean checkAndRequestPermissions(Activity mContext) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        int writeExternalStorage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStorage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int wakeUpLock = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WAKE_LOCK);
        int writeSetting = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_SETTINGS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (wakeUpLock != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }
        if (writeSetting != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_SETTINGS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mContext, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    /**
     * ::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::  Phone Validation  :::::::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (!phone.trim().equals("") || phone.length() > 10 || phone != null) {
            return Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }


    /**
     * ::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::  Is String Contain Number  ::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::
     */
    public boolean isAlphaNumericNameContains(String s) {
        String pattern = "^[a-zA-Z]*$";
        return s.matches(pattern);
    }

    /**
     * A method to download json data from url
     */
    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Exception while downloading url", e.toString());
        } finally {
            if (iStream != null)
                iStream.close();


            urlConnection.disconnect();
        }
        return data;
    }

    /***
     * GET Latitude and longitude from url
     * @param requestURL
     * @return
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::::::::::ENable this service in android:::::::::::::::::::::::::
     * :::::::::::::::::::Google Places API Web Service:::::::::::::::::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    /***
     * :::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::Method To Print The Value In Log::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::
     *
     */
    public void printLogOfAllValues(String key, String value) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put(key, value);
        for (Map.Entry<String, String> mapEntry : hashmap.entrySet()) {
            String key1 = mapEntry.getKey();
            String value1 = mapEntry.getValue();
            Log.e("TAG", "\n" + key1 + "=" + value1);
        }
    }


    /***
     * ::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::Used to get thumbnail from video's URL:::
     * ::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public Bitmap getThumbnailFromURL(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retrieve VideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }






    /*****
     * ::::::::::::::::::::::::::::::::::::::::
     * :::::Shows the current time and date::::
     * ::::::::::::::::::::::::::::::::::::::::
     */
    public String showCurrentDateandTime() {
        Calendar calender = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(calender.getTime());
        return date;
    }

    /*****
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::::show the alert dialog with different situation::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public void showAlertDialog(final Context context, String message, final int value) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (value) {
                }
            }
        });
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    /*******
     * ::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::::create the file from bitmap:::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public static File createFileFromBitmap(Bitmap bmp, Context context) {
        // DEPENDENCY compile 'id.zelory:compressor:1.0.4'
        try {
            //create a file to write bitmap data
            File file = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
            if (file.exists())
                file.delete();
            file.createNewFile();
            //Convert bitmap to byte array
            Bitmap bitmap = bmp;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90 /*ignored for PNG*/, byteArrayOutputStream);
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();
            //write the bytes in file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bitmapdata);
            fileOutputStream.flush();
            fileOutputStream.close();
//            return Compressor.getDefault(context).compressToFile(file);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /****
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::method that is used to clear all editext's value at simultaneously at same time:::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public void clearAllEditext(final ArrayList<EditText> list) {
        for (EditText edittext : list) {
            edittext.setText("");
        }
    }

    /******
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::method to convert the Imageview's image into the base64 format::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public String convertImageViewIntoBase64(ImageView imageView) {
        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }



    /****
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::::::::open our app in the Playstore::::::::::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public void openAppInPlaystore(Context context) {
//        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=chifferobe.app.com.chifferobe")));
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.emil.errand&hl=en")));
    }



    /****
     * ::::::::::::::::::::::::::::
     * :::used to set the color::::
     * ::::::::::::::::::::::::::::
     */
    public int setcolor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }

    /******
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * :::::::change the date format into another format::::::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public String changeDateFormatFromAnother(String date) {
        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");
        String resultDate = "";
        try {
            resultDate = outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    /*****
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::Finish the application with clearing its all activity from the stack:::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public void finishApplication(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }


    /*****
     * :::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::change given date into milliseconds::::::
     * :::::::::::::::::::::::::::::::::::::::::::::::::
     */
    public Long changeDateIntoMilliseconds(String date) {
        Long result = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(date);
            result = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*****
     * ::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::::GET Date Time and Day from TimeStamp::::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::
     */

    public String getDateTimeDayFromTimeStamp(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            //output= 1521455862
            //static timestamp =2018-03-19 15:44:05 Mon;
            TimeZone tz = TimeZone.getDefault();
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E", Locale.getDefault());
            Date currenTimeZone = new Date((long) timestamp * 1000);

            Log.e("204", "myDefaultTimeStamp>>" + sdf.format(currenTimeZone));
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
            Log.e("CAtch", "<<");
        }
        return "";
    }


}
