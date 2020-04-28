package com.example.myapplication;
import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.widget.Chronometer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Sub extends  AppCompatActivity  {
    String Time;
    fragment1 frag1;
    String Id="abc";
    String date;
    double lat=0;//위도
    double lon=0;//경도
    long stopTime = 0;
    ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
    long myBaseTime;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=1000;
    private static final long MIN_TIME_BW_UPDATES=1000;
    TMapGpsManager tmapgps ;
    TMapView tmapview;
    double startX=127.104997;
    double endX= 127.028131;
    double startY=37.220800;
    double endY=37.239072;
    RelativeLayout relativeLayout;
    String getTime;
    boolean st = false;
    Chronometer chronometer;
    Button startBtn, endBtn;
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanchack);
        Id = getIntent().getExtras().getString("id");
        Toast.makeText(getApplicationContext(),Id,Toast.LENGTH_LONG).show();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        chronometer = (Chronometer)findViewById(R.id.displaytime);
        startBtn = (Button)findViewById(R.id.start);
        endBtn = (Button)findViewById(R.id.end);

        relativeLayout =  (RelativeLayout) findViewById(R.id.map_view);
        tmapview =new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxcc595953ea344f68b5a45b96fd5bc629");
        relativeLayout.addView(tmapview);
        tmapview.setCompassMode(true);//보는 방향
        tmapview.setIconVisibility(true);// 현위치 아이콘표시
        tmapview.setZoomLevel(15);
        tmapgps=  new TMapGpsManager(Sub.this);
        tmapview.setTrackingMode(true);//현재위치로 화면 이동
        tmapview.setSightVisible(true);


        //우성 현위치정보
        ;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        setGps();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st=true;
                myBaseTime = SystemClock.elapsedRealtime();
                Date mDate = new Date(myBaseTime);
                chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                getTime = simpleDate.format(System.currentTimeMillis());
                Toast.makeText(getApplicationContext(),getTime,Toast.LENGTH_LONG).show();
                chronometer.start();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                stopTime = 0;
                st=false;
                chronometer.stop();

                insertoToDatabase2(Id,getTime,getTimeOut());
                Intent intent = new Intent(Sub.this, MainActivity.class);

                Intent intent2 = new Intent(Sub.this, fragment1.class);
                intent.putExtra("id",Id);
               intent2.putExtra("id",Id);

                startActivity(intent);
                finish();
            }



        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmapview.setLocationPoint(longitude, latitude);
                tmapview.setCenterPoint(longitude, latitude);
                if(st==true){
                    insertoToDatabase(Id,String.valueOf(latitude),String.valueOf(longitude),getTime);

                }
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
    }


    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;

    }

    public void insertoToDatabase(String Id, String rat,String ron,String Time) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

             }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String Id = (String) params[0];
                    String rat=(String) params[1];
                    String ron = (String) params[2];
                    String Time = (String) params[3];
                    String link = "http://121.168.248.192/route.php";

                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8")+"&" +URLEncoder.encode("rat", "UTF-8") + "=" + URLEncoder.encode(rat, "UTF-8")+"&"+ URLEncoder.encode("ron", "UTF-8") + "=" + URLEncoder.encode(ron, "UTF-8")+"&" + URLEncoder.encode("Time", "UTF-8") + "=" + URLEncoder.encode(Time, "UTF-8");


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

        task.execute(Id,rat,ron,Time);

    }


    public void insertoToDatabase2(String Id, String Time,String totalTime) {
        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String Id = (String) params[0];

                    String Time = (String) params[1];

                    String totalTime = (String) params[2];
                    String link = "http://121.168.248.192/time.php";

                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8")+"&" + URLEncoder.encode("Time", "UTF-8") + "=" + URLEncoder.encode(Time, "UTF-8")+"&" + URLEncoder.encode("totalTime", "UTF-8") + "=" + URLEncoder.encode(totalTime, "UTF-8");


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

        task.execute(Id,Time,totalTime);

    }



}