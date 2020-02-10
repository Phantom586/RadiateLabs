package com.younoq.noq;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SlideFirstFragment extends Fragment {

    public View onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.activity_slide_first_fragment, container, false);

        return v;
    }

    public static SlideFirstFragment newInstance() {

        SlideFirstFragment f = new SlideFirstFragment();
        return f;

    }
}
