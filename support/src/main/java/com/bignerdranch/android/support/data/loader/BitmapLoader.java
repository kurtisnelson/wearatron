package com.bignerdranch.android.support.data.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitmapLoader extends DataLoader<Bitmap> {
    private static final String TAG = BitmapLoader.class.getSimpleName();
    private int mWidth;
    private int mHeight;
    private File mFile;
    private String mFilename;

    public BitmapLoader(Context context, String filename, int width, int height) {
        super(context);
        mFilename = filename;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public Bitmap loadInBackground() {
        FileInputStream inputStream = null;
        try {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            inputStream = getInputStream();
            BitmapFactory.decodeStream(inputStream, null, bmOptions);
            closeInputStream(inputStream);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / mWidth, photoH / mHeight);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            inputStream = getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bmOptions);
            closeInputStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "could not load receipt bitmap", e);
            return null;
        } finally {
            closeInputStream(inputStream);
        }
    }

    private FileInputStream getInputStream() throws IOException {
        mFile = new File(mFilename);
        if (mFile.exists()) {
            return new FileInputStream(mFile);
        } else {
            mFile = null;
        }
        return null;
    }

    private void closeInputStream(FileInputStream inputStream) {
        if (mFile != null && mFile.exists() && inputStream != null) {
            try {
                inputStream.close();
                mFile = null;
            } catch (IOException e) {
                Log.e(TAG, "while trying to close the input stream", e);
            }
        }
    }
}

