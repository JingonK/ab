package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangeinfoMysql extends StringRequest {

    final static private String URL = "http://121.168.8.78/change_info_.php";
    private Map<String, String> parameters;

    public ChangeinfoMysql(String id, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("id",id);
    }
    @Override
    public Map<String, String> getParams(){return parameters;}
}
