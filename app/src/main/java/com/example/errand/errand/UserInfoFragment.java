/*
显示一些用户信息，对应userinfo.xml
点击显示过往任务列表，用户信息等信息
区别于详细用户信息user_info
 */

package com.example.errand.errand;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.errand.errand.TaskInfoDetailActivity.disableEditText;


/**
 * Created by xiasifeng on 2016/5/25.
 */
public class UserInfoFragment extends Fragment {

    private Errand app;
    //    UserLogoutTask mLogoutTask;
    private LinearLayout user;
    private TextView username;
    private TextView create_view;
    private TextView posted_waiting;
    private TextView posted_accepted;
    private TextView taken;
    private TextView execute;
    private TextView past_posted;
    private TextView past_executed;
    private TextView logout;
    private Calendar startTime;
    private Calendar endTime;

    private SimpleDateFormat format;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Errand) getActivity().getApplication();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userinfo, container, false);

        user = (LinearLayout) view.findViewById(R.id.user);
        username = (TextView) view.findViewById(R.id.tv_username);
        create_view = (TextView) view.findViewById(R.id.create_task);
        posted_waiting = (TextView) view.findViewById(R.id.posted_waiting_task);
        posted_accepted = (TextView) view.findViewById(R.id.posted_accepted_task);
        taken = (TextView) view.findViewById(R.id.taken_task);
        execute = (TextView) view.findViewById(R.id.execute_task);
        past_posted = (TextView) view.findViewById(R.id.past_posted_task);
        past_executed = (TextView) view.findViewById(R.id.past_executed_task);
        logout = (TextView) view.findViewById(R.id.logout);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoDetailActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        username.setText(app.username);

        create_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Task");
                final LinearLayout content = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.task_add, null);
                builder.setView(content);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String headline = ((TextInputLayout) content.findViewById(R.id.task_add_headline)).getEditText().getText().toString();
                        final String reward = ((TextInputLayout) content.findViewById(R.id.task_add_payment)).getEditText().getText().toString();
                        final String detail = ((TextInputLayout) content.findViewById(R.id.task_add_detail)).getEditText().getText().toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Add Action");
                        final LinearLayout content = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.task_action_change, null);
                        final TextInputEditText loc = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText();
                        final TextInputEditText act = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText();
                        final TextInputEditText st = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText();
                        final TextInputEditText et = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText();
                        final Button bst = (Button) content.findViewById(R.id.b_st);
                        final Button bet = (Button) content.findViewById(R.id.b_et);
                        disableEditText(st, true);
                        disableEditText(et, true);
                        bst.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar currentDate = Calendar.getInstance();
                                startTime = Calendar.getInstance();
                                new TaskInfoDetailActivity.MyDatePickDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        startTime.set(year, monthOfYear, dayOfMonth);
                                        new TaskInfoDetailActivity.MyTimePickDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                startTime.set(Calendar.MINUTE, minute);
                                                st.setText(format.format(startTime.getTime()));
                                            }
                                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                                    }
                                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        bet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar currentDate = Calendar.getInstance();
                                endTime = Calendar.getInstance();
                                new TaskInfoDetailActivity.MyDatePickDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        endTime.set(year, monthOfYear, dayOfMonth);
                                        new TaskInfoDetailActivity.MyTimePickDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                endTime.set(Calendar.MINUTE, minute);
                                                et.setText(format.format(endTime.getTime()));
                                            }
                                        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                                    }
                                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        builder.setView(content);
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String location = loc != null ? loc.getText().toString() : null;
                                String action = act != null ? act.getText().toString() : null;
                                String start = st != null ? st.getText().toString() : null;
                                String end = et != null ? et.getText().toString() : null;
                                new UserAddtaskTask(headline, detail, reward, location, action, start, end).execute();
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        AlertDialog dialogg = builder.create();
                        dialogg.show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        posted_waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "create_account");
                intent.putExtra("state", "W");
                startActivityForResult(intent, 0);
            }
        });

        posted_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "create_account");
                intent.putExtra("state", "A");
                startActivityForResult(intent, 0);
            }
        });
        taken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "execute_account");
                intent.putExtra("state", "W");
                startActivityForResult(intent, 0);
            }
        });
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "execute_account");
                intent.putExtra("state", "A");
                startActivityForResult(intent, 0);
            }
        });
        past_posted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "create_account");
                intent.putExtra("state", "C");
                startActivityForResult(intent, 0);
            }
        });

        past_executed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserInfoTaskListActivity.class);
                intent.putExtra("title", ((TextView) view).getText());
                intent.putExtra("typeofaccount", "execute_account");
                intent.putExtra("state", "C");
                startActivityForResult(intent, 0);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserLogoutTask().execute();
            }
        });

        return view;
    }

    /*
       添加任务类
       */
    public class UserAddtaskTask extends AsyncTask<Void, Void, Boolean> {
        private final String mHeadline;//标题
        private final String mDetail;//内容
        private final String mReward;//奖励
        private final String mLocation;
        private final String mAction;
        private final String mStartTime;
        private final String mEndTime;
        CookieManager msCookieManager = (CookieManager) CookieHandler.getDefault();
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

        UserAddtaskTask(String headline, String detail, String reward, String loc, String act, String st, String et) {
            mHeadline = headline;
            mDetail = detail;
            mReward = reward;
            mLocation = loc;
            mAction = act;
            mStartTime = st;
            mEndTime = et;
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
                result = "FAILED: " + e.toString();
                return false;
            }
            return result.indexOf("FAILED") < 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
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

                    new UserAddTaskActionTask(Integer.toString(pk), mStartTime, mEndTime, mLocation, mAction).execute();


                } catch (Exception ejson) {
                    System.out.println("AddTask:解析JSON异常" + ejson);
                    Toast.makeText(getContext(), "创建任务失败", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), "创建任务失败", Toast.LENGTH_LONG).show();
                System.out.println("Add Task Failed");
            }
        }

        @Override
        protected void onCancelled() {
            //mAddtaskTask = null;
        }
    }

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
            //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(getActivity(), TaskInfoDetailActivity.class);
                    intent.putExtra("pk", Integer.parseInt(mPk));
                    intent.putExtra("isMine", true);
                    startActivityForResult(intent, 0);
                } catch (Exception ejson) {
                    System.out.println("Add Task Action:解析JSON异常" + ejson);
                    Toast.makeText(getContext(), "创建任务部分失败", Toast.LENGTH_LONG).show();
                }
                System.out.println("Add Task Action succeed");
            } else {
                new UserRemovetaskTask(mPk).execute();
                Toast.makeText(getContext(), "创建任务失败", Toast.LENGTH_LONG).show();
                System.out.println("Add Task Action Failed!");
            }
        }

        @Override
        protected void onCancelled() {
            //mAddTaskActionTask = null;
        }
    }

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
            if (success) {
                System.out.println("Remove Task succeed");
            } else {
                Toast.makeText(getContext(), "内部错误，您的任务将自动关闭", Toast.LENGTH_LONG).show();
                System.out.println("Remove Task Failed!");
            }
        }
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
            //mLogoutTask = null;
            if (success) {
//                SharedPreferences cookies = getActivity().getSharedPreferences("Cookie", 0);
//                SharedPreferences.Editor editor = cookies.edit();
//                editor.putString("Cookie", null);
//                editor.commit();
                msCookieManager.getCookieStore().removeAll();
                getActivity().getSharedPreferences("User", Context.MODE_PRIVATE).edit().remove("username").remove("password").apply();
                app.key = null;
                //登出之前清理cookie
                System.out.println("Logout!");
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            } else {
                System.out.println("Logout Failed!");
                Toast.makeText(getActivity().getApplicationContext(), "注销失败", Toast.LENGTH_LONG);
            }
        }

        @Override
        protected void onCancelled() {
            //mLogoutTask = null;
        }
    }
}
