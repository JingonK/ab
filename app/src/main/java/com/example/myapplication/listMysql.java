package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class listMysql extends StringRequest {

    final static private String URL = "http://121.168.248.192/list.php";
    private Map<String, String> parameters;

    public listMysql(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("id",id);
    }
    @Override
    public Map<String, String> getParams(){return parameters;}
}
