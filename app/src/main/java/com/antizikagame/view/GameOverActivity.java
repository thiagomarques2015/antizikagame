package com.antizikagame.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.antizikagame.R;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView vScore = (TextView) findViewById(R.id.score);
        final int score = getIntent().getIntExtra("score", 0);
        vScore.setText(String.format(getString(R.string.score), score));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(score);
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void share(int score) {
        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_share));

        sendIntent.setType("text/plain");

        sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.text_share), score));
        startActivity(sendIntent);
    }

}
