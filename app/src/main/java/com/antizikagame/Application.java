package com.antizikagame;

import com.antizikagame.control.SoundManager;

/**
 * Created by Thiago on 15/03/2016.
 */
public class Application extends android.app.Application {

    private SoundManager soundManager;

    @Override
    public void onCreate() {
        super.onCreate();

        soundManager = SoundManager.getInstance(this).initialize();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        switch (level){
            case TRIM_MEMORY_UI_HIDDEN : case TRIM_MEMORY_RUNNING_LOW : case TRIM_MEMORY_BACKGROUND :
                soundManager.pause(SoundManager.BACKGROUND);
            break;
        }
    }
}
