package com.xjie.myapplication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Connect {

    private String url = "https://webvpn.fjut.edu.cn";
    private String url_jwxt = "https://jwxt-443.webvpn.fjut.edu.cn";
    private String modulus,exponent,authenticity_token,csrftoken;
    private String user,password,password_webvpn;

    private Map<String,String> cookies = new HashMap<>();
    private Connection connection;
    private Connection.Response response;
    private Document document;

    public Connect(String user, String password, String password_webvpn) {
        this.user = user;
        this.password = password;
        this.password_webvpn = password_webvpn;
        getAuthenticityToken();
        loginHome();
        getCsrftoken();
        getRSApublickey();
    }

    public void getAuthenticityToken() {
        connection = Jsoup.connect(url+"/users/sign_in");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");//待考量
        try {
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        cookies = response.cookies();
        document = Jsoup.parse(response.body());
        authenticity_token = document.select("input[name=authenticity_token]").val();
    }

    public void loginHome(){
        connection = Jsoup.connect(url+"/users/sign_in");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("utf8","✓");
        connection.data("authenticity_token",authenticity_token);
        connection.data("user[login]",user);
        connection.data("user[password]",password_webvpn);
        connection.data("user[dymatice_code]","unknown");
        connection.data("commit","登录 Login");
        try{

            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
            cookies = response.cookies();

        }catch (Exception e){
            e.printStackTrace();
        }
        document = Jsoup.parse(response.body());

    }

    public void getCsrftoken() {
        connection = Jsoup.connect(url_jwxt+"/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");//待考量
        try{
            response = connection.cookies(cookies).ignoreContentType(true).execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        cookies.put("JSESSIONID",response.cookies().get("JSESSIONID"));

        document = Jsoup.parse(response.body());
        csrftoken = document.getElementById("csrftoken").val();
    }

    public void getRSApublickey() {
        connection = Jsoup.connect(url_jwxt+ "/jwglxt/xtgl/login_getPublicKey.html?time=" + new Date().getTime());
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
        try{
            response = connection.cookies(cookies).ignoreContentType(true).execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        password = RSAEncoder.RSAEncrypt(password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        password = B64.hex2b64(password);

    }

    public String login() {

        connection = Jsoup.connect(url_jwxt+ "/jwglxt/xtgl/login_slogin.html");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("csrftoken",csrftoken);
        connection.data("yhm",user);
        connection.data("mm",password);
        connection.data("mm",password);
        try{
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());
        if(document.getElementById("tips") == null){
            return "SUCCESS";
        }else{
            return(document.getElementById("tips").text());
        }
    }

    public List<CourseTable> getCourseTable(String xnm, String xqm) {

        connection = Jsoup.connect(url_jwxt+"/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("xnm", xnm);
        connection.data("xqm", xqm);
        try {
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();

            response = connection.execute();
        }
        catch (java.net.ConnectException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());

        JSONObject jsonObject = JSON.parseObject(response.body());
        if (jsonObject.get("kbList") == null) {
            System.out.println("暂无课表");
            return null;
        }

        List<CourseTable> courseTableList = new ArrayList<>();

        JSONArray course = JSON.parseArray(jsonObject.getString("kbList"));
        for (Iterator iterator = course.iterator(); iterator.hasNext(); ) {
            JSONObject lesson = (JSONObject) iterator.next();
            CourseTable d = new CourseTable(lesson.getString("kcmc"), lesson.getString("jc"), lesson.getString("cdmc"), lesson.getString("xm"), lesson.getString("zcd"), lesson.getString("xqmc"), lesson.getString("xqjmc"));
            courseTableList.add(d);

        }
        return courseTableList;
    }

    public List<GradeTable> getMarkTable(String xnm, String xqm) {

        connection = Jsoup.connect(url_jwxt+"/jwglxt/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("xnm", xnm);
        connection.data("xqm", xqm);
        connection.data("queryModel.showCount","100");
        try {
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();

            response = connection.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());

        JSONObject jsonObject = JSON.parseObject(response.body());
        if (jsonObject.get("items") == null) {
            System.out.println("暂无成绩信息");
            return null;
        }

        List<GradeTable> gradeTableList = new ArrayList<>();

        JSONArray course = JSON.parseArray(jsonObject.getString("items"));
        for (Iterator iterator = course.iterator(); iterator.hasNext(); ) {
            JSONObject lesson = (JSONObject) iterator.next();
            GradeTable m = new GradeTable(lesson.getString("kcmc"), lesson.getString("cj"), lesson.getString("xf"), lesson.getString("jd"), lesson.getString("ksxz"));
            gradeTableList.add(m);

        }
        return gradeTableList;
    }
    public List<ExamTable> getExamTable(String xnm,String xqm) {

        connection = Jsoup.connect(url_jwxt+"/jwglxt/kwgl/kscx_cxXsksxxIndex.html?doType=query&gnmkdm=N358105");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");

        connection.data("xnm", xnm);
        connection.data("xqm", xqm);
        connection.data("queryModel.showCount","100");//展示100条信息 默认10条可能无法显示完全
        try {
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();

            response = connection.execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());

        JSONObject jsonObject = JSON.parseObject(response.body());
        if (jsonObject.get("items") == null) {
            System.out.println("暂无考试信息");
            return null;
        }

        List<ExamTable> ExamTableList = new ArrayList<>();

        JSONArray course = JSON.parseArray(jsonObject.getString("items"));
        for (Iterator iterator = course.iterator(); iterator.hasNext(); ) {
            JSONObject lesson = (JSONObject) iterator.next();
            ExamTable e = new ExamTable(lesson.getString("kcmc"), lesson.getString("cdmc"),lesson.getString("kssj"),lesson.getString("zwh"));
            ExamTableList.add(e);

        }
        return ExamTableList;
    }
    public String getEnterYear(){

        connection = Jsoup.connect(url_jwxt+"/jwglxt/xsxxxggl/xsgrxxwh_xgXsgrxx.html?time=1563945876064&gnmkdm=N100808&"+user);

        try{
            connection.cookies(cookies).ignoreContentType(true).method(Connection.Method.POST).execute();
            response = connection.execute();
        }catch (Exception e){
            e.printStackTrace();
        }

        document = Jsoup.parse(response.body());

        return(document.select("#col_njdm_id").select("p").text());
    }
	
}
