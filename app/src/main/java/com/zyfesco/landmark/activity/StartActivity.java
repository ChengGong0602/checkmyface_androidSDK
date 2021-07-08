package com.zyfesco.landmark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zyfesco.landmark.R;

public class StartActivity extends AppCompatActivity {
    private Button Face_register, Face_verify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Face_register= (Button)findViewById(R.id.register_btn);
        Face_verify= (Button)findViewById(R.id.verify_btn);



        Face_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), GoregisterActivity.class);
                    startActivity(intent);
            }
        });
        Face_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GoverifyActivity.class);
                startActivity(intent);

            }
        });
    }
}
