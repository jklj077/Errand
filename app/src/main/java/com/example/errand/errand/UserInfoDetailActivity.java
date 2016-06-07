/*

用户信息详情页，对应的是activity_user_info.xml
更改信息可以设置点击跳转到changeinfo.java
流程类似于task_info

*/
package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;

public class UserInfoDetailActivity extends Activity {
    private TextView back;
    private TextView confirm;
    private EditText nickname;
    private UserGetInfoTask mGetUserInfoTask;
    private EditText phone;
    private EditText sign;
    private EditText sex;
    private EditText birth;
    private TextView post_num;
    private TextView finish_num;
    private RatingBar rating;
    private Errand app;
    private UserInfoChangeActivity.UserChangeInfoTask mChangeUserInfoTask;
    private UserInfoChangeActivity.UserChangePasswordTask mChangePasswordTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Errand) getApplication();
        setContentView(R.layout.activity_user_info);
        String user = getIntent().getStringExtra("username");
        //Log.d("ERRAND", taskinfo.mPhone_number);

        post_num = (TextView) findViewById(R.id.post_task_num);
        finish_num = (TextView) findViewById(R.id.finish_task_num);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setClickable(false);
        rating.setFocusable(false);
        rating.setEnabled(false);
        phone = (EditText) findViewById(R.id.phone);

        sign = (EditText) findViewById(R.id.sign);

        sex = (EditText) findViewById(R.id.sex);

        birth = (EditText) findViewById(R.id.birth);

        nickname = (EditText) findViewById(R.id.nickname);



        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        confirm = (TextView) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mphone = ((EditText) findViewById(R.id.phone)).getText().toString();
                String mnickname = ((EditText) findViewById(R.id.nickname)).getText().toString();
                Log.d("ERRAND", mnickname);
                String msign = ((EditText)findViewById(R.id.sign)).getText().toString();
                String msex =  ((EditText)findViewById(R.id.sex)).getText().toString();
                String mbirth =  ((EditText)findViewById(R.id.birth)).getText().toString();

                Log.d("ERRAND", "BEFORE START");

                UserChangeInfoTask task = new UserChangeInfoTask(mnickname,msex, mphone,mbirth, msign);
                task.execute();
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        if(!app.username.equals(user) && user != null){
            nickname.setClickable(false);
            nickname.setEnabled(false);
            sex.setClickable(false);
            sex.setEnabled(false);
            birth.setClickable(false);
            birth.setEnabled(false);
            sign.setClickable(false);
            sign.setEnabled(false);
            phone.setClickable(false);
            phone.setEnabled(false);

            new UserGetUserProfileTask(user).execute();

        }
        else {
            UserGetInfoTask taskinfo = new UserGetInfoTask();
            taskinfo.execute();
            new UserGetUserProfileTask(app.username).execute();
        }

    }
    /*
    修改用户信息类
    */
    public class UserChangeInfoTask extends AsyncTask<Void, Void, Boolean> {
        private final String mNickname;
        private final String mSex; //长度为1的字符串：M 或 F
        private final String mPhone_number; //11位数字
        private final String mBirthday; //格式 ：1990-1-11
        private final String mSignature;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserChangeInfoTask(String nickname, String sex, String phone_number, String birthday, String signature) {
            mNickname = nickname;
            mSex = sex;
            mPhone_number = phone_number;
            mBirthday = birthday;
            mSignature = signature;
        }

        @Override
      //  protected void onPreExecute() {
       //     super.onPreExecute();
       //     String param = "nickname=" + mNickname + "&" + "sex=" + mSex + "&" + "phone_number=" + mPhone_number + "&" + "birthday=" + mBirthday + "&" + "signature=" + mSignature;
       //     Log.d("a", param);
      //  }

     //   @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/changeuserinfo";
            URL Url = null;
            try {
                Log.d("ERRAND", "A");
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("ChangeUserInfo!");
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    System.out.println(msCookieManager.getCookieStore().getCookies());
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("accept", "*/*");
                urlConnection.setRequestProperty("connection", "Keep-Alive");
                urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "nickname=" + mNickname + "&" + "sex=" + mSex + "&" + "phone_number=" + mPhone_number + "&" + "birthday=" + mBirthday + "&" + "signature=" + mSignature;
                System.out.println(param);
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                Log.d("ERRAND", "B");
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ERRAND", "Exception");
                System.out.println("ChangeUserInfo:发送POST请求出现异常！ " + e);
                return false;
            }

            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mChangeUserInfoTask = null;
            if (success) {
                System.out.println("ChangeUserInfo succeed");
            } else {
                System.out.println("ChangeUserInfo Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mChangeUserInfoTask = null;
        }
    }
    /*
        获取用户信息类

        */
    public class UserGetInfoTask extends AsyncTask<Void, Void, Boolean> {
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private String mNickname = null;
        private String mSex = null;
        private String mPhone_number = null;
        private String mBirthday = null;
        private String mSignature = null;
        private int pk;//UserInfo的序号，可以无视

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/getmyuserinfo";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Get My User Info!");
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    System.out.println(msCookieManager.getCookieStore().getCookies());
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("accept", "*/*");
                urlConnection.setRequestProperty("connection", "Keep-Alive");
                urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Get User Info:发送POST请求出现异常！ " + e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mGetUserInfoTask = null;
            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    JSONObject jsonObject = jsonArray.optJSONObject(len - 1);
                    pk = jsonObject.optInt("pk");
                    String UserInfo = jsonObject.optString("fields");
                    jsonObject = new JSONObject(UserInfo);
                    mNickname = jsonObject.optString("nickname");
                    mSex = jsonObject.optString("sex");
                    mPhone_number = jsonObject.optString("phone_number");
                    mBirthday = jsonObject.optString("birthday");
                    mSignature = jsonObject.optString("signature");
                    System.out.println("pk = " + String.valueOf(pk));
                    System.out.println("nickname = " + mNickname);
                    System.out.println("sex = " + mSex);
                    System.out.println("phone_number = " + mPhone_number);
                    System.out.println("birthday = " + mBirthday);
                    System.out.println("signature = " + mSignature);
                    System.out.println("Get My User Info succeed");
                    phone.setText(mPhone_number);
                    sign = (EditText)findViewById(R.id.sign);
                    sign.setText(mSignature);
                    sex = (EditText)findViewById(R.id.sex);
                    sex.setText(mSex);
                    birth = (EditText)findViewById(R.id.birth);
                    birth.setText(mBirthday);
                    nickname = (EditText)findViewById(R.id.nickname);
                    nickname.setText(mNickname);
                } catch (Exception Ejson) {
                    System.out.println("analyze failed: " + Ejson);
                }
            } else {
                System.out.println("Fuck!");
            }
        }
    }

    /*
查看指定用户名的用户信息
注意：如果本用户和被查看的用户之间有任务关系，会返回查看用户的手机号。否则返回的手机号为空
*/
    public class UserGetUserProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private String znickname;
        private String zsex = null;
        private String phone_number = null;
        private String zbirthday = null;
        private String zsignature = null;
        private int score;
        private int taskCompleted;
        private int taskCreated;

        UserGetUserProfileTask(String username) {
            mUsername = username;
        }

        @Override

        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/getuserprofile";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Get User Profile!");
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                    System.out.println(msCookieManager.getCookieStore().getCookies());
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                }
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("accept", "*/*");
                urlConnection.setRequestProperty("connection", "Keep-Alive");
                urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "username=" + mUsername;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Get User Profile:发送POST请求出现异常！ " + e);
                //logger.catching(e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mGetUserProfileTask = null;
            if (success) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    znickname = jsonObject.optString("nickname");
                    zsex = jsonObject.optString("sex");
                    phone_number = jsonObject.optString("phone_number");
                    zbirthday = jsonObject.optString("birthday");
                    zsignature = jsonObject.optString("signature");
                    score = jsonObject.optInt("score");

                    taskCompleted = jsonObject.optInt("taskCompleted");
                    taskCreated = jsonObject.optInt("taskCreated");

                    System.out.println("nickname = " + znickname);
                    System.out.println("sex = " + zsex);
                    System.out.println("phone_number = " + phone_number);
                    System.out.println("birthday = " + zbirthday);
                    System.out.println("signature = " + zsignature);
                    System.out.println("score = " + String.valueOf(score));
                    System.out.println("taskCompleted = " + String.valueOf(taskCompleted));
                    System.out.println("taskCreated = " + String.valueOf(taskCreated));
                    System.out.println("Get User Profile Succeed");
                    phone = (EditText) findViewById(R.id.phone);
                    phone.setText(phone_number);
                    sign = (EditText)findViewById(R.id.sign);
                    sign.setText(zsignature);
                    sex = (EditText)findViewById(R.id.sex);
                    sex.setText(zsex);
                    birth = (EditText)findViewById(R.id.birth);
                    birth.setText(zbirthday);
                    nickname = (EditText)findViewById(R.id.nickname);
                    nickname.setText(znickname);
                    post_num.setText(String.valueOf(taskCreated));
                    finish_num.setText(String.valueOf(taskCompleted));
                    rating.setRating((float) score);
                } catch (Exception Ejson) {
                    System.out.println("Get User Profile: 解析JSON异常 " + Ejson);
                }

            } else {

                System.out.println("Get User Profile Failed");
            }
        }

        @Override
        protected void onCancelled() {
            //mGetUserProfileTask = null;
        }
    }


}
