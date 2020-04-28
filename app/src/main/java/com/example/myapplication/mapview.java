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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class mapview extends  AppCompatActivity  {
    String mJsonString;
    ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();

    private static final String TAG_JSON="totaltimelist";
    private static String TAG = "phptest_MainActivity";

    HashMap<String, String> map = new HashMap<>();;
    private static final String t="time" ;
      String Id="Id";
    String id ;
    String lat_ ="lat";
    String lon_ = "ron";
    TMapView tmapview;
    ArrayAdapter<String> adapter;
    ArrayList<HashMap<String, String>> mArrayList;
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);


        ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();

        TMapGpsManager tmapgps ;

        RelativeLayout relativeLayout;
        String getTime;

        String id =getIntent().getStringExtra("id");
        String time =getIntent().getStringExtra("time");




            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            relativeLayout =  (RelativeLayout) findViewById(R.id.map_view2);


           tmapview =new TMapView(this);

            tmapview.setCenterPoint(126.96544,37.299, false);
            tmapview.setSKTMapApiKey("l7xxcc595953ea344f68b5a45b96fd5bc629");
            relativeLayout.addView(tmapview);
            tmapview.setZoomLevel(14);

        GetData task = new GetData();
        map.put("id",id);
        map.put("time",time);
        task.execute("http://121.168.248.192/maproute.php");
            //우성 현위치정보

        }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(mapview.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null){

            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream =httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bw.write(getPostString(map));
                bw.flush();
                bw.close();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }



                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
    private String getPostString(HashMap<String, String> map) {
        StringBuilder result = new StringBuilder();
        boolean first = true; // 첫 번째 매개변수 여부

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first)
                first = false;
            else // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                result.append("&");

            try { // UTF-8로 주소에 키와 값을 붙임
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException ue) {
                ue.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }

    private void showResult(){
        try {

            JSONObject jsonObject = new JSONObject(mJsonString);

            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String lat = item.getString(lat_);
                String lon = item.getString(lon_);
                alTMapPoint.add( new TMapPoint(Double.parseDouble(lat), Double.parseDouble(lon)) );
            }

            Toast.makeText(mapview.this,String.valueOf(alTMapPoint.size()),Toast.LENGTH_LONG).show();
            TMapPolyLine tMapPolyLine = new TMapPolyLine();
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);

            Toast.makeText(mapview.this,String.valueOf(alTMapPoint.size()),Toast.LENGTH_LONG).show();
            for( int i=0; i<alTMapPoint.size(); i++ ) {
                tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
            }
            tmapview.addTMapPolyLine("Line1", tMapPolyLine);
            tmapview.setCompassMode(false);//보는 방향
            tmapview.setIconVisibility(false);// 현위치 아이콘표시
            tmapview.setZoomLevel(14);
            tmapview.setSightVisible(false);
        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
