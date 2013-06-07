package com.amobile.mems.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.amobile.mems.R;
import com.amobile.mems.helpers.AssetsHelper;
import com.amobile.mems.helpers.CatalogCell;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
public class GridAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater lInflater;
    public ArrayList<CatalogCell> objects;
    private LayoutInflater layoutInflater;

    // Gets the context so it can be used later
    public GridAdapter(Context c, ArrayList<CatalogCell> cell) {
        mContext = c;
        objects = cell;
        layoutInflater = LayoutInflater.from(c);
    }

    // Total number of things contained within the adapter
    public int getCount() {
        return objects.size();
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return objects.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public void SetArray(ArrayList<CatalogCell> new_array)
    {
        objects=new_array;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid = convertView;
        CatalogCell tmp = (CatalogCell) getItem(position);
        if (tmp.isFolder) {
            grid = inflater.inflate(R.layout.grid_cell, parent, false);
        }
        else
        {
            grid = inflater.inflate(R.layout.grid_cell_withouttitle, parent, false);
        }
        //String imageUri = "assets://image.png"; // from assets
        String imageUri = "assets://"+AssetsHelper.MainPath+"/"+tmp.path+"/"+tmp.fileName; // from assets
        ImageView imageView = (ImageView) grid.findViewById(R.id.image);
        TextView textView = (TextView) grid.findViewById(R.id.text);
        if (tmp.isFolder)//если это папка
        textView.setText(tmp.path);
            ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
           // imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            imageLoader.displayImage(imageUri, imageView);
        return grid;
    }

}