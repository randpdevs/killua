package com.randpdevs.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by Rakesh on 4/2/2017.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText userNameEditText,passwordEditText;
    private TextView userNameTextView,passwordTextView;
    private Intent intentObj;
    private String userName="",password="";


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }

    public void loginButtonFunction(View view){
        getValues();
        loginUser();
    }

    private void loginUser()
    {
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.accumulate("userName", userName);
                    jsonObject.accumulate("password", password);

                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.user_login), jsonObject);

                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel","LoginActivity","MainActivity");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("404"))
                    {
                        alertBoxFunction("No user found!","Please try again.","Retry","Cancel","LoginActivity","MainActivity");

                    }
                    else if (result.toString().equalsIgnoreCase("400"))
                    {
                        alertBoxFunction("Wrong password","Please try again.","Retry","Cancel","LoginActivity","MainActivity");
                    }
                    else
                    {
                        addUserIntoDb(result);
//                        SetUpDB and redirect to main page
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel","LoginActivity","MainActivity");
                }
            }
        }.execute();



    }


    private void addUserIntoDb(String result)
    {
        try{
            JSONObject row=new JSONObject(result);
            DatabaseHandler  db=new DatabaseHandler(this);
            String userName=row.getString("UserName").toString();
            db.addUserData(userName,row.getString("EmailID").toString(),row.getString("Age").toString(),row.getString("Country").toString());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        catch (Exception e){
            System.out.println("DB_ENTRY_FAILED"+e);
        }
    }
    private void getValues()
    {
        userName=userNameEditText.getText().toString();
        password=passwordEditText.getText().toString();

        if (userName.length()==0)
        {
            Toast.makeText(this, "UserName required!", Toast.LENGTH_LONG).show();
            userNameTextView.setTextColor(Color.RED);
        }
        else
        {
            userNameTextView.setTextColor(Color.BLACK);
        }
        if (password.length()==0)
        {
            Toast.makeText(this, "Password required!", Toast.LENGTH_LONG).show();
            passwordTextView.setTextColor(Color.RED);
        }
        else
        {
            passwordTextView.setTextColor(Color.BLACK);
        }

    }
    private void init()
    {
        userNameTextView=(TextView) findViewById(R.id.userNameTextView);
        passwordTextView=(TextView) findViewById(R.id.passwordTextView);
        userNameEditText=(EditText) findViewById(R.id.userNameEditText);
        passwordEditText=(EditText) findViewById(R.id.passwordEditText);
    }
    private void setIntentFunction(String className)
    {
        if (className=="MainActivity")
            intentObj=new Intent(this, MainActivity.class);
        else if (className=="GamePlayActivity")
            intentObj=new Intent(this, GamePlayActivity.class);
        else if (className=="RankListActivity")
            intentObj=new Intent(this, RankListActivity.class);
        else if (className=="LoginActivity")
            intentObj=new Intent(this, LoginActivity.class);
        startActivity(intentObj);
    }
    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton, final String YES, final String NO){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage((Html.fromHtml("<font color='#FFFFFF'>"+Message+"</font>")));
        alertDialogBuilder.setTitle((Html.fromHtml("<font color='#FFFFFF'>"+Title+"</font>")));
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
