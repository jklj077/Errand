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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ProgressBar progressBar;
    private Button land;
    private Button register;
    private EditText username;
    private EditText password;
    private LinearLayout validateLayout;
    private Button validate;
    private EditText validateCode;
    private Errand app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        app = (Errand) getApplication();
        setContentView(R.layout.activity_login);

        CookieHandler.setDefault(new CookieManager());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);
        validate = (Button) findViewById(R.id.validate);
        validateLayout = (LinearLayout) findViewById(R.id.validateLayout);
        validateCode = (EditText) findViewById(R.id.validateCode);
        land = (Button) findViewById(R.id.land);

        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserLogin(username.getText().toString(), password.getText().toString(), false).execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new RegisterUser(username.getText().toString(), password.getText().toString()).execute();
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ActivateUser(username.getText().toString(), password.getText().toString(), validateCode.getText().toString()).execute();
            }
        });

        new GetKey(false).execute();

        SharedPreferences UserPassport = getSharedPreferences("User", MODE_PRIVATE);
        String savedUsername = UserPassport.getString("username", null);
        String savedPassword = UserPassport.getString("password", null);
        if (savedUsername != null && savedPassword != null) {
            new UserLogin(savedUsername, savedPassword, true).execute();
        }

    }

    private void showToast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }

    private void Froze(boolean froze) {
        if (progressBar != null) {
            progressBar.setVisibility(froze ? View.VISIBLE : View.INVISIBLE);
        }
        if (username != null) {
            username.setEnabled(!froze);
        }
        if (password != null) {
            password.setEnabled(!froze);
        }
        if (register != null) {
            register.setEnabled(!froze);
        }
        if (land != null) {
            land.setEnabled(!froze);
        }
        if (validate != null) {
            validate.setEnabled(!froze);
        }
        if (validateCode != null) {
            validateCode.setEnabled(!froze);
        }
    }

    private class GetKey extends AsyncTask<Void, Void, String> {
        private final boolean isRetry;
        public GetKey(boolean isRetry){
            this.isRetry = isRetry;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Froze(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/";
            URL Url;
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
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "FAILED: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if(!result.contains("FAILED")){
                int l = result.indexOf('(');
                int r = result.indexOf(')');
                app.key = result.substring(l, r+1);
            }else{
                if(isRetry) {
                    showToast("Internal Error: Please Retry");
                }
            }
            Froze(false);

        }

    }

    private class UserLogin extends AsyncTask<Void, Void, String> {
        private final String username;
        private final String password;
        private final boolean isAuto;

        public UserLogin(String username, String password, boolean isAuto){
            this.username = username;
            this.password = password;
            this.isAuto = isAuto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(app.key==null) {
                new GetKey(!isAuto).execute();
                if(app.key==null){
                    this.cancel(true);
                }
            }
            Froze(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/login";
            URL Url;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                PrintWriter out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "username=" + username + "&" + "password=" + password;
                out.print(param);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "FAILED: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Froze(false);
            //showToast(result);
            if (!result.contains("FAILED")) {
                getSharedPreferences("User", MODE_PRIVATE).edit().putString("username", username).putString("password", password).apply();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                if (result.contains("active")) {
                    showToast("Account Not Activated");
                    validateLayout.setVisibility(View.VISIBLE);
                }else if(isAuto){
                    showToast("FAILED: Password Changed");
                }else{
                    showToast(result);
                }
            }
        }
    }

    private class RegisterUser extends AsyncTask<Void, Void, String> {
        private final String username;
        private final String password;

        public RegisterUser(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(app.key==null) {
                new GetKey(true).execute();
                if(app.key==null){
                    this.cancel(true);
                }
            }
            Froze(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/register";
            URL Url;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
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
                PrintWriter out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "username=" + username + "&" + "password=" + password;
                out.print(param);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "FAILED: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Froze(false);
            showToast(result);
            if (!result.contains("FAILED")) {
                land.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                validateLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class ActivateUser extends AsyncTask<String, Void, String> {
        private final String username;
        private final String password;
        private final String activeCode;

        public ActivateUser(String username, String password, String activeCode){
            this.username = username;
            this.password = password;
            this.activeCode = activeCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Froze(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/active";
            URL Url;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if (msCookieManager.getCookieStore().getCookies().size() > 0) {
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
                PrintWriter out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "username=" + username + "&" + "password=" + password + "&" + "activecode=" + activeCode;
                out.print(param);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                String result = "";
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "FAILED: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            Froze(false);
            showToast(result);
            if (!result.contains("FAILED")) {
                validateLayout.setVisibility(View.INVISIBLE);
                land.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            Froze(false);
            showToast(this.getClass().getSimpleName() + " Cancelled");
        }
    }
}
