package com.example.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;

public class fragment1 extends Fragment {


    private Button btn;
    private Button btn2;
    TextView temp1, cloud, wind;
    public static final int sub = 1001;
    LocationManager locationManager;
    boolean isGPSEnabled =false;//GPS 사용 유무
    boolean isNetworkEnabled = false;//네트워크 사용 유무
    boolean isGetLocation =false; //GPS 상태값
    Location location;
    double lat=0;//위도
    double lon=0;//경도
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES=1000;
    private static final long MIN_TIME_BW_UPDATES=1000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view2 = inflater.inflate(R.layout.activity_fragment1, container, false);
        TextView t = view2.findViewById(R.id.t2);
        final LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        btn = (Button) view2.findViewById(R.id.button5);
        btn2 = (Button) view2.findViewById(R.id.button6);
        temp1 = (TextView) view2.findViewById(R.id.textView6);
        cloud = (TextView) view2.findViewById(R.id.t2);
        wind = (TextView) view2.findViewById(R.id.t3);
        Bundle extra = this.getArguments();
        String id = extra.getString("id"); // 전달한 key 값

         Toast.makeText(getActivity(),id+"님 어서오세요~~ ",Toast.LENGTH_LONG).show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Sub.class);

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);

                startActivityForResult(intent, sub);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWeatherData(lat,lon);

                Toast.makeText(getActivity(),lat+","+lon, LENGTH_LONG).show();
            }
        });

        getWeatherData(lat,lon);
try{
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,gpsLocationListener);
    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,networkLocationListener);}
catch(SecurityException e){
    e.printStackTrace();

}
        t.append(String.valueOf(lon));


        return view2;
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            lon=location.getLongitude();
            lat=location.getLatitude();
            getWeatherData(lat,lon);

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };

    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            lon=location.getLongitude();
            lat=location.getLatitude();
            getWeatherData(lat,lon);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };


    private void getWeatherData(double lat, double lng){
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lng+"&appid=dafbdf14f38344cea716791d2ae1e147";
        //abcdㄷ
          ReceiveWeatherTask receiveUseTask = new ReceiveWeatherTask();
        receiveUseTask.execute(url);
    }
    private class ReceiveWeatherTask extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected void onPreExecute(){


            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... datas){

            try{
                HttpURLConnection conn= (HttpURLConnection) new URL(datas[0]).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream is = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(reader);

                    String readed ;
                    while((readed=in.readLine())!=null){
                        JSONObject jObject = new JSONObject(readed);
                        return jObject;
                    }
                }else{
                    return null;

                }
                return null;
            }catch(Exception e){

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result){

            if(result !=null){


                String iconName ="";
                String nowTemp = "";
                String minTemp = "";
                String maxTemp="";
                String humidity ="";
                String speed ="";
                String main = "";
                String description = "";

                try{

                    iconName=result.getJSONArray("weather").getJSONObject(0).getString("icon");
                    nowTemp=result.getJSONObject("main").getString("temp");
                    humidity = result.getJSONObject("main").getString("humidity");
                    minTemp = result.getJSONObject("main").getString("temp_min");
                    maxTemp = result.getJSONObject("main").getString("temp_max");
                    main = result.getJSONArray("weather").getJSONObject(0).getString("main");


                }catch(JSONException e){
                    e.printStackTrace();
                }
                double _temp = Double.parseDouble(nowTemp);
                _temp=_temp-273.15;
                 temp1.setText( "온도 :"+_temp);
                cloud.setText( "날씨 : "+main);
                wind.setText( "습도 : "+humidity);
            }
        }
    }
}