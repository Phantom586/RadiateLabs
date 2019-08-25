package com.example.noq_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import java.util.concurrent.ExecutionException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class BarcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView, mScannerView;
    public static final String RESULT = "com.example.noq_1.RESULT";
    public static String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        scannerView = findViewById(R.id.zxscan);
        mScannerView = new ZXingScannerView(this);
        scannerView.addView(mScannerView);

        Intent in = getIntent();
        type = in.getStringExtra("Type");
        final String TAG = "BarcodeScanner";
        Log.d(TAG, "Barcode Scan Type : "+type);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( checkPermission() ) {

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {

                requestPermission();

            }
        }

    }

    private boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(BarcodeScannerActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);

    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode)
        {
            case REQUEST_CAMERA:
                if (grantResults.length > 0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted){

                        Toast.makeText(this, "Permission Granted, Now you can access camera", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if(shouldShowRequestPermissionRationale(CAMERA)) {

                                displayAlertMessage("You need to allow access for both permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                    }
                                });
                                return;
                            }

                        }
                    }
                }
                break;
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkPermission()) {

                if(scannerView == null) {

                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);

                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {

                requestPermission();

            }
        }

    }


    public void OnDestroy() {

        super.onDestroy();
        scannerView.stopCamera();

    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {

        new AlertDialog.Builder(BarcodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void showAlert(String msg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                scannerView.resumeCameraPreview(BarcodeScannerActivity.this);

            }
        });
        //        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener() {
        //            @Override
        //            public void onClick(DialogInterface dialog, int which) {
        //
        //                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
        //                startActivity(in);
        //
        //            }
        //        });
        builder.setMessage(msg);
        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public void handleResult(Result result) {

        final String scanResult = result.getText();
        final String TAG = "BarcodeScanner";
        Log.d(TAG, "Barcode Scan Result : "+scanResult);

        if (type.equals("Product_Scan")){

            final String type = "verify_product";
            String res = "";
            boolean flag = false;

            try {
                res = new BackgroundWorker(this).execute(type, scanResult).get();
                Log.d(TAG, "Product Scan Result : "+res);
                if(!res.equals("FALSE")){
                    flag = true;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(flag){
                Intent in = new Intent(BarcodeScannerActivity.this, ProductDetails.class);
                in.putExtra(RESULT, res);
                startActivity(in);
            } else {
                showAlert("Product Not Found! Please Try Again");
            }

        } else if (type.equals("Store_Scan")) {

            final String type = "verify_store";
            String res = "";
            boolean flag = false;

            try {
                res = new BackgroundWorker(this).execute(type, scanResult).get();
                Log.d(TAG, "Store Scan Result : "+res);
                if(!res.equals("FALSE")){
                    flag = true;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(flag){
                Intent in = new Intent(BarcodeScannerActivity.this, ShopDetails.class);
                in.putExtra(RESULT, res);
                startActivity(in);
            } else {
                showAlert("No Store Found! Please Try Again");
            }

        }

    }
}