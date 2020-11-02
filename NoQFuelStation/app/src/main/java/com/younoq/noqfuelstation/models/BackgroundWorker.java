package com.younoq.noqfuelstation.models;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class BackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    StringBuilder result = new StringBuilder();
    SaveInfoLocally saveInfoLocally;
    private Logger logger;
    private static String TAG = "BackgroundActivity";

    public BackgroundWorker(Context context) {

        this.context = context;
        logger = new Logger(context);

    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder result1 = new StringBuilder();
        String type = params[0];

        if (type.equals("send_msg")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/send_message.php";
            Log.d("BackgroundActivity", "Send_SMS in BackgroundActivity");

            try {

                final String msg = params[1];
                final String phone = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode("OTP : "+msg, "UTF-8");
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

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("verify_user")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/verify_user_pp.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UtF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
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

                final String TAG = "MainActivity";
                Log.d(TAG, result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("store_user")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/storeUser_PP.php";

            try {

                final String full_name = params[1];
                final String email = params[2];
                final String phone = params[3];
                final String ref_no = params[4];

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
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("update_ref")) {

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/updateUserReferral_PP.php";
            Log.d("BackgroundActivity", "Update_Ref in BackgroundActivity");

            try {

                final String phone = params[1];
                final String ref_no = params[2];

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                        URLEncoder.encode("referrer", "UTF-8") + "=" + URLEncoder.encode(ref_no, "UTF-8");
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
                Log.d(TAG, "Update_Ref result : "+result.toString());

                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("retrieve_data")) {

            String retrieve_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/retrieveUserData_PP.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("fetchPetrolPumps")) {

            String retrieve_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/fetchPetrolPumps.php";

            try {

                final String city = params[1];
                final String city_area = params[2];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("city", "UTF-8")+"="+URLEncoder.encode(city, "UTF-8") + "&" +
                             URLEncoder.encode("city_area", "UTF-8")+"="+URLEncoder.encode(city_area, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("re-verify_checksum")){

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Paytm/txn_status_api.php";

            try {

                final String order_id = params[1];
                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("o_id", "UtF-8") + "=" + URLEncoder.encode(order_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while ((line = bufferedReader.readLine()) != null) {
                    result1.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                final String TAG = "BackgroundWorker";
                Log.d(TAG, "Re-Verification Results : "+result1.toString());
                return result1.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("Send_Invoice_Msg")){

            saveInfoLocally = new SaveInfoLocally(context);

            /* Retrieving the ShoppingMethod from the SharedPreferences. */
            final String shoppingMethod = "InStore";

            String final_amt;

            final String phone = saveInfoLocally.getPhone();
            final String uname = saveInfoLocally.getUserName();
            final String store_name = saveInfoLocally.getPumpName();
            final String store_addr = saveInfoLocally.getPumpAddress();
            final String time = params[1];
            final_amt = params[2];
            final String r_no = params[3];
            final String tot_retail_price = params[4];
            final String tot_savings = params[5];

            final String[] dt = time.split(" ");
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Invoice Date : "+dt[0]+ " and Time: "+dt[1]);

            List<String> details = new ArrayList<>();
            details.add(uname);
            details.add(store_name);
            details.add(r_no);
            details.add(store_addr);
            details.add(dt[0]);
            details.add(dt[1]);
            details.add(final_amt);
            details.add(tot_retail_price);
            details.add(tot_savings);

            JSONArray jsDetails = new JSONArray(details);
            Log.d(TAG, "Invoice SMS Details : "+jsDetails.toString());
            /* Storing Logs in the Logger. */
            logger.writeLog(TAG, "doInBackground()","Invoice Sms Details : "+jsDetails.toString()+" \n");

            String insert_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/Amazon/sendInvoiceMsg_PP.php";

            try {

                String line = "";

                URL url = new URL(insert_data_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8")+"&"+
                        URLEncoder.encode("msg_details", "UTF-8") + "=" + URLEncoder.encode(jsDetails.toString(), "UTF-8");
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

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("Store_Invoice")) {

            saveInfoLocally = new SaveInfoLocally(context);

            final String shoppingMethod = "InPump";

            final String phone = params[1];
            final String total_mrp = params[2];
            final String total_discount = params[3];
            final String total_amt = params[4];
            final String referral_amt = params[5];
            final String comment = params[6];
            final String txn_id = params[7];
            final String order_id = params[8];
            final String pay_mode = params[9];
            final String total_retail_price = params[10];
            final String total_our_price = params[11];

            List<String> details = new ArrayList<>();
            details.add(txn_id);
            details.add(order_id);
            details.add(pay_mode);
            details.add(phone);
            details.add(saveInfoLocally.getSessionID());
            details.add(saveInfoLocally.getPumpId());
            details.add(total_mrp);
            details.add(total_retail_price);
            details.add(total_our_price);
            details.add(total_discount);
            details.add(referral_amt);
            details.add(total_amt);
            String tot_savings;
            tot_savings = String.valueOf(Double.valueOf(total_discount) + Double.valueOf(referral_amt));
            details.add(tot_savings);
            details.add(comment);
            details.add(shoppingMethod);

            JSONArray jsArray = new JSONArray(details);
            final String TAG = "BackgroundWorker";
            Log.d(TAG, "Invoice Details : "+jsArray.toString());


            String retrieve_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/store_in_invoice.php";

            try {

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("details", "UTF-8")+"="+URLEncoder.encode(jsArray.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("set_logout_flag")) {

            String retrieve_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/setLogoutFlag_PP.php";

            try {

                final String phone = params[1];
                final String flag = params[2];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")+"&"+
                        URLEncoder.encode("flag", "UTF-8")+"="+URLEncoder.encode(flag, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (type.equals("retrieve_last_txns")) {

            String retrieve_data_url = "http://ec2-13-234-120-100.ap-south-1.compute.amazonaws.com/DB/lastFiveTxns_PP.php";

            try {

                final String phone = params[1];

                String line = "";

                URL url = new URL(retrieve_data_url) ;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                while( (line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                httpURLConnection.disconnect();

                return result.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
