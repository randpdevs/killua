package com.randpdevs.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Rakesh on 4/9/2017.
 */
public class ProfileActivity extends AppCompatActivity {
    private boolean onActivityStart=false;
    private String userName="",friendUserName="";
    private DatabaseHandler  db;
    private TextView userNumOfGamePlayed,userTotalScore,userTotalCorrectAns,userTotalWrongAns;
    private TextView userNumOfGamePlayedVal,userTotalScoreVal,userTotalCorrectAnsVal,userTotalWrongAnsVal;
    private EditText userFriendUserName;
    private Button userAddFriend;
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart:MainActivity", Toast.LENGTH_LONG).show();
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
    public void addFriendFunction(View view)
    {
        if(getValue())
        {
            addUserFriend();
        }
        else
        {
            userFriendUserName.setText("");
        }
    }


    private void startFunction(){
        init();
        setUserName();
//        setFriendData("Username","Max score");
        getUserStat();
        getUserFriends();

    }

    private void init(){
        userName="";
        friendUserName="";
        onActivityStart=false;
        db = new DatabaseHandler(this);
        ll=(LinearLayout) findViewById(R.id.UserProfileLayout);

        while(ll.getChildCount()>11)
        {   ll.removeViewAt(ll.getChildCount()-1);
        }




        userNumOfGamePlayedVal=(TextView) findViewById(R.id.TextViewNumOfGamePlayedVal);
        userTotalScoreVal=(TextView) findViewById(R.id.TextViewTotalScoreVal);
        userTotalCorrectAnsVal=(TextView) findViewById(R.id.TextViewTotalCorrectAnsVal);
        userTotalWrongAnsVal=(TextView) findViewById(R.id.TextViewTotalWrongAnsVal);
        userFriendUserName=(EditText) findViewById(R.id.editTextEnterUserName);

        userNumOfGamePlayedVal.setText("");
        userTotalScoreVal.setText("");
        userTotalCorrectAnsVal.setText("");
        userTotalWrongAnsVal.setText("");

    }


    private void setUserName(){
        JSONObject jsonObject=db.fetchUserData(db.lastInsertedRowUserData());
        try{
            userName=jsonObject.getString("userName");
        }
        catch (Exception e)
        {System.out.println("SettingUserNameFailed");}
    }

    private void getUserStat(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("UserName", userName);
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.get_user_stat), jsonObject);
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
                    setUserStat(result);
                }
                else
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }

            }
        }.execute();

    }


    private  void setUserStat(String result){
        try {
            JSONArray rankListArray = new JSONArray(result);
            JSONObject row = rankListArray.getJSONObject(0);
            userNumOfGamePlayedVal.setText(row.getString("numGamePlayed").toString());
            userTotalScoreVal.setText(row.getString("totalScore").toString());
            userTotalCorrectAnsVal.setText(row.getString("totalCorrectAns").toString());
            userTotalWrongAnsVal.setText(row.getString("totalWrongAns").toString());
        }
        catch (Throwable t) {
            alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel");
        }
    }

    private void  getUserFriends(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userName", userName);
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.get_user_friend), jsonObject);
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
                    if(result.length()==2)
                    {
                        setFriendData("No friends","");
                    }
                    else
                        setUserFriends(result);
                }

                else
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }

            }
        }.execute();

    }

    private void setFriendData(String name,String score)
    {
        LinearLayout li = (LinearLayout) getLayoutInflater().inflate(R.layout.user_friend_data, null);
        TextView tv=(TextView) li.findViewById(R.id.userFriendName);
        TextView tv1=(TextView) li.findViewById(R.id.userFriendScore);
        tv.setText(name);
        tv1.setText(score);
        ll.addView(li);

    }

//    [{"Score":37.75,"friendName":"cceenu"},{"Score":35.0,"friendName":"Raks"}]
    private void setUserFriends(String result){
        try {
            JSONArray rankListArray = new JSONArray(result);
            for (int i = 0; i < rankListArray.length(); i++) {
                JSONObject row = rankListArray.getJSONObject(i);
                setFriendData(row.getString("friendName").toString(),row.getString("Score").toString());

            }
        }
        catch (Throwable t) {
            alertBoxFunction("Oops!","_Something went wrong. Please start again.","Retry","Cancel");
        }
    }



    private boolean getValue(){
        friendUserName=userFriendUserName.getText().toString();
        if(friendUserName.length()==0)
        {
            Toast.makeText(this, "Enter your friends username", Toast.LENGTH_LONG).show();
            return false;
        }
        for (int i=0;i<friendUserName.length();i++)
        {
            if(friendUserName.charAt(i)==' ')
            {
                Toast.makeText(this, "Username should not contain any spaces!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(friendUserName.equals(userName))
        {
            Toast.makeText(this, "Sorry, you cannot add yourself!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    //    curl -X POST https://mathathon.herokuapp.com/login/addfriend/  -d '{"userName":"prakhar","friendName":"rakesh"}' -H "Content-Type: application/json"
    private void addUserFriend(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userName", userName);
                    jsonObject.accumulate("friendName", friendUserName);
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.add_friend), jsonObject);
                }
                catch (Exception e1)
                {
                    alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                System.out.println("befire");
                addFriendSuccess(result);
                System.out.println("After");
            }
        }.execute();

    }

    private void addFriendSuccess(String result)
    {
        if(result.equalsIgnoreCase("\"202\""))
        {
            userFriendUserName.setText("");
            startFunction();
        }
        else if(result.equalsIgnoreCase("\"404\""))
        {
            Toast.makeText(this, "Username does not exist!", Toast.LENGTH_LONG).show();
        }
        else if(result.equalsIgnoreCase("\"404-1\""))
        {
            Toast.makeText(this, "Your account does not exist!", Toast.LENGTH_LONG).show();
        }
        else if(result.length()==0)
        {
            alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
        }

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

}
