package com.ard.moritanian.androino;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Moritanian on 2017/06/11.
 */

public class SettingsActivity extends AppCompatActivity {

    public static final String SETTINGS_PREF_KEY = "settings_pref";
    private EditText viewUrlEditText;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences pref = getSharedPreferences(SETTINGS_PREF_KEY, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        prefEditor = pref.edit();

        // アクションバーに前画面に戻る機能をつける
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Settings");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewUrlEditText = (EditText)findViewById(R.id.viewUrlEditText);
        String viewUrl = pref.getString(getString(R.string.view_url_key), MainActivity.defaultURL);
        viewUrlEditText.setText(viewUrl, TextView.BufferType.EDITABLE);
        viewUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                prefEditor.putString(getString(R.string.view_url_key) ,s.toString());
                prefEditor.commit();

            }
        });

        Switch fullScreenSwitch = (Switch)findViewById(R.id.full_screen_toggle);
        fullScreenSwitch.setChecked(getIsFullScreen(this));

        fullScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean(getString(R.string.is_full_screen_key), isChecked);
                prefEditor.commit();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getViewURL(Context con, String defaultURL){
        SharedPreferences pref = con.getSharedPreferences(SETTINGS_PREF_KEY , MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        return pref.getString(con.getString(R.string.view_url_key), defaultURL);
    }

    public static Boolean getIsFullScreen(Context con){
        SharedPreferences pref = con.getSharedPreferences(SETTINGS_PREF_KEY , MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        String s = con.getString(R.string.is_full_screen_key );
        return pref.getBoolean(s, false);
    }

}
