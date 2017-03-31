package com.example.rakesh.mathathon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rakesh on 3/29/2017.
 */
public class RankList extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.rakesh.mathathon.MESSAGE";

    TextView textView;
    JSONArray rankListArray;
    long countdownStartTime,countdownEndTime,countdownBoutEndTime;
    ArrayAdapter<String> listAdapter ;

    ListView mainListView;
    String userName="";
    CountDownTimer myCountDownTimer;
    @Override
    public void onBackPressed() {
        myCountDownTimer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_list);

        mainListView = (ListView) findViewById( R.id.listView );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerowlistview, new ArrayList<String>());


        Intent intent = getIntent();
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
//                        "http://172.16.4.101:8000/genQues/getRanking/"
                        content=JSONSender.POSTCALL(getString(R.string.server_path)+getString(R.string.rank_list_path), jsonObject);
                    }
                    catch (Exception e1){
                        Log.e("SubmitScoreError: ", e1.toString()); }
                    return content;
                }
                protected void onPostExecute(String result) {
                    try {

                        rankListArray = new JSONArray(result);
//                        [{"userName":"","Rank":1}
                        listAdapter.add("Rank\t\tUserName\t\tScore");
                        for (int i = 0; i < rankListArray.length(); i++) {
                            JSONObject row = rankListArray.getJSONObject(i);
//                            System.out.println(row.getString("userName").toString()+" "+row.getString("Rank").toString());
                            listAdapter.add(row.getString("Rank").toString()+"\t\t"+row.getString("userName").toString()+"\t\t"+row.getString("userScore").toString());
                        }
                        mainListView.setAdapter( listAdapter );


                    } catch (Throwable t) {
                        Log.e("Fetch ranking:", "Could not parse malformed JSON: \"" + t + "\"");
                    }
                }
            }.execute();

        }
        catch (Exception e)
        {
            System.out.println("ReceiveIntentJsonObjError:"+e);
        }
        long epochTime= System.currentTimeMillis()/1000;
        myCountDownTimer=new CountDownTimer((countdownBoutEndTime-epochTime)*1000, 1000) {
            public void onTick(long millisUntilFinished) {
//                textViewCountDownTime.setText("Rank fetch in: " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                myCountDownTimer.cancel();
                startAnotherActivity();
            }
        }.start();





    }
    public void startAnotherActivity(){
        Intent intent = new Intent(this, QuestionFetch.class);
        intent.putExtra(EXTRA_MESSAGE, userName);
        startActivity(intent);

    }
}
