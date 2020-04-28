package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.BirthdayType;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN=1;
    SessionCallback callback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button sign = (Button) findViewById(R.id.signupButton);
        final EditText idText = (EditText) findViewById(R.id.emailInput);
        final EditText passwordText = (EditText) findViewById(R.id.passwordInput);
        final Button loginButton = (Button) findViewById(R.id.loginButton);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);


        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
                finish();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = idText.getText().toString();
                final String pw = passwordText.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

                new Thread(new Runnable() {
                    @Override public void run() {

                        Response.Listener<String> responseListener = new Response.Listener<String>(){
                            @Override
                            public void onResponse(String response){
                                try{

                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if(success){
                                        String id = jsonResponse.getString("id");
                                        String pw= jsonResponse.getString("pw");

                                        String name= jsonResponse.getString("name");


                                        Intent intent = new Intent(Login.this,MainActivity.class);
                                        intent.putExtra("id",id);
                                        intent.putExtra("pw",pw);
                                        intent.putExtra("name",name);
                                        Intent intent2 = new Intent(Login.this,fragment1.class);
                                        intent2.putExtra("id",id);
                                        Login.this.startActivity(intent);
                                        finish();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                        builder.setMessage("로그인에 실패하였습니다.")
                                                .setNegativeButton("다시 시도",null)
                                                .create()
                                                .show();
                                    }


                                }catch(Exception e){

                                    e.printStackTrace();
                                }

                            }

                        };

                        LoginRequest loginRequest = new LoginRequest(id, pw, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(Login.this);
                        queue.add(loginRequest);
                    }
                }).start();



            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
        finish();
    }
    private void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }


            @Override
            public void onSuccess(MeV2Response response) {
                Logger.d("user id : " + response.getId());

              UserAccount kakaoAccount = response.getKakaoAccount();
                if (kakaoAccount != null) {
                    String email = kakaoAccount.getEmail();
                    if (email != null) {
                        Logger.d("email: " + email);
                    } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 이메일 획득 가능
                        // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                    } else {
                        // 이메일 획득 불가
                    }

                    Profile profile = kakaoAccount.getProfile();
                    if (profile != null) {
                        Logger.d("nickname: " + profile.getNickname());
                        Logger.d("profile image: " + profile.getProfileImageUrl());
                        Logger.d("thumbnail image: " + profile.getThumbnailImageUrl());
                    } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 프로필 정보 획득 가능
                    } else {
                        // 프로필 획득 불가
                    }
                }
                insertoToDatabase(kakaoAccount.getEmail(),"kakao",kakaoAccount.getProfile().getNickname());

                String name = kakaoAccount.getProfile().getNickname();
                String id = kakaoAccount.getEmail();

                redirectMainActivity(name,kakaoAccount.getEmail());
            }

        });
    }

    private void redirectMainActivity(String name, String id) {


        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("id",id);
        startActivity(intent);
        finish();
    }

    private void redirectLoginActivity() { final Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void insertoToDatabase(String Id, String Pw,String Name) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
             }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String Name = (String) params[2];

                    String link = "http://121.168.248.192/signup.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8")+"&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8")+ "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");


                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();


                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(Id, Pw,Name);
    }
}