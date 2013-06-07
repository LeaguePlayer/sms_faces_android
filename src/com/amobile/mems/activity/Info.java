package com.amobile.mems.activity;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.amobile.mems.R;
import com.amobile.mems.helpers.AssetsHelper;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 10.05.13
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class Info extends Activity {
    TextView title;
    TextView application_title;
    TextView version_title;
    TextView developer_title;
    TextView designer_title;
    TextView manufacture_title;
    Button mail;
    Button sharing;
    Context con;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int CAMERA_REQUEST = 2;
    private final static int ACTIVITY_COLOR_PICKER_REQUEST_CODE = 3;
    ViewPager pager;
    public AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        con=this;
        Init();
        SetONClickListeners();
    }
    public void Init()
    {
        mail=(Button)findViewById(R.id.back_report);
        sharing=(Button)findViewById(R.id.sharing);
        title=(TextView)findViewById(R.id.title_info);
        application_title=(TextView)findViewById(R.id.apptitle);
        version_title=(TextView)findViewById(R.id.vertitle);
        developer_title=(TextView)findViewById(R.id.devtitle);
        designer_title=(TextView)findViewById(R.id.designtitle);
        manufacture_title=(TextView)findViewById(R.id.manufacturetitle);
        Typeface font = Typeface.createFromAsset(getAssets(), "Fonts/v_BD_Cartoon_Shout Cyr.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "Fonts/domino.ttf");
        title.setTypeface(font);
        application_title.setTypeface(font);
        version_title.setTypeface(font2);
        developer_title.setTypeface(font2);
        designer_title.setTypeface(font2);
        manufacture_title.setTypeface(font2);
    }
    public void SetONClickListeners()
    {
       mail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(Intent.ACTION_SEND);
               i.setType("message/rfc822");
               i.putExtra(Intent.EXTRA_EMAIL, new String[]{"fuks.jacob.com@gmail.com"});
               i.putExtra(Intent.EXTRA_SUBJECT, "Обратная связь Android-Mems");
               //i.putExtra(Intent.EXTRA_TEXT   , "body of email");
               try {
                   startActivity(Intent.createChooser(i, "Send mail..."));
               } catch (android.content.ActivityNotFoundException ex) {
                   Toast.makeText(con, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
               } //To change body of implemented methods use File | Settings | File Templates.//To change body of implemented methods use File | Settings | File Templates.
           }
       });
       sharing.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent shareIntent = new Intent(Intent.ACTION_SEND);
               shareIntent.setType("text/plain");
               startActivity(Intent.createChooser(shareIntent, "Поделится"));//To change body of implemented methods use File | Settings | File Templates.
           }
       });
    }
}