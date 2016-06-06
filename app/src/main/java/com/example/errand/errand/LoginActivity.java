package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

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

public class LoginActivity extends Activity {
    private RelativeLayout loginLayout;
    private ProgressBar progressBar;
    private Button land;
    private Button register;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;
    private LinearLayout validateLayout;
    private Button validate;
    private EditText validateCode;
    private Errand app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Errand) getApplication();
        setContentView(R.layout.activity_login);

        loginLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        register = (Button) findViewById(R.id.register);
        validate = (Button) findViewById(R.id.validate);
        validateLayout = (LinearLayout) findViewById(R.id.validateLayout);
        validateCode = (EditText) findViewById(R.id.validateCode);
        land = (Button) findViewById(R.id.land);


        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserLogin(usernameWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString()).execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new RegisterUser(usernameWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString()).execute();
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ActivateUser(usernameWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString(), validateCode.getText().toString()).execute();
            }
        });

        usernameWrapper.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordWrapper.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordWrapper.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordWrapper.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        validateCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                passwordWrapper.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        CookieHandler.setDefault(new CookieManager());
        new GetKey().execute();
    }

    private void setLoad(boolean load) {
            progressBar.setVisibility(load ? View.VISIBLE : View.INVISIBLE);
            usernameWrapper.setEnabled(!load);
            passwordWrapper.setEnabled(!load);
            register.setEnabled(!load);
            land.setEnabled(!load);
            validate.setEnabled(!load);
            validateCode.setEnabled(!load);
    }

    private class GetKey extends AsyncTask<Void, Void, String> {
          @Override
        protected void onPreExecute() {
            super.onPreExecute();
              if(app.key!=null){
                  cancel(true);
              }
              setLoad(true);
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
                return "FAILED: NET:" + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            setLoad(false);
            if(!result.contains("FAILED")){
                int l = result.indexOf('(');
                int r = result.indexOf(')');
                app.key = result.substring(l, r+1);
                SharedPreferences UserPassport = getSharedPreferences("User", MODE_PRIVATE);
                String savedUsername = UserPassport.getString("username", null);
                String savedPassword = UserPassport.getString("password", null);
                if (savedUsername != null && savedPassword != null) {
                    new UserLogin(savedUsername, savedPassword).execute();
                }
            }else{
                if(result.contains("NET")){
                    Snackbar.make(loginLayout, "Network Error", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new GetKey().execute();
                        }
                    }).show();
                }else {
                    Snackbar.make(loginLayout, result, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new GetKey().execute();
                        }
                    }).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            setLoad(false);
        }
    }

    private class UserLogin extends AsyncTask<Void, Void, String> {
        private final String username;
        private final String password;

        public UserLogin(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(app.key==null) {
                Snackbar.make(loginLayout, "Please Get Key First", Snackbar.LENGTH_LONG).setAction("Get", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GetKey().execute();
                    }
                }).show();
                cancel(true);
            }else {
                setLoad(true);
            }
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
                return "FAILED: NET: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            setLoad(false);
            if (!result.contains("FAILED")) {
                getSharedPreferences("User", MODE_PRIVATE).edit().putString("username", username).putString("password", password).apply();
                app.username = username;
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                if(result.contains("NET")){
                    Snackbar.make(loginLayout, "Network Error", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new UserLogin(username, password).execute();
                        }
                    }).show();
                }else if (result.contains("active")) {
                    Snackbar.make(loginLayout, "Account Not Activated", Snackbar.LENGTH_LONG).setAction("Activate", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            validateLayout.setVisibility(View.VISIBLE);
                        }
                    }).show();
                }else {
                    passwordWrapper.setError(result);
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
                Snackbar.make(loginLayout, "Please Get Key First", Snackbar.LENGTH_LONG).setAction("Get", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new GetKey().execute();
                    }
                }).show();
                cancel(true);
            }else {
                setLoad(true);
            }
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
                return "FAILED: NET: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            setLoad(false);
            if (!result.contains("FAILED")) {
                Snackbar.make(loginLayout, "Registered", Snackbar.LENGTH_LONG).show();
            } else {
                if(result.contains("NET")){
                    Snackbar.make(loginLayout, "Network Error", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new RegisterUser(username, password).execute();
                        }
                    }).show();
                }else {
                    passwordWrapper.setError(result);
                }
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
            setLoad(true);
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
                return "FAILED: NET: " + e.toString();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            setLoad(false);
            if (!result.contains("FAILED")) {
                validateLayout.setVisibility(View.INVISIBLE);
                Snackbar.make(loginLayout, "Validated", Snackbar.LENGTH_LONG).show();
            }else{
                if(result.contains("NET")){
                    Snackbar.make(loginLayout, "Network Error", Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new RegisterUser(username, password).execute();
                        }
                    }).show();
                }else {
                    passwordWrapper.setError(result);
                }
            }
        }
    }
}
