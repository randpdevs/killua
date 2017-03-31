package com.example.rakesh.mathathon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.rakesh.mathathon.MESSAGE";
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void questionFetch(View view) {
        Intent intent = new Intent(this, QuestionFetch.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        if (message.length() == 0) {
            Snackbar mySnackbar = Snackbar.make(view, "Please enter valid userName", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        } else {
            intent.putExtra(EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    public void testLayoutFunction(View view){
        Intent intent = new Intent(this, testLayoutActivity.class);
        startActivity(intent);

    }


}
