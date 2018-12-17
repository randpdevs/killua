package com.example.rakesh.calcoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Function_Home_page(View view)
    {
        Intent intent = new Intent(this, Activity_Home_Page.class);
        startActivity(intent);
    }
}
