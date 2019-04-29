package com.sam.smartpillbottle;

public class Time {
    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }



    public Time(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString(){
        if(hour <= 12){
            if(minute < 10){
                return hour + ":" + String.format("%01d", minute) + " AM";
            }
            return hour + ":" + minute + " AM";
        }
        else {
            hour -= 12;
            if(minute < 10){
                return hour + ":" + String.format("%01d", minute) + " PM";
            }
            return hour + ":" + minute + " PM";
        }
    }


}
