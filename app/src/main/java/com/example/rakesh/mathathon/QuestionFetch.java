package com.example.rakesh.mathathon;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Rakesh on 3/26/2017.
 */
public class QuestionFetch extends AppCompatActivity   {
    public static final String EXTRA_MESSAGE = "com.example.rakesh.mathathon.MESSAGE";
    JSONArray QuesBank;
    String userName="",rankListData="";
    int index=1,numOfWrongAns=0,numOfCorrectAns=0,quesSet=0,serverAnsDigits=0;
    TextView textView;
    TextView textViewAnswer,textViewQuesCount,textViewUserName,textViewCountDownTime;
    long startTime,endTime;
    long countdownStartTime,countdownEndTime,countdownBoutEndTime;

    Button ansB0,ansB1,ansB2,ansB3,ansB4,ansB5,ansB6,ansB7,ansB8,ansB9;

    CountDownTimer myCountDownTimer;
    public void setButtonFunction(boolean flag)
    {
        ansB0.setEnabled(flag);
        ansB1.setEnabled(flag);
        ansB2.setEnabled(flag);
        ansB3.setEnabled(flag);
        ansB4.setEnabled(flag);
        ansB5.setEnabled(flag);
        ansB6.setEnabled(flag);
        ansB7.setEnabled(flag);
        ansB8.setEnabled(flag);
        ansB9.setEnabled(flag);

    }
    @Override
    public void onBackPressed() {
        myCountDownTimer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_fetch);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        userName=message;
        // Capture the layout's TextView and set the string as its text
        textView = (TextView) findViewById(R.id.textView);
        textViewAnswer=(TextView) findViewById(R.id.textViewAnswer);
        textViewQuesCount=(TextView) findViewById(R.id.textViewQuesCount);
        textViewUserName=(TextView) findViewById(R.id.textViewUserName);
        textViewCountDownTime=(TextView) findViewById(R.id.textViewCountDownTime);

        ansB0=(Button) findViewById(R.id.answerButton0);
        ansB1=(Button) findViewById(R.id.answerButton1);
        ansB2=(Button) findViewById(R.id.answerButton2);
        ansB3=(Button) findViewById(R.id.answerButton3);
        ansB4=(Button) findViewById(R.id.answerButton4);
        ansB5=(Button) findViewById(R.id.answerButton5);
        ansB6=(Button) findViewById(R.id.answerButton6);
        ansB7=(Button) findViewById(R.id.answerButton7);
        ansB8=(Button) findViewById(R.id.answerButton8);
        ansB9=(Button) findViewById(R.id.answerButton9);
        setButtonFunction(true);

        textViewUserName.setText(userName);
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    long epochTime= System.currentTimeMillis()/1000;
                    jsonObject.accumulate("clientTimeStamp", epochTime);
                    System.out.println("EpochTIme"+epochTime);
//                    "http://172.16.4.101:8000/genQues/getQuest/"
                    content=JSONSender.POSTCALL(getString(R.string.server_path)+getString(R.string.fetch_ques_path), jsonObject);
                }
                catch (Exception e1){
                    Log.e("FetchQuesError: ", e1.toString()); }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    int n1,n2;
                    String op;
                    QuesBank = new JSONArray(result);
                    JSONObject row0=QuesBank.getJSONObject(0);
                    quesSet= row0.getInt("QuesSet");
                    countdownEndTime=row0.getInt("EndTime");
                    countdownStartTime=row0.getInt("StartTime");
                    countdownBoutEndTime=row0.getInt("BoutEndTime");

                    JSONObject row=QuesBank.getJSONObject(index);
                    op = row.getString("operator");
                    n1 = row.getInt("firstNumber");
                    n2 = row.getInt("secondNumber");
                    serverAnsDigits=row.getString("answer").length();
                    startTime=System.currentTimeMillis()/10;

                    if(op.equalsIgnoreCase("~"))
                        textView.setText(row.getString("answer"));
                    else
                        textView.setText("Ques:"+n1+" "+op+ " "+n2);
                    textViewQuesCount.setText((index-1)+"/"+(QuesBank.length()-1));


                    System.out.println("S "+countdownStartTime+" e "+countdownStartTime);
                    System.out.println("Cooldown time:"+ (countdownEndTime-countdownStartTime));
                    long epochTime= System.currentTimeMillis()/1000;
                    if ((countdownEndTime-epochTime)<0)
                    {
                        setButtonFunction(false);
                        submitUserScore();
                    }
                    myCountDownTimer=new CountDownTimer((countdownEndTime-epochTime)*1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            textViewCountDownTime.setText("Time left: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {

//                            textViewCountDownTime.setText("done!");
                            myCountDownTimer.cancel();
                            setButtonFunction(false);
                            submitUserScore();
                        }
                    }.start();

                    } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + t + "\"");
                }
            }
        }.execute();

    }
    public void answerCheck(View view,String data)
    {
        System.out.println("DataLength"+data.length()+" Data:"+data);
        if(data.length()==serverAnsDigits)
        {
            submitFunction(view);
        }
    }
    public void button0Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"0");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);
    }
    public void button1Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"1");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button2Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"2");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }

    public void button3Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"3");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button4Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"4");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button5Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"5");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button6Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"6");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button7Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"7");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void button8Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"8");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);;

    }
    public void button9Function(View view){
        String data=textViewAnswer.getText().toString();
        textViewAnswer.setText(data+"9");
        data=textViewAnswer.getText().toString();
        answerCheck(view,data);

    }
    public void deleteFunction(View view){
        String data=textViewAnswer.getText().toString();
        if(data.length()>0)
            textViewAnswer.setText(data.substring(0,data.length()-1));

    }

    public void fetchUserRank()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("questionID", quesSet);
            jsonObject.accumulate("userName", userName);
            jsonObject.accumulate("StartTime", countdownStartTime);
            jsonObject.accumulate("EndTime", countdownEndTime);
            jsonObject.accumulate("BoutEndTime", countdownBoutEndTime);
            Intent intent = new Intent(this, RankList.class);
            String message = "{\"}";
            intent.putExtra("JsonData", jsonObject.toString());
            startActivity(intent);
        }
        catch (Exception e)
        {
            System.out.println("RankListActivityIntentError:"+e);
        }
    }
    public void submitUserScore()
    {

        endTime=System.currentTimeMillis()/10;
        double tt=(numOfWrongAns*0.05)+(endTime-countdownStartTime);
        String t=Long.toString((long)tt);
        long div=1;
        for (int i=1;i<=t.length();i++)
        {
            div=div*10;
        }
        double score;
        if (numOfCorrectAns>0)
            score=numOfCorrectAns+(1-(tt/div));
        else
            score=0;
        final double totalScore=score;
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userScore", totalScore);
                    jsonObject.accumulate("questionID", quesSet);
                    jsonObject.accumulate("userName", userName);
                    content=JSONSender.POSTCALL(getString(R.string.server_path)+getString(R.string.submit_score_path), jsonObject);
                }
                catch (Exception e1){
                    Log.e("SubmitScoreError: ", e1.toString()); }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    long epochTime= System.currentTimeMillis()/1000;
                    if ((countdownBoutEndTime-15-epochTime)<0)
                    {
                        fetchUserRank();
                    }
                    myCountDownTimer=new CountDownTimer(((countdownBoutEndTime-15-epochTime))*1000, 1000) {
                       public void onTick(long millisUntilFinished) {
                            textViewCountDownTime.setText("Rank fetch in: " + millisUntilFinished / 1000);
                        }
                        public void onFinish() {

//                            textViewCountDownTime.setText("done!");
                            setButtonFunction(false);
                            myCountDownTimer.cancel();
                            fetchUserRank();
                        }
                    }.start();

                } catch (Throwable t) {
                    Log.e("Submit Ranking Error", "Could not parse malformed JSON: \"" + result + "\"");
                }
            }
        }.execute();

    }

    public int matchAnswer()
    {
        try {
            String ans = textViewAnswer.getText().toString();
            JSONObject row=QuesBank.getJSONObject(index);
            String serverAns=row.getString("answer");
            System.out.println("Matching:"+ans+" "+serverAns);
            if(ans.equalsIgnoreCase(serverAns))
            {
                numOfCorrectAns+=1;
                return 1;
            }
            else
            {
                return 0;
            }
        }
        catch (Exception e)
        {
            System.out.println("Answer matching error");
        }
        return 0;
    }
    public void fetchNextQues()
    {
        index+=1;
        textViewAnswer.setText("");
        try{
            int n1,n2,n3;
            String op;
            JSONObject row=QuesBank.getJSONObject(index);
            op = row.getString("operator");
            n1 = row.getInt("firstNumber");
            n2 = row.getInt("secondNumber");
            serverAnsDigits=row.getString("answer").length();
            textView.setText("Ques:"+n1+" "+op+ " "+n2);
        }
        catch (Exception er)
        {
            System.out.println("SubmitFunctionError: " + er );
        }
    }
    public void submitFunction(View view){
        System.out.println("Index:"+index+" QuesLength:"+QuesBank.length());
        if (matchAnswer()==1)
        {
            textView.setTextColor(Color.parseColor("#000000"));
            fetchNextQues();
            textViewQuesCount.setText((index-1)+"/"+(QuesBank.length()-1));
        }
        else
        {

            numOfWrongAns+=1;
            textView.setTextColor(Color.parseColor("#ff0000"));
            textViewAnswer.setText("");
        }


        if(index==QuesBank.length())
        {
            myCountDownTimer.cancel();
            setButtonFunction(false);
            submitUserScore();

        }

    }








}
