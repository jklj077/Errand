/*
显示一些用户信息，对应userinfo.xml
点击显示过往任务列表，用户信息等信息
区别于详细用户信息user_info
 */

package com.example.errand.errand;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class UserInfoFragment extends Fragment {

    UserLogoutTask mLogoutTask;
    private LinearLayout user;
    private TextView create_view;
    private TextView posted_view;
    private TextView taken_view;
    private TextView past_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userinfo, container, false);

        user = (LinearLayout) view.findViewById(R.id.user);
        create_view = (TextView) view.findViewById(R.id.create_task);
        posted_view = (TextView) view.findViewById(R.id.posted_task);
        taken_view = (TextView) view.findViewById(R.id.taken_task);
        past_view = (TextView) view.findViewById(R.id.past_task);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        create_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TaskInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        posted_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", "已发布任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        taken_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", "已领取任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        past_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", "过往任务列表");
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        return view;
    }

    /*
        用户登出类
        */
    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/logout";
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
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Logout:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLogoutTask = null;
            if (success) {
                SharedPreferences cookies = getActivity().getSharedPreferences("Cookie", 0);
                SharedPreferences.Editor editor = cookies.edit();
                editor.putString("Cookie", null);
                editor.commit();
                msCookieManager.getCookieStore().removeAll();
                //登出之前清理cookie
                System.out.println("Logout!");
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            } else {
                System.out.println("Logout Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mLogoutTask = null;
        }
    }
}
