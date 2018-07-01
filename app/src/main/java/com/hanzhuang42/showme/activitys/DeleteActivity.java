package com.hanzhuang42.showme.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hanzhuang42.showme.R;
import com.hanzhuang42.showme.util.DbUtility;
import com.hanzhuang42.showme.util.MyToast;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class DeleteActivity extends AppCompatPreferenceActivity {

    public static final String[] keys = {
            "if_delete_pictures", "if_delete_all_type",
            "if_delete_food", "if_delete_car",
            "if_delete_logo", "if_delete_animal",
            "if_delete_plant", "delete_ensure"
    };

    public final static Map<String, String> modeMap = new HashMap<>();
    private static SharedPreferences preferences;
    private static final String PREFERENCE_NAME = "com.hanzhuang42.showme_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.preference);
        preferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        findPreference(keys[1]).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean b = (Boolean) newValue;
                for(int i = 2; i< 7;i++){
                    findPreference(keys[i]).setEnabled(!b);
                }
                return true;
            }
        });

        @SuppressLint("HandlerLeak") final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                Boolean res = data.getBoolean("res");
                if(res) {
                    finish();
                    MyToast.showToast(DeleteActivity.this, "删除成功！", Toast.LENGTH_SHORT);
                }
            }
        };

        findPreference(keys[7]).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteActivity.this,R.style.Theme_AppCompat_Light_Dialog_Alert);
                builder.setTitle("确认删除");
                builder.setMessage("删除之后将无法恢复，是否确认删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Runnable() {
                            @Override
                            public void run() {
                                boolean res = deleteChoose();
                                Message msg = new Message();
                                Bundle data = new Bundle();
                                data.putBoolean("res", res);
                                msg.setData(data);
                                handler.sendMessage(msg);
                            }
                        }.run();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                return true;
            }
        });
    }

    private boolean deleteChoose(){
        boolean isChoosed[] = new boolean[8];
        boolean hasType = false;
        for(int i = 0;i<8;i++){
            isChoosed[i] = preferences.getBoolean(keys[i], false);
            if(i > 0 && i < 7) {
                hasType = hasType | isChoosed[i];
                if(isChoosed[1])
                    isChoosed[i] = true;
            }
        }
        if(!hasType){
            MyToast.showToast(DeleteActivity.this, "请选择类型后再进行删除！", Toast.LENGTH_SHORT);
            return false;
        } else{
            for(int i = 2;i<7;i++){
                if(isChoosed[i]) {
                    int type = i - 2;
                    if(isChoosed[0])
                        DbUtility.deleteAllByType(DeleteActivity.this,type);
                    else{
                        DbUtility.deleteByType(type);
                    }
                }
            }
        }
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
        }
        return true;
    }
}
