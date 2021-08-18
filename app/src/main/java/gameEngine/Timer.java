package gameEngine;

import java.util.Date;

public class Timer {
    private long startTime, endTime;

    public Timer(){
        startTime = endTime = 0;
    }

    public void start(){
        startTime = System.currentTimeMillis() / 1000;
    }

    public void stop(){
        endTime = System.currentTimeMillis() / 1000;
    }

    public void reset(){
        startTime = endTime = 0;
    }

    public int getElapsedTime(){
        return (int)((endTime - startTime));
    }

    private String getElapsedSeconds(){
        String elapsedSeconds = String.valueOf(getElapsedTime() % 60);

        if(elapsedSeconds.length() == 1)
            elapsedSeconds = "0" + elapsedSeconds;

        return elapsedSeconds;
    }

    private String getElapsedMinutes(){
        String elapsedMinutes = String.valueOf(getElapsedTime() / 60);

        if(elapsedMinutes.length() == 1)
            elapsedMinutes = "0" + elapsedMinutes;

        return elapsedMinutes;
    }

    public String getElapsedTimeString(){
        return getElapsedMinutes() + ":" + getElapsedSeconds();
    }
}
