package com.amobile.mems.fragments;

import android.content.Context;
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
import com.amobile.mems.adapters.GridAdapter;
import com.amobile.mems.database.mems_db;
import com.amobile.mems.helpers.CatalogCell;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
public class FavoritesFragment extends SherlockFragment
{
    Context con;
    GridAdapter adapter;
    GridView catalog;
    ArrayList<CatalogCell> data;
    mems_db db;
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
        db=new mems_db(con);
        db.open();
        data=db.GetAllFavorites();
        db.close();
        adapter=new GridAdapter(con,data);
        catalog=(GridView)getView().findViewById(R.id.catalog_grid);
        catalog.setNumColumns(3);
        catalog.setAdapter(adapter);
        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Choose_mem.class);
                intent.putExtra("folder",data.get(i).path);
                intent.putExtra("name",data.get(i).fileName);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(db!=null)
        {
        db.open();
        data=db.GetAllFavorites();
        db.close();
        adapter.SetArray(data);
        adapter.notifyDataSetChanged();
        }
    }
}
