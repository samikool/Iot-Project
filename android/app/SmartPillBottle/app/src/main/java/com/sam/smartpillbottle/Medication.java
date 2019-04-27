package com.sam.smartpillbottle;

import java.util.Calendar;
import java.util.Date;

public class Medication {
    //index for accessing array
    private int localID;
    //unique id from database
    private String medicineUID;
    private String name;
    private int pillsPerDose;
    private int remainingPills;
    private int latitude;
    private int longitude;
    private Calendar lastDose;
    private Calendar nextDose;



    public Medication(int localID, String medicineUID,
                      String name, int pillsPerDose,
                      int estimatedPills, int remainingPills,
                      int latitude, int longitude,
                      Calendar lastDose, Calendar nextDose){

    }
}
