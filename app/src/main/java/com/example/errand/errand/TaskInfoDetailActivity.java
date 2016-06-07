/*
任务详情页，对应的是activity_task_info.xml，
点击用户列表可以跳转到对应的用户详情页，user_info.java
可以设置点击事件更改项，通过跳转到changeinfo.java更改对应内容并返回输入值
返回图标返回，确认图标在编辑的时候可以显示并且点击后确认，
如果简单浏览的话可以隐藏
*/

package com.example.errand.errand;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.errand.errand.Objects.TaskActionInfo;
import com.example.errand.errand.Objects.TaskInfo;
import com.example.errand.errand.Objects.UserInfo;

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

public class TaskInfoDetailActivity extends Activity {
    private TaskInfo taskInfo;
    private UserInfo userInfo;
    private int pk;
    private boolean isMine;
    private Errand app;

    private TextView headline;
    private TextView ownerUsername;
    private TextView ownerLevel;
    private LinearLayout executorLayout;
    private TextView executorUsername;
    private TextView executorLevel;
    private TextView status;
    private TextView payment;
    private TextView detail;
    private LinearLayout commentLayout;
    private TextView comment;

    private TextView back;
    private TextView confirm;
    private TextView delete;
    private ListView actionList;

    private RecyclerView takerView;
    private RecyclerView.Adapter takerAdapter;
    private RecyclerView.LayoutManager takerLayout;
    private List<String> takers;

    private EditText addNewAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pk = getIntent().getIntExtra("pk", -1);
        isMine = getIntent().getBooleanExtra("isMine", false);
        app = (Errand) getApplication();
        setContentView(R.layout.activity_task_info);

        headline = (TextView) findViewById(R.id.healine);
        confirm = (TextView) findViewById(R.id.confirm);
        delete = (TextView) findViewById(R.id.delete);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ownerUsername = (TextView) findViewById(R.id.ll_owner).findViewById(R.id.user_item_username);
        ownerLevel = (TextView) findViewById(R.id.ll_owner).findViewById(R.id.user_item_level);
        status = (TextView) findViewById(R.id.status);

        payment = (TextView) findViewById(R.id.reward);
        detail = (TextView) findViewById(R.id.detail);

        actionList = (ListView) findViewById(R.id.taskActionListView);
        actionList.setAdapter(new TaskActionListAdapter(this, R.layout.task_action_item, new ArrayList<TaskActionInfo>()));
        addNewAction = (EditText) findViewById(R.id.addNewTaskAction);

        takerView = (RecyclerView) findViewById(R.id.user_list);
        takerLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        takerView.setLayoutManager(takerLayout);
        takers = new ArrayList<>();
        takerAdapter = new TakerAdapter(takers);
        takerView.setAdapter(takerAdapter);

        executorLayout = (LinearLayout) findViewById(R.id.ll_executor);
        executorUsername = (TextView) findViewById(R.id.ll_executor).findViewById(R.id.user_item_username);
        executorLevel = (TextView) findViewById(R.id.ll_executor).findViewById(R.id.user_item_level);

        commentLayout = (LinearLayout) findViewById(R.id.ll_comment);
        comment = (TextView) findViewById(R.id.comment);
    }

    private void enableEdit(boolean editable) {
        headline.setOnClickListener(!editable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Change Headline");
                final EditText content = new EditText(getApplicationContext());
                content.setText(((TextView) v).getText());
                content.setTextColor(Color.BLACK);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String headline = content.getText().toString();
                        new UserChangetaskTask(Integer.toString(pk), headline, taskInfo.detail, taskInfo.reward).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        payment.setOnClickListener(!editable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Change Reward");
                final EditText content = new EditText(getApplicationContext());
                content.setText(((TextView) v).getText());
                content.setTextColor(Color.BLACK);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String reward = content.getText().toString();
                        new UserChangetaskTask(Integer.toString(pk), taskInfo.headline, taskInfo.detail, reward).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        detail.setOnClickListener(!editable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Change Detail");
                final EditText content = new EditText(getApplicationContext());
                content.setText(((TextView) v).getText());
                content.setTextColor(Color.BLACK);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String detail = content.getText().toString();
                        new UserChangetaskTask(Integer.toString(pk), taskInfo.headline, detail, taskInfo.reward).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        addNewAction.setVisibility(editable ? View.VISIBLE : View.GONE);
        addNewAction.setOnClickListener(!editable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Add Action");
                final LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.task_action_change, null);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String location = ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().getText().toString();
                        String action = ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().getText().toString();
                        String start = ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().getText().toString();
                        String end = ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().getText().toString();
                        new UserAddTaskActionTask(Integer.toString(pk), start, end, location, action).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        actionList.setOnItemClickListener(!editable ? new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskActionInfo info = (TaskActionInfo) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Action Info");
                final LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.task_action_change, null);
                ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().setBackground(null);
                ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().setFocusable(false);
                ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().setText(info.place);
                ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().setBackground(null);
                ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().setFocusable(false);
                ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().setText(info.action);
                ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().setBackground(null);
                ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().setFocusable(false);
                ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().setText(info.startTime);
                ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().setBackground(null);
                ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().setFocusable(false);
                ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().setText(info.endTime);
                builder.setView(content);
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } : new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TaskActionInfo info = (TaskActionInfo) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Change Action");
                final LinearLayout content = (LinearLayout) getLayoutInflater().inflate(R.layout.task_action_change, null);
                ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().setText(info.place);
                ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().setText(info.action);
                ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().setText(info.startTime);
                ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().setText(info.endTime);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String location = ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText().getText().toString();
                        String action = ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText().getText().toString();
                        String start = ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText().getText().toString();
                        String end = ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText().getText().toString();
                        new UserChangeTaskActionTask(Integer.toString(info.actionPk), start, end, location, action).execute();
                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new UserRemoveTaskActionTask(Integer.toString(info.actionPk)).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        takerView.setClickable(editable);
    }

    private void showExecutor(boolean show) {
        executorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void enableDelete(boolean enable) {
        delete.setVisibility(enable ? View.VISIBLE : View.GONE);
        delete.setOnClickListener(!enable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserRemovetaskTask(Integer.toString(pk)).execute();
            }
        });
    }

    private void setConfirm(boolean show) {
        confirm.setVisibility(show ? View.VISIBLE : View.GONE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMine) {
                    new UserClosetaskTask(Integer.toString(pk)).execute();
                } else {
                    new UserResponsetaskTask(Integer.toString(pk)).execute();
                }
            }
        });
    }

    private void enableComment(boolean show, boolean editable) {
        commentLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        comment.setOnClickListener(!editable ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Comment");
                LinearLayout content = new LinearLayout(getApplicationContext());
                final EditText comment = new EditText(getApplicationContext());
                comment.setTextColor(Color.BLACK);
                final RatingBar ratingBar = new RatingBar(getApplicationContext());
                ratingBar.setStepSize(1);
                content.addView(ratingBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                content.addView(comment, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String com = comment.getText().toString();
                        String score = Integer.toString((int) ratingBar.getRating());
                        new UserCommenttaskTask(Integer.toString(pk), score, com).execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Refresh();
    }

    protected void Refresh() {
        new GetTaskInfo(pk).execute();
        new UserGetTaskActionTask(Integer.toString(pk)).execute();
    }

    private class TaskActionListAdapter extends ArrayAdapter<TaskActionInfo> {
        private int resource;
        public TaskActionListAdapter(Context context, int resource, List<TaskActionInfo> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout taskActionItemView;
            TaskActionInfo info = getItem(position);
            if(convertView == null){
                taskActionItemView = new LinearLayout(getContext());
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(resource, taskActionItemView, true);
            }else{
                taskActionItemView = (LinearLayout)convertView;
            }
            TextView location = (TextView) taskActionItemView.findViewById(R.id.location);
            location.setText(info.place);
            TextView start = (TextView) taskActionItemView.findViewById(R.id.start);
            start.setText(info.startTime);
            TextView end = (TextView) taskActionItemView.findViewById(R.id.end);
            end.setText(info.endTime);
            TextView action = (TextView) taskActionItemView.findViewById(R.id.action);
            action.setText(info.action);
            return taskActionItemView;
        }
    }

    private class TakerAdapter extends RecyclerView.Adapter<TakerAdapter.ViewHolder> {
        private List<String> takers;

        public TakerAdapter(List<String> takers) {
            this.takers = takers;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
            TextView username = (TextView) v.findViewById(R.id.user);
            ViewHolder vh = new ViewHolder(v, username);
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.username.setText(takers.get(position));
        }

        public int getItemCount() {
            return takers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView username;

            public ViewHolder(View content, TextView username) {
                super(content);
                this.username = username;
                if (isMine) {
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String user = takers.get(getAdapterPosition());
                            AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                            builder.setTitle("Pick Executor");
                            builder.setMessage("Choose " + user + " as executor?");
                            builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new UserSelectTaskExecutorTask(Integer.toString(pk), user).execute();
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        }

    }

    private class GetTaskInfo extends AsyncTask<Void, Void, String> {
        private final Integer pk;

        public GetTaskInfo(Integer pk){
            this.pk = pk;
        }

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/seetask";
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
                String param = "pk=" + pk;
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
            if (!result.contains("FAILED")) {
                try {
                    taskInfo = new TaskInfo();
                    JSONArray jsonArray = new JSONArray(result);
                            JSONObject jsonObject = jsonArray.optJSONObject(0);
                            taskInfo.pk = jsonObject.optInt("pk");
                            jsonObject = new JSONObject(jsonObject.optString("fields"));
                    taskInfo.creator = jsonObject.optString("create_account");
                    taskInfo.createTime = jsonObject.optString("create_time");
                    taskInfo.status = jsonObject.optString("status");
                    taskInfo.headline = jsonObject.optString("headline");
                    taskInfo.detail = jsonObject.optString("detail");
                    taskInfo.executor = jsonObject.optString("execute_account");
                    taskInfo.reward = jsonObject.optString("reward");
                    taskInfo.comment = jsonObject.optString("comment");
                    taskInfo.score = jsonObject.optInt("score");
                            JSONArray takers = jsonObject.optJSONArray("response_accounts");
                            for (int j = 0; j < takers.length(); ++j) {
                                taskInfo.takers.add(takers.optString(j));
                            }
                } catch (Exception eJson) {
                    Toast.makeText(getApplicationContext(), "ERROR: "+eJson.toString(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
            if (!taskInfo.pk.equals(this.pk)) {
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_LONG).show();
            } else {
                ownerUsername.setText(taskInfo.creator);
                headline.setText(taskInfo.headline);
                if (taskInfo.status.equals("A")) {
                    status.setText("已指派");
                    enableComment(false, false);
                    enableDelete(false);
                    enableEdit(false);
                    setConfirm(isMine);
                    showExecutor(true);
                } else if (taskInfo.status.equals("W")) {
                    status.setText("待指派");
                    enableComment(false, false);
                    showExecutor(false);
                    setConfirm(!isMine && !taskInfo.takers.contains(app.username));
                    if (taskInfo.takers.size() == 0) {
                        enableEdit(isMine);
                        enableDelete(isMine);
                    } else {
                        enableEdit(false);
                        enableDelete(false);
                    }
                } else if (taskInfo.status.equals("C")) {
                    status.setText("已结束");
                    enableComment(true, isMine);
                    showExecutor(false);
                    enableDelete(true);
                    enableEdit(false);
                    setConfirm(false);
                }
                detail.setText(taskInfo.detail);
                payment.setText(taskInfo.reward);
                comment.setText(taskInfo.comment);
                executorUsername.setText(taskInfo.executor);
                takers.clear();
                for (String taker : taskInfo.takers) {
                    takers.add(taker);
                }
            }
        }
    }


    /*
        添加任务条目类
        */
    public class UserAddTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mStart_time;//格式：2016-10-11 13:00:00
        private final String mEnd_time;
        private final String mPlace;//地点
        private final String mAction;//内容
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //mAddTaskActionTask = null;
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mAddTaskActionTask = null;
        }
    }



    /*
        修改任务条目类
        与添加任务条目类似，注意需要任务条目的编号
        */
    public class UserChangeTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是TaskAction的pk号
        private final String mStart_time;
        private final String mEnd_time;
        private final String mPlace;
        private final String mAction;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //mChangeTaskActionTask = null;
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
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mChangeTaskActionTask = null;
        }
    }

    /*
        修改任务类，与添加任务类基本类似，只是需要指定任务编号pk
        */
    public class UserChangetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;
        private final String mHeadline;
        private final String mDetail;
        private final String mReward;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
            //mChangetaskTask = null;
        }
    }

    /*
        关闭任务类
        任务发布者将任务关闭
        */
    public class UserClosetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //mClosetaskTask = null;
            if (success) {
                System.out.println("Close Task succeed");
            } else {
                System.out.println("Close Task Failed");
            }
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mClosetaskTask = null;
        }
    }

    /*
        任务评价类
        发布者评价任务的完成状况并打分
        */
    public class UserCommenttaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mScore;//打分 目前范围是1-5
        private final String mComment;//评价
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //mCommenttaskTask = null;
            if (success) {
                System.out.println("Comment Task succeed");
            } else {
                System.out.println("Comment Task Failed");
            }
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mCommenttaskTask = null;
        }
    }

    /*
        删除任务条目类，需要任务条目的编号
        */
    public class UserRemoveTaskActionTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//TaskAction的pk号
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                System.out.println("RemoveTaskAction succeed");
            } else {
                System.out.println("Remove Task Action Failed!");
            }
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mRemoveTaskActionTask = null;
        }
    }

    /*
        删除任务类，只需要指定任务编号
        */
    public class UserRemovetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                System.out.println("Remove Task succeed");
                setResult(RESULT_OK);
                finish();
            } else {
                System.out.println("Remove Task Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            //mRemovetaskTask = null;
        }
    }

    /*
        接受任务，需要被接受任务的编号
        */
    public class UserResponsetaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                System.out.println("Response Task succeed");
            } else {
                System.out.println("Response Task Failed");
            }
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mResponsetaskTask = null;
        }
    }

    /*
        选择完成者类
        */
    public class UserSelectTaskExecutorTask extends AsyncTask<Void, Void, Boolean> {
        private final String mPk;//这个是任务的pk号
        private final String mUsername;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.equals("OK");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                System.out.println("Select Task Executor succeed");
            } else {
                System.out.println("Select Task Executor Failed");
            }
            Refresh();
        }

        @Override
        protected void onCancelled() {
            //mSelectTaskExecutorTask = null;
        }
    }


    /*
根据任务的pk号查找属于这个任务的任务条目并列出
*/
    public class UserGetTaskActionTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPk;//这个是任务的pk号

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        UserGetTaskActionTask(String pk) {
            mPk = pk;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String strUrl = "http://139.129.47.180:8002/Errand/gettaskactions";
            URL Url = null;
            try {
                Url = new URL(strUrl);
                URLConnection urlConnection = Url.openConnection();
                System.out.println("GetTaskAction!");
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
                String param = "pk=" + mPk;
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
                System.out.println("GetTaskAction:发送POST请求出现异常！ " + e);
                result = "FAILED: " + e.toString();
                return false;
            }
            if (result.indexOf("FAILED") >= 0)
                return false;
            else
                return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    ((TaskActionListAdapter) actionList.getAdapter()).clear();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        TaskActionInfo info = new TaskActionInfo();
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        info.actionPk = jsonObject.optInt("pk");
                        String TaskInfo = jsonObject.optString("fields");
                        jsonObject = new JSONObject(TaskInfo);
                        info.startTime = jsonObject.optString("start_time");
                        info.endTime = jsonObject.optString("end_time");
                        info.place = jsonObject.optString("place");
                        info.action = jsonObject.optString("action");
                        info.pk = jsonObject.optInt("task_belong");
                        ((TaskActionListAdapter) actionList.getAdapter()).add(info);
                    }
                } catch (Exception ejson) {
                    System.out.println("Get Task Action:解析JSON异常" + ejson);
                }

                System.out.println("Get Task Action succeed");

            } else {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                System.out.println("Get Task Action Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            //mGetTaskActionTask = null;
        }

    }

}
