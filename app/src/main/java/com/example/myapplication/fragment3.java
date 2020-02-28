package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static android.widget.Toast.LENGTH_LONG;

public class fragment3 extends Fragment  {
    private Button btn_f_3;
    private Button btn2_f_3;
    String id;
    public static final int sub = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view_fragment_3 = inflater.inflate(R.layout.activity_fragment3, container, false);
        Intent intent = getActivity().getIntent();

        id = intent.getStringExtra("id");
        btn_f_3 = (Button) view_fragment_3.findViewById(R.id.CF);
        btn2_f_3 = (Button) view_fragment_3.findViewById(R.id.changeinfopet);

        Toast.makeText(getActivity(),id,Toast.LENGTH_LONG).show();

        btn_f_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "OK", LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Changeinfo.class);
                intent.putExtra("userid",id);
                startActivityForResult(intent,sub);
            }
        });
        btn2_f_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Changeinfo_pet.class);
                startActivityForResult(intent,sub);
            }
        });
        return view_fragment_3;
        //return inflater.inflate(R.layout.activity_fragment3, container, false);
    }
}