package com.xjie.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private List<CourseTable> courseTableList = new ArrayList<>();
    private List<GradeTable> gradeTableList = new ArrayList<>();
    private List<ExamTable> examTableList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefresh;
    private CourseTableAdapter adapter;
    private GradeTableAdapter gradeTableAdapter;
    private ExamTableAdapter examTableAdapter;
    private DrawerLayout mDrawerLayout;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Connect connect;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private String stuNum,stuPwd,stuPwd_webvpn,exam_xnm,exam_xqm,grade_xnm,grade_xqm;


    public static final int COURSE_TEXT = 1;
    public static final int EXAM_TEXT = 2;
    public static final int MARK_TEXT = 3;
    public int TEXT = COURSE_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getString("account","").equals("")){
            Intent intent = new Intent(MainActivity.this,LoginToSystem.class);
            startActivity(intent);
        }
        init();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);//出现错位请把图片放到xxxhdpi中
        setSupportActionBar(toolbar);

        final int end_week = pref.getInt("end_week",0);


        getSupportActionBar().setTitle("第"+end_week+"周 "+getDayOfWeek());

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_courseTable);
        navigationView.setItemIconTintList(null);//解决侧边栏使用图标没有颜色问题

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_courseTable:

                        toolbar.setTitle("第"+end_week+"周 "+getDayOfWeek());
                        mFloatingActionButton.show();

                        Message message = new Message();
                        message.what = COURSE_TEXT;
                        TEXT = COURSE_TEXT;
                        handler.sendMessage(message);
                        break;
                    case R.id.nav_examTable:

                        int exam_xnm_1 = pref.getInt("EXAMXNM",0) + 1;
                        int exam_xqm_1 = pref.getInt("EXAMXQM_1",1);
                        toolbar.setTitle(exam_xnm+"-"+exam_xnm_1+" 第"+exam_xqm_1+"学期");
                        mFloatingActionButton.show();

                        TEXT = EXAM_TEXT;
                        message = new Message();
                        message.what = EXAM_TEXT;
                        handler.sendMessage(message);
                        break;
                    case R.id.nav_gradeTable:

                        int grade_xnm_1 = pref.getInt("GRADEXNM",0) + 1;
                        int grade_xqm_1 = pref.getInt("GRADEXQM_1",1);
                        toolbar.setTitle(grade_xnm+"-"+grade_xnm_1+" 第"+grade_xqm_1+"学期");
                        mFloatingActionButton.show();

                        TEXT = MARK_TEXT;

                        message = new Message();
                        message.what = MARK_TEXT;
                        handler.sendMessage(message);
                        break;
                        default:


                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TEXT == COURSE_TEXT){
                    refreshCourseTable();
                }
                else if (TEXT == EXAM_TEXT){
                    refreshExamTable();
                }
                else if (TEXT == MARK_TEXT){
                    refreshGradeTable();
                }

            }
        });
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycleView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0 && mFloatingActionButton.getVisibility() == VISIBLE){
                    mFloatingActionButton.hide();
                }else if (dy < 0 && mFloatingActionButton.getVisibility() != VISIBLE){
                    mFloatingActionButton.show();
                }
            }
        });
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TEXT == EXAM_TEXT){

                    Calendar calendar = Calendar.getInstance();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final int enter_year = Integer.parseInt(pref.getString("ENTER_YEAR",""));
                    builder.setTitle("请选择查询的学期");
                    final String[] term = new String[]{
                            String.valueOf(enter_year)+"-"+String.valueOf(enter_year+1)+" 第1学期",String.valueOf(enter_year)+"-"+String.valueOf(enter_year+1)+" 第2学期",
                            String.valueOf(enter_year+1)+"-"+String.valueOf(enter_year+2)+" 第1学期",String.valueOf(enter_year+1)+"-"+String.valueOf(enter_year+2)+" 第2学期",
                            String.valueOf(enter_year+2)+"-"+String.valueOf(enter_year+3)+" 第1学期",String.valueOf(enter_year+2)+"-"+String.valueOf(enter_year+3)+" 第2学期",
                            String.valueOf(enter_year+3)+"-"+String.valueOf(enter_year+4)+" 第1学期",String.valueOf(enter_year+3)+"-"+String.valueOf(enter_year+4)+" 第2学期"};

                    int choose_item = pref.getInt("EXAM_choose_item",0);
                    builder.setSingleChoiceItems(term, choose_item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor = pref.edit();
                            if (i%2 == 0){
                                editor.putInt("EXAMXNM",i/2 + enter_year);
                                editor.putInt("EXAMXQM",3);
                                editor.putInt("EXAMXQM_1",1);
                                editor.putInt("EXAM_choose_item",i);
                                editor.apply();

                            }else {
                                editor.putInt("EXAMXNM",i/2 + enter_year);
                                editor.putInt("EXAMXQM",12);
                                editor.putInt("EXAMXQM_1",2);
                                editor.putInt("EXAM_choose_item",i);
                                editor.apply();

                            }

                            dialogInterface.dismiss();
                            init();
                            refreshExamTable();
                            swipeRefresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefresh.setRefreshing(true);
                                }
                            });

                        }
                    });

                    builder.show();
                }
                else if (TEXT == MARK_TEXT){
                    Calendar calendar = Calendar.getInstance();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final int enter_year = Integer.parseInt(pref.getString("ENTER_YEAR",""));
                    builder.setTitle("请选择查询的学期");
                    final String[] term = new String[]{
                            String.valueOf(enter_year)+"-"+String.valueOf(enter_year+1)+" 第1学期",String.valueOf(enter_year)+"-"+String.valueOf(enter_year+1)+" 第2学期",
                            String.valueOf(enter_year+1)+"-"+String.valueOf(enter_year+2)+" 第1学期",String.valueOf(enter_year+1)+"-"+String.valueOf(enter_year+2)+" 第2学期",
                            String.valueOf(enter_year+2)+"-"+String.valueOf(enter_year+3)+" 第1学期",String.valueOf(enter_year+2)+"-"+String.valueOf(enter_year+3)+" 第2学期",
                            String.valueOf(enter_year+3)+"-"+String.valueOf(enter_year+4)+" 第1学期",String.valueOf(enter_year+3)+"-"+String.valueOf(enter_year+4)+" 第2学期"};

                    int choose_item = pref.getInt("GRADE_choose_item",0);
                    builder.setSingleChoiceItems(term, choose_item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            editor = pref.edit();
                            if (i%2 == 0){
                                editor.putInt("GRADEXNM",i/2 + enter_year);
                                editor.putInt("GRADEXQM",3);
                                editor.putInt("GRADEXQM_1",1);
                                editor.putInt("GRADE_choose_item",i);
                                editor.apply();

                            }else {
                                editor.putInt("GRADEXNM",i/2 + enter_year);
                                editor.putInt("GRADEXQM",12);
                                editor.putInt("GRADEXQM_1",2);
                                editor.putInt("GRADE_choose_item",i);
                                editor.apply();

                            }

                            dialogInterface.dismiss();
                            init();
                            refreshGradeTable();
                            swipeRefresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    swipeRefresh.setRefreshing(true);
                                }
                            });

                        }
                    });

                    builder.show();
                }
            }
        });

    }
    private void init(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        editor = pref.edit();

        stuNum = pref.getString("account","");
        stuPwd = pref.getString("password","");
        stuPwd_webvpn = pref.getString("password_webvpn","");
        if(stuPwd_webvpn.equals("")){
            stuPwd_webvpn = stuPwd;
        }

        Calendar cal = Calendar.getInstance();

        exam_xnm = String.valueOf(pref.getInt("EXAMXNM",0));
        exam_xqm = String.valueOf(pref.getInt("EXAMXQM",3));
        grade_xnm = String.valueOf(pref.getInt("GRADEXNM",0));
        grade_xqm = String.valueOf(pref.getInt("GRADEXQM",3));


        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

        int end_week = cal.get(Calendar.WEEK_OF_YEAR);
        int begin_week = pref.getInt("begin_week",0);
        if(end_week - begin_week < 0){
            end_week += 53;
        }
        end_week = end_week - begin_week + 1;
        editor.putInt("end_week",end_week);
        editor.apply();
        toolbar.setTitle("第"+end_week+"周 "+getDayOfWeek());



        if (TEXT == COURSE_TEXT){
            toolbar.setTitle("第"+end_week+"周 "+getDayOfWeek());
        }
        else if (TEXT == EXAM_TEXT){
            int exam_xnm_1 = pref.getInt("EXAMXNM",0) + 1;
            int exam_xqm_1 = pref.getInt("EXAMXQM_1",1);
            toolbar.setTitle(exam_xnm+"-"+exam_xnm_1+" 第"+exam_xqm_1+"学期");
        }
        else if (TEXT == MARK_TEXT){
            int grade_xnm_1 = pref.getInt("GRADEXNM",0) + 1;
            int grade_xqm_1 = pref.getInt("GRADEXQM_1",1);
            toolbar.setTitle(grade_xnm+"-"+grade_xnm_1+" 第"+grade_xqm_1+"学期");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        Calendar calendar = Calendar.getInstance();

        stuNum = pref.getString("account","");
        stuPwd = pref.getString("password","");
        exam_xnm = String.valueOf(pref.getInt("EXAMXNM",0));
        exam_xqm = String.valueOf(pref.getInt("EXAMXQM",3));
        grade_xnm = String.valueOf(pref.getInt("GRADEXNM",0));
        grade_xqm = String.valueOf(pref.getInt("GRADEXQM",3));

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void refreshCourseTable() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    getCourseTable(stuNum,stuPwd,"2019","3");
                    Message message = new Message();
                    message.what = COURSE_TEXT;
                    handler.sendMessage(message);

                }

                catch (Exception e){

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        if (courseTableList.size() == 0){
                            Toast.makeText(MainActivity.this,"暂时无该学期课程表信息",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();
    }
    private void refreshGradeTable() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    getMarkTable(stuNum,stuPwd,grade_xnm,grade_xqm);
                    Message message = new Message();
                    message.what = MARK_TEXT;
                    handler.sendMessage(message);

                }

                catch (Exception e){
                    e.printStackTrace();

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        gradeTableAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        if (gradeTableList.size() == 0){
                            Toast.makeText(MainActivity.this,"暂时无该学期成绩信息",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();
    }

    private void refreshExamTable() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    getExamTable(stuNum,stuPwd,exam_xnm,exam_xqm);
                    Message message = new Message();
                    message.what = EXAM_TEXT;
                    handler.sendMessage(message);

                }

                catch (Exception e){
                    e.printStackTrace();

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        examTableAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                        if (examTableList.size() == 0){
                            Toast.makeText(MainActivity.this,"暂时无该学期考试信息",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this,LoginToSystem.class);
                startActivity(intent);
                break;
            default:

        }
        return true;
    }

    private void getCourseTable(String stuNum, String password, String xnm, String xqm){

        connect = new Connect(stuNum,password,stuPwd_webvpn);
        connect.login();
        courseTableList = connect.getCourseTable(xnm,xqm);

    }

    private void getMarkTable(String stuNum,String password,String xnm,String xqm){

        connect = new Connect(stuNum,password,stuPwd_webvpn);
        connect.login();
        gradeTableList = connect.getMarkTable(xnm,xqm);


    }

    private void getExamTable(String stuNum,String password,String xnm,String xqm){

        connect = new Connect(stuNum,password,stuPwd_webvpn);
        connect.login();
        examTableList = connect.getExamTable(xnm,xqm);

    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mRecyclerView = (RecyclerView)findViewById(R.id.recycleView);
            mRecyclerView.setItemViewCacheSize(20);//解决有时卡顿 onBindView生效错误问题
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            mRecyclerView.setLayoutManager(layoutManager);

            switch (msg.what){
                case COURSE_TEXT:

                    adapter = new CourseTableAdapter(courseTableList);
                    mRecyclerView.setAdapter(adapter);
                    break;
                case EXAM_TEXT:
                    examTableAdapter = new ExamTableAdapter(examTableList);
                    mRecyclerView.setAdapter(examTableAdapter);
                    break;
                case MARK_TEXT:

                    gradeTableAdapter = new GradeTableAdapter(gradeTableList);
                    mRecyclerView.setAdapter(gradeTableAdapter);
                    break;

                default:
            }
            return false;
        }
    });

    private String getDayOfWeek(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String c = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if(c.equals("1")){
            return "星期天";
        }
        else if(c.equals("2")){
            return "星期一";
        }else if(c.equals("3")){
            return "星期二";
        }else if(c.equals("4")){
            return "星期三";
        }else if(c.equals("5")){
            return "星期四";
        }else if(c.equals("6")){
            return "星期五";
        }else if(c.equals("7")){
            return "星期六";
        }
        return "NULL";
    }

}
