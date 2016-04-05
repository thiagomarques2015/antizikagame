package com.antizikagame.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.antizikagame.R;
import com.antizikagame.control.SoundManager;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView vScore = (TextView) findViewById(R.id.score);
        final int score = getIntent().getIntExtra("score", 0);
        vScore.setText(String.format(getString(R.string.score), score));

        Button vTry = (Button) findViewById(R.id.again);
        vTry.setOnClickListener(this);

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

    @Override
    protected void onResume() {
        super.onResume();

        soundManager = SoundManager.getInstance(this).play(SoundManager.BACKGROUND_GAMEOVER_1).play(SoundManager.BACKGROUND_GAMEOVER_2);
    }

    @Override
    protected void onPause() {
        super.onPause();

        soundManager.pause(SoundManager.BACKGROUND_GAMEOVER_1).pause(SoundManager.BACKGROUND_GAMEOVER_2);
    }

    private void back() {
        Intent intent = new Intent(this, GamePlayActivity.class);
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

    @Override
    public void onClick(View view) {
        onBackPressed();
    }
}
