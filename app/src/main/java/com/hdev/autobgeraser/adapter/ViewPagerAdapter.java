package com.hdev.autobgeraser.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hdev.autobgeraser.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] layouts;

    public void addItem(Context context, int[] layouts) {
        this.context = context;
        this.layouts = layouts;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layouts[position], container, false);
        if (position == 0) {
            TextView signUpLink = (TextView) view.findViewById(R.id.tv_link_signup);
            signUpLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

        ImageView imageScreenshot = (ImageView) view.findViewById(R.id.iv_screenshot);
        setImage(imageScreenshot, position);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = container;
        viewPager.removeView(view);
    }

    //set ImageView
    private void setImage(ImageView imageScreenshot, int position){
        switch (position){
            case 0:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_1)).into(imageScreenshot);
                break;

            case 1:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_2)).into(imageScreenshot);
                break;

            case 2:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_3)).into(imageScreenshot);
                break;

            case 3:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_4)).into(imageScreenshot);
                break;

            case 4:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_5)).into(imageScreenshot);
                break;

            case 5:
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.tutorial_6)).into(imageScreenshot);
                break;
        }

    }
}
