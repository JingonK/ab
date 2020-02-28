package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class Changeinfo  extends AppCompatActivity
{
    static EditText editTextId;
    static EditText editTextPw;
    static EditText editTextName;
    static EditText editTextAge;
    static EditText editTextDogName;
    static EditText editTextDogAge;
    static EditText editTextKind;
    static Handler mHandler;
    static String User_id;
    static String User_password;
    static String userid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        Intent intent = getIntent();
        Bundle extras = this.getIntent().getExtras();
        userid = extras.getString("userid");

        editTextId=(EditText)findViewById(R.id.email_edit_CI);
        editTextName=(EditText)findViewById(R.id.name_edit_CI);

        Toast.makeText(Changeinfo.this,userid,Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            AlertDialog.Builder builder = new AlertDialog.Builder(Changeinfo.this);

            @Override
            public void run() {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {    JSONObject jsonResponse = new JSONObject(response);

                                editTextId.setText(jsonResponse.getString("id")) ;
                                editTextName.setText(jsonResponse.getString("name"));


                        } catch (Exception e) {
                            editTextId.setText(userid);
                            e.printStackTrace();
                        }

                    }

                };

                ChangeinfoMysql changeinfoMysql= new ChangeinfoMysql(userid, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Changeinfo.this);
                queue.add(changeinfoMysql);
            }


}).start();
    }

}