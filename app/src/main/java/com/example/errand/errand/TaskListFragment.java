package com.example.errand.errand;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.errand.errand.Objects.TaskInfo;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

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
import java.util.List;

public class TaskListFragment extends ListFragment {
    private List<TaskInfo> mTasks;
    private Integer minPk;
    private TaskListAdapter mAdapter;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private Errand app;
    private TextView headline;
    private EditText search;
    private Button searchB;
    private TextView back;
    private LinearLayout searchLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTasks = new ArrayList<>();
        minPk = Integer.MAX_VALUE;
        app = ((Errand)getActivity().getApplication());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new TaskListAdapter(getActivity(),R.layout.tasklist_item, mTasks);
        setListAdapter(mAdapter);
        mSwipeRefreshLayout = (SwipyRefreshLayout) getActivity().findViewById(R.id.tasklist_refresh);
        setSearch(false, "");
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Integer pk = mAdapter.getItem(position).pk;
        String username = mAdapter.getItem(position).creator;
        Intent intent = new Intent(getActivity(), TaskInfoDetailActivity.class);
        intent.putExtra("pk", pk);
        intent.putExtra("isMine", username.equals(app.username));
        startActivityForResult(intent, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tasklist, container, false);
        headline = (TextView) view.findViewById(R.id.healine);
        back = (TextView) view.findViewById(R.id.back_list);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearch(false, " ");
            }
        });
        search = (EditText) view.findViewById(R.id.tv_search);
        searchB = (Button) view.findViewById(R.id.b_search);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = search.getText().toString();
                if (info.isEmpty()) {
                    showToast("请输入");
                } else {
                    setSearch(true, info);
                }
            }
        });
        searchLayout = (LinearLayout) view.findViewById(R.id.ll_search);
        return view;
    }

    private void showToast(String content) {
        Toast.makeText(getActivity().getApplicationContext(), content, Toast.LENGTH_LONG).show();
    }

    private void setSearch(boolean isSearch, final String headline) {
        minPk = Integer.MAX_VALUE;
        searchLayout.setVisibility(isSearch ? View.GONE : View.VISIBLE);
        back.setVisibility(isSearch ? View.VISIBLE : View.GONE);
        if (isSearch) {
            this.headline.setText(headline);
            mAdapter.clear();
            mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    new UserSearchtaskTask(Integer.toString(minPk), headline, direction).execute();
                }
            });
            new UserSearchtaskTask(headline).execute();
        } else {
            this.headline.setText("任务列表");
            mAdapter.clear();
            mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    new BrowseAllTasks(minPk, direction).execute();
                }
            });
            new BrowseAllTasks().execute();
        }
    }



    private class BrowseAllTasks extends AsyncTask<Void, Void, String> {
        private final Integer lastPk;
        private boolean isRefresh;
        private final SwipyRefreshLayoutDirection direction;

        public BrowseAllTasks(Integer pk, SwipyRefreshLayoutDirection direction){
            if(direction == SwipyRefreshLayoutDirection.TOP) {
                minPk = Integer.MAX_VALUE;
                lastPk = Integer.MAX_VALUE;
            }else{
                lastPk = pk;
            }
            isRefresh = true;
            this.direction = direction;
        }

        public BrowseAllTasks() {
            lastPk = Integer.MAX_VALUE;
            isRefresh = false;
            this.direction=SwipyRefreshLayoutDirection.TOP;
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
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if(isRefresh) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if (!result.contains("FAILED")) {
                if(direction==SwipyRefreshLayoutDirection.TOP) {
                    mAdapter.clear();
                }
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if(jsonArray.length() == 0){
                        showToast("No More");
                    }else {
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            TaskInfo info = new TaskInfo();
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            info.pk = jsonObject.optInt("pk");
                            minPk = Math.min(minPk, info.pk);
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
                            for (int j = 0; j < takers.length(); ++j) {
                                info.takers.add(takers.optString(j));
                            }
                            mAdapter.add(info);
                        }
                    }
                } catch (Exception eJson) {
                    showToast("ERROR: "+eJson.toString());
                }
            }
        }
    }

    /*
根据关键词mInfo查找任务
*/
    public class UserSearchtaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是上次浏览到的最后一个任务的pk号，返回比该pk号小的最大的5个任务，如果没有应为最大整数，同browsetask中的用法
        private final String mInfo;
        private final SwipyRefreshLayoutDirection direction;
        private final boolean isRefresh;
        private int pk;//这个是查找到的任务的pk号，注意区分
        private String create_account;
        private String create_time;
        private String status;
        private String execute_account;
        private String comment;
        private int score;
        private List<String> response_accounts = new ArrayList<String>();
        private String headline;
        private String detail;
        private String reward;
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";


        UserSearchtaskTask(String pk, String info, SwipyRefreshLayoutDirection direction) {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                minPk = Integer.MAX_VALUE;
                mPk = Integer.toString(Integer.MAX_VALUE);
            } else {
                mPk = pk;
            }
            mInfo = info;
            isRefresh = true;
            this.direction = direction;
        }

        UserSearchtaskTask(String info) {
            mPk = Integer.toString(Integer.MAX_VALUE);
            mInfo = info;
            isRefresh = false;
            this.direction = SwipyRefreshLayoutDirection.TOP;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/searchtask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("SearchTask!");
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
                out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8"));
                String param = "pk=" + mPk + "&" + "text=" + mInfo;
                out.print(param);
                out.flush();
                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                System.out.println("result = " + result);
                //Thread.sleep(500);
            } catch (Exception e) {
                System.out.println("Search Task:发送POST请求出现异常！ " + e);
                result = "FAILED: " + e.toString();
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
            if (isRefresh) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            //mSearchtaskTask = null;
            if (success) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mAdapter.clear();
                }
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        showToast("No More");
                    } else {
                        for (int i = 0; i < jsonArray.length(); ++i) {
                            TaskInfo info = new TaskInfo();
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            info.pk = jsonObject.optInt("pk");
                            minPk = Math.min(minPk, info.pk);
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
                            for (int j = 0; j < takers.length(); ++j) {
                                info.takers.add(takers.optString(j));
                            }
                            mAdapter.add(info);
                        }
                    }
                } catch (Exception ejson) {
                    System.out.println("Search Task:解析JSON异常" + ejson);
                }
            } else {
                if (result.indexOf("[]") >= 0) {
                    showToast("No More");
                } else {
                    showToast(result);
                }

                System.out.println("Search Task Failed:没有符合条件的任务");
            }
        }

        @Override
        protected void onCancelled() {
            //mSearchtaskTask = null;
        }
    }



    private class TaskListAdapter extends ArrayAdapter<TaskInfo>{
        private int resource;
        public TaskListAdapter(Context context, int resource, List<TaskInfo> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout taskItemView;
            TaskInfo task = getItem(position);
            if(convertView == null){
                taskItemView = new LinearLayout(getContext());
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resource, taskItemView, true);
            }else{
                taskItemView = (LinearLayout)convertView;
            }
            TextView headline = (TextView) taskItemView.findViewById(R.id.task_item_headline);
            headline.setText(task.headline);
            TextView username = (TextView) taskItemView.findViewById(R.id.task_item_username);
            username.setText(task.creator);
            TextView pay = (TextView) taskItemView.findViewById(R.id.task_item_pay);
            pay.setText(task.reward);
            return taskItemView;
        }
    }

}
