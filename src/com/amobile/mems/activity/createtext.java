package com.amobile.mems.activity;

import afzkl.development.mColorPicker.ColorPickerActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.amobile.mems.R;
import com.amobile.mems.helpers.AssetsHelper;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 13.05.13
 * Time: 0:54
 * To change this template use File | Settings | File Templates.
 */
public class createtext extends Activity {
    EditText text;
    EditText size;
    Button color;
    int refcolor = 0xff000000;
    Button finish;
    Context con;
    private final static int ACTIVITY_COLOR_PICKER_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.textview);
        con = this;
        Init();
    }

    public void Init() {
        text = (EditText) findViewById(R.id.text);
        size = (EditText) findViewById(R.id.textheight);
        color = (Button) findViewById(R.id.colorchooser);
        color.setBackgroundColor(refcolor);
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(con, ColorPickerActivity.class);
                i.putExtra(ColorPickerActivity.INTENT_DATA_INITIAL_COLOR, 0xff000000);
                startActivityForResult(i, ACTIVITY_COLOR_PICKER_REQUEST_CODE);//To change body of implemented methods use File | Settings | File Templates.
            }
        });
        finish = (Button) findViewById(R.id.ok_button);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!text.getText().equals("")) {
                    TextView result = new TextView(con);
                    result.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) Integer.parseInt(size.getText().toString()));
                    result.setText(text.getText());
                    result.setTextColor(refcolor);
                    AssetsHelper.text = result;
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_COLOR_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            int color = data.getIntExtra(
                    ColorPickerActivity.RESULT_COLOR, 0xff000000);
            refcolor = color;
            this.color.setBackgroundColor(color);
        }
    }
}
