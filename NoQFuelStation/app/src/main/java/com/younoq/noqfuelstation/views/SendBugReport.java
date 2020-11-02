package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.models.AwsBackgroundWorker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ExecutionException;

public class SendBugReport extends AppCompatActivity {

    private final String TAG = "SendBugReport";
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bug_report);

        constraintLayout = findViewById(R.id.sbr_constraint_layout);

    }

    public void sendReport(View view) {

        /* Uploading the Logs to the Server */
        try {

            final String res = new AwsBackgroundWorker(this).execute("upload_logs").get();
            Log.d(TAG, "Logs Uploaded to Server : "+res);

            Snackbar snackbar = Snackbar.make(constraintLayout, "Thanks for your valuable input!", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.BLUE));
            snackbar.show();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void go_back(View view) { super.onBackPressed(); }

}