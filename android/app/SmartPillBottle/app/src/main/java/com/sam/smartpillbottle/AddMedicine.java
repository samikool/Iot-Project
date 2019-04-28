package com.sam.smartpillbottle;

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

                //create medication

                //write to database

                //call get medication method from medicinelist
                //maybe the on resume method or maybe make it static ;)

                //start medication list activity on successful write to database

                //no need for error checking lol
            }
        });
    }


}
