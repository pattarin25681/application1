package com.example.projectqueue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    EditText txt_fname,txt_lname,txt_email,txt_cardnum,txt_passregis,txt_username;
    Button btnsaveregis,btn_return;

    String name,surname,email,cardnumber,password,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        /*txt_fname = findViewById(R.id.txt_fname);
        txt_lname = findViewById(R.id.txt_lname);*/
        txt_email = findViewById(R.id.txt_email);
        txt_cardnum = findViewById(R.id.txt_cardnum);
        txt_passregis = findViewById(R.id.txt_passregis);
        txt_username = findViewById(R.id.txt_username);
       // btn_return = findViewById(R.id.btn_return);
       // btn_return.setOnClickListener(this);
        btnsaveregis = findViewById(R.id.btn_saveregis);
        btnsaveregis.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_saveregis:
               /* name = (txt_fname.getText().toString());
                surname = (txt_lname.getText().toString());*/
                email = (txt_email.getText().toString());
                cardnumber = (txt_cardnum.getText().toString());
                password = (txt_passregis.getText().toString());
                username = (txt_username.getText().toString());
                /*if (TextUtils.isEmpty(name)) {
                    txt_fname.setError("Please enter Firstname");
                    txt_fname.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(surname)) {
                    txt_lname.setError("Please enter Lastname");
                    txt_lname.requestFocus();
                    return;
                }*/
                /*if (TextUtils.isEmpty(email)) {
                    txt_email.setError("Please enter email");
                    txt_email.requestFocus();
                    return;
                }*/
                if (cardnumber.length()<10) {
                    txt_cardnum.setError("กรุณาใส่เบอร์โทรศัพท์ 10 หลัก");
                    txt_cardnum.requestFocus();
                return;
                }
                if (TextUtils.isEmpty(cardnumber)) {
                    txt_cardnum.setError("กรุณาใส่เบอร์โทรศัพท์");
                    txt_cardnum.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    txt_username.setError("กรุณาใส่ชื่อผู้ใช้");
                    txt_username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    txt_passregis.setError("กรุณาใส่รหัสผ่าน");
                    txt_passregis.requestFocus();
                    return;
                }
                //Toast.makeText(getApplicationContext(), "SAVE", Toast.LENGTH_SHORT).show();
                new PostMethodDemo().execute();
                break;
            /*case R.id.btn_return:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;*/
        }
    }
    public class PostMethodDemo extends AsyncTask<String, Void, String> {
        String server_reponse;
        private Dialog loadingDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show
                    (RegisterActivity.this, "Please wait", "Loading...");
        }

        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://172.20.10.8/projectq/register.php");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                try {
                    JSONObject obj = new JSONObject();
                    //obj.put("name", name);
                    obj.put("username", username);
                    obj.put("email", email);
                    obj.put("cardnumber", cardnumber);
                    obj.put("password", password);

                    writer.write(getPostDataString(obj));

                    Log.e("JSON Input", obj.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_reponse = readStream(urlConnection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

            return server_reponse;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loadingDialog.dismiss();

            Log.e("aaaaaaaaaaaaa", s);
            if (s.matches("cannot register")) {
                Toast.makeText(getApplication(), "No", Toast.LENGTH_SHORT).show();
                txt_username.setError("ชื่อผู้นี้ถูกใช้แล้ว");
                txt_username.requestFocus();
                return;

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Thai Traditional Queue");
                builder.setMessage("ลงทะเบียนสำเร็จ");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "ยืนยัน",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        });

                AlertDialog alert3 = builder.create();
                alert3.show();

            }
        }
    }



        public String getPostDataString(JSONObject params) throws Exception{
            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();
            while (itr.hasNext()){
                String key = itr.next();
                Object value = params.get(key);

                if(first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key,"UTF-8"));
                result.append('=');
                result.append(URLEncoder.encode(value.toString(),"UTF-8"));
            }
            return  result.toString();
        }
        public String readStream(InputStream in){
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();

            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null){
                    response.append(line);
                }
            }catch (IOException e){
                e.printStackTrace();;
            }finally {
                if (reader != null){
                    try {
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return  response.toString();
        }
    public void ClickBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}

