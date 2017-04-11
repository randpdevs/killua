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
    private boolean onActivityStart=false;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

    }
    @Override
    protected void onStart() {
//        Toast.makeText(this, "onStart", Toast.LENGTH_LONG).show();
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
//        Toast.makeText(this, "onStop", Toast.LENGTH_LONG).show();
        super.onStop();
    }


    public void loginButtonFunction(View view){
        if(getValues())
            loginUser();
    }


    private void startFunction(){
        init();
    }

    private boolean getValues()
    {
        userName=userNameEditText.getText().toString();
        password=passwordEditText.getText().toString();
        boolean spaceFlag=false;
        for (int i=0;i<userName.length();i++)
        {
            if(userName.charAt(i)==' ')
            {
                spaceFlag=true;
                break;
            }
        }
        if(spaceFlag)
        {
            Toast.makeText(this, "Username should not contain any space!", Toast.LENGTH_LONG).show();
            userNameEditText.setText("");
            userNameTextView.setTextColor(Color.RED);
            return false;
        }
        else
            userNameTextView.setTextColor(Color.BLACK);



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
        return true;

    }

    private void loginUser()
    {

        if (userName.length()==0)
        {
            Toast.makeText(this, "UserName required!", Toast.LENGTH_LONG).show();
            userNameTextView.setTextColor(Color.RED);
        }
        else if(userName.length()>10)
        {
            Toast.makeText(this, "UserName less than 10 characters!", Toast.LENGTH_LONG).show();
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


        if(userName.length()!=0 &&userName.length()<10 &&  password.length()!=0)
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
                                addUserIntoDb(result);
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
            alertBoxFunction("Oops!","-Something went wrong. Please start again.","Retry","Cancel" );

        }
    }

    private void init()
    {
        userNameTextView=(TextView) findViewById(R.id.userNameTextView);
        passwordTextView=(TextView) findViewById(R.id.passwordTextView);
        userNameEditText=(EditText) findViewById(R.id.userNameEditText);
        passwordEditText=(EditText) findViewById(R.id.passwordEditText);
        userNameEditText.setText("");
        passwordEditText.setText("");
        intentObj=null;
        userName="";
        password="";
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
