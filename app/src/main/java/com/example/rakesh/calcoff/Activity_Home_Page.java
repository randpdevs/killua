package com.example.rakesh.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rakesh on 6/12/2018.
 */


public class Activity_Home_Page extends AppCompatActivity {
    private DatabaseHandler  db;
    private boolean onActivityStart = false;
    private View view1, view2;
    private long epochTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home_page);
        view1 = getLayoutInflater().inflate(R.layout.activity_home_page, null);
        view2 = getLayoutInflater().inflate(R.layout.activity_home_loggedin, null);
        init();
        setLoginRegisterButton();
        //setContentView(view1);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Activity_Home_Page.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!onActivityStart) {
            StartFunction();
            onActivityStart = true;
        }
        else
        {
            onBackPressed();
        }
    }



    private void StartFunction(){
        //setEpochTime();
        init();
        setLoginRegisterButton();

    }





    private void setLoginRegisterButton(){
        if (db.getUserDataCount() == 0)
        {
            setContentView(view1);
        }
        else
        {
            setContentView(view2);
            JSONObject jsonObject = db.fetchUserData(db.lastInsertedRowUserData());
            try{
                Button usernameButton = (Button) findViewById(R.id.button_username);
                usernameButton.setText(jsonObject.getString("userName"));
            }
            catch (Exception e)
            {
                //System.out.println("SettingUserNameFailed");
            }

        }


    }




    private void init(){
        db = new DatabaseHandler(this);

    }
    //onClick button functions
    public void function_Play(View view)
    {
        if (db.getUserDataCount()!=0){
            Intent intent = new Intent(this, Activity_Play.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please login to play", Toast.LENGTH_LONG).show();
        }

    }
    public void function_Leaderboard(View view)
    {
        Intent intent = new Intent(this, Activity_Leaderboard.class);
        startActivity(intent);
    }
    public void function_Login(View view)
    {
        Intent intent = new Intent(this, Activity_Login.class);
        startActivity(intent);
    }
    public void function_Profile(View view)
    {
        Intent intent = new Intent(this, Activity_Profile.class);
        startActivity(intent);
    }
    public void function_Register(View view)
    {
        Intent intent = new Intent(this, Activity_Register.class);
        startActivity(intent);
    }


    public void function_Logout(View view)
    {
        if (db.getUserDataCount()!=0) {
            db.deleteUserData(db.lastInsertedRowUserData());
        }
        setLoginRegisterButton();
    }

    private void setEpochTime()
    {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = f.parse(f.format(new Date()));
            epochTime = date.getTime() / 1000;
        }
        catch (Exception e){
            //alertBoxFunction("Oops!","__Something went wrong. Please start again.","Retry","Cancel");

        }
    }


}
