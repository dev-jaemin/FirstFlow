package com.example.firstflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        init();
    }

    protected void init(){
        // indent로 연락처 정보 불러와서 매핑시키기
        Intent detailIntent = getIntent();

        TextView nameTextView = (TextView)findViewById(R.id.contact_name);
        TextView phoneTextView = (TextView)findViewById(R.id.contact_num);
        ImageButton callImageButton = (ImageButton)findViewById(R.id.contact_callBtn);

        nameTextView.setText(detailIntent.getStringExtra("name"));
        phoneTextView.setText(detailIntent.getStringExtra("phone"));

        callImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = "tel:"+detailIntent.getStringExtra("phone");
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
            }
        });
    }
}