package com.davidpaul.getsetgoo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 300;
    private RelativeLayout rlContainer;
    private View view;
    private PreferenceHelper preferenceHelper;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        view = (View) findViewById(R.id.view);
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext(), "ReceiverPrefs");
        userName = preferenceHelper.getData("_keyUserName");
        Log.e("userName--", "userName" + userName);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                hide(view);
//                }
//                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(i);
//                // close this activity
//                finish();
            }
        }, SPLASH_TIME_OUT);
        new UtilityClass(this).setViewGroupTypeface(rlContainer, UtilityClass.fontItalic);
    }

    private void hide(final View view) {

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth();

        // create the animation (the final radius is zero)

        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                    initialRadius, 0);
            anim.setDuration(1200);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!userName.equals("")) {
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(SplashActivity.this, TutorialActivity.class);
                                startActivity(i);
//                                Intent i = new Intent(SplashActivity.this, RegistrationActivity.class);
//                                startActivity(i);
                            }

                            // close this activity
                            finish();

                        }
                    }, 500);

                }
            });

            // start the animation
            anim.start();
        } else {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            animation.setDuration(1200);
            view.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!userName.equals("")) {
                                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(SplashActivity.this, RegistrationActivity.class);
                                startActivity(i);
                            }
                            finish();

                        }
                    }, 500);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.start();
        }


    }
}
