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
import android.widget.Toast;

import com.example.errand.errand.Objects.TaskInfo;

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


public class MainTaskListFragment extends Fragment {
    private ListView mListView;
    private SimpleAdapter mSchedule;
    private List<TaskInfo> mTasks;
    private Integer minPk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasklist, container, false);
        mListView = (ListView) view.findViewById(R.id.tasklist_listView);
        mTasks = new ArrayList<>();
        minPk = Integer.MAX_VALUE;
        new BrowseAllTasks(minPk).execute();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        new BrowseAllTasks(minPk).execute();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> map = (Map<String, String>) mSchedule.getItem(i);
                String temp = map.get("task_item_content");
                Intent intent = new Intent(getActivity(), TaskInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void showToast(String content) {
        Toast.makeText(getActivity().getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }

//    public void refresh() {
//        BrowseAllTasks mUserBrowseAllTask = new BrowseAllTasks(Integer.toString(Integer.MAX_VALUE));
//        mUserBrowseAllTask.execute();
//        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
//        int item_num = 20;
//        for (int i = 0; i < item_num; i++) {
//            String taskcontent = "求帮忙拿快递！！";
//            String taskuser = "personA";
//            String taskpay = "      5RMB";
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("task_item_content", taskcontent);
//            map.put("task_item_person", taskuser);
//            map.put("task_item_pay", taskpay);
//            mylist.add(map);
//        }
//        mSchedule = new SimpleAdapter(this.getActivity(), mylist, R.layout.tasklist_item,
//                new String[]{"task_item_content", "task_item_person", "task_item_pay"},
//                new int[]{R.id.task_item_content, R.id.task_item_person, R.id.task_item_pay});
//        mListView.setAdapter(mSchedule);
//
//    }


    private class BrowseAllTasks extends AsyncTask<Void, Void, String> {
        private final Integer lastPk;

        public BrowseAllTasks(Integer pk) {
            lastPk = pk;
        }

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/browsealltask";
            URL Url;
            String result="";
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
                String param = "pk=" + lastPk;
                out.print(param);
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return "FAILED: "+e.toString();
            }}

        @Override
        protected void onPostExecute(final String result) {
            showToast(result);
            if (!result.contains("FAILED")) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    TaskInfo info = new TaskInfo();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        info.pk = jsonObject.optInt("pk");
                        jsonObject = new JSONObject(jsonObject.optString("fields"));
                        info.creator = jsonObject.optString("create_account");
                        info.createTime = jsonObject.optString("create_time");
                        info.status = jsonObject.optString("status");
                        info.headline = jsonObject.optString("headline");
                        info.detail = jsonObject.optString("detail");
                        info.executor = jsonObject.optString("execute_account");
                        info.reward = jsonObject.optString("reward");
                        info.comment = jsonObject.optString("comment");
                        info.score = jsonObject.optInt("score");
                        JSONArray takers = jsonObject.optJSONArray("response_accounts");
                        for (int j = 0; j < takers.length(); ++j)
                            info.takers.add(takers.optString(j));
                        mTasks.add(info);
                    }
                } catch (Exception eJson) {
                    showToast("ERROR: "+eJson.toString());
                }

//                mSchedule = new SimpleAdapter(getActivity(),mTasks, R.layout.tasklist_item,
//                        new String[]{"task_item_content", "task_item_person", "task_item_pay"},
//                        new int[]{R.id.task_item_content, R.id.task_item_person, R.id.task_item_pay});
                mListView.setAdapter(mSchedule);
            }
        }

        @Override
        protected void onCancelled() {
            showToast(this.getClass().getSimpleName() + " Cancelled");
        }
    }
}
