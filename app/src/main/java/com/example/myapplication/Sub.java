package com.example.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skt.Tmap.TMapView;


public class Sub extends  AppCompatActivity  {
   @Override
    protected  void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_sanchack);

       RelativeLayout relativeLayout =  (RelativeLayout) findViewById(R.id.map_view);
       TMapView tmapview =new TMapView(this);

       tmapview.setSKTMapApiKey("l7xxcc595953ea344f68b5a45b96fd5bc629");
       relativeLayout.addView(tmapview);
       tmapview.setCompassMode(true);//보는 방향
       tmapview.setIconVisibility(true);// 현위치 아이콘표시
       tmapview.setZoomLevel(15);

   }

}