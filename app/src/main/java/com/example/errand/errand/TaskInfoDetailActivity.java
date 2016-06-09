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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskInfoDetailActivity extends Activity {
    private TaskInfo taskInfo;
    private UserInfo userInfo;
    private int pk;
    private boolean isMine;
    private Errand app;

    private TextView headline;
    private TextView ownerUsername;
    private LinearLayout executorLayout;
    private TextView executorUsername;
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
    private Calendar startTime;
    private Calendar endTime;

    private SimpleDateFormat format;

    public static void disableEditText(TextInputEditText v, boolean isEdit) {
        if (!isEdit) {
            v.setBackground(null);
        }
        v.setFocusable(false);
        v.setFocusableInTouchMode(false);
        v.setClickable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pk = getIntent().getIntExtra("pk", -1);
        isMine = getIntent().getBooleanExtra("isMine", false);
        app = (Errand) getApplication();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                        new MyDatePickDialog(TaskInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startTime.set(year, monthOfYear, dayOfMonth);
                                new MyTimePickDialog(TaskInfoDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        new MyDatePickDialog(TaskInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endTime.set(year, monthOfYear, dayOfMonth);
                                new MyTimePickDialog(TaskInfoDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                final TextInputEditText loc = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText();
                final TextInputEditText act = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText();
                final TextInputEditText st = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText();
                final TextInputEditText et = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText();
                final Button bst = (Button) content.findViewById(R.id.b_st);
                final Button bet = (Button) content.findViewById(R.id.b_et);
                disableEditText(loc, false);
                disableEditText(act, false);
                disableEditText(st, false);
                disableEditText(et, false);
                bst.setVisibility(View.GONE);
                bet.setVisibility(View.GONE);
                loc.setText(info.place);
                act.setText(info.action);
                st.setText(info.startTime);
                et.setText(info.endTime);
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
                final TextInputEditText loc = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_location)).getEditText();
                final TextInputEditText act = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_action)).getEditText();
                final TextInputEditText st = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_start)).getEditText();
                final TextInputEditText et = (TextInputEditText) ((TextInputLayout) content.findViewById(R.id.action_change_end)).getEditText();
                disableEditText(st, true);
                disableEditText(et, true);
                loc.setText(info.place);
                act.setText(info.action);
                st.setText(info.startTime);
                et.setText(info.endTime);
                final Button bst = (Button) content.findViewById(R.id.b_st);
                final Button bet = (Button) content.findViewById(R.id.b_et);
                bst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar currentDate = Calendar.getInstance();
                        startTime = Calendar.getInstance();
                        new MyDatePickDialog(TaskInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startTime.set(year, monthOfYear, dayOfMonth);
                                new MyTimePickDialog(TaskInfoDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        new MyDatePickDialog(TaskInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endTime.set(year, monthOfYear, dayOfMonth);
                                new MyTimePickDialog(TaskInfoDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        String location = loc.getText().toString();
                        String action = act.getText().toString();
                        String start = st.getText().toString();
                        String end = et.getText().toString();
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
        takerView.setFocusable(editable);
        takerView.setFocusableInTouchMode(editable);
        if (!editable) {
            takerView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }


    private void showExecutor(boolean show) {
        executorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            executorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TaskInfoDetailActivity.this, UserInfoDetailActivity.class);
                    intent.putExtra("username", taskInfo.executor);
                    startActivityForResult(intent, 0);
                }
            });
        }
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

    private void enableComment(boolean show, boolean editable, boolean ischange) {
        commentLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        comment.setOnClickListener(!editable ? null : (!ischange ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Comment");
                LinearLayout content = new LinearLayout(getApplicationContext());
                content.setOrientation(LinearLayout.VERTICAL);
                final TextInputEditText comment = new TextInputEditText(TaskInfoDetailActivity.this);
                comment.setTextColor(Color.BLACK);
                comment.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                comment.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                final TextInputLayout inputLayout = new TextInputLayout(TaskInfoDetailActivity.this);
                inputLayout.setHint("评价");
                inputLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                inputLayout.addView(comment);
                final RatingBar ratingBar = new RatingBar(TaskInfoDetailActivity.this);
                ratingBar.setStepSize(1);
                ratingBar.setNumStars(5);
                ratingBar.setMax(5);
                ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                content.addView(ratingBar);
                content.addView(inputLayout);
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
        } : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskInfoDetailActivity.this);
                builder.setTitle("Change Comment");
                LinearLayout content = new LinearLayout(TaskInfoDetailActivity.this);
                content.setOrientation(LinearLayout.VERTICAL);
                final TextInputEditText comment = new TextInputEditText(TaskInfoDetailActivity.this);
                comment.setTextColor(Color.BLACK);
                comment.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                comment.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                comment.setText(taskInfo.comment);
                final TextInputLayout inputLayout = new TextInputLayout(TaskInfoDetailActivity.this);
                inputLayout.setHint("评价");
                inputLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                inputLayout.addView(comment);
                final RatingBar ratingBar = new RatingBar(TaskInfoDetailActivity.this);
                ratingBar.setStepSize(1);
                ratingBar.setNumStars(5);
                ratingBar.setMax(5);
                ratingBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ratingBar.setRating((float) taskInfo.score);
                content.addView(ratingBar);
                content.addView(inputLayout);
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
        }));
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

    public static class MyDatePickDialog extends DatePickerDialog {

        public MyDatePickDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        public MyDatePickDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, theme, callBack, year, monthOfYear, dayOfMonth);
        }

        @Override
        protected void onStop() {

        }
    }

    public static class MyTimePickDialog extends TimePickerDialog {
        public MyTimePickDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
            super(context, listener, hourOfDay, minute, is24HourView);
        }

        public MyTimePickDialog(Context context, int themeResId, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
            super(context, themeResId, listener, hourOfDay, minute, is24HourView);
        }

        @Override
        protected void onStop() {
        }
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
            taskActionItemView.measure(View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
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
                    JSONArray mtakers = jsonObject.optJSONArray("response_accounts");
                    for (int j = 0; j < mtakers.length(); ++j) {
                        taskInfo.takers.add(mtakers.optString(j));
                            }
                    ownerUsername.setText(taskInfo.creator);
                    headline.setText(taskInfo.headline);
                    if (taskInfo.status.equals("A")) {
                        status.setText("已指派");
                        enableComment(false, false, false);
                        enableDelete(false);
                        enableEdit(false);
                        setConfirm(isMine);
                        showExecutor(true);
                    } else if (taskInfo.status.equals("W")) {
                        status.setText("待指派");
                        enableComment(false, false, false);
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
                        if (taskInfo.executor == "null") {
                            showExecutor(false);
                            enableComment(false, false, false);
                        } else {
                            showExecutor(true);
                            enableComment(true, true, !taskInfo.score.equals(-1));
                        }
                        enableDelete(true);
                        enableEdit(false);
                        setConfirm(false);
                    }
                    detail.setText(taskInfo.detail);
                    payment.setText(taskInfo.reward);
                    comment.setText(taskInfo.comment);
                    executorUsername.setText(taskInfo.executor);
                    takers.clear();
                    takerAdapter.notifyDataSetChanged();
                    for (String taker : taskInfo.takers) {
                        takers.add(taker);
                    }
                    takerAdapter.notifyDataSetChanged();
                } catch (Exception eJson) {
                    Toast.makeText(getApplicationContext(), "ERROR: "+eJson.toString(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "获取任务信息失败", Toast.LENGTH_LONG).show();
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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
                Refresh();
            } else {
                Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_LONG).show();
                System.out.println("Add Task Action Failed!");
            }

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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_LONG).show();
                Refresh();

            } else {
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                System.out.println("Fuck!");
            }

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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                Refresh();
                System.out.println("Change Task succeed");

            } else {
                Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_LONG).show();
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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //mClosetaskTask = null;
            if (success) {
                Toast.makeText(getApplicationContext(), "任务确认成功", Toast.LENGTH_LONG).show();
                System.out.println("Close Task succeed");
                Refresh();
            } else {
                Toast.makeText(getApplicationContext(), "任务确认失败", Toast.LENGTH_LONG).show();
                System.out.println("Close Task Failed");
            }

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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //mCommenttaskTask = null;
            if (success) {
                Refresh();
                Toast.makeText(getApplicationContext(), "评价成功", Toast.LENGTH_LONG).show();
                System.out.println("Comment Task succeed");
            } else {
                Toast.makeText(getApplicationContext(), "评价失败", Toast.LENGTH_LONG).show();
                System.out.println("Comment Task Failed");
            }

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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                Toast.makeText(getApplicationContext(), "删除任务条目成功", Toast.LENGTH_LONG).show();
                System.out.println("RemoveTaskAction succeed");
                Refresh();
            } else {
                Toast.makeText(getApplicationContext(), "删除任务条目失败", Toast.LENGTH_LONG).show();
                System.out.println("Remove Task Action Failed!");
            }
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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                System.out.println("Remove Task succeed");
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "删除任务失败", Toast.LENGTH_LONG).show();
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
            if (success) {
                Toast.makeText(getApplicationContext(), "领取成功", Toast.LENGTH_LONG).show();
                System.out.println("Response Task succeed");
                Refresh();
            } else {
                Toast.makeText(getApplicationContext(), "领取失败", Toast.LENGTH_LONG).show();
                System.out.println("Response Task Failed");
            }
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
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            if (success) {
                Toast.makeText(getApplicationContext(), "指派成功", Toast.LENGTH_LONG).show();
                Refresh();
                System.out.println("Select Task Executor succeed");
            } else {
                Toast.makeText(getApplicationContext(), "指派失败", Toast.LENGTH_LONG).show();
                System.out.println("Select Task Executor Failed");
            }

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
            return result.indexOf("FAILED") < 0;
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
                        Calendar temp = Calendar.getInstance();
                        temp.setTime(format.parse(jsonObject.optString("start_time").replace("T", " ").replace("Z", "")));
                        temp.add(Calendar.HOUR, 8);
                        info.startTime = format.format(temp.getTime());
                        temp.setTime(format.parse(jsonObject.optString("end_time").replace("T", " ").replace("Z", "")));
                        temp.add(Calendar.HOUR, 8);
                        info.endTime = format.format(temp.getTime());
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
                Toast.makeText(getApplicationContext(), "获取任务条目失败", Toast.LENGTH_LONG).show();
                System.out.println("Get Task Action Failed!");
            }
        }

    }


}
