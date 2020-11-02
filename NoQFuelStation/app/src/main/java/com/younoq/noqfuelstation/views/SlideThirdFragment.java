package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import com.younoq.noqfuelstation.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SlideThirdFragment extends AppCompatActivity {

    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.activity_slide_third_fragment, container, false);

        return v;
    }

    public static SlideThirdFragment newInstance() {

        SlideThirdFragment f = new SlideThirdFragment();
        return f;

    }

}