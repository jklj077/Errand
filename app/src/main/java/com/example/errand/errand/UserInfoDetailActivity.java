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
import android.view.View;
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
    private UserGetInfoTask mGetUserInfoTask;
    private UserActiveTask mActiveTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

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
                } catch (Exception Ejson) {
                    System.out.println("analyze failed: " + Ejson);
                }
            } else {
                System.out.println("Fuck!");
            }
        }
    }

    /*
        用户激活类，用pku邮箱中的激活码激活，默认激活码为"1111"
        */
    public class UserActiveTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        private final String mPassword;
        private final String mActivecode;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserActiveTask(String username, String password, String activecode) {
            mUsername = username;
            mPassword = password;
            mActivecode = activecode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/active";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Active");
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
                String param = "username=" + mUsername + "&" + "password=" + mPassword + "&" + "activecode=" + mActivecode;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Active:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mActiveTask = null;
            if (success) {
                System.out.println("Active Succeed");
            } else {
                System.out.println("Active Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mActiveTask = null;
        }
    }
}
