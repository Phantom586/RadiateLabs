package com.younoq.noq;

import android.content.Context;
import android.os.AsyncTask;

public class AwsBackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    StringBuilder result = new StringBuilder();
    DBHelper db;
    SaveInfoLocally saveInfoLocally;

    AwsBackgroundWorker(Context context) {

        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
