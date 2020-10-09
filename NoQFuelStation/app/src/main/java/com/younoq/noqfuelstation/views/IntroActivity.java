package com.younoq.noqfuelstation.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.younoq.noqfuelstation.R;
import com.younoq.noqfuelstation.adapters.MyPageFragmentAdapter;
import com.younoq.noqfuelstation.models.Logger;
import com.younoq.noqfuelstation.models.SaveInfoLocally;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on Sept 18, 2020.
 */

public class IntroActivity extends AppCompatActivity {

    private SaveInfoLocally saveInfoLocally;
    final String TAG = "IntroActivity";
    private TabLayout tabIndicator;
    private ViewPager viewPager;
    private ImageView next_btn;
    private Button getStarted;
    private Animation btnAnim;
    private Logger logger;
    private int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveInfoLocally = new SaveInfoLocally(this);
        logger = new Logger(this);

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()", "User opened the App\n");

        // Storing the Logs in the Logger.
        logger.writeLog(TAG, "onCreate()","Checking isFirstLogin Status : "+ saveInfoLocally.isFirstLogin() +", and hasFinishedIntro : "+ saveInfoLocally.hasFinishedIntro() +"\n");

        if(!saveInfoLocally.isFirstLogin() || saveInfoLocally.hasFinishedIntro()) {

            // Storing the Logs in the Logger.
            logger.writeLog(TAG, "onCreate()", "Not First Login, hence Routing to MainActivity \n");

            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(in);
            finish();

        }

        Log.d("IntroActivity", "not Found");

        setContentView(R.layout.activity_intro);

        next_btn = findViewById(R.id.ia_next_btn);
        tabIndicator = findViewById(R.id.tabLayout);
        getStarted = findViewById(R.id.ia_btn_get_started);
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_animation);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPageFragmentAdapter(this));
        tabIndicator.setupWithViewPager(viewPager);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos = viewPager.getCurrentItem();
                if (pos < 2){
                    pos++;
                    viewPager.setCurrentItem(pos);
                }

                if (pos == 2) {
                    // After this we will be in the last Screen.
                    prepareLastScreen();
                }

            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 2) {
                    prepareLastScreen();
                } else {
                    showOptions();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Setting the Flag as True.
                saveInfoLocally.setHasFinishedIntro();

                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
                finish();

            }
        });

    }

    private void showOptions() {

        next_btn.setVisibility(View.VISIBLE);
        getStarted.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);

    }

    private void prepareLastScreen() {

        next_btn.setVisibility(View.INVISIBLE);
        getStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        getStarted.setAnimation(btnAnim);

    }

}