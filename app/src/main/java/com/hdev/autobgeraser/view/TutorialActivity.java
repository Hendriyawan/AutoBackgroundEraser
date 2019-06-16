package com.hdev.autobgeraser.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hdev.autobgeraser.R;
import com.hdev.autobgeraser.adapter.ViewPagerAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TutorialActivity extends AppCompatActivity {
    @BindView(R.id.vp_slider)
    ViewPager vpSlider;
    @BindView(R.id.indicator)
    TabLayout tabIndicator;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.button_skip)
    Button buttonSkip;
    @BindString(R.string.tutorial)
    String toolbarTitle;
    @BindString(R.string.text_button_skip)
    String textSkip;
    @BindString(R.string.text_button_next)
    String textNext;
    @BindString(R.string.text_button_done)
    String textDone;
    private ViewPagerAdapter sliderAdapter;
    private int[] layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        layout = new int[]{
                R.layout.tutorial_1,
                R.layout.tutorial_2,
                R.layout.tutorial_3,
                R.layout.tutorial_4,
                R.layout.tutorial_5
        };
        initToolbar();
        initViewPager(layout);
    }

    @OnClick(R.id.button_skip)
    public void onSkip() {
        vpSlider.setCurrentItem(layout.length - 1);

    }

    @OnClick(R.id.button_next)
    public void onNext() {
        int current = getItem(+1);
        if (current < layout.length) {
            vpSlider.setCurrentItem(current);

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    //init Toolbar
    private void initToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(toolbarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //init ViewPager
    private void initViewPager(final int[] layout) {
        sliderAdapter = new ViewPagerAdapter();
        sliderAdapter.addItem(this, layout);
        vpSlider.setAdapter(sliderAdapter);
        tabIndicator.setupWithViewPager(vpSlider);

        vpSlider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == layout.length - 1) {
                    buttonNext.setText(textDone);
                    buttonSkip.setVisibility(View.GONE);

                } else {
                    buttonNext.setText(textNext);
                    buttonSkip.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    //get item / position of ViewPager
    private int getItem(int item) {
        return vpSlider.getCurrentItem() + item;
    }
}
