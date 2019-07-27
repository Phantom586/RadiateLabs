package com.example.noq_1;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.nio.charset.StandardCharsets;

public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;
//    AlertDialog alertDialog;

    private JSONObject jsonObject = null;
    private JSONObject jobj = null;

    StringBuilder result = new StringBuilder();

    BackgroundWorker(Context context) {

        this.context = context;

    }

    @Override
    protected String doInBackground(String... params) {

//        String type = params[0];
        String insert_data_url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/insert_data.php";
//        String retrieve_data_url = "http://ec2-13-232-56-100.ap-south-1.compute.amazonaws.com/DB/retrieve_data_test.php";


//        if (type.equals("save_user_details")) {

            try {

                final String full_name = params[0];
                final String email = params[1];
                final String phone = params[2];
                final String ref_no = params[3];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("full_name", "UtF-8") + "=" + URLEncoder.encode(full_name, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("ref_no", "UTF-8") + "=" + URLEncoder.encode(ref_no, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                final String TAG = "Background Worker";
                Log.d(TAG, result.toString());

                //json = result.toString();
                //jsonObject = new JSONObject(result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
        return null;
    }

    @Override
    protected void onPreExecute() {

//        alertDialog = new AlertDialog.Builder(context).create();
//        alertDialog.setTitle("DB Status");

    }

    @Override
    protected void onPostExecute(String result) {

//        alertDialog.setMessage(result);
//        alertDialog.show();

//        listener.onResponseReceive(result);
//        try {
//            jsonObject = new JSONObject(result.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        try {
//            jsonObject = new JSONObject(result);
//            JSONObject user = jsonObject.getJSONObject("user");
//            listener.onResponseReceive(user);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

//    public JSONObject RetrieveData() throws JSONException {
//
//       // boolean error = jsonObject.getBoolean("error");
//
//        //if (!error ) {
//
//            return jsonObject;
//
//        //} else {
//
//           // return jobj;
//        //}
//    }
}
