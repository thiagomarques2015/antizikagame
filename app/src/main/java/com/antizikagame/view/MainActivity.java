package com.antizikagame.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.antizikagame.R;
import com.antizikagame.control.TutoAdpater;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private int last;
    private Button vStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new ImageFragment(R.drawable.tuto_1));
        fragments.add(new ImageFragment(R.drawable.tuto_2));
        fragments.add(new ImageFragment(R.drawable.tuto_3));

        last = fragments.size()-1;

        TutoAdpater adapter = new TutoAdpater(getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(adapter);

        vStart = (Button) findViewById(R.id.start);

        //Bind the title indicator to the adapter
        CircleIndicator pagination = (CircleIndicator) findViewById(R.id.pagination);
        pagination.setViewPager(pager);
        pagination.setOnPageChangeListener(this);
        vStart.setOnClickListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel){

    }

    @Override
    public void onPageSelected(int position) {
        if(position == last){
            vStart.setVisibility(View.VISIBLE);
        }else{
            vStart.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        if(view.getVisibility() != View.VISIBLE) return;

        Intent intent = new Intent(this, GamePlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
