package com.sam.smartpillbottle;

import android.view.View;

public class Medication {
    //index for accessing array
    private int localID;
    //unique id from database
    private String medicineUID;
    private String name;

    public String getMedicineUID() {
        return medicineUID;
    }

    public String getName() {
        return name;
    }

    public String getPillsPerDose() {
        return pillsPerDose;
    }

    public String getDosePerDay() {
        return dosePerDay;
    }

    public String getRemainingPills() {
        return remainingPills;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Date getLastDose() {
        return lastDose;
    }

    public Date getNextDose() {
        return nextDose;
    }

    private String pillsPerDose;
    private String dosePerDay;
    private String remainingPills;
    private String latitude;
    private String longitude;
    private Date lastDose;
    private Date nextDose;
    private View view;

    public Medication(int localID, String medicineUID, String name,
                      String pillsPerDose, String dosePerDay, String remainingPills,
                      String latitude, String longitude, Date lastDose, Date nextDose){

        this.localID = localID;
        this.medicineUID = medicineUID;
        this.name = name;
        this.pillsPerDose = pillsPerDose;
        this.dosePerDay = dosePerDay;
        this.remainingPills = remainingPills;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastDose = lastDose;
        this.nextDose = nextDose;
    }

    public int getLocalID() {
        return localID;
    }

    public View getView(View v) {


        return view;
    }
}
