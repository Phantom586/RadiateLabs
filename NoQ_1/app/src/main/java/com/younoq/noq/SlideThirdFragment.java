package com.younoq.noq;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SlideThirdFragment extends Fragment {

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
