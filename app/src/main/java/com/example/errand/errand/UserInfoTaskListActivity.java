/*
对应activity_nofragment_tasklist.xml
主要是用于userinfo（用户信息）fragment的一些历史任务列表的显示
 */

package com.example.errand.errand;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class UserInfoTaskListActivity extends ListActivity {

    private TextView titletext;
    private TextView back;
    private List<TaskInfo> mTasks;
    private Integer minPk;
    private TaskListAdapter mAdapter;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private Errand app;
    private String title;
    private String typeoftask; // execute_account create_account
    private String state; // A(ccepted) W(aiting) C(losed)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTasks = new ArrayList<>();
        minPk = Integer.MAX_VALUE;
        app = ((Errand) getApplication());

        title = getIntent().getStringExtra("title");
        typeoftask = getIntent().getStringExtra("typeofaccount");
        state = getIntent().getStringExtra("state");

        setContentView(R.layout.activity_nofragment_tasklist);

        titletext = (TextView) findViewById(R.id.title) ;
        titletext.setText(title);

        mAdapter = new TaskListAdapter(this, R.layout.tasklist_item, mTasks);
        setListAdapter(mAdapter);
        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.tasklist_my_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                new UserGetUsertaskTask(app.username, typeoftask, state, Integer.toString(minPk), direction).execute();
            }
        });
        new UserGetUsertaskTask(app.username, typeoftask, state).execute();

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

    public void onListItemClick(ListView l, View v, int position, long id) {
        Integer pk = mAdapter.getItem(position).pk;
        String username = mAdapter.getItem(position).creator;
        Intent intent = new Intent(this, TaskInfoDetailActivity.class);
        intent.putExtra("pk", pk);
        intent.putExtra("isMine", username.equals(app.username));
        startActivityForResult(intent, 0);
    }

//    public void refresh()
//    {
//        mylist = new ArrayList< HashMap<String, String> >();
//        for (int i=0;i<item_num;i++)
//        {
//            taskcontent="求帮忙拿快递！！";
//            taskuser="personA";
//            taskpay="      5RMB";
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("task_item_content", taskcontent);
//            map.put("task_item_person", taskuser);
//            map.put("task_item_pay", taskpay);
//            mylist.add(map);
//        }
//        mSchedule = new SimpleAdapter( this, mylist, R.layout.tasklist_item,
//                new String[] {"task_item_content", "task_item_person" , "task_item_pay"},
//                new int[]{R.id.task_item_content,R.id.task_item_person, R.id.task_item_pay});
//        listview.setAdapter(mSchedule);
//
//    }

    private class TaskListAdapter extends ArrayAdapter<TaskInfo> {
        private int resource;

        public TaskListAdapter(Context context, int resource, List<TaskInfo> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout taskItemView;
            TaskInfo task = getItem(position);
            if (convertView == null) {
                taskItemView = new LinearLayout(getContext());
                getLayoutInflater().inflate(resource, taskItemView, true);
            } else {
                taskItemView = (LinearLayout) convertView;
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

    /*
根据用户信息、任务状态和类型来查找任务
*/
    public class UserGetUsertaskTask extends AsyncTask<Void, Void, Boolean> {
        private boolean isRefresh;
        private final SwipyRefreshLayoutDirection direction;

        private final String mUsername;//用户名
        private final String mTypeofTask;
        //任务类型：execute_account或create_account
        //根据任务状态分别表示该用户是任务的执行者(execute)/响应者(response)或创建者
        private final String mState;
        //任务状态：A(accepted)、W(waiting)、C(closed)
        //在waiting状态按响应者查找，在accepted和closed状态按执行者查找
        private final String mPk;
        //这个是上次浏览到的最后一个任务的pk号，返回比该pk号小的最大的5个任务，如果没有应为最大整数，之前的用法
        private int pk;
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

        UserGetUsertaskTask(String username, String typeoftask, String state, String pk, SwipyRefreshLayoutDirection direction) {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                minPk = Integer.MAX_VALUE;
                mPk = Integer.toString(Integer.MAX_VALUE);
            } else {
                mPk = pk;
            }
            isRefresh = true;
            this.direction = direction;
            mUsername = username;
            mTypeofTask = typeoftask;
            mState = state;
        }

        UserGetUsertaskTask(String username, String typeoftask, String state) {
            mPk = Integer.toString(Integer.MAX_VALUE);
            isRefresh = false;
            this.direction = SwipyRefreshLayoutDirection.TOP;
            mUsername = username;
            mTypeofTask = typeoftask;
            mState = state;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/getusertask";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("Get User Task!");
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
                String param = "username=" + mUsername + "&" + "typeOfTask=" + mTypeofTask + "&" + "state=" + mState + "&" + "pk=" + mPk;
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
                System.out.println("Get User Task:发送POST请求出现异常！ " + e);
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
            //mGetUsertaskTask = null;
            if (isRefresh) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if (success) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    mAdapter.clear();
                }
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        Toast.makeText(getApplicationContext(), "No More", Toast.LENGTH_LONG).show();
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
                    System.out.println("Get User Task:解析JSON异常" + ejson);

                }
            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                System.out.println("Get User Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            //mGetUsertaskTask = null;
        }
    }

}
