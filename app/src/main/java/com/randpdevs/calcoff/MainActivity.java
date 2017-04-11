package com.randpdevs.calcoff;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler  db;
    private Button loginButton,registerButton,logoutButton,playButton,leaderBoardButton,profileButton;
    private TextView userNameTextView;
    private long epochTime;
    private boolean onActivityStart=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onBackPressed() {
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

    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart:MainActivity", Toast.LENGTH_LONG).show();
        super.onStart();

        if(!onActivityStart) {
            StartFunction();
            onActivityStart=true;
        }
        else
        {
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
//        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show();
        super.onPause();
    }

    @Override
    protected void onPostResume() {
//        Toast.makeText(this, "onPostResume", Toast.LENGTH_LONG).show();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
//        Toast.makeText(this, "onRestart", Toast.LENGTH_LONG).show();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
//        Toast.makeText(this, "onStop:MainActivity", Toast.LENGTH_LONG).show();
        super.onStop();
    }





    public void playButtonFunction(View view)
    {
        if (db.getUserDataCount()!=0){
            Intent intent = new Intent(this, GamePlayActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please login to play", Toast.LENGTH_LONG).show();
        }
    }
    public void LeaderBoardButtonFunction(View view)
    {
        Intent intent = new Intent(this, LeaderBoardActivity.class);
        startActivity(intent);
    }
    public  void LoginButtonFunction(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void registerButtonFunction(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void logoutButtonFunction(View view){
        if (db.getUserDataCount()!=0) {
            db.deleteUserData(db.lastInsertedRowUserData());
            userNameTextView.setText("");
        }
        setLoginRegisterButton();
    }
    public void ProfileFunction(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }



    private void StartFunction(){
        init();
        setButtonFunction(false);
        checkTimeDiff();
        setLoginRegisterButton();
    }
    void init(){
        playButton=(Button) findViewById(R.id.ButtonNamePlay);
        loginButton=(Button) findViewById(R.id.ButtonNameLogin);
        registerButton=(Button) findViewById(R.id.ButtonNameRegister);
        logoutButton=(Button) findViewById(R.id.ButtonNameLogout);
        leaderBoardButton=(Button) findViewById(R.id.ButtonNameLeaderboard);
        profileButton=(Button) findViewById(R.id.ButtonNameProfile);
        userNameTextView=(TextView) findViewById(R.id.userNameDisplayTextView);
        db = new DatabaseHandler(this);
        epochTime=0;
    }

    private void setLoginRegisterButton(){
        if (db.getUserDataCount()==0)
        {
            //Show login and register button
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);

            userNameTextView.setVisibility(View.INVISIBLE);
            profileButton.setVisibility(View.INVISIBLE);
            //dissable play button, and ask for login
        }
        else
        {
            JSONObject jsonObject=db.fetchUserData(db.lastInsertedRowUserData());
            try{
                userNameTextView.setText(jsonObject.getString("userName"));
            }
            catch (Exception e)
            {System.out.println("SettingUserNameFailed");        }
            loginButton.setVisibility(View.INVISIBLE);
            registerButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
            profileButton.setText(userNameTextView.getText().toString());
            profileButton.setVisibility(View.VISIBLE);
        }
    }

    private void checkTimeDiff()
    {
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("", "");
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.fetch_server_time), jsonObject);
                }
                catch (Exception e1)
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel","YES","MainActivity");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    setEpochTime();
                    long diff = Math.abs(epochTime - Long.parseLong(result));
                    if (diff >= 6) {
                        alertBoxFunction("Date time error!", "Please check if your date and time is correct.", "Retry", "Cancel", "-", "MainActivity");
                    } else {
                        setButtonFunction(true);
                    }
                }
                catch (Exception e1)
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel","YES","MainActivity");
                }
            }
        }.execute();

    }
























    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton, final String YES, final String NO){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((Html.fromHtml("<font color='#FFFFFF'>"+Message+"</font>")));
        alertDialogBuilder.setTitle((Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>")));
        if (YES.equalsIgnoreCase("YES"))
        {
            alertDialogBuilder.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    StartFunction();
                }
            });
        }

        alertDialogBuilder.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
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
            alertBoxFunction("Oops!","__Something went wrong. Please start again.","Retry","Cancel","GamePlayActivity","MainActivity");
        }
    }
    private void setButtonFunction(boolean flag)
    {
        playButton.setEnabled(flag);
        loginButton.setEnabled(flag);
        registerButton.setEnabled(flag);
        logoutButton.setEnabled(flag);
        leaderBoardButton.setEnabled(flag);
        profileButton.setEnabled(flag);
    }

}