package com.randpdevs.calcoff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rakesh on 4/1/2017.
 */
public class RankListActivity extends AppCompatActivity {
    private CountDownTimer gameCountDownTimer=null;
    private TextView TextViewGameStart,rankTextView;
    private  Long epochTime;

    private DatabaseHandler  db;
    private JSONArray rankListArray;
    private long countdownBoutEndTime;
    private String userName="";
    private CountDownTimer myCountDownTimer=null;
    private Intent intentObj;
    private boolean onPauseFlag=false;
    TableLayout detailsTable;

    private boolean onActivityStart=false;
    @Override
    public void onBackPressed() {
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();
        setGameCountsToNull();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart:RankList", Toast.LENGTH_LONG).show();
        super.onStart();
        if(!onActivityStart) {
            startFunction();
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
        onStop();

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
    protected void onStop() {
//        Toast.makeText(this, "onStop:RankList", Toast.LENGTH_LONG).show();
        super.onStop();
        setGameCountsToNull();

    }

    private void startFunction(){
        init();

        SetUserName();
        setEpochTime();
        fetchRankFromServer();

    }




    private void init(){
        detailsTable = (TableLayout) findViewById(R.id.rankTableLayout);

        for(int i=3;i<detailsTable.getChildCount();i++)
        {
            detailsTable.removeViewAt(i);
        }

        TextViewGameStart=(TextView) findViewById(R.id.gameStartTextViewRankList);
        rankTextView=(TextView) findViewById(R.id.rankTextViewRankList);
        TextViewGameStart.setText("");
        rankTextView.setText("");

        db = new DatabaseHandler(this);
        gameCountDownTimer=null;
        epochTime=null;
        rankListArray=null;
        countdownBoutEndTime=0;
        userName="";
        myCountDownTimer=null;
        intentObj=null;
        onPauseFlag=false;


    }
//    private void setTableLayout(){
//        TableRow tableRow0 = (TableRow) getLayoutInflater().inflate(R.layout.ranklist_top_part, null);
//        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.ranklist_2nd_top_part, null);
//        FrameLayout f2 = (FrameLayout) getLayoutInflater().inflate(R.layout.rank_list_layout, null);
//
//        detailsTable.addView(tableRow0);
//        detailsTable.addView(tableRow);
//        detailsTable.addView(f2);
//    }
    private void setGameCountsToNull(){
        if(gameCountDownTimer!=null)
            gameCountDownTimer.cancel();
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();
        gameCountDownTimer=null;myCountDownTimer=null;
    }

    private void SetUserName(){
        JSONObject jsonObject=db.fetchUserData(db.lastInsertedRowUserData());
        try{
            userName=jsonObject.getString("userName");
        }
        catch (Exception e)
        {System.out.println("SettingUserNameFailed");}

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_list_layout);

    }

    private void fetchRankFromServer(){
        try{
            final JSONObject jsonData = new JSONObject(getIntent().getStringExtra("JsonData"));
//            countdownStartTime=Long.parseLong(jsonData.get("StartTime").toString());
//            countdownEndTime=Long.parseLong(jsonData.get("EndTime").toString());
            countdownBoutEndTime=Long.parseLong(jsonData.get("BoutEndTime").toString());
            userName=jsonData.get("userName").toString();

            new AsyncTask<Integer, String, String>(){
                String content;
                protected String doInBackground(Integer... params) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("questionID", jsonData.get("questionID"));
                        jsonObject.accumulate("userName", jsonData.get("userName"));
                        content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.rank_list_path), jsonObject);
                    }
                    catch (Exception e1){
                        alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                    }
                    return content;
                }
                protected void onPostExecute(String result) {
                    if(result.length()!=0)
                    {
                        onFetchRankListFromServerSuccess(result);
                    }
                    else
                        alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }
            }.execute();

        }
        catch (Exception e)
        {
            alertBoxFunction("Oops!",":Something went wrong. Please start again.","Retry","Cancel");

        }


    }

    private void onFetchRankListFromServerSuccess(String result) {
        try {

            rankListArray = new JSONArray(result);
            JSONObject totalRanks = rankListArray.getJSONObject(0);
            for (int i = 1; i < rankListArray.length(); i++) {
                JSONObject row = rankListArray.getJSONObject(i);

                final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_item, null);
                TextView tv;
                final FrameLayout fl = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);
                final FrameLayout f2 = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);

                tv = (TextView) tableRow.findViewById(R.id.rank);
                tv.setText(row.getString("Rank").toString());
                if(userName.equalsIgnoreCase(row.getString("userName").toString()))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Ranks") && row.getString("userScore").toString().equals("near") && row.getString("correctans").toString().equals("you"))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Friend's") && row.getString("userScore").toString().equals("Rank"))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }


                tv = (TextView) tableRow.findViewById(R.id.userName);
                tv.setText(row.getString("userName").toString());
                if(userName.equalsIgnoreCase(row.getString("userName").toString()))
                {
                    rankTextView.setText(row.getString("Rank").toString()+"/"+totalRanks.getString("TotalRank").toString());
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Ranks") && row.getString("userScore").toString().equals("near") && row.getString("correctans").toString().equals("you"))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Friend's") && row.getString("userScore").toString().equals("Rank"))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }



                tv = (TextView) tableRow.findViewById(R.id.score);
                tv.setText(row.getString("userScore").toString());
                if(userName.equalsIgnoreCase(row.getString("userName").toString()))
                {

                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Ranks") && row.getString("userScore").toString().equals("near") && row.getString("correctans").toString().equals("you"))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Friend's") && row.getString("userScore").toString().equals("Rank"))
                {tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }

                tv = (TextView) tableRow.findViewById(R.id.correctAns);
                tv.setText(row.getString("correctans").toString());
                if(userName.equalsIgnoreCase(row.getString("userName").toString()))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Ranks") && row.getString("userScore").toString().equals("near") && row.getString("correctans").toString().equals("you"))
                {tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Friend's") && row.getString("userScore").toString().equals("Rank"))
                {tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }

                tv = (TextView) tableRow.findViewById(R.id.wrongAns);
                tv.setText(row.getString("wrongans").toString());
                if(userName.equalsIgnoreCase(row.getString("userName").toString()))
                {
                    tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Ranks") && row.getString("userScore").toString().equals("near") && row.getString("correctans").toString().equals("you"))
                {tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }
                if(row.getString("userName").toString().equals("Friend's") && row.getString("userScore").toString().equals("Rank"))
                {tv.setTextColor(Color.parseColor("#000000"));
                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                }

                detailsTable.addView(fl);
                detailsTable.addView(tableRow);
                detailsTable.addView(f2);

            }
            AddExtraAtTheBottom();
        } catch (Throwable t) {
            alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel");

        }

        startNewGameCountDown();
    }

    private void startNewGameCountDown(){
        myCountDownTimer=new CountDownTimer((countdownBoutEndTime-epochTime)*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                TextViewGameStart.setText(":" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                TextViewGameStart.setText(": 0");
                startAnotherActivity();
            }
        }.start();
    }


    private void AddExtraAtTheBottom()
    {
        for (int i = 0; i <=5; i++) {
            final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_item, null);
            TextView tv;
            tv = (TextView) tableRow.findViewById(R.id.rank);tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.userName); tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.score); tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.correctAns);tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.wrongAns);tv.setText("");
            detailsTable.addView(tableRow);
        }
    }
    public void startAnotherActivity(){
        Intent intent = new Intent(this, GamePlayActivity.class);
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();
        startActivity(intent);
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
                System.out.println("EPOCH TIME FAILED");
        }
    }



    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((Html.fromHtml("<font color='#FFFFFF'>" + Message + "</font>")));
        alertDialogBuilder.setTitle((Html.fromHtml("<font color='#FFFFFF'>" + Title + "</font>")));
        alertDialogBuilder.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startFunction();
            }
        });
        alertDialogBuilder.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onBackPressed();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);

        }
    }
