package com.antizikagame.control;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gerenciador de tempo
 * Created by Thiago on 24/08/2015.
 */
public class ClockManager {
    private static final String LOG = "Time";
    private final SimpleDateFormat format;
    private Timer T;

    private Calendar dif;
    private ClockState delegate;
    private long timesInitial;
    private int timeLimit;
    private boolean isOut;
    private boolean isPaused;

    public ClockManager() {
        T=new Timer();
        T.scheduleAtFixedRate(timerTask, 1000, 1000);
        format = new SimpleDateFormat("mm:ss", Locale.getDefault());
    }

    public void cancel(){
        if(T != null)
            T.cancel();
    }
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(isPaused) return;
            // Verifica todos os relogios ativos se o tempo ja foi esgotado ou ainda esta em progresso
            if(dif == null) return;
            // Diminui um segundo no tempo restante
            dif.add(Calendar.SECOND, -1);
            // Recupera o minuto e segundo restantes
            int min = dif.get(Calendar.MINUTE);
            int seconds = dif.get(Calendar.SECOND);

            if(delegate != null)
                delegate.changeTime(timeLimit, dif);

            if(min == 0 && seconds == 0){
                Log.d(LOG, String.format("Tempo esgotado para tempo %s (minutos|segundos) " , timeLimit));
                isOut = true;
                if (delegate != null) {
                    delegate.outTime(timeLimit, dif);
                }

                stop();
            }
        }
    };

    public ClockManager maxTime(int unit, int timeLimit){
        this.timeLimit = timeLimit;
        final Calendar now = Calendar.getInstance();
        final Calendar last = Calendar.getInstance();
        last.setTimeInMillis(timesInitial);
        final Calendar limit = (Calendar) last.clone();
        limit.add(unit, timeLimit); // Tempo maximo em minutos
        // Ultimo delegate adicionado

        Log.d(LOG, "[LAST] " + limit.getTime().toString());
        Log.d(LOG, "[NOW] " + now.getTime().toString());

        // Tempo esgotado
        if(timesInitial == 0 || limit.before(now)){
            Log.d(LOG, String.format("Tempo esgotado para tempo %s (minutos|segundos) : " , timeLimit));
            if(delegate != null)
                delegate.outTime(timeLimit, null);
        }else{ // Ainda tem tempo
            Log.d(LOG, String.format("Ainda existe tempo para %s (minutos|segundos) " , timeLimit));
            if(delegate != null)
                delegate.inTime(timeLimit);
            // Tempo total para esgotar um tempo
            dif = Calendar.getInstance();
            dif.setTimeInMillis(limit.getTimeInMillis() - now.getTimeInMillis());
        }

        return this;
    }

    public Calendar getDif() {
        return dif;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setPause(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public String getTime(){
        return (getDif() != null)? format.format(getDif().getTime()) : "00:00";
    }

    public int getSecond(){
        return (getDif() != null)? getDif().get(Calendar.SECOND) : 0;
    }

    public ClockManager stop(){
        this.dif = null;
        return this;
    }

    public ClockManager timeInitial(long timeInitial) {
        this.timesInitial = timeInitial;
        return this;
    }

    public ClockManager setDelegate(ClockState delegate) {
        this.delegate = delegate;
        return this;
    }

    public interface ClockState{
        void outTime(int max, Calendar dif);
        void changeTime(int max, Calendar dif);
        void inTime(int max);
    }
}
