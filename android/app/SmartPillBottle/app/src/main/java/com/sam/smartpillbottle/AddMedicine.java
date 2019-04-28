package com.sam.smartpillbottle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        dayBoxes = new CheckBox[] {mondayCheckbox, tuesdayCheckbox, wednesdayCheckbox, thursdayCheckbox, fridayCheckbox, saturdayCheckbox, sundayCheckbox};
        everydayCheckbox = findViewById(R.id.addEverydayCheckBox);
        currentPillsBox = findViewById(R.id.addNumOfPillsBox);
        addMedicineButton = findViewById(R.id.addAddMedicineButton);



        //test reading and writing to database
        //read medicine in database and display on gui
        firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());

                String days = (String) dataSnapshot.child("days").getValue();
                if(days.matches("all")){
                    everydayCheckbox.setChecked(true);
                    for(int i=0; i<dayBoxes.length; i++){
                        dayBoxes[i].setChecked(false);
                    }
                }
                else{
                    String[] daysArray = days.split(",");
                    for(int i=0; i<daysArray.length; i++){
                        switch(daysArray[i]){
                            case "monday":
                                mondayCheckbox.setChecked(true);
                                i++;
                            case "tuesday":
                                tuesdayCheckbox.setChecked(true);
                                i++;
                            case "wednesday":
                                wednesdayCheckbox.setChecked(true);
                                i++;
                            case "thursday":
                                thursdayCheckbox.setChecked(true);
                                i++;
                            case "friday":
                                fridayCheckbox.setChecked(true);
                                i++;
                            case "saturday":
                                saturdayCheckbox.setChecked(true);
                                i++;
                            case "sunday":
                                sundayCheckbox.setChecked(true);
                                i++;
                                break;
                        }
                    }
                }


                medicineNameBox.setText((String) dataSnapshot.child("name").getValue());
                pillsPerDoseBox.setText((String) dataSnapshot.child("pillsPerDose").getValue());
                dosesPerDayBox.setText((String) dataSnapshot.child("dosesPerDay").getValue());
                currentPillsBox.setText((String) dataSnapshot.child("remaining").getValue());

                //System.out.println(dataSnapshot.toString()/);
                System.out.println();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
