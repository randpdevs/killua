package com.randpdevs.calcoff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    private TextView TextViewGameStart;
    private  Long epochTime;

    public static final String EXTRA_MESSAGE = "com.example.rakesh.mathathon.MESSAGE";

    private TextView textView;
    private JSONArray rankListArray;
    private long countdownStartTime,countdownEndTime,countdownBoutEndTime;
    private String userName="";
    private CountDownTimer myCountDownTimer=null;
    private Intent intentObj;
    private boolean onPauseFlag=false;
    @Override
    public void onBackPressed() {
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onPauseFlag)
        {
            setIntentFunction("MainActivity");
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseFlag=true;
        if(gameCountDownTimer!=null)
            gameCountDownTimer.cancel();
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_list_layout);
        TextViewGameStart=(TextView) findViewById(R.id.gameStartTextView);


        try{
            final JSONObject jsonData = new JSONObject(getIntent().getStringExtra("JsonData"));
            countdownStartTime=Long.parseLong(jsonData.get("StartTime").toString());
            countdownEndTime=Long.parseLong(jsonData.get("EndTime").toString());
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
                        alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel","RankListActivity","MainActivity");

                    }
                    return content;
                }
                protected void onPostExecute(String result) {
                    try {

                        rankListArray = new JSONArray(result);
                        for (int i = 0; i < rankListArray.length(); i++) {
                            JSONObject row = rankListArray.getJSONObject(i);
                            final TableLayout detailsTable = (TableLayout) findViewById(R.id.rankTableLayout);
                            final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row_item, null);
                            TextView tv;
                            final FrameLayout fl = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);
                            final FrameLayout f2 = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);

                            //Filling in cells
                            tv = (TextView) tableRow.findViewById(R.id.rank);
                            tv.setText(row.getString("Rank").toString());

                            tv = (TextView) tableRow.findViewById(R.id.userName);
                            tv.setText(row.getString("userName").toString());

                            tv = (TextView) tableRow.findViewById(R.id.score);
                            tv.setText(row.getString("userScore").toString());

                            tv = (TextView) tableRow.findViewById(R.id.correctAns);
                            tv.setText(row.getString("correctans").toString());

                            tv = (TextView) tableRow.findViewById(R.id.wrongAns);
                            tv.setText(row.getString("wrongans").toString());

                            //Add row to the table

                            detailsTable.addView(fl);
                            detailsTable.addView(tableRow);

                            detailsTable.addView(f2);

                        }

                    } catch (Throwable t) {
                        alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel","RankListActivity","MainActivity");

                    }
                }
            }.execute();

        }
        catch (Exception e)
        {
            alertBoxFunction("Oops!",":Something went wrong. Please start again.","Retry","Cancel","RankListActivity","MainActivity");

        }

        setEpochTime();
        myCountDownTimer=new CountDownTimer((countdownBoutEndTime-epochTime)*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                TextViewGameStart.setText(":" + millisUntilFinished / 1000);
            }
            public void onFinish() {

                startAnotherActivity();
            }
        }.start();





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

    private void setIntentFunction(String className)
    {
        if (className=="MainActivity")
            intentObj=new Intent(this, MainActivity.class);
        else if (className=="GamePlayActivity")
            intentObj=new Intent(this, GamePlayActivity.class);
        else if (className=="RankListActivity")
            intentObj=new Intent(this, RankListActivity.class);
        if(gameCountDownTimer!=null)
            gameCountDownTimer.cancel();
        if(myCountDownTimer!=null)
            myCountDownTimer.cancel();
        startActivity(intentObj);
    }

    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton, final String YES, final String NO) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(Message)
//                .setTitle(Title);
        alertDialogBuilder.setMessage((Html.fromHtml("<font color='#FFFFFF'>" + Message + "</font>")));
        alertDialogBuilder.setTitle((Html.fromHtml("<font color='#FFFFFF'>" + Title + "</font>")));
        alertDialogBuilder.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setIntentFunction(YES);

            }
        });
        alertDialogBuilder.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setIntentFunction(NO);
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);

        }
    }
