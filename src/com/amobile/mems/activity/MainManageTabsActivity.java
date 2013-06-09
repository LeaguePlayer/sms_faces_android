package com.amobile.mems.activity;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.amobile.mems.R;
import com.amobile.mems.adapters.TabsAdapter;
import com.amobile.mems.fragments.CatalogFragment;
import com.amobile.mems.fragments.FavoritesFragment;
import com.amobile.mems.fragments.LastSeenFragment;
import com.amobile.mems.helpers.AssetsHelper;
import com.amobile.mems.helpers.ImageUtils;
import com.android.camera.CropImage;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.IOException;

public class MainManageTabsActivity extends SherlockFragmentActivity {
    private TabsAdapter mTabsAdapter;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;
    private final static int ACTIVITY_COLOR_PICKER_REQUEST_CODE = 3;
    private final static int ACTIVITY_CROP_IMAGE = 4;
    ViewPager pager;
    public AlertDialog dialog;
    Context con;
    ContentValues values;
   public Uri photoUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_app);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_activity);
        Init();
        SetTabs();
    }

    public void Init() {
        //getActionBar().setDisplayShowHomeEnabled(false);//Убрать иконку приложения
        //getSupportActionBar().setLogo(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(true);
        // getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setIcon(R.drawable.one);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        SetCustomActionBar();
        pager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(this, pager);
        con = this;

        initImageLoader(con);
    }

    public void SetCustomActionBar() {
        ActionBar ab = getSherlock().getActionBar();
        ab.setDisplayShowCustomEnabled(true);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.myactionbar, null);
        ab.setCustomView(customView);
        ImageButton ibItem1 = (ImageButton) customView.findViewById(R.id.item1);
        ibItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Info.class);
                startActivity(intent);
            }
        });
        TextView title = (TextView) customView.findViewById(R.id.ttitle_application);
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/v_BD_Cartoon_Shout Cyr.ttf");
        title.setTypeface(font);
        title.setText("TROLLFACES");
        ImageButton ibItem2 = (ImageButton) customView.findViewById(R.id.item2);
        ibItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);// ...
            }
        });
    }

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .enableLogging() // Not necessary in common
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void SetTabs() {
        Bundle catalogArgs = new Bundle();
        catalogArgs.putString("keyT", getString(R.string.catalog));
        addTab(getString(R.string.catalog), CatalogFragment.class, catalogArgs, R.drawable.icon_ms);

        Bundle favoritesArgs = new Bundle();
        favoritesArgs.putString("keyT", getString(R.string.favorites));
        addTab(getString(R.string.favorites), FavoritesFragment.class, favoritesArgs, R.drawable.icon_heart);

        Bundle lastseenArgs = new Bundle();
        lastseenArgs.putString("keyT", getString(R.string.lastseen));
        addTab(getString(R.string.lastseen), LastSeenFragment.class, lastseenArgs, R.drawable.icon_clock);
    }

    private void addTab(String title, Class<?> clss, Bundle args, int res) {
        com.actionbarsherlock.app.ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(title);
        tab.setIcon(res);
        mTabsAdapter.addTab(tab, args, clss);
    }
   /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem drawing = menu.add("");
        drawing.setIcon(R.drawable.camera);
        drawing.setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {

               showDialog(1);
                     selectedView = movedView;
           return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        drawing.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AssetsHelper.Crop) //если включено обрезание
        {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                AssetsHelper.photoUri=selectedImage;
                Intent intent = new Intent(getApplicationContext(), CropImage.class);
                //intent.putExtra("action", "1");
               // intent.putExtra("scaleUp", true);
                intent.setData(AssetsHelper.photoUri);
                startActivityForResult(intent,ACTIVITY_CROP_IMAGE);
            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                photoUri = ImageUtils.getInstance().getTempUri(MainManageTabsActivity.this);
                AssetsHelper.photoUri=photoUri;
                Intent intent = new Intent(getApplicationContext(), CropImage.class);
               // intent.putExtra("action", "2");
                //intent.putExtra("scaleUp", true);
                intent.setData(AssetsHelper.photoUri);
                startActivityForResult(intent,ACTIVITY_CROP_IMAGE);
            } else if (requestCode == ACTIVITY_COLOR_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
                int color = data.getIntExtra(
                        ColorPickerActivity.RESULT_COLOR, 0xff000000);
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                intent.putExtra("action", "3");
                intent.putExtra("color", color);
                startActivity(intent);
            }else if (requestCode == ACTIVITY_CROP_IMAGE && resultCode == RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                intent.putExtra("action", "2");
                intent.setData(AssetsHelper.photoUri);
                startActivity(intent);
            }
        } else// если не включено обрезание
        {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                AssetsHelper.photoUri=selectedImage;
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                intent.putExtra("action", "1");
                intent.setData(AssetsHelper.photoUri);
                //intent.putExtra("picturePath", picturePath);
                startActivity(intent);
            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK ) {
                photoUri = ImageUtils.getInstance().getTempUri(MainManageTabsActivity.this);
               AssetsHelper.photoUri=photoUri;
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                intent.putExtra("action", "2");
                intent.setData(AssetsHelper.photoUri);
                startActivity(intent);
            } else if (requestCode == ACTIVITY_COLOR_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
                int color = data.getIntExtra(
                ColorPickerActivity.RESULT_COLOR, 0xff000000);
                Intent intent = new Intent(getApplicationContext(), drawing.class);
                intent.putExtra("action", "3");
                intent.putExtra("color", color);
                startActivity(intent);
            }
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // this method is called after invoking 'showDialog' for the first time
        // here we initiate the corresponding DateSlideSelector and return the dialog to its caller
        switch (id) {
            case 1:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = this.getLayoutInflater().inflate(R.layout.chechbox, null);
                final CheckBox check = (CheckBox) view.findViewById(R.id.checkbox);
                check.setText("CROP");
                check.setChecked(AssetsHelper.Crop);
                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        AssetsHelper.Crop = b;
                        check.setChecked(b);

                    }
                });
                Button use_camera = (Button) view.findViewById(R.id.use_camera);
                use_camera.setText("Использовать камеру");
                use_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        photoUri = ImageUtils.getInstance().getTempUri(MainManageTabsActivity.this);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                photoUri);

                        startActivityForResult(intent, CAMERA_REQUEST);
                        dialog.dismiss();
                    }
                });
                Button use_gallery = (Button) view.findViewById(R.id.use_gallery);
                use_gallery.setText("Выбрать из галереи");
                use_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent i = new Intent(
//                                Intent.ACTION_PICK,
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                        dialog.dismiss();
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                Button use_colorpicker = (Button) view.findViewById(R.id.use_colorpicker);
                use_colorpicker.setText("Выбрать цвет");
                use_colorpicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(con, ColorPickerActivity.class);
                        i.putExtra(ColorPickerActivity.INTENT_DATA_INITIAL_COLOR, 0xff000000);
                        startActivityForResult(i, ACTIVITY_COLOR_PICKER_REQUEST_CODE);
                        dialog.dismiss();
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                builder.setView(view);
                return dialog = builder.create();
        }
        return null;
    }//создание диалога выбора

    @Override
    public void onBackPressed() //Перегрузка кнопки назад для каталога
    {
        if (getSupportActionBar().getSelectedNavigationIndex() == 0) {
            CatalogFragment tmp = ((CatalogFragment) ((TabsAdapter) pager.getAdapter()).instantiateItem(pager, 0));
            tmp.Back();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
