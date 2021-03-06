package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class fragment2 extends Fragment {
    private static final String TAG_JSON="totaltimelist";
    private static String TAG = "phptest_MainActivity";
    private static final String totalt="totaltime" ;

    HashMap<String, String> map = new HashMap<>();;
    private static final String t="time" ;
    String mJsonString;


    ListView mlistView;
    String Id="Id";
    String id ;

    ArrayAdapter<String> adapter;
    ArrayList<HashMap<String, String>> mArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.activity_fragment2, container, false);
        mlistView = (ListView)view2.findViewById(R.id.listv);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("id");
        mArrayList = new ArrayList<>();
        GetData task = new GetData();
        AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {



            //ListView의 아이템 중 하나가 클릭될 때 호출되는 메소드

            //첫번째 파라미터 : 클릭된 아이템을 보여주고 있는 AdapterView 객체(여기서는 ListView객체)

            //두번째 파라미터 : 클릭된 아이템 뷰

            //세번째 파라미터 : 클릭된 아이템의 위치(ListView이 첫번째 아이템(가장위쪽)부터 차례대로 0,1,2,3.....)

            //네번재 파리미터 : 클릭된 아이템의 아이디(특별한 설정이 없다면 세번째 파라이터인 position과 같은 값)

            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), mapview.class);
                intent.putExtra("id",mArrayList.get(position).get("Id"));
                intent.putExtra("time",mArrayList.get(position).get("time"));

                startActivityForResult(intent, 1001);


                //클릭된 아이템의 위치를 이용하여 데이터인 문자열을 Toast로 출력


            }

        };



        mlistView.setOnItemClickListener(listener);


        map.put("id",id);
        task.execute("http://121.168.248.192/list.php");


        return view2;
    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(getActivity(),
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

                String id = item.getString(Id);
                String time_ = item.getString(t);
                String total_time = item.getString(totalt);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(Id, id);
                hashMap.put(t, time_);
                hashMap.put(totalt, total_time);
                mArrayList.add(hashMap);
            }


            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), mArrayList, R.layout.item_list,
                    new String[]{Id,t, totalt},
                    new int[]{R.id.textView_list_id, R.id.textView_list_name, R.id.textView_list_address}
            );
            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
