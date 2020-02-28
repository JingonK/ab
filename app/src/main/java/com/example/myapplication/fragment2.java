package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class fragment2 extends Fragment implements View.OnClickListener {
    public static final int sub = 1001;
Button btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.activity_fragment2, container, false);
        btn = (Button) view2.findViewById(R.id.please);
        Toast.makeText(getActivity(),"a",Toast.LENGTH_LONG).show();
        btn.setOnClickListener(this);


        return inflater.inflate(R.layout.activity_fragment2, container, false);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.please:
                Toast.makeText(getActivity(), "abc", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Sub.class);

                startActivityForResult(intent, sub);
        }
    }
}