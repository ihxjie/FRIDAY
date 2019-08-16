package com.xjie.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.security.spec.ECField;
import java.util.Calendar;
import java.util.TimeZone;

public class LoginToSystem extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText stuNum,stuPwd,stuPwd_webvpn;
    private Button setCourse,setExam,setGrade;
    private CheckBox rememberPass;
    private Connect connect;
    private String login_message = "出错了";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_system);
        ActivityCollector.addActivity(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("登录到教务管理系统");
        setSupportActionBar(toolbar);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        stuNum = (EditText) findViewById(R.id.stuNum);
        stuPwd = (EditText) findViewById(R.id.stuPwd);
        stuPwd_webvpn = (EditText)findViewById(R.id.stuPwd_webvpn);

        stuNum.setText(pref.getString("account", ""));
        stuPwd.setText(pref.getString("password", ""));
        stuPwd_webvpn.setText(pref.getString("password_webvpn",""));



        setCourse = (Button) findViewById(R.id.set_course);
        setCourse.setOnClickListener(new View.OnClickListener() {

            Calendar calendar = Calendar.getInstance();

            final int EnterYEAR = pref.getInt("YEAR",calendar.get(Calendar.YEAR));
            final int EnterMonth = pref.getInt("MONTH",calendar.get(Calendar.MONTH));
            final int EnterDayOfMonth = pref.getInt("DayOfMonth",calendar.get(Calendar.DAY_OF_MONTH));

            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(LoginToSystem.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        editor = pref.edit();

                        editor.putInt("YEAR",year);
                        editor.putInt("MONTH",monthOfYear);
                        editor.putInt("DayOfMonth",dayOfMonth);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        editor.putInt("begin_week", cal.get(Calendar.WEEK_OF_YEAR));
                        editor.apply();

                    }
                }, EnterYEAR, EnterMonth, EnterDayOfMonth);
                datePickerDialog.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_login_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.confirm:
                final ProgressDialog progressDialog = new ProgressDialog(LoginToSystem.this);
                progressDialog.setTitle("正在登录中");
                progressDialog.setMessage("获取登录信息");
                progressDialog.setCancelable(true);
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if (stuPwd_webvpn.getText().toString().equals("")){
                                connect = new Connect(stuNum.getText().toString(),stuPwd.getText().toString(),stuPwd.getText().toString());
                            }else {
                                connect = new Connect(stuNum.getText().toString(),stuPwd.getText().toString(),stuPwd_webvpn.getText().toString());
                            }

                            login_message = connect.login();

                            editor = pref.edit();
                            editor.putString("ENTER_YEAR",connect.getEnterYear());
                            editor.apply();

                        }catch (Exception e){

                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(login_message.equals("SUCCESS")){
                                    editor = pref.edit();

                                    editor.putString("account",stuNum.getText().toString());
                                    editor.putString("password",stuPwd.getText().toString());
                                    editor.putString("password_webvpn",stuPwd_webvpn.getText().toString());

                                    editor.apply();

                                    Toast.makeText(LoginToSystem.this,login_message,Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(LoginToSystem.this,MainActivity.class));

                                }
                                else{
                                    Toast.makeText(LoginToSystem.this,login_message,Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

                    }
                }).start();
                break;
            default:

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getString("account","").equals("")) {
            ActivityCollector.finishAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
