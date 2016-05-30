/*
登录界面，初始打开它，对应的是activity_login.xml
需要解决登陆和注册两个按钮事件
*/

package com.example.errand.errand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    GetRSAPublicKey mRSATask = null;
    UserLoginTask mUserLoginTask = null;
    UserRegisterTask mResTask = null;
    String PublicKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_login);

        Button button = (Button) findViewById(R.id.land);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CookieManager msCookieManager = new CookieManager();
        //在本地读取cookie，需要在进入每个activity时读取
        CookieHandler.setDefault(msCookieManager);
        SharedPreferences cookies = getSharedPreferences("Cookie", 0);
        String cookie = cookies.getString("Cookie", null);

        if (cookie != null) {
            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    /*该类用于在登录前获取RSA公钥和cookie
          其中加密和客户端的解密还未更新
          除了该请求外，任何请求都必须带有cookie
          所以建议在登录界面先生成该类的实例并运行*/
    public class GetRSAPublicKey extends AsyncTask<Void, Void, Boolean> {
        String result = "";
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        BufferedReader in = null;

        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) Url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("accept", "*/*");
                urlConnection.setRequestProperty("connection", "Keep-Alive");
                urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                Map<String, List<String>> headerFiles = urlConnection.getHeaderFields();
                List<String> cookiesHeader = headerFiles.get("Set-Cookie");
                SharedPreferences cookies = getSharedPreferences("Cookie", 0);
                SharedPreferences.Editor editor = cookies.edit();
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        System.out.println(cookie);
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                        editor.putString("Cookie", cookie);
                    }
                }
                editor.commit();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("RSA result = " + result);
            } catch (Exception e) {
                System.out.println("请求异常 " + e);
                return false;
            }
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            mRSATask = null;
            if (success) {
                int l = result.indexOf("(");
                int r = result.indexOf(")");
                PublicKey = result.substring(l + 1, r);//PublicKey为获得的公钥，全局变量
                System.out.println("PublicKey = " + PublicKey);
            }
        }

        protected void onCancelled() {
            mRSATask = null;
        }
    }

    //用户登录类，其中mUserLoginTask是该类在外部实例化的一个对象
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;//必须是10位数字（学号）
        private final String mPassword;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        String result = "";//请求的结果
        PrintWriter out = null;
        BufferedReader in = null;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String strUrl = "http://139.129.47.180:8002/Errand/login";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
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
                String param = "username=" + mUsername + "&" + "password=" + mPassword;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("login:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserLoginTask = null;

            if (success) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                System.out.println("Login succeed");
                startActivity(i);
                finish();
                //若请求成功，这里写的是进入主界面，可以根据自己的想法随意改，其他的类类似。
            } else {

                System.out.println("Login Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mUserLoginTask = null;
        }
    }

    /*
        用户注册类，与登录类基本类似
        */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        private final String mPassword;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserRegisterTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/register";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("register!");
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
                String param = "username=" + mUsername + "&" + "password=" + mPassword;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("register:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mResTask = null;
            //showProgress(false);
            if (success) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                System.out.println("Register succeed!");
                startActivity(i);
                finish();
            } else {
                System.out.println("Register Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mResTask = null;
        }
    }
}
