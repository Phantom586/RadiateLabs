package com.younoq.noq_retailer.models;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.snatik.storage.Storage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utilities {

    private final String TAG = "Utilities";
    private SimpleDateFormat dateFormat;
    private Context context;
    private Storage storage;
    private Logger logger;

    public Utilities(Context ctx) {
        this.context = ctx;
        this.storage = new Storage(this.context);
        this.logger = new Logger(this.context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    public void showTopSnackBar(Context context, CoordinatorLayout coordinatorLayout, String msg, int color) {

        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, color));
        final Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        for (int i = 0; i < snackBarLayout.getChildCount(); i++) {
            View parent = snackBarLayout.getChildAt(i);
            if (parent instanceof LinearLayout) {
                ((LinearLayout) parent).setRotation(180);
                break;
            }
        }
        snackbar.show();

    }

    public boolean isBefore(String s_date, String e_date) {

        final SimpleDateFormat db_date_format = new SimpleDateFormat("yyyy-MM-d HH:mm:ss", Locale.ENGLISH);

        try {

            final Date sDate = db_date_format.parse(s_date);
            final Date eDate = db_date_format.parse(e_date);

            final Calendar csDate = Calendar.getInstance();
            csDate.setTime(sDate);
            final Calendar ceDate = Calendar.getInstance();
            ceDate.setTime(eDate);

            return csDate.before(ceDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

}
