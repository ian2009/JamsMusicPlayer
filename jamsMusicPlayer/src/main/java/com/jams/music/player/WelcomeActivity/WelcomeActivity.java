/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jams.music.player.WelcomeActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.jams.music.player.R;
import com.jams.music.player.MiscFragments.BuildingLibraryProgressFragment;
import com.jams.music.player.Services.BuildMusicLibraryService;
import com.viewpagerindicator.LinePageIndicator;

public class WelcomeActivity extends FragmentActivity {

    private Context mContext;
    private ViewPager welcomeViewPager;
    private LinePageIndicator indicator;
    public static BuildingLibraryProgressFragment mBuildingLibraryProgressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        setContentView(R.layout.activity_welcome);
        setTheme(R.style.AppThemeLight);

        if (getActionBar() != null) {
            getActionBar().hide();
        }

        welcomeViewPager = (ViewPager) findViewById(R.id.welcome_pager);

        FragmentManager fm = getSupportFragmentManager();
        welcomeViewPager.setAdapter(new WelcomePagerAdapter(fm));
        welcomeViewPager.setOffscreenPageLimit(2);
        indicator = (LinePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(welcomeViewPager);

        final float density = getResources().getDisplayMetrics().density;
        indicator.setSelectedColor(0x880099CC);
        indicator.setUnselectedColor(0xFF4F4F4F);
        indicator.setStrokeWidth(2 * density);
        indicator.setLineWidth(30 * density);
        indicator.setOnPageChangeListener(pageChangeListener);

        //Check if the library needs to be rebuilt and this isn't the first run.
        if (getIntent().hasExtra("REFRESH_MUSIC_LIBRARY")) {
            showBuildingLibraryProgress();
        }
    }

    /**
     * Page scroll listener.
     */
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int scrollState) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int page) {
            if (1 == page) {
                showBuildingLibraryProgress();
            }
        }
    };

    private void showBuildingLibraryProgress() {
        //Disables swiping events on the pager.
        welcomeViewPager.setCurrentItem(1);
        welcomeViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });

        //Fade out the ViewPager indicator.
        Animation fadeOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        fadeOutAnim.setDuration(600);
        fadeOutAnim.setAnimationListener(fadeOutListener);
        indicator.startAnimation(fadeOutAnim);
    }

    /**
     * Fade out animation listener.
     */
    private AnimationListener fadeOutListener = new AnimationListener() {

        @Override
        public void onAnimationEnd(Animation arg0) {
            indicator.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(mContext, BuildMusicLibraryService.class);
            startService(intent);
        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationStart(Animation arg0) {
        }
    };

    class WelcomePagerAdapter extends FragmentStatePagerAdapter {

        public WelcomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //This method controls which fragment should be shown on a specific screen.
        @Override
        public Fragment getItem(int position) {
            //Assign the appropriate screen to the fragment object, based on which screen is displayed.
            switch (position) {
                case 0:
                    return new WelcomeFragment();
                case 1:
                    mBuildingLibraryProgressFragment = new BuildingLibraryProgressFragment();
                    return mBuildingLibraryProgressFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

}
