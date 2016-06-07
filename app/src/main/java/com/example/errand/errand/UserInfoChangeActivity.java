/*
更改信息的活动页，点击返回不返回输入的值，
点击确认返回输入的值

 */

package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;

public class UserInfoChangeActivity extends Activity {

    private String content;
    private TextView back;
    private TextView confirm;
    private TextView view_content;
    private UserChangeInfoTask mChangeUserInfoTask;
    private UserChangePasswordTask mChangePasswordTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);

        content = this.getIntent().getExtras().getString("content");
        back = (TextView) findViewById(R.id.back);
        confirm = (TextView) findViewById(R.id.confirm);
        view_content = (TextView) findViewById(R.id.content);
        view_content.setText(content);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = view_content.getText().toString();
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = view_content.getText().toString();
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString("content", content);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/changeuserinfo";
            URL Url = null;
            try {
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
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("ChangeUserInfo:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mChangeUserInfoTask = null;
            if (success) {
               // mNickname = (EditText) findViewById(R.id.username);
              //  naili.setText(value_naili + " + " + String.valueOf(num_naili));
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
        修改密码类
        */
    public class UserChangePasswordTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        private final String mPassword;
        private final String mNewPassword;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserChangePasswordTask(String username, String password, String newpassword) {
            mUsername = username;
            mPassword = password;
            mNewPassword = newpassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/changepassword";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("changepassword!");
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
                String param = "username=" + mUsername + "&" + "password=" + mPassword + "&" + "newpassword=" + mNewPassword;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Changepassword:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mChangePasswordTask = null;
            if (success) {
                System.out.println("Change Password succeed");
            } else {
                System.out.println("Change Password Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mChangePasswordTask = null;
        }
    }
}
