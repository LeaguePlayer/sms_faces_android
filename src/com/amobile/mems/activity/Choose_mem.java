package com.amobile.mems.activity;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.amobile.mems.R;
import com.amobile.mems.database.mems_db;
import com.amobile.mems.helpers.AssetsHelper;
import com.amobile.mems.helpers.CatalogCell;
import com.amobile.mems.helpers.ImageUtils;
import com.android.camera.CropImage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 04.05.13
 * Time: 0:07
 * To change this template use File | Settings | File Templates.
 */
public class Choose_mem extends Activity {
    ImageView photo;
    String folder;
    String name;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;
    private final static int ACTIVITY_COLOR_PICKER_REQUEST_CODE = 3;
    private final static int ACTIVITY_CROP_IMAGE = 4;
    Button send_mms;
    Button save_togallery;
    Button addtoFavorite;
    Button camera_mem;
    public boolean inFavorites = false;
    CatalogCell tmp;
    mems_db db;
    Context con;
    public AlertDialog dialog;
    ContentValues values;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_mems);
        con = this;
        db = new mems_db(this);
        Init();
        GetExtra();
        SetOnClickListeners();
        photo.setDrawingCacheEnabled(true);
        String imageUri = "assets://" + AssetsHelper.MainPath + "/" + tmp.path + "/" + tmp.fileName; // from assets
        ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
        //imageLoader.init(ImageLoaderConfiguration.createDefault(con));
        imageLoader.displayImage(imageUri, photo);//
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(AssetsHelper.fromcrop)
        {
            AssetsHelper.fromcrop=false;
            Intent intent = new Intent(getApplicationContext(), drawing.class);
            intent.putExtra("action", "1");
            //intent.putExtra("picturePath", picturePath);
            startActivity(intent);
        }
    }
    public void Init() {
        photo = (ImageView) findViewById(R.id.photo_mem);
        send_mms = (Button) findViewById(R.id.sendmms);
        save_togallery = (Button) findViewById(R.id.save);
        addtoFavorite = (Button) findViewById(R.id.addtofavorite);
        camera_mem = (Button) findViewById(R.id.photo_mem_detail);
    }
    public void sendMMS() {
        try {
            if (photo != null)
            photo.buildDrawingCache();
            Bitmap b = photo.getDrawingCache();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "cached.png");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            Uri uri = Uri.parse(f.toURI().toString());
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra("sms_body", "");
            sendIntent.setType("image/png");
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(sendIntent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void SetOnClickListeners() {
        send_mms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sendMMS();
                } catch (Exception ex) {

                }
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        save_togallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    MediaStore.Images.Media.insertImage(con.getContentResolver(), BitmapFactory.decodeStream(getAssets().open(AssetsHelper.MainPath + "/" + folder + "/" + name)), tmp.fileName, "");
                    AlertDialog alertDialog = new AlertDialog.Builder(con).create();
                    alertDialog.setTitle("");
                    alertDialog.setMessage("Изображение сохранено!");
                    alertDialog.setButton2("Ок", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    alertDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        addtoFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inFavorites) {
                    db.open();
                    db.deleteFromFavorites(tmp.id.toString());
                    db.close();
                    inFavorites = false;
                    addtoFavorite.setText("Добавить в избранное");
                } else {
                    db.open();
                    db.addToFavorite(tmp);
                    db.close();
                    inFavorites = true;
                    addtoFavorite.setText("Удалить из избранного");
                }

            }
        });
        camera_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });
    }

    public void GetExtra() {
        if (getIntent().hasExtra("folder") && getIntent().hasExtra("name")) {
            folder = getIntent().getStringExtra("folder");
            name = getIntent().getStringExtra("name");
            tmp = new CatalogCell();
            tmp.isFolder = false;
            tmp.fileName = name;
            tmp.path = folder;
            db.open();
            tmp.id = db.isFavorite(tmp);
            db.close();
            if (tmp.id > 0) {
                inFavorites = true;
                addtoFavorite.setText("Удалить из избранного");
                addtoFavorite.invalidate();
            }
            if (tmp.id == 0) {
                inFavorites = false;
                addtoFavorite.setText("Добавить в избранное");
            }
        }
    }

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
                photoUri = ImageUtils.getInstance().getTempUri(Choose_mem.this);
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
                photoUri = ImageUtils.getInstance().getTempUri(Choose_mem.this);
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
                        photoUri = ImageUtils.getInstance().getTempUri(Choose_mem.this);
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


    public void toStartEditfromGallery(Uri imagepath) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imagepath,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Intent intent = new Intent(getApplicationContext(), CropImage.class);
        intent.putExtra("action", "1");
        intent.putExtra("picturePath", picturePath);
        intent.putExtra("scale", false);
        startActivity(intent);
    } //запуск активити CROP

    public void toStartEditfromCamera(Bitmap photo) {
        AssetsHelper.tmp_photo = photo;
        Intent intent = new Intent(getApplicationContext(), CropImage.class);
        intent.putExtra("action", "2");
        intent.putExtra("scale", false);
        startActivity(intent);
    } //запуск активити CROP



}