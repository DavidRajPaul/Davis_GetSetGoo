package com.davidpaul.getsetgoo;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class TutorialActivity extends AppCompatActivity {

    protected View view;
    private ImageButton btnNext, btnFinish;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;

    private int[] mImageResources = {
            R.drawable.tut_one,
            R.drawable.tut_two,
            R.drawable.tut_three
    };
    private RelativeLayout container;
    private ViewPager mViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;
    private LinearLayout indicator_layout;
    private RadioButton[] rb;
    private RadioGroup rg;
    private CheckBox[] chk;
    private FloatingActionButton btnContinue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        createRadioButton();
        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager_tutorial);
        btnContinue = (FloatingActionButton) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TutorialActivity.this, RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnContinue.setVisibility(View.GONE);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        mViewPager.setAdapter(mCustomPagerAdapter);
        indicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int last = mImageResources.length - 1;
                if (position == last) {
                    btnContinue.setVisibility(View.VISIBLE);
                } else {
                    btnContinue.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void createRadioButton() {
//        chk = new CheckBox[mImageResources.length];
//        for (int i = 0; i < chk.length; i++) {
//            indicator_layout.addView(chk[i]);
//        }

//        rb = new RadioButton[mImageResources.length];
//        rg = new RadioGroup(this); //create the RadioGroup
//        rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
//        for (int i = 0; i < rb.length; i++) {
//            rb[i] = new RadioButton(this);
//            rb[i].setId(i + 100);
//            rg.addView(rb[i]);
//        }
//        indicator_layout.addView(rg);//you add the whole RadioGroup to the layout
//        rb[0].setChecked(true);
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return mImageResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.tutorial_pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mImageResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }


    }
}

