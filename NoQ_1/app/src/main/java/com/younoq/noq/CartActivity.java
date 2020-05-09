package com.younoq.noq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    // Declaring Required Variables
    RecyclerView recyclerView;
    ProductAdapter adapter;
    Button payment_btn, scan_product;
    SaveInfoLocally save;
    Bundle txnReceipt;
    ArrayList<String> txnData;
    private String shoppingMethod, category_name, coming_from;

    JSONArray jsonArray;
    JSONObject jobj1, jobj2;
    List<Product> ProductList;
    ImageView im, im1, im2, comm;
    EditText et_comment;
    LayoutInflater li;
    View comm_prompt;
    TextView tv_total_our_price, tv_referral_amt, tv_final_amt, items_qty;
    // ---------------------------- If Referral Enabled -------------------------------
    String ref_bal;
    // ----------------------------- X X X X X X X X X X X -----------------------------
    DBHelper dbHelper;
    private double total_mrp = 0.0;
    private double total_retail_price = 0.0;
    private double total_our_price = 0.0;
    private double total_discount = 0.0;
    private static int item_qty = 0;
    private DecimalFormat df = new DecimalFormat("###.##");
    String txnAmount;
    private String comment = "";
    private static final String TAG = "CartActivity";

    // Function to Delete a Specific Item from the Basket, based on its position in the cart.
    public void removeItem(int position, int id, double our_price, double mrp, int qty, double retail_price, double tot_discount, boolean delete){

        dbHelper = new DBHelper(this);

        // Reducing the MRP(total_mrp * qty) of the Item that is being deleted, from Total_MRP.
        total_mrp -= (mrp * qty);

        //Reducing the Total No. of Items in the Cart
        item_qty -= qty;
        final String tot_items = "("+item_qty+")";
        items_qty.setText(tot_items);

        // Reducing the Total_Retailers_Price (retail_price * qty) of the Item that is being deleted, from Total_Retail_Price.
        total_retail_price -= (retail_price * qty);

        // Reducing the Total_Discount (tot_discount * qty) of the Item that is being deleted, from Total_Discount.
        total_discount -= (tot_discount * qty);

        total_discount = Double.parseDouble(df.format(total_discount));

        // --------------------------------------- If Referral Enabled -------------------------------
        // Current Final_Amount Value.
        String current_final_amt = tv_final_amt.getText().toString();
        current_final_amt = current_final_amt.replace("₹", "");
        // --------------------------------------- X X X X X X X X X X X -----------------------------
        if(total_our_price > 0.0){

            total_our_price -=  (our_price * qty);

            total_our_price = Double.valueOf(df.format(total_our_price));


            final Double value_ref_bal =  Double.valueOf(ref_bal);
            if ( total_our_price > value_ref_bal ) {
                current_final_amt = String.valueOf(total_our_price - value_ref_bal);
            } else {
                current_final_amt = "0";
            }
        } else {
            current_final_amt = "0";
        }
        if(total_our_price == 0.0){
            payment_btn.setVisibility(View.INVISIBLE);
        } else {
            payment_btn.setVisibility(View.VISIBLE);
        }
        final String amt = "₹"+df.format(total_our_price);
        tv_total_our_price.setText(amt);
        // ------------------------------- If Referral Enabled ---------------------------------------
        final String amt1 = "₹"+ df.format(Double.valueOf(current_final_amt));
        tv_final_amt.setText(amt1);
        // -------------------------------- X X X X X X X X X X X -------------------------------------
        // Deleting the Specific Product from the DB.
        Log.d(TAG, "Delete Product : "+delete);
        if(delete){
            dbHelper.DeleteData_by_id(id);
            // Removing the Product from the ProductList.
            ProductList.remove(position);
            // Notifying the Adapter of the Change.
            adapter.notifyItemRemoved(position);
            // Creating an Instance of the DB.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ProductList = new ArrayList<>();
        dbHelper = new DBHelper(this);
        save = new SaveInfoLocally(this);

        txnReceipt = new Bundle();
        txnData = new ArrayList<>();

        tv_total_our_price = findViewById(R.id.ca_total_our_price);
        tv_referral_amt = findViewById(R.id.ca_referral_amt);
        tv_final_amt = findViewById(R.id.ca_final_amt);
        payment_btn = findViewById(R.id.btn_payment);
        scan_product = findViewById(R.id.ac_scan_product);
        items_qty = findViewById(R.id.ca_item_qty);
//        comm = findViewById(R.id.ca_comm);
//
        recyclerView = findViewById(R.id.ac_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent in = getIntent();
        shoppingMethod = in.getStringExtra("shoppingMethod");
        if(shoppingMethod.equals("Takeaway")){
            coming_from = in.getStringExtra("comingFrom");
            category_name = in.getStringExtra("category_name");
            scan_product.setText(R.string.ca_add_more);
        }

        RetrieveFromDatabase();

        final String TAG = "CartActivity";

        // If any product is Deleted from the Cart, to retrieve the details of the deleted item.
        adapter.setOnItemClickListener(new ProductAdapter.onItemClickListener() {
            @Override
            public void onDeleteClick(int position, int id, double our_price, double mrp, int qty, double retail_price, double tot_discount, boolean delete) {
                Toast.makeText(CartActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Position : "+position+" Id : "+id+" Price : "+our_price);
                removeItem(position, id, our_price, mrp, qty, retail_price, tot_discount, delete);
//                RetrieveFromDatabase();
            }

            @Override
            public void onIncreaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount) {
                increaseQuantity(position, id, mrp, our_price, retail_price, discount);
            }

            @Override
            public void onDecreaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount, boolean delete) {
                decreaseQuantity(position, id, mrp, our_price, retail_price, discount, delete);
            }
        });

        // if User clicks on Scan_Product Button in UI.
        scan_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in;
                if (shoppingMethod.equals("In Store")) {
                    in = new Intent(CartActivity.this, BarcodeScannerActivity.class);
                    in.putExtra("Type", "Product_Scan");
                } else if(shoppingMethod.equals("Takeaway")){
                    in = new Intent(CartActivity.this, ProductsList.class);
                    in.putExtra("coming_from", "Cart");
                    in.putExtra("shoppingMethod", shoppingMethod);
                    in.putExtra("category_name", category_name);
                } else {
                    in = new Intent();
                }
                startActivity(in);
            }
        });

        // ------------------------------- If Referral Enabled -----------------------------------------
        // Fetch the User's Referral Balance
        fetch_referral_amt();
        // -------------------------------- X X X X X X X X X X X ---------------------------------------

    }

    private void increaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount) {

        // Initializing DBHelper
        dbHelper = new DBHelper(this);

        final String details = "Before -> pos : "+position+", id : "+id+", mrp : "+mrp+", our_price : "+our_price+", retail_price : "+retail_price;
        final String amt2 = "Total_our_Price : "+total_our_price+", Total_MRP : "+total_mrp;
        Log.d(TAG, details);
        Log.d(TAG, amt2);

        final int qty = 1;

        // Increasing the MRP(total_mrp * qty) for the Selected Item.
        total_mrp += (mrp * qty);

        //Increasing the Total No. of Items in the Cart
        item_qty += qty;
        final String tot_items = "("+item_qty+")";
        items_qty.setText(tot_items);

        // Increasing the Total_Retailers_Price (retail_price * qty) of the Item.
        total_retail_price += (retail_price * qty);

        // Increasing the Total_Discount (tot_discount * qty) of the Item.
        total_discount += (discount * qty);

        total_discount = Double.parseDouble(df.format(total_discount));

        // --------------------------------------- If Referral Enabled -------------------------------
        // Current Final_Amount Value.
        String current_final_amt = tv_final_amt.getText().toString();
        current_final_amt = current_final_amt.replace("₹", "");
        // --------------------------------------- X X X X X X X X X X X -----------------------------
        total_our_price +=  our_price;

        total_our_price = Double.valueOf(df.format(total_our_price));

        final Double value_ref_bal =  Double.valueOf(ref_bal);

        if ( total_our_price > value_ref_bal ) {
            current_final_amt = String.valueOf(total_our_price - value_ref_bal);
        } else {
            current_final_amt = "0";
        }

        final String amt = "₹"+df.format(total_our_price);
        tv_total_our_price.setText(amt);
        // ------------------------------- If Referral Enabled ---------------------------------------
        final String amt1 = "₹"+ df.format(Double.valueOf(current_final_amt));
        tv_final_amt.setText(amt1);
        // -------------------------------- X X X X X X X X X X X -------------------------------------
        final String details1 = "After -> pos : "+position+", id : "+id+", mrp : "+mrp+", our_price : "+our_price+", retail_price : "+retail_price;
        final String amt3 = "Total_our_Price : "+total_our_price+", Total_MRP : "+total_mrp;
        Log.d(TAG, details1);
        Log.d(TAG, amt3);

    }

    private void decreaseQuantity(int position, int id, double mrp, double our_price, double retail_price, double discount, boolean delete) {

        // Initializing DBHelper
        dbHelper = new DBHelper(this);

        final int qty = 1;

        // Reducing/Adding the MRP(total_mrp * qty) for the Selected Item, from Total_MRP.
        total_mrp -= (mrp * qty);

        //Reducing the Total No. of Items in the Cart
        item_qty -= qty;
        final String tot_items = "("+item_qty+")";
        items_qty.setText(tot_items);

        // Reducing the Total_Retailers_Price (retail_price * qty) of the Item that is being deleted, from Total_Retail_Price.
        total_retail_price -= (retail_price * qty);

        // Reducing the Total_Discount (tot_discount * qty) of the Item that is being deleted, from Total_Discount.
        total_discount -= (discount * qty);

        total_discount = Double.parseDouble(df.format(total_discount));

        // --------------------------------------- If Referral Enabled -------------------------------
        // Current Final_Amount Value.
        String current_final_amt = tv_final_amt.getText().toString();
        current_final_amt = current_final_amt.replace("₹", "");
        // --------------------------------------- X X X X X X X X X X X -----------------------------
        if(total_our_price > 0.0){

            total_our_price -=  our_price;

            total_our_price = Double.valueOf(df.format(total_our_price));

            final Double value_ref_bal =  Double.valueOf(ref_bal);
            if ( total_our_price > value_ref_bal ) {
                current_final_amt = String.valueOf(total_our_price - value_ref_bal);
            } else {
                current_final_amt = "0";
            }
        } else {
            current_final_amt = "0";
        }
        if(total_our_price == 0.0){
            payment_btn.setVisibility(View.INVISIBLE);
        } else {
            payment_btn.setVisibility(View.VISIBLE);
        }
        final String amt = "₹"+df.format(total_our_price);
        tv_total_our_price.setText(amt);
        // ------------------------------- If Referral Enabled ---------------------------------------
        final String amt1 = "₹"+ df.format(Double.valueOf(current_final_amt));
        tv_final_amt.setText(amt1);
        // -------------------------------- X X X X X X X X X X X -------------------------------------
        // Deleting the Specific Product from the DB.
        Log.d(TAG, "Delete Product : "+delete);
        if(delete){
            dbHelper.DeleteData_by_id(id);
            // Removing the Product from the ProductList.
            ProductList.remove(position);
            // Notifying the Adapter of the Change.
            adapter.notifyItemRemoved(position);
            // Creating an Instance of the DB.
            dbHelper.close();
        }

    }

    // If User clicks on the comment Icon
    public void showCommentDialog(View view) {

        // Due to Layout error..
        li = LayoutInflater.from(this);
        comm_prompt = li.inflate(R.layout.prompt_comment, null);
        et_comment = comm_prompt.findViewById(R.id.ca_comment);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Comments")
                .setMessage("Enter your Comments for this Purchase")
                .setCancelable(false)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comment = et_comment.getText().toString();
                        Log.d(TAG, "Comment in onCreate : "+comment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(comm_prompt)
                .show();
    }

    private void RetrieveFromDatabase(){

        // For Retrieving the Products from the SqliteDB and Creating an Instance of Product class.
        Cursor res = dbHelper.retrieveData();
        if(res.getCount() == 0){
            Toast.makeText(this, "No Products Added Yet..", Toast.LENGTH_SHORT).show();
        } else {
            while(res.moveToNext()){

                // Retrieving Store_ID of the Product from Database
                final String store_ID = res.getString(1);
                Log.d(TAG, "Product Store ID : "+store_ID);
                // Retrieving the Current Store_ID form SharedPreferences.
                final String curr_Store_ID = save.get_store_id();
                Log.d(TAG, "Current Store ID : "+curr_Store_ID);

                if (store_ID.equals(curr_Store_ID)) {

                    // Retrieving the Total_Our_Price from the Database for all the Entries.
                    total_our_price += Double.parseDouble(res.getString(6));
                    // Retrieving No.of Items for each Item from the Database.
                    final double qty = Double.parseDouble(res.getString(3));
                    // Setting the Total No. of Items in the Database
                    item_qty += qty;
    //                Log.d(TAG, "Quantity : "+qty);
                    // Retrieving the (No.of Items * Total_MRP) from the Database for all the Entries.
                    total_mrp += qty * Double.parseDouble(res.getString(5));
    //                Log.d(TAG, "Total MRP : "+total_mrp);
                    // Retrieving the (No.of Items * Total_Retailers_Price) from the Database for all the Entries.
                    total_retail_price += qty * Double.parseDouble(res.getString(7));
    //                Log.d(TAG, "Total Retail Price : "+total_retail_price);
                    // Retrieving the Total Discount for all items in the Database.
                    total_discount += qty * Double.parseDouble(res.getString(9));
//                   Log.d(TAG, "Total Discount : "+total_discount);
                    // Only add those products which belong to the Current Store ID.

                    ProductList.add(
                            new Product(
                                    res.getInt(0),
                                    store_ID,
                                    res.getString(2),
                                    res.getString(4),
                                    res.getString(5),
                                    res.getString(6),
                                    res.getString(7),
                                    res.getString(8),
                                    res.getString(9),
                                    res.getString(3),
                                    res.getString(10),
                                    res.getString(11),
                                    ""
                            )
                    );

                }
            }
        }

        res.close();

        // If Total_Amount == 0, then Hide the Checkout Button.
        if(total_our_price == 0.0){
            payment_btn.setVisibility(View.INVISIBLE);
        } else {
            payment_btn.setVisibility(View.VISIBLE);
        }

        final String amt = "₹"+df.format(total_our_price);
        tv_total_our_price.setText(amt);
        final String tot_items = "("+item_qty+")";
        items_qty.setText(tot_items);

        adapter = new ProductAdapter(this, ProductList);
        recyclerView.setAdapter(adapter);

    }

    public void fetch_referral_amt(){

        String tmp;
        double f_amt = 0;
        ref_bal = save.getReferralBalance();
        Log.d(TAG, "Referral Amount Balance : "+ref_bal);
        tmp = "- ₹"+ref_bal;
        tv_referral_amt.setText(tmp);
        final String str = tv_total_our_price.getText().toString();
        String fin = str.replace("₹", "");
        if (Double.valueOf(fin) > Double.valueOf(ref_bal)) {
            f_amt =  Double.valueOf(fin) - Double.valueOf(ref_bal);
        }

        tmp = "₹"+df.format(f_amt);
        tv_final_amt.setText(tmp);

    }

//    public void Go_to_BarcodeScanner(View view) {
//        Intent in = new Intent(CartActivity.this, BarcodeScannerActivity.class);
//        in.putExtra("Type", "Product_Scan");
//        startActivity(in);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        total_our_price = 0.0;
        item_qty = 0;
    }

    @Override
    public void onBackPressed() {
        Intent in;
        if(shoppingMethod.equals("In Store")){
            in  = new Intent(CartActivity.this, BarcodeScannerActivity.class);
            in.putExtra("Type", "Product_Scan");
            in.putExtra("shoppingMethod", shoppingMethod);
        } else if(shoppingMethod.equals("Takeaway")){
            // If coming from Products Category Screen then, go back there.
            if(coming_from.equals("ProductCategory")){
                in  = new Intent(CartActivity.this, ProductsCategory.class);
            } else {
                in  = new Intent(CartActivity.this, ProductsList.class);
                in.putExtra("coming_from", "Cart");
                in.putExtra("category_name", category_name);
            }
            in.putExtra("shoppingMethod", shoppingMethod);
        } else{
            in = new Intent();
        }
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }

    public void Go_Back(View view) {

        Intent in;
        if(shoppingMethod.equals("In Store")){
            in  = new Intent(CartActivity.this, BarcodeScannerActivity.class);
            in.putExtra("Type", "Product_Scan");
        } else if(shoppingMethod.equals("Takeaway")){
            if(coming_from.equals("ProductCategory")){
                in  = new Intent(CartActivity.this, ProductsCategory.class);
            } else {
                in  = new Intent(CartActivity.this, ProductsList.class);
                in.putExtra("coming_from", "Cart");
                in.putExtra("category_name", category_name);
            }
        } else{
            in = new Intent();
        }
        in.putExtra("shoppingMethod", shoppingMethod);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

    }

    private String generateTxn_Order() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    public void Make_Payment(View view) {

        // ------------------------------- If Referral Disabled ----------------------------------------

        //generateCheckSum();

        // -------------------------------- X X X X X X X X X X X ---------------------------------------

        // ------------------------------- If Referral Enabled ------------------------------------------
        Double a, b;
        String f_amt  = tv_final_amt.getText().toString();
        f_amt = f_amt.replace("₹", "");
        // Value of Final_Amount
        a = Double.valueOf(f_amt);
        // Value of Referral_Balance
        b = Double.valueOf(ref_bal);

        if ( a > 0) {
            // If Total_amount is Greater then Referral_Balance, then Proceed to Payment from Paytm.
            generateCheckSum();
        } else {
            // else go to Payment_Successful Page.
            String ref_bal_used = tv_total_our_price.getText().toString();
            ref_bal_used = ref_bal_used.replace("₹", "");
            // Calculating the Referral_balance to be Stored in SharedPreference.
            final Double cal_ref_bal = b - Double.valueOf(ref_bal_used);
            Log.d(TAG, "Updated Referral Amount : "+cal_ref_bal);
            // Setting the Updated Referral_Balance to SharedPreferences.
            save.setReferralBalance(String.valueOf(cal_ref_bal));
            // Setting txnAmount's value to final_amt.
            txnAmount = f_amt;
            // Doing all the things to be done after Successful Payment(which is already done here :-)..)
            afterPaymentConfirm(ref_bal_used, generateTxn_Order(), generateTxn_Order(), "[Referral_Used]");
            // Redirect to Payment Successful Page.
            Log.d(TAG, "Sending the User to PaymentSuccess Activity");
            Intent in = new Intent(this, PaymentSuccess.class);
            in.putExtra("referral_balance_used", ref_bal_used);
            in.putExtras(txnReceipt);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
        }
        // -------------------------------- X X X X X X X X X X X ---------------------------------------

    }

    public void afterPaymentConfirm(String ref_bal_used, String Txn_ID, String Order_ID, String Pay_Mode) {

        // Retrieving the User_Phone_NO from SharedPreferences.
        final String user_phone_no = save.getPhone();

        try {
            // Now Inserting the Products list into the Basket_Table.
            final String type = "Store_Basket";
            final String res = new BackgroundWorker(this).execute(type, user_phone_no).get();
            // Now Inserting the Transaction Details into the Invoice_Table.
            final String type3 = "Store_Invoice";
            // Converting the required values to String
            final String tot_retailer_price = String.valueOf(total_retail_price);
            final String tot_mrp = String.valueOf(total_mrp);

            String tot_our_price = tv_total_our_price.getText().toString();
            tot_our_price = tot_our_price.replace("₹", "");
            Log.d(TAG, "Total_Our_Price for Storing in Invoice : "+tot_our_price);

            String rest = new BackgroundWorker(this).execute(type3, user_phone_no, tot_mrp, String.valueOf(total_discount), txnAmount, ref_bal_used, comment, Txn_ID, Order_ID, Pay_Mode, tot_retailer_price, tot_our_price).get();
            Log.d(TAG, "Invoice Result : " + rest);

            // Verifying if the Push to Basket_Table was Successful or not.
            boolean b = Boolean.parseBoolean(res.trim());
            if (b) {

                // Verifying if the Push to Invoice Table was Successful or not.
                rest = rest.trim();
                if (!rest.equals("FALSE")) {
                    // Retrieve the details from the result of the Invoice Push.
                    String receipt_no = "";
                    String final_user_amt = "";
                    String tot_retail_price = "";
                    String to_our_price = "";
                    String tot_discount = "";
                    String time = "";
                    String comment = "";
                    try {
                        jsonArray = new JSONArray(rest);
                        jobj1 = jsonArray.getJSONObject(0);
                        if (!jobj1.getBoolean("error")) {
                            jobj2 = jsonArray.getJSONObject(1);
                            receipt_no = jobj2.getString("r_no");
                            final_user_amt = jobj2.getString("final_amt");
                            tot_retail_price = jobj2.getString("tot_retail_price");
                            to_our_price = jobj2.getString("tot_our_price");
                            tot_discount = jobj2.getString("tot_discount");
                            time = jobj2.getString("time");
                            comment = jobj2.getString("comment");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // If Invoice is Successfully Pushed to DB, then Send the Invoice SMS to the user.
                    final String type4 = "Send_Invoice_Msg";
                    final String sms_res = new BackgroundWorker(this).execute(type4, time, final_user_amt, comment, receipt_no, tot_retail_price, ref_bal_used, tot_discount, to_our_price).get();

                    // Storing the Details in txnData ArrayList.
                    txnData.add(receipt_no);
                    txnData.add(tot_discount);
                    txnData.add(tot_retail_price);
                    txnData.add(ref_bal_used);
                    txnData.add(to_our_price);
                    txnData.add(final_user_amt);
                    txnData.add(time);
                    txnData.add(Pay_Mode);
                    txnData.add(String.valueOf(item_qty));
                    // Adding the txnData ArrayList to txnReceipt Bundle.
                    txnReceipt.putStringArrayList("txnReceipt", txnData);
                    Log.d(TAG, "Stored Required Details in Bundle");
                    // Sending an Email to our official Account containing this Invoice Details.
                    // Currently Not Working.
//                    final String type5 = "Send_Invoice_Mail";
//                    final String email_res = new AwsBackgroundWorker(this).execute(type5, time, final_user_amt, comment, receipt_no).get();
//                    Log.d(TAG, "AWS_SES Response : " + email_res);
                }
//                dbHelper = new DBHelper(this);
//                // Now after the Re-Verification of Payment, Deleting all the Products Stored in the DB.
//                dbHelper.Delete_all_rows();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    private void generateCheckSum() {

        //getting the amount to be paid by the User, from UI.
        final String str = tv_final_amt.getText().toString();
        txnAmount = str.replace("₹", "");

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                PConstants.M_ID,
                PConstants.CHANNEL_ID,
                txnAmount,
                PConstants.WEBSITE,
                PConstants.CALLBACK_URL,
                PConstants.INDUSTRY_TYPE_ID
        );

        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();

        //creating a call object from the apiService
        Call<PChecksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                c_url,
                paytm.getIndustryTypeId()
        );

        Log.d(TAG, "Paytm CustomerId : "+paytm.getCustId());
        Log.d(TAG, "Paytm OrderId : "+paytm.getOrderId());

        //making the call to generate checksum
        call.enqueue(new Callback<PChecksum>() {
            @Override
            public void onResponse(Call<PChecksum> call, Response<PChecksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<PChecksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service for Staging.
//        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production.
        PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", PConstants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());
        final String c_url = paytm.getCallBackUrl() + paytm.getOrderId();
        Log.d(TAG, "Callback URL : "+c_url);
        paramMap.put("CALLBACK_URL", c_url);
        paramMap.put("CHECKSUMHASH", checksumHash);


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    //all these overriden method is to detect the payment result accordingly
    @Override
    public void onTransactionResponse(Bundle bundle) {

//        final String user_phone_no = save.getPhone();
        Log.d(TAG, "From onTransactionResponse : "+bundle.toString());
        try {
            // Retrieving the Txn_Details from the TxnResponse i.e., bundle.
            final String txn_status = bundle.get("STATUS").toString();
            final String order_id = bundle.get("ORDERID").toString();
            final String Txn_ID = bundle.get("TXNID").toString();
            final String Order_ID = bundle.get("ORDERID").toString();
            final String Pay_Mode = bundle.get("PAYMENTMODE").toString();
            // Verifying if the Transaction was Successful or not.
            if (txn_status.equals("TXN_SUCCESS")) {

                // Forwarding the Order_id to the Server for the Re-Verification Process.
                final String type1 = "re-verify_checksum";
                final String res1 = new BackgroundWorker(this).execute(type1, order_id).get();

                // Converting the result String in form of JSONObject from the response to JSONObject.
                JSONObject jobj = new JSONObject(res1);
                // Extracting the required value from JSONObject.
                final String status = jobj.getString("STATUS");
                Log.d(TAG, "Re-verify Status : " + status);

                // Re-Verifying if the Transaction was Successful or not.
                if (status.equals("TXN_SUCCESS")) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
                    // Doing all the things to be done after Successful Payment.
                    afterPaymentConfirm(ref_bal, Txn_ID, Order_ID, Pay_Mode);
                    // Saving the Referral_Balance Value to SharedPreference.
                    save.setReferralBalance("0");
                    // Intent to PaymentSuccess Activity.
                    Intent in = new Intent(this, PaymentSuccess.class);
                    in.putExtra("referral_balance_used", ref_bal);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                } else {
                    // If the Re-verification of the Txn Fails.
                    Toast.makeText(this, "Payment Verification Failed.", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(CartActivity.this, PaymentFailed.class);
                    in.putExtras(txnReceipt);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                }

            } else {
                // If the Txn Fails.
                Toast.makeText(this, "Payment Failed.", Toast.LENGTH_LONG).show();
                Intent in = new Intent(CartActivity.this, PaymentFailed.class);
                in.putExtras(txnReceipt);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
//        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();Intent in;
        Intent in;
        if(shoppingMethod.equals("In Store")){
            in  = new Intent(this, CartActivity.class);
            in.putExtra("shoppingMethod", shoppingMethod);
        } else if(shoppingMethod.equals("Takeaway")){
            in  = new Intent(this, CartActivity.class);
            in.putExtra("comingFrom", "Cart");
            in.putExtra("shoppingMethod", shoppingMethod);
            in.putExtra("category_name", category_name);
        } else{
            in = new Intent();
        }
//        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);

//        Intent in = new Intent(this, CartActivity.class);
//        in.putExtra("shoppingMethod", shoppingMethod);
//        in.putExtra("category_name", category_name);
//        startActivity(in);
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.d(TAG, "Transaction Failed : "+bundle.toString());
//        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
