package com.sam.smartpillbottle;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AddMedicine extends AppCompatActivity {
    private DatabaseReference firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private TextView medicineUIDBox;
    private TextView medicineNameBox;
    private TextView dosesPerDayBox;
    private TextView pillsPerDoseBox;
    private CheckBox mondayCheckbox;
    private CheckBox tuesdayCheckbox;
    private CheckBox wednesdayCheckbox;
    private CheckBox thursdayCheckbox;
    private CheckBox fridayCheckbox;
    private CheckBox saturdayCheckbox;
    private CheckBox sundayCheckbox;
    private CheckBox everydayCheckbox;
    private CheckBox[] dayBoxes;
    private TextView currentPillsBox;
    private Button addMedicineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        //initialize Firebase stuff
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = MedicineList.getFirebaseAuth();
        firebaseUser = MedicineList.getFirebaseUser();

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
        dayBoxes = new CheckBox[] {mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox};
        everydayCheckbox = findViewById(R.id.addEverydayCheckBox);
        currentPillsBox = findViewById(R.id.addNumOfPillsBox);
        addMedicineButton = findViewById(R.id.addAddMedicineButton);

        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get info
                String medicineUID = medicineUIDBox.getText().toString();
                String medicineName = (String) medicineNameBox.getText().toString();
                String dosesPerDay = (String) dosesPerDayBox.getText().toString();
                String pillsPerDose = (String) pillsPerDoseBox.getText().toString();
                String remainingPills = (String) currentPillsBox.getText().toString();
                String days = "";
                if(everydayCheckbox.isChecked()){
                    days = "all";
                }
                else{
                    if(mondayCheckbox.isChecked()){
                        days += "monday,";
                    }
                    if(tuesdayCheckbox.isChecked()){
                        days += "tuesday";
                    }
                    if(wednesdayCheckbox.isChecked()){
                        days += "wednesday";
                    }
                    if(thursdayCheckbox.isChecked()){
                        days += "thrusday";
                    }
                    if(fridayCheckbox.isChecked()){
                        days += "friday";
                    }
                    if(saturdayCheckbox.isChecked()){
                        days += "saturday";
                    }
                    if(sundayCheckbox.isChecked()){
                        days +="sunday";
                    }
                }

                Date today = new Date(Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                //create medication
                Medication medication = new Medication(MedicineList.getNextMedicationLocalID(), medicineUID, medicineName,
                        pillsPerDose, dosesPerDay, remainingPills, "0", "0", today, today);

                //write to database
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/name").setValue(medicineName);
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/dosesPerDay").setValue(dosesPerDay);
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/pillsPerDose").setValue(pillsPerDose);
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/remaining").setValue(medicineName);
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/latitude").setValue("0");
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/longitude").setValue("0");
                firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/days").setValue(days);

                finish();
            }
        });

        everydayCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(everydayCheckbox.isChecked()){
                    mondayCheckbox.setChecked(false);
                    tuesdayCheckbox.setChecked(false);
                    wednesdayCheckbox.setChecked(false);
                    thursdayCheckbox.setChecked(false);
                    fridayCheckbox.setChecked(false);
                    saturdayCheckbox.setChecked(false);
                    sundayCheckbox.setChecked(false);

                    mondayCheckbox.setClickable(false);
                    tuesdayCheckbox.setClickable(false);
                    wednesdayCheckbox.setClickable(false);
                    thursdayCheckbox.setClickable(false);
                    fridayCheckbox.setClickable(false);
                    saturdayCheckbox.setClickable(false);
                    sundayCheckbox.setClickable(false);
                }
                else if(!everydayCheckbox.isChecked()){
                    mondayCheckbox.setClickable(true);
                    tuesdayCheckbox.setClickable(true);
                    wednesdayCheckbox.setClickable(true);
                    thursdayCheckbox.setClickable(true);
                    fridayCheckbox.setClickable(true);
                    saturdayCheckbox.setClickable(true);
                    sundayCheckbox.setClickable(true);
                }
            }
        });
    }


}
