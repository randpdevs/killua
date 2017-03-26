package com.example.rakesh.mathathon;
import android.content.Intent;
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
public class QuestionFetch extends AppCompatActivity implements OnClickListener  {
    JSONArray QuesBank;
    String userName="";
    int index=1,numOfWrongAns=0,quesSet=0,serverAnsDigits=0;
    TextView textView;
    TextView textViewAnswer,textViewQuesCount,textViewUserName,textViewCountDownTime;
    long startTime,endTime;
    long countdownStartTime,countdownEndTime;

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


        textViewUserName.setText(userName);
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    long epochTime= System.currentTimeMillis()/1000;
                    jsonObject.accumulate("clientTimeStamp", epochTime);
                    System.out.println("EpochTIme"+epochTime);
                    content=JSONSender.POSTCALL("http://172.16.4.101:8000/genQues/getQuest/", jsonObject);
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
                    new CountDownTimer((countdownEndTime-countdownStartTime)*1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            textViewCountDownTime.setText("Time left: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            textViewCountDownTime.setText("done!");
                        }
                    }.start();

                    } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
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


    public int matchAnswer()
    {
        try {
            String ans = textViewAnswer.getText().toString();
            JSONObject row=QuesBank.getJSONObject(index);
            String serverAns=row.getString("answer");
            System.out.println("Matching:"+ans+" "+serverAns);
            if(ans.equalsIgnoreCase(serverAns))
            {
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
        if(index==QuesBank.length())
        {
            endTime=System.currentTimeMillis()/10;
            double tt=(numOfWrongAns*0.05)+(endTime-countdownStartTime);
            String t=Long.toString((long)tt);
            long div=1;
            for (int i=1;i<=t.length();i++)
            {
                div=div*10;
            }
            final double totalScore=(QuesBank.length()-1)+(tt/div);
            new AsyncTask<Integer, String, String>(){
                String content;
                protected String doInBackground(Integer... params) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("userScore", totalScore);
                        jsonObject.accumulate("questionID", quesSet);
                        jsonObject.accumulate("userName", userName);
                        content=JSONSender.POSTCALL("http://172.16.4.101:8000/genQues/submitRanking/", jsonObject);
                    }
                    catch (Exception e1){
                        Log.e("SubmitScoreError: ", e1.toString()); }
                    return content;
                }
                protected void onPostExecute(String result) {
                    try {
                        int n1,n2;
                        String op;
                        QuesBank = new JSONArray(result);
//                        JSONObject row0=QuesBank.getJSONObject(0);
//                        quesSet= row0.getInt("QuesSet");
//                        countdownEndTime=row0.getInt("EndTime");
//                        countdownStartTime=row0.getInt("StartTime");
//

                    } catch (Throwable t) {
                        Log.e("Submit Ranking Error", "Could not parse malformed JSON: \"" + result + "\"");
                    }
                }
            }.execute();


        }
        else if (matchAnswer()==1)
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

    }


    public void onClick(View view) {
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("firstNumber", 1);
                    jsonObject.accumulate("secondNumber", 5);
                    content=JSONSender.POSTCALL("http://172.16.4.101:8000/genQues/genQuest1/", jsonObject);
                }
                catch (Exception e1)
                {
                    Log.e("FetchQuesError: ", e1.toString());
                }
                return content;
            }
            @Override
            protected void onPostExecute(String result) {
                try {
                    int n1,n2,n3;
                    String op;
                    QuesBank = new JSONArray(result);
                    JSONObject row=QuesBank.getJSONObject(0);
                    op = row.getString("operator");
                    n1 = row.getInt("firstNumber");
                    n2 = row.getInt("secondNumber");
                    n3 = row.getInt("answer");
                    textView.setText("Ques:"+n1+" "+op+ " "+n2);

//                    for (int i = 0; i < QuesBank.length(); i++) {
//                        JSONObject row = QuesBank.getJSONObject(i);
//                        op = row.getString("operator");
//                        n1 = row.getInt("firstNumber");
//                        n2 = row.getInt("secondNumber");
//                        n3 = row.getInt("answer");
//                        System.out.println(n1+" "+op+ " "+n2+" "+n3);
//                    }
                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                }
            }
        }.execute();
    }




}
