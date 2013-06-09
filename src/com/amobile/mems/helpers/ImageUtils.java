package com.amobile.mems.helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;


public class ImageUtils {

    private volatile static ImageUtils instance;

    private ImageUtils() {}

    public static ImageUtils getInstance()
    {
        if (instance == null)
        {
            synchronized (ImageUtils.class)
            {
                if (instance == null)
                {
                    instance = new ImageUtils();
                }
            }
        }
        return instance;
    }


    // Получение временного файла изображения,
// сделанного с камеры
    public Uri getTempUri(Context context)
    {
        //it will return /sdcard/filename.tmp
        final File path = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (!path.exists())
        {
            path.mkdir();
        }

        return Uri.fromFile(new File(path, "Pic.jpg"));
    }
}
