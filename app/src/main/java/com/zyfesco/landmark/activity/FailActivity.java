package com.zyfesco.landmark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zyfesco.landmark.R;


public class FailActivity extends AppCompatActivity {
    private Button retryButton;
    private TextView failerrorTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fail);
        String Failerror = getIntent().getStringExtra("Failreason");
        failerrorTxt=(TextView)findViewById(R.id.failerror);
        failerrorTxt.setText(Failerror);

        retryButton=(Button)findViewById(R.id.Retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),StartActivity.class);
                startActivity(intent);
            }
        });
    }
}
