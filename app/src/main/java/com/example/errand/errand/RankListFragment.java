/*
用户排名页，点击打开详细用户表，对应rank.xml
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
import android.widget.Button;
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


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class RankListFragment extends Fragment {

    private int item_num=20;
    private ListView listview;
    private String rank;
    private String username;
    private String level;
    private View view;
    private ArrayList<HashMap<String, String>> mylist;
    private SimpleAdapter mSchedule;
    private Button task_score;
    private Button task_complete;
    private Button task_create;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rank,container,false);
        listview = (ListView) view.findViewById(R.id.ranklist_listview);
      //  refresh();
        System.out.println("zzl");
        task_score = (Button)view.findViewById(R.id.button_s);
        task_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserOrderbyScoreTask task2 = new UserOrderbyScoreTask();
                task2.execute();
            }
        });
        System.out.println("zzl1");
        task_complete = (Button)view.findViewById(R.id.button_co);
        task_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserOrderbyTaskCompletedTask task2 = new UserOrderbyTaskCompletedTask();
                task2.execute();
            }
        });
        task_create = (Button)view.findViewById(R.id.button_cr);
        task_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserOrderbyTaskCreatedTask task3 = new UserOrderbyTaskCreatedTask();
                task3.execute();
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              //  UserGetUserProfileTask task4 = new UserGetUserProfileTask();
                Intent intent = new Intent(getActivity(), UserInfoDetailActivity.class);
                startActivityForResult(intent,0);
            }
        });
        return view;
    }

    public void refresh()
    {
        mylist = new ArrayList< HashMap<String, String> >();
        for (int i=0;i<item_num;i++)
        {
            rank=String.valueOf(i+1)+'.';
            username="personA";
            level="1";
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("RankListFragment", rank);
            map.put("username", username);
            map.put("level", level);
            mylist.add(map);
        }
        mSchedule = new SimpleAdapter( this.getActivity(), mylist, R.layout.user_item,
                new String[]{"RankListFragment", "username", "level"},
                new int[]{R.id.rank,R.id.user_item_username, R.id.level});
        listview.setAdapter(mSchedule);

    }
    /*
查看指定用户名的用户信息
注意：如果本用户和被查看的用户之间有任务关系，会返回查看用户的手机号。否则返回的手机号为空
*/
    public class UserGetUserProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUsername;
        private String nickname;
        private String sex = null;
        private String phone_number = null;
        private String birthday = null;
        private String signature = null;
        private int score;
        private int taskCompleted;
        private int taskCreated;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        UserGetUserProfileTask(String username)
        {
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
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if(msCookieManager.getCookieStore().getCookies().size() > 0)
                {
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
                while ((line = in.readLine()) != null){
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            }catch (Exception e) {
                System.out.println("Get User Profile:发送POST请求出现异常！ " + e);
                //logger.catching(e);
                return false;
            }
            if (result.indexOf("FAILED") >= 0)
                return false;
            else
                return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
          //  mGetUserProfileTask = null;
            if (success) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    nickname = jsonObject.optString("nickname");
                    sex = jsonObject.optString("sex");
                    phone_number = jsonObject.optString("phone_number");
                    birthday = jsonObject.optString("birthday");
                    signature = jsonObject.optString("signature");
                    score = jsonObject.optInt("score");
                    taskCompleted = jsonObject.optInt("taskCompleted");
                    taskCreated = jsonObject.optInt("taskCreated");
                    System.out.println("nickname = " + nickname);
                    System.out.println("sex = " + sex);
                    System.out.println("phone_number = " + phone_number);
                    System.out.println("birthday = " + birthday);
                    System.out.println("signature = " + signature);
                    System.out.println("score = " + String.valueOf(score));
                    System.out.println("taskCompleted = " + String.valueOf(taskCompleted));
                    System.out.println("taskCreated = " + String.valueOf(taskCompleted));
                    System.out.println("Get User Profile Succeed");
                }
                catch (Exception Ejson)
                {
                    System.out.println("Get User Profile: 解析JSON异常 " + Ejson);
                }


            } else {

                System.out.println("Get User Profile Failed");
            }
        }

        @Override
        protected void onCancelled() {
          //  mGetUserProfileTask = null;
        }
    }

    /*
根据分数查看排名，目前服务器写的是返回的是所有的排名，不是像之前根据上一次浏览到的最后一项返回固定数量
返回用户名和分数
没有失败的情况，这里写的是如果返回的是空集合，即服务器端的排名数据为空，算作失败。这里请根据需要调整
*/
    public class UserOrderbyScoreTask extends AsyncTask<Void, Void, Boolean> {
        PrintWriter out = null;
        BufferedReader in = null;
        private String nickname;
        private int score;
        String result = "";
        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/orderbyscores";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Order by Score!");
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if(msCookieManager.getCookieStore().getCookies().size() > 0)
                {
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
                while ((line = in.readLine()) != null){
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            }catch (Exception e) {
                System.out.println("Order by Score:发送POST请求出现异常！ " + e);
                //logger.catching(e);
                return false;
            }
            if (result.indexOf("[]") >= 0)
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
          //  mOrderbyScoreTask = null;
            if (success) {
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    mylist = new ArrayList< HashMap<String, String> >();
                    System.out.println("zl");
                    for (int i = 0; i < jsonArray.length(); ++ i)
                    {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        nickname = jsonObject.optString("nickname");
                        score = jsonObject.optInt("scores");
                        System.out.println("nickname = " + nickname);
                        System.out.println("score = " + String.valueOf(score));
                        System.out.println("------------------------");
                        rank=String.valueOf(i+1)+'.';
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("RankListFragment", "排名： "+rank);
                        map.put("username", nickname);
                        map.put("level", "分数: "+String.valueOf(score));
                        mylist.add(map);
                    }
                    mSchedule = new SimpleAdapter( RankListFragment.this.getActivity(), mylist, R.layout.user_item,
                            new String[]{"RankListFragment", "username", "level"},
                            new int[]{R.id.rank,R.id.user_item_username, R.id.user_item_level});
                    listview.setAdapter(mSchedule);
                    System.out.println("Order by Task Score");
                }
                catch (Exception ejson)
                {
                    System.out.println("Order by Score :解析JSON异常 " + ejson);
                }


            } else {

                System.out.println("Order by Score Failed");
            }
        }

        @Override
        protected void onCancelled() {
          //  mOrderbyScoreTask = null;
        }
    }
    /*
根据完成任务的数量查看排名，目前服务器写的是返回的是所有的排名，不是像之前根据上一次浏览到的最后一项返回固定数量
返回用户名和完成的任务数量
*/
    public class UserOrderbyTaskCompletedTask extends AsyncTask<Void, Void, Boolean> {
        PrintWriter out = null;
        BufferedReader in = null;
        private String nickname;
        private int taskCompleted;
        String result = "";
        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/orderbytaskcompleted";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Order by Task Completed!");
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if(msCookieManager.getCookieStore().getCookies().size() > 0)
                {
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
                while ((line = in.readLine()) != null){
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            }catch (Exception e) {
                System.out.println("Order by Task Completed:发送POST请求出现异常！ " + e);
                //logger.catching(e);
                return false;
            }
            if (result.indexOf("[]") >= 0)
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
         //   mOrderbyTaskCompletedTask = null;

            if (success) {
                //result = result.replaceAll("\"", "\'");
                //System.out.println("result replace to " + result);
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    mylist = new ArrayList< HashMap<String, String> >();
                    System.out.println("zl");
                    for (int i = 0; i < jsonArray.length(); ++ i)
                    {
                        rank=String.valueOf(i+1)+'.';
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        nickname = jsonObject.optString("nickname");
                        taskCompleted = jsonObject.optInt("taskCompleted");
                        System.out.println("nickname = " + nickname);
                        System.out.println("task Completed = " + String.valueOf(taskCompleted));
                        System.out.println("------------------------");
                        map.put("RankListFragment", "排名： "+rank);
                        map.put("username", nickname);
                        map.put("level", "完成任务数量: "+String.valueOf(taskCompleted));
                        mylist.add(map);
                    }
                    mSchedule = new SimpleAdapter( RankListFragment.this.getActivity(), mylist, R.layout.user_item,
                            new String[]{"RankListFragment", "username", "level"},
                            new int[]{R.id.rank,R.id.user_item_username, R.id.user_item_level});
                    listview.setAdapter(mSchedule);
                    System.out.println("Order by Task Completed Succeed");
                }
                catch (Exception ejson)
                {
                    System.out.println("Order by Task Completed :解析JSON异常 " + ejson);
                }


            } else {

                System.out.println("Order by Task Completed Failed");
            }
        }

        @Override
        protected void onCancelled() {
        //    mOrderbyTaskCompletedTask = null;
        }
    }
    /*
根据发布任务的数量查看排名，目前服务器写的是返回的是所有的排名，不是像之前根据上一次浏览到的最后一项返回固定数量
返回用户名和发布的任务数量
没有失败的情况，这里写的是如果返回的是空集合，即服务器端的排名数据为空，算作失败。这里请根据需要调整
*/
    public class UserOrderbyTaskCreatedTask extends AsyncTask<Void, Void, Boolean> {
        PrintWriter out = null;
        BufferedReader in = null;
        private String nickname;
        private int taskCreated;
        String result = "";
        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/orderbytaskcreated";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Order by Task Created!");
                CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
                if(msCookieManager.getCookieStore().getCookies().size() > 0)
                {
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
                while ((line = in.readLine()) != null){
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            }catch (Exception e) {
                System.out.println("Order by Task Created:发送POST请求出现异常！ " + e);
                //logger.catching(e);
                return false;
            }
            if (result.indexOf("[]") >= 0)
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
          //  mOrderbyTaskCreatedTask = null;
            if (success) {
                //result = result.replaceAll("\"", "\'");
                //System.out.println("result replace to " + result);
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    mylist = new ArrayList< HashMap<String, String> >();
                    for (int i = 0; i < jsonArray.length(); ++ i)
                    {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        nickname = jsonObject.optString("nickname");
                        taskCreated = jsonObject.optInt("taskCreated");
                        System.out.println("nickname = " + nickname);
                        System.out.println("task Created = " + String.valueOf(taskCreated));
                        System.out.println("------------------------");
                        rank=String.valueOf(i+1)+'.';
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("RankListFragment", "排名： "+rank);
                        map.put("username", nickname);
                        map.put("level", "发布任务数量: "+String.valueOf(taskCreated));
                        mylist.add(map);
                    }
                    mSchedule = new SimpleAdapter( RankListFragment.this.getActivity(), mylist, R.layout.user_item,
                            new String[]{"RankListFragment", "username", "level"},
                            new int[]{R.id.rank,R.id.user_item_username, R.id.user_item_level});
                    listview.setAdapter(mSchedule);
                    System.out.println("Order by Task Created Succeed");
                }
                catch (Exception ejson)
                {
                    System.out.println("Order by Task Created :解析JSON异常 " + ejson);
                }


            } else {

                System.out.println("Order by Task Created Failed");
            }
        }

        @Override
        protected void onCancelled() {
           // mOrderbyTaskCreatedTask = null;
        }
    }


}
