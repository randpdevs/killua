package com.example.rakesh.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Rakesh on 6/12/2018.
 */


public class Activity_Leaderboard extends AppCompatActivity {

    private JSONArray rankListArray;
    private boolean onActivityStart=false;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Activity_Home_Page.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
    }
    private void startFunction(){
        init();
        fetchData();
    }

    private void init(){
        rankListArray=null;
    }

    private void fetchData(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("","" );
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.leader_board_path), jsonObject);
                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");

                }
                return content;
            }
            protected void onPostExecute(String result) {

                try {

                    rankListArray = new JSONArray(result);
                    for (int i = 0; i < rankListArray.length(); i++) {
                        JSONObject row = rankListArray.getJSONObject(i);
                        final TableLayout detailsTable = (TableLayout) findViewById(R.id.leaderTableLayout);
                        final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.leader_row_item, null);
                        TextView tv;
                        final FrameLayout fl = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);
                        final FrameLayout f2 = (FrameLayout) getLayoutInflater().inflate(R.layout.divider_layout, null);

                        //Filling in cells
                        tv = (TextView) tableRow.findViewById(R.id.leaderRank);
                        tv.setText(row.getString("Rank").toString());

                        tv = (TextView) tableRow.findViewById(R.id.leaderUserName);
                        tv.setText(row.getString("userName").toString());


                        tv = (TextView) tableRow.findViewById(R.id.maxscore);
                        tv.setText(row.getString("Score").toString());
                        detailsTable.addView(fl);
                        detailsTable.addView(tableRow);

                        detailsTable.addView(f2);

                    }
                    setExtraAtTheBotton();

                }
                catch (Throwable t) {
                    if(result.length()==0)
                        alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");
                    else
                        alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel");
                }
            }
        }.execute();
    }
    private void setExtraAtTheBotton(){
        for (int i=1;i<=5;i++)
        {
            final TableLayout detailsTable = (TableLayout) findViewById(R.id.leaderTableLayout);
            final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.leader_row_item, null);
            TextView tv;
            tv = (TextView) tableRow.findViewById(R.id.leaderRank);
            tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.leaderUserName);
            tv.setText("");
            tv = (TextView) tableRow.findViewById(R.id.maxscore);
            tv.setText("");
            detailsTable.addView(tableRow);
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