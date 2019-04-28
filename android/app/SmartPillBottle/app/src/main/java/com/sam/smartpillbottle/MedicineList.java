package com.sam.smartpillbottle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

public class MedicineList extends AppCompatActivity {
    private Connection connection;
    private ServerRequester serverRequester;
    private ServerSender serverSender;
    private ExecutorService executor;
    private LinearLayout medicineListContainer;
    private TextView test1;
    private TextView test2;
    private static FirebaseAuth firebaseAuth;
    private static FirebaseUser firebaseUser;
    private static DatabaseReference firebaseDatabase;
    private TextView emailLabel;
    private String token;
    private static ArrayList<Medication> medicineArrayList;
    private static ArrayList<View> medicineViewArrayList;
    private static int clickedTile = -1;


    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        medicineArrayList = new ArrayList<Medication>(16);
        medicineViewArrayList = new ArrayList<View>(16);

        emailLabel = findViewById(R.id.listEmailLabel);
        emailLabel.setText(firebaseUser.getEmail());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connection = Login.getConnection();
        executor = Login.getExecutor();
        serverRequester = new ServerRequester(connection);
        serverSender = new ServerSender(connection);

        medicineListContainer = (LinearLayout) findViewById(R.id.medicineListContainer);
        getMedicineFromDatabase();







        /*LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = View.inflate(this, R.layout.activity_medicine_tile, null);
        View view = inflater.inflate(R.layout.activity_medicine_tile, null);

        medicineListContainer.addView(view);
        medicineListContainer.addView(view1);

        test1 = view.findViewById(R.id.tileMedicineNameLabel);
        test2 = view1.findViewById(R.id.tileMedicineNameLabel);*/




        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }
                token = task.getResult().getToken();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addMedicineButton);

        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "testing123";
                serverSender.addData(test);
                executor.execute(serverSender);
                //executor.execute(serverRequester);

                serverSender.addData("message");
                executor.execute(serverSender);
                executor.execute(serverRequester);
                String response = (String) serverRequester.getData();
                if(response.matches("ready")){
                    serverSender.addData(firebaseUser.getUid());
                    //System.out.println("added");
                    serverSender.addData(token.substring(0, token.length()/2));
                    //System.out.println("added");
                    serverSender.addData(token.substring(token.length()/2));
                    executor.execute(serverSender);
                }

                startActivity(new Intent(MedicineList.this, AddMedicine.class ));


                //serverSender.setData("notification");
                //executor.execute(serverSender);
                //executor.execute(serverRequester);

                //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, )


                //Snackbar.make(view, (String) serverRequester.getData(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void getMedicineFromDatabase(){

        firebaseDatabase.child("users/" + firebaseUser.getUid() + "/medicine").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot medicine: dataSnapshot.getChildren()){
                    int localID = count;
                    count++;
                    String medicineUID = (String) medicine.getKey();
                    String name = (String) medicine.child("name").getValue();
                    String dosePerDay = (String) medicine.child("dosePerDay").getValue();
                    String pillsPerDose = (String) medicine.child("pillsPerDose").getValue();
                    String days = (String) medicine.child("days").getValue();
                    String remaining = (String) medicine.child("remaining").getValue();
                    String latitude = (String) medicine.child("latitude").getValue();
                    String longitude = (String) medicine.child("longitude").getValue();

                    Calendar now = Calendar.getInstance();
                    Date today = new Date(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH));


                    medicineArrayList.add(new Medication(localID, medicineUID, name, pillsPerDose, dosePerDay, remaining, latitude, longitude, today, today));
                }
                getMedicineViews();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getMedicineViews(){
        for(int i=0; i<medicineArrayList.size(); i++){
            View v = View.inflate(this, R.layout.activity_medicine_tile, null);
            TextView medicineName = v.findViewById(R.id.tileMedicineNameLabel);
            TextView nextDose = v.findViewById(R.id.tileNextDosageLabel);
            TextView remaining = v.findViewById(R.id.tileRemainingLabel);

            medicineName.setText(medicineArrayList.get(i).getName());
            nextDose.setText("Next Dose: " + medicineArrayList.get(i).getNextDose().toString());
            remaining.setText("Remaining Pills: " + medicineArrayList.get(i).getRemainingPills());
            medicineViewArrayList.add(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i=0; i < medicineViewArrayList.size(); i++){
                        if(v == medicineViewArrayList.get(i)){
                            clickedTile = i;
                            Intent intent = new Intent(MedicineList.this, MedicineDetail.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        }



        for(View v : medicineViewArrayList){
            medicineListContainer.addView(v);
        }
        medicineListContainer.refreshDrawableState();
    }

    public static Medication getClickedMedication() {
        return medicineArrayList.get(clickedTile);
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseAuth.signOut();
    }


}
