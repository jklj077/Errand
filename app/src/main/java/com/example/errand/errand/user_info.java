/*

用户信息详情页，对应的是activity_user_info.xml
更改信息可以设置点击跳转到changeinfo.java
流程类似于task_info

*/
package com.example.errand.errand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class user_info extends Activity {
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
}
