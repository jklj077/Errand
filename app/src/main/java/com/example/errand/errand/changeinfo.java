/*
更改信息的活动页，点击返回不返回输入的值，
点击确认返回输入的值

 */

package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class changeinfo extends Activity {

    private String content;
    private TextView back;
    private TextView confirm;
    private TextView view_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);

        content = this.getIntent().getExtras().getString("content");
        back = (TextView) findViewById(R.id.back);
        confirm = (TextView) findViewById(R.id.confirm);
        view_content = (TextView) findViewById(R.id.content);
        view_content.setText(content);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = view_content.getText().toString();
                Intent intent = getIntent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = view_content.getText().toString();
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString("content",content);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


}
