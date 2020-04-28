package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
private FragmentManager  fragmentManager;
private fragment1 frag1;
private fragment2 frag2;
private fragment3 frag3;
private FragmentTransaction transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String id =intent.getStringExtra("id");
        Intent intent2 = new Intent(MainActivity.this,fragment3.class);
        Intent intent3 =new Intent(MainActivity.this,fragment1.class);

        intent2.putExtra("id",id);

        intent3.putExtra("id",id);



        fragmentManager = getSupportFragmentManager();
        frag1 = new fragment1();
        frag2 = new fragment2();
        frag3 = new fragment3();
        Bundle bundle = new Bundle();
        bundle.putString("id",name);
        frag1.setArguments(bundle);


        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,frag1).commitAllowingStateLoss();
    }

    public void clickHandler(View view)
    {
        transaction = fragmentManager.beginTransaction();

        switch(view.getId())
        {
            case R.id.button1:
                transaction.replace(R.id.container, frag1).commitAllowingStateLoss();

                break;
            case R.id.button2:
                transaction.replace(R.id.container, frag2).commitAllowingStateLoss();
                break;
            case R.id.button3:
                transaction.replace(R.id.container, frag3).commitAllowingStateLoss();
                break;
        }
    }

}
