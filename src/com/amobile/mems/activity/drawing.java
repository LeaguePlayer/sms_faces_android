package com.amobile.mems.activity;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.amobile.mems.R;
import com.amobile.mems.database.mems_db;
import com.amobile.mems.helpers.*;
import com.android.camera.ImageManager;
import com.android.camera.Util;
import com.android.camera.gallery.IImage;
import com.android.camera.gallery.IImageList;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class drawing extends SherlockFragmentActivity implements View.OnClickListener, View.OnTouchListener,
        View.OnLongClickListener {
    public static Bitmap tmp_bitmap;
    private float mScaleFactor = 1.0f;
    private float mRotationDegrees = 0.f;
    private LinearLayout mems;
    private RelativeLayout photoContainer;
    private ImageView photo, imageDelete, imageCallHelp, imageHelp;
    saveDialog saveDialog;
    ArrayList<View> memsOnScreen;
    Button chooseCategory;
    mems_db db;
    boolean isPointer = false;
    Toast toast;
    View movedView, selectedView;
    int offset_x = 0, offset_y = 0, crashX = 0, crashY = 0, action;
    boolean touchFlag = false, isLongClick = false;
    ViewGroup.LayoutParams imageParams;
    ViewGroup parentContainer;
    Vibrator vibrator;
    private ScaleGestureDetector mScaleDetector;
    private RotateGestureDetector mRotateDetector;
    int ACTIVITY_CREATE_TEXT_REQUEST_CODE = 3;
    private Bitmap mBitmap;
    private ContentResolver mContentResolver;
    private IImageList mAllImages;
    private IImage mImage;
    private Bitmap.CompressFormat mOutputFormat =
            Bitmap.CompressFormat.JPEG; // only used with mSaveUri
    private Uri mSaveUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.drawing);
        mContentResolver = getContentResolver();
        SetCustomActionBar();
        init();

    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putSerializable("mems",memsOnScreen);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        memsOnScreen = (ArrayList<View>)savedInstanceState.getSerializable("mems");
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void SetCustomActionBar() {
        ActionBar ab = getSherlock().getActionBar();
        ab.setDisplayShowCustomEnabled(true);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.drawingactionbar, null);
        ab.setCustomView(customView);
        TextView title = (TextView) customView.findViewById(R.id.ttitle_application);
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/v_BD_Cartoon_Shout Cyr.ttf");
        title.setTypeface(font);
        title.setText("TROLLFACES");
    }

    public void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(new ColorDrawable(Color.TRANSPARENT));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        photo = (ImageView) findViewById(R.id.photo);
        imageCallHelp = (ImageView) findViewById(R.id.imageHelp);
        imageDelete = (ImageView) findViewById(R.id.imageDelete);
        imageHelp = (ImageView) findViewById(R.id.help);
        photoContainer = (RelativeLayout) findViewById(R.id.photoContainer);
        mems = (LinearLayout) findViewById(R.id.mems);
        chooseCategory = (Button) findViewById(R.id.chooseCategory);
        parentContainer = (ViewGroup) findViewById(R.id.parent);
        saveDialog = new saveDialog();

        photoContainer.setOnTouchListener(new MyTouchListener());
        ImageView textcreator = (ImageView) findViewById(R.id.imageText);
        textcreator.setOnClickListener(this);
        findViewById(R.id.imageTurn).setOnClickListener(this);
        imageDelete.setOnClickListener(this);
        imageCallHelp.setOnClickListener(this);
        imageHelp.setOnClickListener(this);
        findViewById(R.id.hsv).setOnTouchListener(new MyTouchListener());
        memsOnScreen = new ArrayList<View>();
        chooseCategory.setOnClickListener(this);
        db = new mems_db(drawing.this);
        mRotateDetector = new RotateGestureDetector(drawing.this, new RotateListener());
        mScaleDetector = new ScaleGestureDetector(drawing.this, new ScaleListener());
        chooseCategory("Все");

      if (this.getIntent().hasExtra("action"))
           action = Integer.parseInt(getIntent().getStringExtra("action"));
        if(action==1|action==2)
        {
            ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
            // imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            String imageUri = AssetsHelper.photoUri.toString();
//            Uri target = AssetsHelper.photoUri;
//            mAllImages = ImageManager.makeImageList(mContentResolver, target,
//                    ImageManager.SORT_ASCENDING);
//            mImage = mAllImages.getImageForUri(target);
//            if (mImage != null) {
//                // Don't read in really large bitmaps. Use the (big) thumbnail
//                // instead.
//                // TODO when saving the resulting bitmap use the
//                // decode/crop/encode api so we don't lose any resolution.
//                mBitmap = mImage.thumbBitmap(IImage.ROTATE_AS_NEEDED);
//            }
//            photo.setImageBitmap(mBitmap);
            if(action==1)
            {
                try {
                    photo.setImageBitmap(scaleImage(this,AssetsHelper.photoUri));
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else
            {
                imageLoader.displayImage(imageUri, photo);
            }
        }
        else
        {
            if (this.getIntent().hasExtra("color"))
                   photo.setBackgroundColor(this.getIntent().getIntExtra("color", 0xff000000));
        }
//        BitmapFactory.Options options = null;
//        int imageHeight;
//        int imageWidth;
//        switch (action) {
//            case 1:
//                int i = getOrientation(this, AssetsHelper.photoUri);
//                options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(getPath(AssetsHelper.photoUri), options);
//                imageHeight = options.outHeight;
//                imageWidth = options.outWidth;
//                try {
//                    mBitmap=scaleImage(loadResizedBitmap(getPath(AssetsHelper.photoUri), imageWidth / 2, imageHeight / 2, true), i);
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                break;
//            case 2:
//                options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                try {
//                    BitmapFactory.decodeStream(getContentResolver().openInputStream(AssetsHelper.photoUri), null, options);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                imageHeight = options.outHeight;
//                imageWidth = options.outWidth;
//                try {
//                    mBitmap=scaleImage(this, AssetsHelper.photoUri, (loadResizedBitmap(AssetsHelper.photoUri.getPath(), imageWidth / 2, imageHeight / 2, true)));
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                break;
//            case 3:
//                if (this.getIntent().hasExtra("color"))
//                    photo.setBackgroundColor(this.getIntent().getIntExtra("color", 0xff000000));
//                break;
//        }
    }
    public int getWidth(Boolean flag) {
        WindowManager w = getWindowManager();
        Integer Measuredwidth = 0;
        Integer Measuredheight = 0;
        Point size = new Point(); //размеры экрана под разные версии андроида
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            Measuredheight = size.y;
            Measuredwidth = size.x;
        } else {
            Display disp = w.getDefaultDisplay();
            Measuredheight = disp.getHeight();
            Measuredwidth = disp.getWidth();
        }
        if (flag)
            return Measuredwidth;
        else
            return Measuredheight;
    }

    private Bitmap decodeFile(Uri uri) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o);


            //The new size we want to scale to
            final int REQUIRED_SIZE = getWidth(false);

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("")
                .setIcon(R.drawable.open_button)
                .setOnMenuItemClickListener(new com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(com.actionbarsherlock.view.MenuItem item) {
                        //saveImage();
                        saveDialog.show(getSupportFragmentManager(), "saveDialog");
                        return false;  //To change body of implemented methods use File | Settings | File Templates.
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageDelete:
                removeLastMem();
                break;
            case R.id.chooseCategory:
                new categoryDialog().show(getSupportFragmentManager(), "categoryDialog");
                break;
            case R.id.imageText:
                Intent i = new Intent(drawing.this, createtext.class);
                startActivityForResult(i, ACTIVITY_CREATE_TEXT_REQUEST_CODE);
                break;
            case R.id.imageTurn:
                if (selectedView != null) {
                    if (selectedView.getClass().equals(PinchImageView.class)) {
                        Matrix matrixMirrorY;
                        BitmapDrawable bd = (BitmapDrawable) ((PinchImageView) selectedView).getDrawable();
                        Bitmap bitmap = bd.getBitmap();
                        int bmpWidth, bmpHeight;
                        float[] mirrorY =
                                {-1, 0, 0,
                                        0, 1, 0,
                                        0, 0, 1
                                };
                        float[] mirrorY2 =
                                {1, 0, 0,
                                        0, 1, 0,
                                        0, 0, 1
                                };
                        float[] mirror = mirrorY;
                        if (selectedView.getTag() != null) {
                            mirror = (float[]) selectedView.getTag();
                            if (Arrays.equals(mirror, mirrorY))
                                selectedView.setTag(mirrorY2);
                            else
                                selectedView.setTag(mirrorY);

                        } else
                            selectedView.setTag(mirrorY2);
                        matrixMirrorY = new Matrix();

                        bmpHeight = bitmap.getHeight();
                        bmpWidth = bitmap.getWidth();
                        matrixMirrorY.setValues(mirror);
                        Matrix matrix = new Matrix();
                        matrix.postConcat(matrixMirrorY);

                        Bitmap mirrorBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
                        ((PinchImageView) selectedView).setDrawable(new BitmapDrawable(getResources(), mirrorBitmap));
                    }
                }
                break;
            case R.id.imageHelp:
                imageHelp.setImageResource(R.drawable.helpimage);
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.bottom);
                imageHelp.setAnimation(anim);
                imageHelp.setVisibility(View.VISIBLE);
                break;
            case R.id.help:
                imageHelp.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        try {
            touchFlag = true;
            int[] values = new int[2];
            view.getLocationOnScreen(values);
            //selectedView = (ImageView) view;

            if ((((ViewGroup) view.getParent()).getId() != R.id.parent) && (((ViewGroup) view.getParent()).getId() != R.id.photoContainer)) {
                CatalogCell file = (CatalogCell) view.getTag();
                movedView = new PinchImageView(drawing.this, AssetsHelper.getDrawableByName(getAssets(), file.path,
                        file.fileName));

                photoContainer.addView(movedView);
                //parentContainer.addView(movedView);
                //selectedView = movedView;
                if (!memsOnScreen.contains((movedView)))
                    memsOnScreen.add(movedView);
                movedView.setOnTouchListener(this);
                vibrator.vibrate(50);

                ((PinchImageView)movedView).setSize(getPixels(60),getPixels(60));
               // RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(20,20);

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)movedView.getLayoutParams();
//                lp.width = getPixels(60);
//                lp.height = getPixels(60);
                //lp.setMargins(values[0], (int) movedView.getY() + photoContainer.getBottom(), 0, 0);
                lp.setMargins(photoContainer.getRight() / 2 - movedView.getWidth() / 2, (photoContainer.getBottom() - photoContainer.getTop()) / 2, 0, 0);

                //movedView.setLayoutParams(lp);
//                movedView.getLayoutParams().width = 20;
//                movedView.getLayoutParams().height = 20;


                setSelectedView(movedView);

            }
        } catch (Exception ex) {
            Log.d("mems", ex.getMessage());
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (((ViewGroup) v.getParent()).getId() == R.id.photoContainer) {
                    touchFlag = true;
                    offset_x = (int) event.getX();
                    offset_y = (int) event.getY();
                    movedView = v;

                    selectedView.setBackgroundResource(android.R.color.transparent);
                    selectedView = v;
                    selectedView.setBackgroundResource(R.drawable.border);

                    if (selectedView.getClass().equals(PinchImageView.class))
                        mRotationDegrees = ((PinchImageView) selectedView).getRotationDegrees();
                    else if (selectedView.getTag() != null)
                        mRotationDegrees = (Float) selectedView.getTag();
                    else
                        mRotationDegrees = 0.f;

                } else if (((ViewGroup) v.getParent()).getId() == R.id.mems) {
                    offset_x = (int) event.getX();
                    offset_y = (int) event.getY();
                    isLongClick = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                touchFlag = false;
                isPointer = true;
                break;

            case MotionEvent.ACTION_UP:
                if (isLongClick && movedView != null) {
                    if (((ViewGroup) movedView.getParent()).getId() == R.id.parent) {
                        selectedView = movedView;
                        removeLastMem();
                        movedView = null;
                    }
                }
//                    imageDelete.performClick();
                isLongClick = false;
                break;
        }
//        if (((ViewGroup) v.getParent()).getId() == R.id.photoContainer)
//            return true;
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_CREATE_TEXT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (AssetsHelper.text != null) {
                TextView tv = AssetsHelper.text;
                photoContainer.addView(tv);
                tv.setOnTouchListener(this);
                memsOnScreen.add(tv);
                setSelectedView(tv);
            }
        }
    }

//    void showSaveDialog(){
//        AlertDialog.Builder adb = new AlertDialog.Builder(drawing.this);
//        adb.sets
//    }

    public void setSelectedView(View view) {
        if (selectedView != null)
            selectedView.setBackgroundResource(android.R.color.transparent);
        selectedView = view;
        selectedView.setBackgroundResource(R.drawable.border);
    }

    public void saveImage() {
        try {
            if (selectedView != null)
                selectedView.setBackgroundResource(android.R.color.transparent);
            photoContainer.buildDrawingCache();
            Bitmap b = photoContainer.getDrawingCache();
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "image created from smsfaces", "");
            Toast.makeText(drawing.this, "Изображение успешно сохранено", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (selectedView != null)
                selectedView.setBackgroundResource(R.drawable.border);
        }
    }

    public void sendMMS() {
        try {
            if (selectedView != null)
                selectedView.setBackgroundResource(android.R.color.transparent);
            photoContainer.buildDrawingCache();
            Bitmap b = photoContainer.getDrawingCache();

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
        } finally {
            if (selectedView != null)
                selectedView.setBackgroundResource(R.drawable.border);
        }
    }

    public void removeLastMem() {
        ViewGroup parent;
        if (selectedView == null) {
            if (memsOnScreen.size() > 0) {
                View mem = memsOnScreen.get(memsOnScreen.size() - 1);
                parent = (ViewGroup) mem.getParent();
                parent.removeView(mem);
                memsOnScreen.remove(memsOnScreen.size() - 1);
            }
        } else {
            parent = (ViewGroup) selectedView.getParent();
            parent.removeView(selectedView);
            if (memsOnScreen.contains(selectedView))
                memsOnScreen.remove(memsOnScreen.indexOf(selectedView));
        }
        if (memsOnScreen.size() == 0)
            selectedView = null;
        else
            selectedView = memsOnScreen.get(memsOnScreen.size() - 1);
    }

    private int getPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
                r.getDisplayMetrics());
        return px;
    }

    public void chooseCategory(String category) {
        chooseCategory.setText(category);
        mems.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getPixels(60), getPixels(60));

        db.open();
        if (category.equals("Все")) {
            for (String folder : db.getAllFolders())
                for (CatalogCell file : db.GetAllFilesInFolder(folder)) {
                    ImageView iv = new ImageView(drawing.this);
                    iv.setLayoutParams(layoutParams);
                    iv.setPadding(5, 5, 5, 5);
                    String imageUri = "assets://" + AssetsHelper.MainPath + "/" + file.path + "/" + file.fileName; // from assets
                    ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
                    imageLoader.displayImage(imageUri, iv);
                    iv.setTag(file);
                    iv.setOnLongClickListener(this);
                    //iv.setOnTouchListener(this);
                    mems.addView(iv);
                }
        } else if (category.equals("Избранное")) {
            for (CatalogCell file : db.GetAllFavorites()) {
                ImageView iv = new ImageView(drawing.this);
                iv.setLayoutParams(layoutParams);
                iv.setPadding(5, 5, 5, 5);
                String imageUri = "assets://" + AssetsHelper.MainPath + "/" + file.path + "/" + file.fileName; // from assets
                ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
                imageLoader.displayImage(imageUri, iv);
                iv.setTag(file);
                iv.setOnLongClickListener(this);
                //iv.setOnTouchListener(this);
                mems.addView(iv);
            }
        } else
            for (CatalogCell file : db.GetAllFilesInFolder(category)) {
                ImageView iv = new ImageView(drawing.this);
                iv.setLayoutParams(layoutParams);
                iv.setPadding(5, 5, 5, 5);
                String imageUri = "assets://" + AssetsHelper.MainPath + "/" + file.path + "/" + file.fileName; // from assets
                ImageLoader imageLoader = ImageLoader.getInstance(); // Получили экземпляр
                imageLoader.displayImage(imageUri, iv);
                iv.setTag(file);
                //iv.setOnTouchListener(this);
                iv.setOnLongClickListener(this);
                mems.addView(iv);
            }

        db.close();
    }

    class categoryDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ArrayList<String> categories = new ArrayList<String>();
            categories.add("Все");
            categories.add("Избранное");
            Collections.addAll(categories, AssetsHelper.getFolders(getAssets()));

            AlertDialog.Builder adb = new AlertDialog.Builder(drawing.this);
            adb.setTitle(R.string.chooseCategory)
                    .setItems(categories.toArray(new String[categories.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            chooseCategory(categories.get(i));
                        }
                    });
            return adb.create();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    class saveDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ArrayList<String> items = new ArrayList<String>();
            items.add("Отправить ММС");
            items.add("Сохранить на телефон");

            AlertDialog.Builder adb = new AlertDialog.Builder(drawing.this);
            adb.setTitle(R.string.chooseCategory)
                    .setItems(items.toArray(new String[items.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    sendMMS();
                                    break;
                                case 1:
                                    saveImage();
                                    break;
                            }
                        }
                    });
            return adb.create();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScaleDetector.onTouchEvent(event);
            mRotateDetector.onTouchEvent(event);
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_MOVE:
                    Log.d("mems", "move");
                    if (touchFlag && !isPointer && movedView != null) {
                        if (!mScaleDetector.isInProgress()) {
                            int x, y;

                            if (v.getId() != R.id.hsv) {
                                x = (int) event.getX() - offset_x;
                                y = (int) event.getY() - offset_y;
                            } else {
                                x = (int) event.getX() - offset_x;
                                y = photoContainer.getBottom() + (int) event.getY() + 60;
                            }
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) movedView.getLayoutParams();

                            lp.setMargins(x, y, 0, 0);
                            movedView.setLayoutParams(lp);
                        }

                        return v.getId() == R.id.hsv;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    touchFlag = false;
                    isPointer = true;
                    break;

                case MotionEvent.ACTION_UP:
                    isPointer = false;
                    isLongClick = false;
                    touchFlag = false;
//                    try {
//                        if (touchFlag) {
//                            int y;
//                            if (v.getId() == R.id.hsv) {
//                                y = photoContainer.getBottom() + (int) event.getY() - 120;
//                                if ((y + 250 < photoContainer.getBottom()) && (y + 150 > photoContainer.getTop())) {
//                                    parentContainer.removeView(movedView);
//                                    photoContainer.addView(movedView);
//
//                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(getPixels(50), RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                    lp.setMargins((int) movedView.getX(), y, 0, 0);
//                                    movedView.setLayoutParams(lp);
//
//                                    setSelectedView(movedView);
//                                }
//                            }
//                            touchFlag = false;
//                            // movedView = null;
//                            if (v.getId() == R.id.hsv)
//                                return true;
//                        }
//                        //movedView = null;
//                        break;
//                    } catch (Exception ex) {
//                        Log.d("mems", ex.getMessage());
//                    }
//            if (v.getId() == R.id.hsv)
//                return  false;
            }

            return v.getId() != R.id.hsv;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            if (selectedView != null)
                if (selectedView.getClass().equals(PinchImageView.class))
                    ((PinchImageView) selectedView).refresh(mScaleFactor);
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees -= detector.getRotationDegreesDelta();
            if (selectedView.getClass().equals(PinchImageView.class))
                ((PinchImageView) selectedView).setRotationDegrees(mRotationDegrees);
            else
                selectedView.setTag(mRotationDegrees);
            selectedView.setRotation(mRotationDegrees);
            return true;
        }
    }

    public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > 1024 || rotatedHeight > 1024) {
            float widthRatio = ((float) rotatedWidth) / ((float) 1024);
            float heightRatio = ((float) rotatedHeight) / ((float) 1024);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = context.getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Bitmap scaleImage(Context context, Uri photoUri, Bitmap bitmap) throws IOException {
//        Matrix matrix = new Matrix();
//
//        ExifInterface exifReader = new ExifInterface(photoUri.getPath());
//
//        int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//
//        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
//
//            // Do nothing. The original image is fine.
//
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//
//            matrix.postRotate(90);
//
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//
//            matrix.postRotate(180);
//
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//
//            matrix.postRotate(270);
//
//        }
        Matrix matrix = new Matrix();
        float rotation = this.rotationForImage(context, photoUri);
        if (rotation != 0f) {
            matrix.preRotate(rotation);
        }
        try {
            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmap != b2) {
                bitmap.recycle();
                bitmap = b2;
            }
        } catch (OutOfMemoryError ex) {
            throw ex;
        }
        return bitmap;

    }

    public Bitmap scaleImage(Bitmap bitmap, int orientation) throws IOException {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) orientation);
//        Matrix matrix = new Matrix();
//        float rotation = this.rotationForImage(context, photoUri);
//        if (rotation != 0f) {
//            matrix.preRotate(rotation);
//        }
        try {
            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (bitmap != b2) {
                bitmap.recycle();
                bitmap = b2;
            }
        } catch (OutOfMemoryError ex) {
            throw ex;
        }
        return bitmap;

    }

    public static Bitmap loadResizedBitmap(String filename, int width, int height, boolean exact) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);
        if (options.outHeight > 0 && options.outWidth > 0) {
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            while (options.outWidth / options.inSampleSize > width
                    && options.outHeight / options.inSampleSize > height) {
                options.inSampleSize++;
            }
            options.inSampleSize--;

            bitmap = BitmapFactory.decodeFile(filename, options);
            if (bitmap != null && exact) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }
        return bitmap;
    }

    public static float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(
                    uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                Log.e("TAG", "Error checking exif", e);
            }
        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
