/*
fragment，任务列表，对应tasklist.xml，
动态显示了任务的列表，任务列表项对应的是tasjklist_item.xml
并可以点击任务项进入对应的任务详情页task_info.java

*/


package com.example.errand.errand;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class MainTaskListFragment extends Fragment {
    private int item_num = 20;
    private ListView listview;
    private String taskcontent;
    private String taskuser;
    private String taskpay;
    private View view;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter mSchedule;
    private UserBrowseAlltaskTask mBrowseAlltaskTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tasklist, container, false);
        listview = (ListView) view.findViewById(R.id.tasklist_listView);
        refresh();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) mSchedule.getItem(i);
                String temp = map.get("task_item_content");
                Intent intent = new Intent(getActivity(), TaskInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        return view;
    }

    public void refresh() {
        mylist = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < item_num; i++) {
            taskcontent = "求帮忙拿快递！！";
            taskuser = "personA";
            taskpay = "      5RMB";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("task_item_content", taskcontent);
            map.put("task_item_person", taskuser);
            map.put("task_item_pay", taskpay);
            mylist.add(map);
        }
        mSchedule = new SimpleAdapter(this.getActivity(), mylist, R.layout.tasklist_item,
                new String[]{"task_item_content", "task_item_person", "task_item_pay"},
                new int[]{R.id.task_item_content, R.id.task_item_person, R.id.task_item_pay});
        listview.setAdapter(mSchedule);

    }

    /*
        浏览全部任务类
        目前还存在问题
        服务器端还不能返回某项任务的具体条目=。=
        之后应该会补充一个函数
        */
    public class UserBrowseAlltaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是上次浏览到的最后一个任务的pk号，服务器返回比该pk号小的最大的5个任务，如果没有应为最大整数
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private int pk;//任务编号
        private String create_account;//创建人
        private String create_time;//创建时间
        private String status;//状态
        private String execute_account;//完成者，讲道理被选定完成者的任务不会被返回，应该是null，可以无视
        private String comment;//评价
        private int score;//分数
        private List<String> response_accounts = new ArrayList<String>();//已经接受该任务的账号
        private String headline;//标题
        private String detail;//内容
        private String reward;//奖励

        UserBrowseAlltaskTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/browsealltask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("BroseAllTask!");
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
                String param = "pk=" + mPk;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Browse All Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mBrowseAlltaskTask = null;
            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        pk = jsonObject.optInt("pk");
                        System.out.println("task pk = " + String.valueOf(pk) + ":");
                        String TaskInfo = jsonObject.optString("fields");
                        jsonObject = new JSONObject(TaskInfo);
                        create_account = jsonObject.optString("create_account");
                        System.out.println("create_account = " + create_account);
                        create_time = jsonObject.optString("create_time");
                        System.out.println("create_time = " + create_time);
                        status = jsonObject.optString("status");
                        System.out.println("status = " + status);
                        headline = jsonObject.optString("headline");
                        System.out.println("headline = " + headline);
                        detail = jsonObject.optString("detail");
                        System.out.println("detail = " + detail);
                        execute_account = jsonObject.optString("execute_account");
                        System.out.println("execute_account = " + execute_account);
                        reward = jsonObject.optString("reward");
                        System.out.println("reward = " + reward);
                        comment = jsonObject.optString("comment");
                        System.out.println("comment = " + comment);
                        score = jsonObject.optInt("score");
                        System.out.println("score = " + score);
                        JSONArray JSONresponse = jsonObject.optJSONArray("response_accounts");
                        response_accounts.clear();
                        for (int j = 0; j < JSONresponse.length(); ++j)
                            response_accounts.add(JSONresponse.optString(j));
                        System.out.println("response_accounts:");
                        if (response_accounts.isEmpty())
                            System.out.println("no one response!");
                        else
                            System.out.println(response_accounts);
                        System.out.println("-------------------------");
                        //解析客户端返回的信息，可以根据需要使用
                    }
                } catch (Exception ejson) {
                    System.out.println("Browse All Task:解析JSON异常" + ejson);
                }
                System.out.println("Browse All Task succeed");
            } else {
                System.out.println("Browse All Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mBrowseAlltaskTask = null;
        }
    }
}
