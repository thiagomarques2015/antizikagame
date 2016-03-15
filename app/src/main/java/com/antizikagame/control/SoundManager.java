package com.antizikagame.control;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.antizikagame.R;

/**
 * Gerenciador de sons
 * Created by ACER on 09/08/2015.
 */
public class SoundManager {

    private boolean debug = false;

    public static final int BACKGROUND = 1;
    public static final int VOICE = 2;
    public static final int BACKGROUND_GAMEOVER_1 = 3;
    public static final int BACKGROUND_GAMEOVER_2 = 4;
    public static final int HIT = 5;

    private static SoundManager instance;
    private final Context context;
    private MediaPlayer background;
    private MediaPlayer voice;
    private MediaPlayer backgroundGameOver1;
    private MediaPlayer backgroundGameOver2;
    private MediaPlayer hit;
    private int sound;

    public SoundManager(Context context) {
        this.context = context;
    }

    public static SoundManager getInstance(Context context) {
        if(instance == null)
            instance = new SoundManager(context);
        return instance;
    }

    public SoundManager initialize(){
        if(background != null) return this;
        background = create(R.raw.background, true);
        voice = create(R.raw.voice, false);
        backgroundGameOver1 = create(R.raw.background_gameover_1, true);
        backgroundGameOver2 = create(R.raw.background_gameover_2, true);
        hit = create(R.raw.hit, false);
        return this;
    }

    public SoundManager play(int sound){
        this.sound = sound;
        if(debug) return this;

        MediaPlayer player = getAudio();

        if(player != null){
            Log.d("Sound", "=> Tocando o som " + sound);
            player.start(); // no need to call prepare(); create() does that for you
        }else{
            Log.d("Sound", "Nao foi possivel iniciar o som");
        }

        return this;
    }



    public SoundManager pause(int sound){
        this.sound = sound;
        if(debug) return this;

        MediaPlayer player = getAudio();

        if(player != null && player.isPlaying()){
            player.pause(); // no need to call prepare(); create() does that for you
            player.seekTo(0);
        }

        return this;
    }

    private MediaPlayer create(int resource, boolean looping){
        MediaPlayer sound = MediaPlayer.create(context, resource);
        sound.setLooping(looping);
        return sound;
    }

    private MediaPlayer getAudio(){

        switch (sound){
            case BACKGROUND :
                return background;
            case VOICE :
                return voice;
            case BACKGROUND_GAMEOVER_1 :
                return backgroundGameOver1;
            case BACKGROUND_GAMEOVER_2 :
                return backgroundGameOver2;
            case HIT :
                return hit;
        }

        return null;
    }
}
