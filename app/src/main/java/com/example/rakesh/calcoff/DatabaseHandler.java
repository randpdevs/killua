package com.example.rakesh.calcoff;

/**
 * Created by Rakesh on 6/12/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

/**
 * Created by Rakesh on 4/3/2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "calcoff";

    // Contacts table name
    private static final String TABLE_NAME1 = "userModel";
    private static final String TABLE_NAME2 = "urlModel";


    // Contacts Table Columns names
    private static final String KEY_TABLE1_ID = "id";
    private static final String KEY_TABLE1_USERNAME = "username";
    private static final String KEY_TABLE1_EMAIL = "email";
    private static final String KEY_TABLE1_AGE = "age";
    private static final String KEY_TABLE1_COUNTRY = "country";

    private static final String KEY_TABLE2_ID="id";
    private static final String KEY_TABLE2_URL="url";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


//db.addUserData("Raks","email","1","c");

//    --------------------------------------GET COUNT-----------------------------------------
//    System.out.println("UserModelCount:"+db.getUserDataCount());
//    System.out.println("UrlCount:"+db.getUrlDataCount());
//    ----------------------------------------------------------------------------------------

//    ---------------Delete--------------------------------------------------------------------
//        db.deleteUserData(2);
//    ----------------------------------------------------------------------------------------

//    --------------------LAST rom inserted id------------------------------------------------------------
//        System.out.println("LastId:"+db.lastInsertedRowUserData());
//    ----------------------------------------------------------------------------------------

//    --------------------------Fetch Data-----------------------------------------------------
//        JSONObject jsonObject=db.fetchUserData(1);
//        try{
//          System.out.println("UserName:"+jsonObject.getString("userName"));
//        }
//        catch (Exception e)
//        {        }
//    ----------------------------------------------------------------------------------------


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME1 + "("
                + KEY_TABLE1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TABLE1_USERNAME + " TEXT,"
                + KEY_TABLE1_EMAIL + " TEXT," + KEY_TABLE1_AGE + " TEXT," + KEY_TABLE1_COUNTRY + " TEXT" + ")";
        db.execSQL(CREATE_TABLE1);
        String CREATE_TABLE2 = "CREATE TABLE " + TABLE_NAME2 + "("
                + KEY_TABLE2_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TABLE2_URL + " TEXT"+  ")";
        db.execSQL(CREATE_TABLE2);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    int lastInsertedRowUserData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME1,  new String[] { KEY_TABLE1_ID},null, null, null, null, null);
        cursor.moveToLast();
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TABLE1_ID)));
        cursor.close();
        db.close();
        return id;
    }

    int lastInsertedRowUrlData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME2,  new String[] { KEY_TABLE2_ID},null, null, null, null, null);
        cursor.moveToLast();
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TABLE2_ID)));
        cursor.close();
        db.close();
        return id;
    }
    void deleteUserData(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME1,"id="+id,null);
        db.close(); // Closing database connection
    }
    void deleteUrl(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME2,"id="+id,null);
        db.close(); // Closing database connection
    }



    // Adding new userData
    void addUserData(String userName,String email,String age,String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLE1_USERNAME, userName);
        values.put(KEY_TABLE1_EMAIL, email);
        values.put(KEY_TABLE1_AGE, age);
        values.put(KEY_TABLE1_COUNTRY, country);
        db.insert(TABLE_NAME1, null, values);
        db.close(); // Closing database connection
    }

    // Adding new urlData
    void addUrlData(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLE2_URL, url);
        db.insert(TABLE_NAME2, null, values);
        db.close(); // Closing database connection
    }

    void updateUserData(String userName,String email,String age,String country ,int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLE1_USERNAME, userName);
        values.put(KEY_TABLE1_EMAIL, email);
        values.put(KEY_TABLE1_AGE, age);
        values.put(KEY_TABLE1_COUNTRY, country);
        db.update(TABLE_NAME1, values, "id="+id, null);
        db.close(); // Closing database connection

    }

    void updateUrl(String url,int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TABLE2_URL, url);
        db.update(TABLE_NAME2, values, "id="+id, null);
        db.close(); // Closing database connection
    }

    JSONObject fetchUserData(int id)
    {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME1, new String[] { KEY_TABLE1_ID,
                        KEY_TABLE1_USERNAME, KEY_TABLE1_EMAIL,KEY_TABLE1_AGE,KEY_TABLE1_COUNTRY }, KEY_TABLE1_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try{

            jsonObject.accumulate("id", cursor.getString(cursor.getColumnIndex(KEY_TABLE1_ID)));
            jsonObject.accumulate("userName", cursor.getString(cursor.getColumnIndex(KEY_TABLE1_USERNAME)));
            jsonObject.accumulate("email", cursor.getString(cursor.getColumnIndex(KEY_TABLE1_EMAIL)));
            jsonObject.accumulate("age", cursor.getString(cursor.getColumnIndex(KEY_TABLE1_AGE)));
            jsonObject.accumulate("country", cursor.getString(cursor.getColumnIndex(KEY_TABLE1_COUNTRY)));

        }
        catch ( Exception e)
        {
            System.out.println("ErrorFetch:"+e);
        }
        cursor.close();
        db.close(); // Closing database connection

        return jsonObject;
    }
    JSONObject fetchUrlData(int id)
    {
        JSONObject jsonObject = new JSONObject();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME2, new String[] { KEY_TABLE2_ID,
                        KEY_TABLE2_URL }, KEY_TABLE2_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try{

            jsonObject.accumulate("id", cursor.getString(cursor.getColumnIndex(KEY_TABLE2_ID)));
            jsonObject.accumulate("url", cursor.getString(cursor.getColumnIndex(KEY_TABLE2_URL)));
        }
        catch ( Exception e)
        {
            System.out.println("ErrorFetch:"+e);
        }
        cursor.close();
        db.close(); // Closing database connection
        return jsonObject;
    }


    // Getting contacts Count
    public int getUserDataCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int getCount = cursor.getCount();
        cursor.close();
        return getCount;
    }
    // Getting contacts Count
    public int getUrlDataCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME2;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int getCount = cursor.getCount();
        cursor.close();
        return getCount;
    }


}
