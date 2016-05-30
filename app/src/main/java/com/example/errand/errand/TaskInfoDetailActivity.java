/*
任务详情页，对应的是activity_task_info.xml，
点击用户列表可以跳转到对应的用户详情页，user_info.java
可以设置点击事件更改项，通过跳转到changeinfo.java更改对应内容并返回输入值
返回图标返回，确认图标在编辑的时候可以显示并且点击后确认，
如果简单浏览的话可以隐藏
*/

package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//改变消息的类型，用于changeinfo页更改消息完毕后返回时确定更改的是哪个消息
enum infotype {
    short_info, positoin1
}

public class TaskInfoDetailActivity extends Activity {

    private SimpleAdapter mSchedule;
    private infotype send_type;
    private TextView shortinfo;
    private TextView back;
    private ListView user_list;
    private ArrayList<HashMap<String, String>> mylist;
    private int usernum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        user_list = (ListView) findViewById(R.id.user_list);
        shortinfo = (TextView) findViewById(R.id.info_short);
        user_refresh();

        user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent intent = new Intent(TaskInfoDetailActivity.this, UserInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        shortinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_type = infotype.short_info;
                Intent intent = new Intent(TaskInfoDetailActivity.this, UserInfoChangeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("content", shortinfo.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

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

    public void user_refresh() {
        usernum = 4;
        mylist = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < usernum; i++) {
            String username = "personA";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user", username);
            mylist.add(map);
        }
        mSchedule = new SimpleAdapter(this, mylist, R.layout.user,
                new String[]{"user"},
                new int[]{R.id.user});
        user_list.setAdapter(mSchedule);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String content = bundle.getString("content");
                if (send_type == infotype.short_info) {
                    shortinfo.setText(content);
                }
            }
        }
    }

    /*
        添加任务条目类
        */
    public static class UserAddTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mStart_time;//格式：2016-10-11 13:00:00
        private final String mEnd_time;
        private final String mPlace;//地点
        private final String mAction;//内容
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private int ActionPk;//这个是TaskAction的pk号，每个Task和TaskAction的编号是分开记录的
        private String start_time;
        private String end_time;
        private String place;
        private String action;
        private int task_belong;//这个是该TaskAction所属的任务的PK号

        UserAddTaskActionTask(String pk, String start_time, String end_time, String place, String action) {
            mPk = pk;
            mStart_time = start_time;
            mEnd_time = end_time;
            mPlace = place;
            mAction = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/addtaskaction";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("AddTaskAction!");
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
                String param = "pk=" + mPk + "&" + "start_time=" + mStart_time + "&" + "end_time=" + mEnd_time + "&" + "place=" + mPlace + "&" + "action=" + mAction;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Add Task Action:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddTaskActionTask = null;
            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    JSONObject jsonObject = jsonArray.optJSONObject(len - 1);
                    ActionPk = jsonObject.optInt("pk");
                    System.out.println("ActionPk = " + String.valueOf(ActionPk));
                    String TaskInfo = jsonObject.optString("fields");
                    jsonObject = new JSONObject(TaskInfo);
                    start_time = jsonObject.optString("start_time");
                    System.out.println("start_time = " + start_time);
                    end_time = jsonObject.optString("end_time");
                    System.out.println("end_time = " + end_time);
                    place = jsonObject.optString("place");
                    System.out.println("place = " + place);
                    action = jsonObject.optString("action");
                    System.out.println("action = " + action);
                    task_belong = jsonObject.optInt("task_belong");
                    System.out.println("task_belone = " + String.valueOf(task_belong));
                    //同样解析了服务器返回的数据，根据需要使用
                } catch (Exception ejson) {
                    System.out.println("Add Task Action:解析JSON异常" + ejson);
                }
                System.out.println("Add Task Action succeed");
            } else {
                System.out.println("Add Task Action Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mAddTaskActionTask = null;
        }
    }

    /*
        添加任务类
        */
    public static class UserAddtaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mHeadline;//标题
        private final String mDetail;//内容
        private final String mReward;//奖励
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private String create_account;//创建账户
        private int pk;//任务编号
        private String create_time;//创建时间
        private String status;//任务状态
        private String execute_account;//正在执行该任务的用户
        private String comment;//评价
        private int score;//得分
        private List<String> response_accounts = new ArrayList<String>();//目前该任务的领取者
        private String headline;
        private String detail;
        private String reward;

        UserAddtaskTask(String headline, String detail, String reward) {
            mHeadline = headline;
            mDetail = detail;
            mReward = reward;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/addtask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("AddTask!");
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
                String param = "headline=" + mHeadline + "&" + "detail=" + mDetail + "&" + "reward=" + mReward;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("AddTask:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAddtaskTask = null;
            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    JSONObject jsonObject = jsonArray.optJSONObject(len - 1);
                    pk = jsonObject.optInt("pk");
                    System.out.println("pk = " + String.valueOf(pk));
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
                    //以上部分解析了服务器返回的数据，可以根据需要使用
                    System.out.println("Add Task succeed");
                } catch (Exception ejson) {
                    System.out.println("AddTask:解析JSON异常" + ejson);
                }

            } else {
                System.out.println("Add Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mAddtaskTask = null;
        }
    }

    /*
        修改任务条目类
        与添加任务条目类似，注意需要任务条目的编号
        */
    public static class UserChangeTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是TaskAction的pk号
        private final String mStart_time;
        private final String mEnd_time;
        private final String mPlace;
        private final String mAction;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private int ActionPk;//这个是TaskAction的pk号，就是每个Task和TaskAction的编号是分开记录的
        private String start_time;
        private String end_time;
        private String place;
        private String action;
        private int task_belong;//这个是该TaskAction所属的任务的PK号

        UserChangeTaskActionTask(String pk, String start_time, String end_time, String place, String action) {
            mPk = pk;
            mStart_time = start_time;
            mEnd_time = end_time;
            mPlace = place;
            mAction = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/changetaskaction";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("ChangeTaskAction!");
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
                String param = "pk=" + mPk + "&" + "start_time=" + mStart_time + "&" + "end_time=" + mEnd_time + "&" + "place=" + mPlace + "&" + "action=" + mAction;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Change Task Action:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mChangeTaskActionTask = null;
            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    JSONObject jsonObject = jsonArray.optJSONObject(len - 1);
                    ActionPk = jsonObject.optInt("pk");
                    System.out.println("ActionPk = " + String.valueOf(ActionPk));
                    String TaskInfo = jsonObject.optString("fields");
                    jsonObject = new JSONObject(TaskInfo);
                    start_time = jsonObject.optString("start_time");
                    System.out.println("start_time = " + start_time);
                    end_time = jsonObject.optString("end_time");
                    System.out.println("end_time = " + end_time);
                    place = jsonObject.optString("place");
                    System.out.println("place = " + place);
                    action = jsonObject.optString("action");
                    System.out.println("action = " + action);
                    task_belong = jsonObject.optInt("task_belong");
                    System.out.println("task_belone = " + String.valueOf(task_belong));
                } catch (Exception ejson) {
                    System.out.println("Chanege Task Action:解析JSON异常" + ejson);
                }
                System.out.println("Change Task Action succeed");

            } else {
                System.out.println("Fuck!");
            }
        }

        @Override
        protected void onCancelled() {
            mChangeTaskActionTask = null;
        }
    }

    /*
        修改任务类，与添加任务类基本类似，只是需要指定任务编号pk
        */
    public static class UserChangetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;
        private final String mHeadline;
        private final String mDetail;
        private final String mReward;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        private int pk;
        private String create_account;
        private String create_time;
        private String status;
        private String execute_account;
        private String comment;
        private int score;
        private List<String> response_accounts;
        private String headline;
        private String detail;
        private String reward;

        UserChangetaskTask(String pk, String headline, String detail, String reward) {
            mPk = pk;
            mHeadline = headline;
            mDetail = detail;
            mReward = reward;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/changetask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("ChangeTask!");
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
                String param = "pk=" + mPk + "&" + "headline=" + mHeadline + "&" + "detail=" + mDetail + "&" + "reward=" + mReward;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Change Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mChangetaskTask = null;

            if (success) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    JSONObject jsonObject = jsonArray.optJSONObject(len - 1);
                    pk = jsonObject.optInt("pk");
                    System.out.println("pk = " + String.valueOf(pk));
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
                    System.out.println("Change Task succeed");
                } catch (Exception ejson) {
                    System.out.println("Change Task:解析JSON异常" + ejson);
                }

                System.out.println("Change Task succeed");

            } else {
                System.out.println("Change Task Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mChangetaskTask = null;
        }
    }

    /*
        关闭任务类
        任务发布者将任务关闭
        */
    public static class UserClosetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserClosetaskTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/closetask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Close Task!");
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
                System.out.println("Close Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mClosetaskTask = null;
            if (success) {
                System.out.println("Close Task succeed");
            } else {
                System.out.println("Close Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mClosetaskTask = null;
        }
    }

    /*
        任务评价类
        发布者评价任务的完成状况并打分
        */
    public static class UserCommenttaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mScore;//打分 目前范围是1-5
        private final String mComment;//评价
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserCommenttaskTask(String pk, String score, String comment) {
            mPk = pk;
            mScore = score;// 1 - 5
            mComment = comment;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/commenttask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Comment Task!");
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
                String param = "pk=" + mPk + "&" + "score=" + mScore + "&" + "comment=" + mComment;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Comment Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCommenttaskTask = null;
            if (success) {
                System.out.println("Comment Task succeed");
            } else {
                System.out.println("Comment Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mCommenttaskTask = null;
        }
    }

    /*
        删除任务条目类，需要任务条目的编号
        */
    public static class UserRemoveTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//TaskAction的pk号
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserRemoveTaskActionTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/removetaskaction";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("RemoveTaskAction!");
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
                System.out.println("Remove Task Action:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRemoveTaskActionTask = null;
            if (success) {
                System.out.println("RemoveTaskAction succeed");
            } else {
                System.out.println("Remove Task Action Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mRemoveTaskActionTask = null;
        }
    }

    /*
        删除任务类，只需要指定任务编号
        */
    public static class UserRemovetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserRemovetaskTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/removetask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("removetask!");
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
                System.out.println("Remove Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRemovetaskTask = null;
            if (success) {
                System.out.println("Remove Task succeed");
            } else {
                System.out.println("Remove Task Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            mRemovetaskTask = null;
        }
    }

    /*
        接受任务，需要被接受任务的编号
        */
    public static class UserResponsetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserResponsetaskTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/responsetask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("ResponseTask!");
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
                System.out.println("Reponse Task:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mResponsetaskTask = null;
            if (success) {
                System.out.println("Response Task succeed");
            } else {
                System.out.println("Response Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mResponsetaskTask = null;
        }
    }

    /*
        选择完成者类
        */
    public static class UserSelectTaskExecutorTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mUsername;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserSelectTaskExecutorTask(String pk, String username) {
            mPk = pk;
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/selecttaskexecutor";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("SelectTaskExecutor!");
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
                String param = "pk=" + mPk + "&" + "username=" + mUsername;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
            } catch (Exception e) {
                System.out.println("Select Task Executor:发送POST请求出现异常！ " + e);
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSelectTaskExecutorTask = null;
            if (success) {
                System.out.println("Select Task Executor succeed");
            } else {
                System.out.println("Select Task Executor Failed");
            }
        }

        @Override
        protected void onCancelled() {
            mSelectTaskExecutorTask = null;
        }
    }
}
