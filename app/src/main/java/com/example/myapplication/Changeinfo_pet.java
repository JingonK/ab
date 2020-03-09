package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class Changeinfo_pet extends AppCompatActivity {

    String id ;
    private static final int REQUEST_CODE =0;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo_pet);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        Toast.makeText(Changeinfo_pet.this,id,Toast.LENGTH_LONG).show();
        imageView =findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent2 = new Intent();
                intent2.setType("image/*");
                intent2.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent2,REQUEST_CODE);
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE){
            if(resultCode==RESULT_OK){
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    imageView.setImageBitmap(img);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(  resultCode==RESULT_CANCELED){
                Toast.makeText(this,"사진 선택 취소",Toast.LENGTH_LONG).show();
            }
        }


    }
}