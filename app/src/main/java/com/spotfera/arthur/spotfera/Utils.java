package com.spotfera.arthur.spotfera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arthur on 12/09/17.
 */

public class Utils {

    public static byte[] compressBitmap(Bitmap bm)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 75, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    public static Bitmap uncompressBitmap(byte[] bytes)
    {
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bmp;
    }

    public static Bitmap downloadImage(final ImageLoader imageLoader, final String url, final Bitmap defaultImg)
    {
        final File imageFile = imageLoader.getDiskCache().get(url);
        final String[] path = {null};

        if(imageFile.exists())
            path[0] = imageFile.getAbsolutePath();

        final Bitmap[] imgRetorno = {null};

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (path[0] != null) {
                    Log.d("IMG", "Buscou da cache");
                    imgRetorno[0] = imageLoader.loadImageSync(ImageDownloader.Scheme.FILE.wrap(path[0]));
                }
                else
                {
                    Log.d("IMG", "Buscou da web");
                    imgRetorno[0] = imageLoader.loadImageSync(url);
                    if(imgRetorno[0] == null)
                    {
                        imgRetorno[0] = defaultImg;
                        Log.d("IMG", "Pegou a img padrao");
                    }
                }
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return imgRetorno[0];
    }

    //funcao pra deixar a imagem redonda
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static boolean validate(Activity activity, int requestCode, String... permissions)
    {
        List<String> list = new ArrayList<>();
        for(String permission : permissions)
        {
             boolean ok = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            if(!ok)
            {
                list.add(permission);
            }
        }

        if(list.isEmpty())
        {
            return true;
        }

        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        ActivityCompat.requestPermissions(activity, newPermissions, 1);

        return false;
    }

}
