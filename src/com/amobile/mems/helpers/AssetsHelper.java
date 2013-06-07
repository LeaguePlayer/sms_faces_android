package com.amobile.mems.helpers;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 21:11
 * To change this template use File | Settings | File Templates.
 */
public class AssetsHelper {
    public static boolean Crop=false;
    public static Bitmap tmp_photo;
    public static Uri photoUri;
    public static ArrayList<CatalogCell> preload;
    public static String MainPath="smsfaces";
    public static Integer width;
    public static TextView text;
    public static boolean fromcrop=false;
    public static String[] getFolders (AssetManager mgr) {
        String[] fileNames;
        try {
            fileNames=mgr.list(MainPath);
            return fileNames;

        } catch (IOException e) {
            e.printStackTrace();
              return new String[0];//To change body of catch statement use File | Settings | File Templates.
        }
    }
    public static String[] getFileNamesinFolder (AssetManager mgr,String folder) {
        String[] fileNames;
        try {
            fileNames=mgr.list(MainPath+File.separator+folder);
            return fileNames;

        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];//To change body of catch statement use File | Settings | File Templates.
        }
    }
    public static Drawable getDrawableByName(AssetManager mgr,String folder,String filename)
    {
        try {
            Drawable d = Drawable.createFromStream(mgr.open(MainPath+"/"+folder +"/"+filename), null);
            return d;
        } catch (IOException e) {
            return null;
        }
    }
    public static ArrayList<CatalogCell> GetCatalog(AssetManager mgr,String path)
    {
        ArrayList<CatalogCell> result= new ArrayList<CatalogCell>();
        if(path.equals(""))
        {
        String[] folders=getFolders(mgr);
        for(String foldername:folders)
        {
          CatalogCell cell = new CatalogCell();
            cell.isFolder=true;
            cell.path=foldername;
            cell.fileName=(getFileNamesinFolder(mgr,foldername).length>0)?getFileNamesinFolder(mgr,foldername)[0]:"";
            result.add(cell);
        }
        }
        else
        {
            String[] filenames=getFileNamesinFolder(mgr, path);
            for(String filename:filenames)
            {
                CatalogCell cell = new CatalogCell();
                cell.isFolder=false;
                cell.path=path;
                cell.fileName=filename;
                result.add(cell);
            }
        }
        return result;

    }
}
