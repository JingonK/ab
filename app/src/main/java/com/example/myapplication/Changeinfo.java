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
import android.widget.Button;
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
    static EditText editTextPhone;
    static EditText editTextDogAge;
    static EditText editTextKind;
    static Handler mHandler;
    static String User_id;
    static String User_password;
    static String userid ;
    String Name;
    String Phone ;
    String Birth ;
    Button change ;
    String Pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        Intent intent = getIntent();
        Bundle extras = this.getIntent().getExtras();
        userid = extras.getString("userid");
        editTextPhone=(EditText)findViewById(R.id.phone_edit_CI);
        editTextId=(EditText)findViewById(R.id.email_edit_CI);
        editTextName=(EditText)findViewById(R.id.name_edit_CI);
        editTextPw=(EditText)findViewById(R.id.password_edit_CI);
editTextAge=(EditText)findViewById(R.id.age_edit_CI);
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
        change = (Button) findViewById(R.id.signup_CI);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Pw = editTextPw.getText().toString();
                 Name= editTextName.getText().toString();
                 Phone = editTextPhone.getText().toString();
                Birth = editTextAge.getText().toString();
               //  Intent intent = new Intent(Changeinfo.this, MainActivity.class);
                 ChangeDatabase(userid,Pw,Name,Phone,Birth);
               //  startActivityForResult(intent,1001)
                  finish();
            }
        });

    }




    public void ChangeDatabase(String Id, String Pw,String Name,String Phone,String Birth) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Changeinfo.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String Name = (String) params[2];
                    String Phone = (String) params[3];
                    String Birth = (String) params[4];

                    String link = "http://121.168.248.192/change_info_user.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8")+"&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8")+ "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8")+"&"+ URLEncoder.encode("Phone", "UTF-8") + "=" + URLEncoder.encode(Phone, "UTF-8")+"&"+ URLEncoder.encode("Birth", "UTF-8") + "=" + URLEncoder.encode(Birth, "UTF-8");


                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();


                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(Id, Pw,Name,Phone,Birth);
    }
}