package com.sam.smartpillbottle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMedicine extends AppCompatActivity {
    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private TextView medicineUIDBox;
    private TextView medicineNameBox;
    private TextView dosesPerDayBox;
    private TextView pillsPerDoseBox;
    private TextView mondayCheckbox;
    private TextView tuesdayCheckbox;
    private TextView wednesdayCheckbox;
    private TextView thursdayCheckbox;
    private TextView fridayCheckbox;
    private TextView saturdayCheckbox;
    private TextView sundayCheckbox;
    private TextView everydayCheckbox;
    private TextView currentPillsBox;
    private Button addMedicineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        //initialize Firebase stuff
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //initialize GUI
        medicineUIDBox = findViewById(R.id.addMedicineUIDBox);
        medicineNameBox = findViewById(R.id.addMedicineNameBox);
        dosesPerDayBox = findViewById(R.id.addDosesPerDayBox);
        pillsPerDoseBox = findViewById(R.id.addPillPerDoseBox);
        mondayCheckbox = findViewById(R.id.addMondayCheckBox);
        tuesdayCheckbox = findViewById(R.id.addTuesdayCheckBox);
        wednesdayCheckbox = findViewById(R.id.addWednesdayCheckBox);
        thursdayCheckbox = findViewById(R.id.addThursdayCheckBox);
        fridayCheckbox = findViewById(R.id.addFridayCheckBox);
        saturdayCheckbox = findViewById(R.id.addSaturdayCheckBox);
        sundayCheckbox = findViewById(R.id.addSundayCheckBox);
        everydayCheckbox = findViewById(R.id.addEverydayCheckBox);
        currentPillsBox = findViewById(R.id.addNumOfPillsBox);
        addMedicineButton = findViewById(R.id.addAddMedicineButton);

        //test reading and writing to database
        //read medicine in database and display on gui
        String test = firebaseDatabase.child("claimed").toString(); //.child("sadfue2n3lb09g")
        System.out.println(test);

    }


}
