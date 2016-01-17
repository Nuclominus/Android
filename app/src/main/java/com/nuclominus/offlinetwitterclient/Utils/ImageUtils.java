package com.nuclominus.offlinetwitterclient.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class ImageUtils {

    private static ImageUtils instance = null;
    private static Context context;

    private static final String IMG_PROFILE = "profile.jpg";
    private static final String CACHE_FOLDER = "cachefolder";
    private static final String APP_DATA = "imgappdata";

    public static ImageUtils getInstance(Context ctx) {
        if (instance == null) {
            instance = new ImageUtils();
            context = ctx;
        }
        return instance;
    }

    public static void saveImageProfile(String uri) {
        URL img = null;

        try {
            img = new URL(uri);

            URLConnection connection = img.openConnection();
            InputStream inputStream = new BufferedInputStream(img.openStream(), 10240);
            File cacheDir;
            cacheDir = getCacheFolder(context);
            File cacheFile = new File(cacheDir, IMG_PROFILE);
            FileOutputStream outputStream = new FileOutputStream(cacheFile);

            byte buffer[] = new byte[1024];
            int dataSize;
            int loadedSize = 0;
            while ((dataSize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, dataSize);
            }


            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class DownloadAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            saveImageProfile(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public static Bitmap getImageProf() {
        Bitmap imageProf = null;
        try {
            File cacheDir = getCacheFolder(context);
            File cacheFile = new File(cacheDir, IMG_PROFILE);
            InputStream fileInputStream = null;
            fileInputStream = new FileInputStream(cacheFile);
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 2;
            bitmapOptions.inJustDecodeBounds = false;
            imageProf = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return imageProf;
    }

    public static File getCacheFolder(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), CACHE_FOLDER);
            if (!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        if (!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public static File getDataFolder(Context context) {
        File dataDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), APP_DATA);
            if (!dataDir.isDirectory()) {
                dataDir.mkdirs();
            }
        }
        if (!dataDir.isDirectory()) {
            dataDir = context.getFilesDir();
        }
        return dataDir;
    }

    public static void release() {
        context = null;
        instance = null;
    }
}
