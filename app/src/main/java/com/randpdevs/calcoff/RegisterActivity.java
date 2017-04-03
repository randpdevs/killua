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
public class RegisterActivity extends AppCompatActivity {
    private EditText userNameEditText,passwordEditText,emailEditText,ageEditText,countryEditText;
    private TextView userNameTextView,passwordTextView,emailTextView,ageTextView,countryTextView;
    private Intent intentObj;
    private String userName="",password="",email="",age="",country="";
    private boolean checkUserNameAllowed=false,checkRegisteredAllowed=false;
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();
    }

    public void submitButtonFunction(View view){
        getValues();

        if (checkUserNameAllowed)
        {
            checkIfUserNameExistOrNot();
        }


    }

    private void registerUser()
    {
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.accumulate("userName", userName);
                    jsonObject.accumulate("userPassword", password);
                    jsonObject.accumulate("emailID",email);
                    jsonObject.accumulate("userCountry",country);
                    jsonObject.accumulate("userAge",age);

                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.user_Register), jsonObject);

                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel","RegisterActivity","MainActivity");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("202"))
                    {
                        addUserIntoDb();

//                        SetUpDB and redirect to main page
                    }
                    else
                    {
                        alertBoxFunction("Register failed!","Something went wrong. Please start again.","Retry","Cancel","RegisterActivity","MainActivity");
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel","RegisterActivity","MainActivity");
                }
            }
        }.execute();
    }
    private void addUserIntoDb()
    {
        try{
            DatabaseHandler  db=new DatabaseHandler(this);

            db.addUserData(userName,email,age,country);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        catch (Exception e){
            System.out.println("DB_ENTRY_FAILED"+e);
        }
    }
    private void checkIfUserNameExistOrNot(){
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userName",userName);
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.userName_check), jsonObject);

                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel","RegisterActivity","MainActivity");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("400"))
                    {
                        checkRegisteredAllowed=true;
                        registerUser();
                    }
                    else
                    {
                        userNameTakenToast();
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel","RegisterActivity","MainActivity");
                }
            }
        }.execute();

    }

    private void userNameTakenToast()
    {
        Toast.makeText(this, "UserName taken!", Toast.LENGTH_LONG).show();
        userNameTextView.setTextColor(Color.RED);
        userNameEditText.setText("");
    }

    private void getValues()
    {
        checkRegisteredAllowed=false;
        userName=userNameEditText.getText().toString();
        password=passwordEditText.getText().toString();
        email=emailEditText.getText().toString();
        age=ageEditText.getText().toString();
        country=countryEditText.getText().toString();

        if (age==null)
            age="";
        if (country.length()==0)
            country="";
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
        if (email.length()==0)
        {
            Toast.makeText(this, "Email required!", Toast.LENGTH_LONG).show();
            emailTextView.setTextColor(Color.RED);
        }
        else
        {
            emailTextView.setTextColor(Color.BLACK);
        }

        if(userName.length()!=0 && email.length()!=0 && password.length()!=0)
            checkUserNameAllowed=true;
        else
            checkRegisteredAllowed=false;



    }
    private void init()
    {
        userNameTextView=(TextView) findViewById(R.id.userNameTextView);
        passwordTextView=(TextView) findViewById(R.id.passwordTextView);
        emailTextView=(TextView) findViewById(R.id.emailTextView);
        ageTextView=(TextView) findViewById(R.id.ageTextView);
        countryTextView=(TextView) findViewById(R.id.countryTextView);

        userNameEditText=(EditText) findViewById(R.id.userNameEditText);
        passwordEditText=(EditText) findViewById(R.id.passwordEditText);
        emailEditText=(EditText) findViewById(R.id.emailEditText);
        ageEditText=(EditText) findViewById(R.id.ageEditText);
        countryEditText=(EditText) findViewById(R.id.countryEditText);
    }
    private void setIntentFunction(String className)
    {
        if (className=="MainActivity")
            intentObj=new Intent(this, MainActivity.class);
        else if (className=="GamePlayActivity")
            intentObj=new Intent(this, GamePlayActivity.class);
        else if (className=="RankListActivity")
            intentObj=new Intent(this, RankListActivity.class);
        else if (className=="RegisterActivity")
            intentObj=new Intent(this, LoginActivity.class);

        startActivity(intentObj);
    }
    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton, final String YES, final String NO){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(Message)
//                .setTitle(Title);
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
