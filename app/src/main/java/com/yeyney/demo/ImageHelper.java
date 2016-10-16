package com.yeyney.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageHelper {

    public static Bitmap scaleDownPicture(String path) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        int dstWidth = (int) Math.floor(bitmap.getWidth() * 0.50f);
        int dstHeight = (int) Math.floor(bitmap.getHeight() * 0.50f);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


        OutputStream outputStream;
        File file = new File(path);
        outputStream = new FileOutputStream(file);

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush(); // Not really required
        outputStream.close(); // do not forget to close the stream
        return scaledBitmap;
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
}
