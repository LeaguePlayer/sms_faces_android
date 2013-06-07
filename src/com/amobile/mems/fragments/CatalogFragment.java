package com.amobile.mems.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.actionbarsherlock.app.SherlockFragment;
import com.amobile.mems.R;
import com.amobile.mems.activity.Choose_mem;
import com.amobile.mems.activity.drawing;
import com.amobile.mems.adapters.GridAdapter;
import com.amobile.mems.database.mems_db;
import com.amobile.mems.helpers.AssetsHelper;
import com.amobile.mems.helpers.CatalogCell;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
public class CatalogFragment extends SherlockFragment {
    Context con;
    GridAdapter adapter;
    GridView catalog;
    ArrayList<CatalogCell> data;
    mems_db  db;
    ProgressDialog pd;
    public static String old_path="";
    public static boolean isMain=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catalog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Init();
    }
    public void Init()
    {
        con=getActivity();
        db= new mems_db(con);
        catalog=(GridView)getView().findViewById(R.id.catalog_grid);
        catalog.setNumColumns(3);
        pd = ProgressDialog.show(getSherlockActivity(), "Подготовка данных", "Пожалуйста подождите...", true, true);
        db.open();

        if(isMain)
        {
        data=db.GetAllFilesInMainFolder(getActivity().getAssets());
        AssetsHelper.preload=data;
        }
        else
        data= db.GetAllFilesInFolder(old_path);
        db.close();
        adapter= new GridAdapter(getActivity(),data);
        catalog.setAdapter(adapter);
        if (pd != null && pd.isShowing()) pd.dismiss();
        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isMain) {
                    isMain = false;
                    old_path = data.get(i).path;
                    db.open();
                    data = db.GetAllFilesInFolder( data.get(i).path);
                    db.close();
                    adapter.SetArray(data);
                    adapter.notifyDataSetChanged();
                } else {
                    db.open();
                    db.addToLastseen(data.get(i));
                    db.close();
                    Intent intent = new Intent(getActivity().getApplicationContext(), Choose_mem.class);
                    intent.putExtra("folder",data.get(i).path);
                    intent.putExtra("name",data.get(i).fileName);
                    startActivity(intent);
                }
            }
        });
    }
    public void Back()
    {
        if(!isMain)
        {
        //data= AssetsHelper.GetCatalog(getActivity().getAssets(),"");
        adapter=new GridAdapter(con,AssetsHelper.preload);
        catalog.setAdapter(adapter);
        data=AssetsHelper.preload;
        isMain=true;
        }
        else
        {
                AlertDialog alertDialog = new AlertDialog.Builder(con).create();
                alertDialog.setTitle("Выход");
                alertDialog.setMessage("Вы действительно хотите выйти?");
                alertDialog.setButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                alertDialog.setButton2("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
        }
    }


}
