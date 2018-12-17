package com.example.rakesh.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Rakesh on 6/12/2018.
 */


public class Activity_Play extends AppCompatActivity {
    private CountDownTimer gameCountDownTimer=null,rankCountDownTimer=null,progressBarCountDownTimer=null;
    private TextView TextViewCountDown,TextViewUserName,TextViewQuestionCount,TextViewQuestion,TextViewAnswer;
    private Button b0,b1,b2,b3,b4,b5,b6,b7,b8,b9,bc,bb;
    private ProgressBar progressBar;
    private JSONArray QuesBank;
    private Intent intentObj;
    private int index=0,numOfWrongAns=0,numOfCorrectAns=0,quesSet=0,serverAnsDigits=0;
    private String userName="";
    private long startTime,endTime,countdownStartTime,countdownEndTime,countdownBoutEndTime,epochTime;
    private boolean activityBackFlag=true,onPauseFlag=false;
    private  double totalUserScore;
    private DatabaseHandler db;
    private boolean onActivityStart=false;
    @Override
    public void onBackPressed() {
        if (activityBackFlag)
        {
            Intent intent = new Intent(this, Activity_Home_Page.class);
            startActivity(intent);
        }
        else
            return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }


    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart:GamePlay", Toast.LENGTH_LONG).show();
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
    protected void onStop() {
//        Toast.makeText(this, "onStop:GamePlay", Toast.LENGTH_LONG).show();
        super.onStop();
        setGameCountsToNull();
    }

    private void startFunction(){
        setEpochTime();

        init();
        SetUserName();
        setEpochTime();
        fetchQuesFromServer();
    }


    private void fetchQuesFromServer(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("password", "randpdevwork");
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.fetch_ques), jsonObject);
                }
                catch (Exception e1)
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                if(result.length()!=0)
                {
                    SuccesOnServerFetchQues(result);
                }
                else
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }
            }
        }.execute();

    }
    private void SuccesOnServerFetchQues(String result)
    {
        try {
            QuesBank = new JSONArray(result);
            JSONObject row0=QuesBank.getJSONObject(0);
            quesSet= row0.getInt("QuesSet");
            countdownEndTime=row0.getInt("EndTime");
            countdownStartTime=row0.getInt("StartTime");
            countdownBoutEndTime=row0.getInt("BoutEndTime");

            if ((countdownBoutEndTime-15-epochTime)<0)
            {
                fetchUserRank();
            }
            else if ((countdownEndTime-epochTime)<0)
            {
                setButtonFunction(false);
                countDownToRankFunction();
            }
            else
            {
                fetchNextQues();
                CountDownTimer t;

                gameCountDownTimer=new CountDownTimer((countdownEndTime-epochTime)*1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        TextViewCountDown.setText(":" + millisUntilFinished / 1000);
                    }
                    public void onFinish() {
                        TextViewCountDown.setText(":0");
                        setButtonFunction(false);
                        submitUserScore();
                    }

                };
                gameCountDownTimer.start();
            }

        }
        catch (Throwable t) {
            alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel");
        }
    }


    private void fetchUserRank()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("questionID", quesSet);
            jsonObject.accumulate("userName", userName);
            jsonObject.accumulate("StartTime", countdownStartTime);
            jsonObject.accumulate("EndTime", countdownEndTime);
            jsonObject.accumulate("BoutEndTime", countdownBoutEndTime);
            Intent intent = new Intent(this, Activity_Ranklist.class);
            intent.putExtra("JsonData", jsonObject.toString());
            setGameCountsToNull();
            startActivity(intent);
        }
        catch (Exception e)
        {
            alertBoxFunction("Oops!",":_Something went wrong. Please start again.","Retry","Cancel");
        }
    }
    private void submitUserScore()
    {
        double tt=(numOfWrongAns*0.5);
        String t=Long.toString((long)tt);
        if (numOfCorrectAns>0)
            totalUserScore=numOfCorrectAns-tt;
        else
            totalUserScore=0;
        final double totalScore=totalUserScore;

        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userScore", totalScore);
                    jsonObject.accumulate("questionID", quesSet);
                    jsonObject.accumulate("userName",userName);
                    jsonObject.accumulate("correctans",numOfCorrectAns);
                    jsonObject.accumulate("wrongans",numOfWrongAns);

                    activityBackFlag=false;
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.submit_score_path), jsonObject);

                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.length()!=0)
                        countDownToRankFunction();
                    else
                        alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");

                } catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel");
                }
            }
        }.execute();

    }
    private void countDownToRankFunction(){
        if ((countdownBoutEndTime-15-epochTime)<0)
        {
            fetchUserRank();
        }
        else
        {
            setEpochTime();
            rankCountDownTimer=new CountDownTimer(((countdownBoutEndTime-15-epochTime))*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    TextViewCountDown.setText("");
                    TextViewQuestion.setTextColor(Color.parseColor("#000000"));
                    TextViewQuestion.setTextSize(30);
                    TextViewQuestion.setText("Fetching rank in:" + millisUntilFinished / 1000);
                }
                public void onFinish() {
                    TextViewQuestion.setTextSize(30);
                    TextViewQuestion.setText("Fetching rank in:0");
                    setButtonFunction(false);
                    fetchUserRank();
                }
            }.start();
        }
    }

    private void answerCheck(View view, String data)
    {
        if(data.length()==serverAnsDigits)
        {
            submitFunction(view);
        }
    }
    private void updateAnswerTextView(View view,String btn_Num)
    {
        String data=TextViewAnswer.getText().toString();
        if(btn_Num=="0")
        {TextViewAnswer.setText(data+"0");}
        else if (btn_Num=="1")
        {TextViewAnswer.setText(data+"1");}
        else if (btn_Num=="2")
        {TextViewAnswer.setText(data+"2");}
        else if (btn_Num=="3")
        {TextViewAnswer.setText(data+"3");}
        else if (btn_Num=="4")
        {TextViewAnswer.setText(data+"4");}
        else if (btn_Num=="5")
        {TextViewAnswer.setText(data+"5");}
        else if (btn_Num=="6")
        {TextViewAnswer.setText(data+"6");}
        else if (btn_Num=="7")
        {TextViewAnswer.setText(data+"7");}
        else if (btn_Num=="8")
        {TextViewAnswer.setText(data+"8");}
        else if (btn_Num=="9")
        {TextViewAnswer.setText(data+"9");}
        data=TextViewAnswer.getText().toString();

        answerCheck(view,data);
    }


    private void fetchNextQues()
    {
        index+=1;
        TextViewAnswer.setText("");
        try{
            int n1,n2;
            String op;
            JSONObject row=QuesBank.getJSONObject(index);
            op = row.getString("operator");
            n1 = row.getInt("firstNumber");
            n2 = row.getInt("secondNumber");
            serverAnsDigits=row.getString("answer").length();
            TextViewQuestion.setText(n1+" "+op+ " "+n2);
            TextViewQuestionCount.setText((index-1)+"/"+(QuesBank.length()-1));

        }
        catch (Exception er)
        {
            alertBoxFunction("Oops!","|Something went wrong. Please start again.","Retry","Cancel");
        }
    }

    private int matchAnswer()
    {
        try {
            String ans = TextViewAnswer.getText().toString();
            JSONObject row=QuesBank.getJSONObject(index);
            String serverAns=row.getString("answer");
            if(ans.equalsIgnoreCase(serverAns))
            {
                if ((countdownEndTime-epochTime)>0)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    numOfCorrectAns+=1;
                    return 1;
                }
                else
                    return 0;

            }
            else
            {
                numOfWrongAns+=1;
                return 0;
            }
        }
        catch (Exception e)
        {
            alertBoxFunction("Oops!","Something went wrong. Please start again.","Retry","Cancel");

        }
        return 0;
    }
    private void submitFunction(View view){
        if (matchAnswer()==1)
        {
            rankCountDownTimer=new CountDownTimer(((1))*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                public void onFinish() {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }.start();
            TextViewQuestion.setTextColor(Color.parseColor("#000000"));
            fetchNextQues();
            TextViewQuestionCount.setText((index-1)+"/"+(QuesBank.length()-1));

        }
        else
        {
            TextViewQuestion.setTextColor(Color.parseColor("#ff0000"));
            TextViewAnswer.setText("");
        }

        if(index==QuesBank.length())
        {
            setButtonFunction(false);
            submitUserScore();

        }

    }









    //    Completed function
//0.Init()
//1.SetIntentFunction
//2.alertBoxFunction
//3.setEpochTime
//4.setButtonFunction
//5. ButtonFunction {0-9 ,c,b}
    private void init(){
        TextViewCountDown=(TextView) findViewById(R.id.countDownTextView);
        TextViewCountDown.setTextColor(Color.parseColor("#000000"));
        TextViewUserName=(TextView)findViewById(R.id.userNameTextView);
        TextViewUserName.setTextColor(Color.parseColor("#000000"));
        TextViewQuestionCount=(TextView) findViewById(R.id.numberOfQuesTextView);
        TextViewQuestionCount.setTextColor(Color.parseColor("#000000"));
        TextViewQuestion=(TextView)findViewById(R.id.questionTextView);
        TextViewQuestion.setTextColor(Color.parseColor("#000000"));
        TextViewAnswer=(TextView) findViewById(R.id.answerTextView);
        TextViewAnswer.setTextColor(Color.parseColor("#000000"));
        b0=(Button) findViewById(R.id.buttonNo0);
        b1=(Button) findViewById(R.id.buttonNo1);
        b2=(Button) findViewById(R.id.buttonNo2);
        b3=(Button) findViewById(R.id.buttonNo3);
        b4=(Button) findViewById(R.id.buttonNo4);
        b5=(Button) findViewById(R.id.buttonNo5);
        b6=(Button) findViewById(R.id.buttonNo6);
        b7=(Button) findViewById(R.id.buttonNo7);
        b8=(Button) findViewById(R.id.buttonNo8);
        b9=(Button) findViewById(R.id.buttonNo9);
        bc=(Button) findViewById(R.id.buttonNoC);
        bb=(Button) findViewById(R.id.buttonNoB);
        progressBar=(ProgressBar) findViewById(R.id.answerProgressBar);
        db = new DatabaseHandler(this);
        gameCountDownTimer=null;rankCountDownTimer=null;progressBarCountDownTimer=null;
        TextViewCountDown.setText("");
        TextViewUserName.setText("");
        TextViewQuestionCount.setText("");
        TextViewQuestion.setText("");
        TextViewAnswer.setText("");
        QuesBank=null;
        intentObj=null;
        index=0;numOfWrongAns=0;numOfCorrectAns=0;quesSet=0;serverAnsDigits=0;
        userName="";
        startTime=0;endTime=0;countdownStartTime=0;countdownEndTime=0;countdownBoutEndTime=0;epochTime=0;
        activityBackFlag=true;onPauseFlag=false;
        totalUserScore=0.0;
    }

    private void setGameCountsToNull(){
        if(gameCountDownTimer!=null)
            gameCountDownTimer.cancel();
        if(rankCountDownTimer!=null)
            rankCountDownTimer.cancel();
        if(progressBarCountDownTimer!=null)
            progressBarCountDownTimer.cancel();
        gameCountDownTimer=null;rankCountDownTimer=null;progressBarCountDownTimer=null;
    }

    private void SetUserName(){
        JSONObject jsonObject=db.fetchUserData(db.lastInsertedRowUserData());
        try{
            TextViewUserName.setText(jsonObject.getString("userName"));
            userName=jsonObject.getString("userName");
        }
        catch (Exception e)
        {System.out.println("SettingUserNameFailed");}
    }





    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((Html.fromHtml("<font color='#FFFFFF'>"+Message+"</font>")));
        alertDialogBuilder.setTitle((Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>")));
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
    private void setEpochTime()
    {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = f.parse(f.format(new Date()));
            epochTime = date.getTime() / 1000;
        }
        catch (Exception e){
            alertBoxFunction("Oops!","__Something went wrong. Please start again.","Retry","Cancel");

        }
    }
    private void setButtonFunction(boolean flag)
    {
        b0.setEnabled(flag);
        b1.setEnabled(flag);
        b2.setEnabled(flag);
        b3.setEnabled(flag);
        b4.setEnabled(flag);
        b5.setEnabled(flag);
        b6.setEnabled(flag);
        b7.setEnabled(flag);
        b8.setEnabled(flag);
        b9.setEnabled(flag);
        bb.setEnabled(flag);
        bc.setEnabled(flag);
    }
    public void button0Function(View view){updateAnswerTextView(view,"0");}
    public void button1Function(View view){updateAnswerTextView(view,"1");}
    public void button2Function(View view){updateAnswerTextView(view,"2");}
    public void button3Function(View view){updateAnswerTextView(view,"3");}
    public void button4Function(View view){updateAnswerTextView(view,"4");}
    public void button5Function(View view){updateAnswerTextView(view,"5");}
    public void button6Function(View view){updateAnswerTextView(view,"6");}
    public void button7Function(View view){updateAnswerTextView(view,"7");}
    public void button8Function(View view){updateAnswerTextView(view,"8");}
    public void button9Function(View view){updateAnswerTextView(view,"9");}
    public void buttonCFunction(View view){TextViewAnswer.setText("");}
    public void buttonBFunction(View view){
        String data=TextViewAnswer.getText().toString();
        if(data.length()>0)
            TextViewAnswer.setText(data.substring(0,data.length()-1));

    }
}

