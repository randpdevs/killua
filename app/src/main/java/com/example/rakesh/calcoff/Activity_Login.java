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

public class Activity_Login extends AppCompatActivity {
    private EditText userNameEditText, passwordEditText;
    private String userName = "", password = "";
    private boolean onActivityStart = false;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Activity_Home_Page.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show();
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











    //onClick button functions
    public void function_Login(View view)
    {
        //Toast.makeText(this, "Login button", Toast.LENGTH_LONG).show();
        if(validateValues())
            loginUser();
    }
    private void loginUser(){
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
                    alertBoxFunction("Network Error!!","Please check if your network conectivity is active.","Retry","Cancel" );
                }
                return content;
            }

            protected void onPostExecute(String result) {
                try {
                    if(result.toString().equalsIgnoreCase("404"))
                    {
                        alertBoxFunction("No user found!","Please try again.","Retry","Cancel" );
                    }
                    else if (result.toString().equalsIgnoreCase("400"))
                    {
                        alertBoxFunction("Wrong password","Please try again.","Retry","Cancel" );
                    }
                    else
                    {
                        if(result.length()!=0)
                            LoginSuccessful(result);
                        else
                            alertBoxFunction("Network Error!","Please check if your network conectivity is active.","Retry","Cancel");
                    }
                }
                catch (Throwable t) {
                    alertBoxFunction("Oops!!","Something went wrong. Please start again.","Retry","Cancel" );
                }
            }
        }.execute();


    }

    private void LoginSuccessful(String result){
        try{
            JSONObject row=new JSONObject(result);
            DatabaseHandler  db=new DatabaseHandler(this);
            String userName=row.getString("UserName").toString();
            db.addUserData(userName,row.getString("EmailID").toString(),row.getString("Age").toString(),row.getString("Country").toString());
            Intent intent = new Intent(this, Activity_Home_Page.class);
            startActivity(intent);
        }
        catch (Exception e){
            alertBoxFunction("Oops!","-Something went wrong. Please start again.","Retry","Cancel" );

        }
    }

    private boolean validateValues(){
        userName = userNameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        //Username validation
        boolean spaceFlag = false;
        for (int i = 0; i < userName.length(); i++)
        {
            if(userName.charAt(i) == ' ')
            {
                spaceFlag = true;
                break;
            }
        }

        if(spaceFlag)
        {
            Toast.makeText(this, "Username should not contain any space!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userName.length() == 0)
        {
            Toast.makeText(this, "UserName required!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(userName.length() > 10)
        {
            Toast.makeText(this, "UserName less than 10 characters!", Toast.LENGTH_LONG).show();
            return false;
        }

        //Password validation
        if (password.length() == 0)
        {
            Toast.makeText(this, "Password required!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void startFunction(){
        init();
    }
    private void init(){
        userNameEditText = (EditText) findViewById(R.id.editText_username);
        passwordEditText = (EditText) findViewById(R.id.editText_password);
        userNameEditText.setText("");
        passwordEditText.setText("");
        userName = "";
        password = "";
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
