package com.randpdevs.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    protected void Quit() {
        super.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playButtonFunction(View view){
        Intent intent = new Intent(this, GamePlayActivity.class);
        startActivity(intent);
    }
    public void settingsButtonFunction(View view){
        System.out.println("Settings");
    }
}
