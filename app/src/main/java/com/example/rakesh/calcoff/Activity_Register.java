package com.example.rakesh.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Rakesh on 6/12/2018.
 */

public class Activity_Register extends AppCompatActivity {
    private EditText userNameEditText, passwordEditText, emailEditText, ageEditText, countryEditText;

    private String userName = "", password = "", email = "", age = "", country = "";
    private boolean onActivityStart = false;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Activity_Home_Page.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!onActivityStart) {
            startFunction();
            onActivityStart = true;
        }
        else
        {
            onBackPressed();
        }
    }


    private void startFunction()
    {

        init();
    }
    private void init()
    {
        userNameEditText=(EditText) findViewById(R.id.editText_username);
        passwordEditText=(EditText) findViewById(R.id.editText_password);
        emailEditText=(EditText) findViewById(R.id.editText_email);
        ageEditText=(EditText) findViewById(R.id.editText_age);
        countryEditText=(EditText) findViewById(R.id.editText_country);

        userNameEditText.setText("");
        passwordEditText.setText("");
        emailEditText.setText("");
        ageEditText.setText("");
        countryEditText.setText("");
        userName = ""; password = ""; email = ""; age = ""; country = "";



    }


    //onClick button functions
    public void function_Register(View view)
    {
        if(validateData())
        {
            checkIfUserNameExistOrNot();
        }
    }

    private boolean validateData() {
        userName = userNameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();
        age = ageEditText.getText().toString();
        country = countryEditText.getText().toString();
        boolean spaceFlag = false;

        //username check
        for (int i = 0; i < userName.length(); i++) {
            if (userName.charAt(i) == ' ') {
                spaceFlag = true;
                break;
            }
        }

        if (spaceFlag) {
            Toast.makeText(this, "Username should not contain any space!", Toast.LENGTH_LONG).show();
            return false;
        } else if (userName.length() == 0) {
            Toast.makeText(this, "UserName required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (userName.length() > 10) {
            Toast.makeText(this, "UserName less than 10 characters!", Toast.LENGTH_LONG).show();
            return false;
        }

        //password check
        if (password.length()==0)
        {
            Toast.makeText(this, "Password required!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (email.length()==0)
        {
            Toast.makeText(this, "Email required!", Toast.LENGTH_LONG).show();
            return false;
        }


        if (age == null)
            age = "";
        if (country.length() == 0)
            country = "";

        return true;
    }

    private void checkIfUserNameExistOrNot()
    {
        new AsyncTask<Integer, String, String>(){
            String content;
            protected String doInBackground(Integer... params) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userName",userName);
                    content=POSTCALL_FUNCTION.POSTCALL(getString(R.string.server_path)+getString(R.string.userName_check), jsonObject);

                }
                catch (Exception e1){
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("400"))
                    {
                        registerUser();
                    }
                    else if(result.length()!=0)
                    {
                        userNameTakenToast();
                    }
                    else
                    {
                        alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel");
                }
            }
        }.execute();


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
                    alertBoxFunction("Network Error!","_Please check if your network conectivity is active.","Retry","Cancel");
                }
                return content;
            }
            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("202"))
                    {
                        addUserIntoDb();
                    }
                    else
                    {
                        alertBoxFunction("Register failed!","Something went wrong. Please start again.","Retry","Cancel");
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!","~Something went wrong. Please start again.","Retry","Cancel");
                }
            }
        }.execute();
    }
    private void addUserIntoDb()
    {
        try{
            DatabaseHandler  db=new DatabaseHandler(this);
            db.addUserData(userName,email,age,country);
            Intent intent = new Intent(this, Activity_Home_Page.class);
            startActivity(intent);
        }
        catch (Exception e){
            System.out.println("DB_ENTRY_FAILED"+e);
        }
    }


    private void userNameTakenToast()
    {
        Toast.makeText(this, "UserName taken!", Toast.LENGTH_LONG).show();
        userNameEditText.setText("");
    }
    private void alertBoxFunction(String Title, String Message, String PositiveButton, String NegativeButton){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(Message)
//                .setTitle(Title);
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
