package com.sam.smartpillbottle;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.Calendar;
@TargetApi(26)
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
    private Medication medication;
    private boolean canAddMedicine;
    private DataSnapshot bigSnapshot;
    private volatile boolean dataReady = false;

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

        canAddMedicine = false;


        addMedicineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.child("/claimed/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                        LocalTime time = LocalTime.now();
                        int hour = time.getHour();
                        int minute = time.getMinute();

                        Date today = new Date(Calendar.getInstance().get(Calendar.YEAR),
                                Calendar.getInstance().get(Calendar.MONTH)+1,
                                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), new Time(hour, minute));

                        //create medication
                        medication = new Medication(MedicineList.getNextMedicationLocalID(), medicineUID, medicineName,
                                pillsPerDose, dosesPerDay, remainingPills, "0", "0", today, today);


                        System.out.println("In database");
                        bigSnapshot = dataSnapshot;

                        System.out.println((boolean) bigSnapshot.child(medication.getMedicineUID()).getValue());
                        if((boolean) bigSnapshot.child(medication.getMedicineUID()).getValue()){
                            canAddMedicine = false;
                        }else{
                            canAddMedicine = true;
                        }

                        //write to database
                        System.out.println(canAddMedicine);
                        if(canAddMedicine){
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/name").setValue(medicineName);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/dosesPerDay").setValue(dosesPerDay);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/pillsPerDose").setValue(pillsPerDose);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/remaining").setValue(remainingPills);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/latitude").setValue("0");
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/longitude").setValue("0");
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/days").setValue(days);

                            String dateString = String.valueOf(today.getYear()).substring(2) + "," + today.getMonth() + "," + today.getDay() + ","
                                    + today.getTime().getHour() + "," + today.getTime().getMinute();

                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/lastDose").setValue(dateString);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/nextDose").setValue(dateString);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/taken/count").setValue(0);
                            firebaseDatabase.child("claimed/").child(medicineUID).setValue(true);
                            firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine/" + medicineUID + "/notify").setValue(false);

                            Snackbar.make(findViewById(R.id.addMedicineLayout), "Success: Medicine added", Snackbar.LENGTH_LONG);
                            finish();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddMedicine.this)
                                    .setMessage("Failed: Medicine UID already registered\n Please ensure that your medication UID was entered correctly.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            builder.show();
                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
