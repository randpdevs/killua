package com.randpdevs.calcoff;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler  db;
    private Button loginButton,registerButton,logoutButton;
    private TextView userNameTextView;
    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    void init(){
        loginButton=(Button) findViewById(R.id.LLButton);
        registerButton=(Button) findViewById(R.id.registerButton);
        logoutButton=(Button) findViewById(R.id.logoutButton);
        userNameTextView=(TextView) findViewById(R.id.userNameDisplayTextView);
        db = new DatabaseHandler(this);
    }
    protected void Quit() {
        super.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setLoginRegisterButton();


    }
    private void setLoginRegisterButton(){
        if (db.getUserDataCount()==0)
        {
            //Show login and register button
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
            userNameTextView.setVisibility(View.INVISIBLE);
            //dissable play button, and ask for login
        }
        else
        {
            JSONObject jsonObject=db.fetchUserData(db.lastInsertedRowUserData());
            try{
                userNameTextView.setText(jsonObject.getString("userName"));
            }
            catch (Exception e)
            {System.out.println("SettingUserNameFailed");        }
            loginButton.setVisibility(View.INVISIBLE);
            registerButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
        }
    }

    public void playButtonFunction(View view){
        if (db.getUserDataCount()!=0){
            Intent intent = new Intent(this, GamePlayActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please login to play", Toast.LENGTH_LONG).show();
        }

    }
    public void settingsButtonFunction(View view){
        System.out.println("Settings");
    }

    public  void LLButtonFunction(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void registerButtonFunction(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void logoutButtonFunction(View view)
    {
        if (db.getUserDataCount()!=0) {
            db.deleteUserData(db.lastInsertedRowUserData());
            userNameTextView.setText("");
        }
        setLoginRegisterButton();
    }
}
