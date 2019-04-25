package com.sam.smartpillbottle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;

public class MedicineList extends AppCompatActivity {
    private Connection connection;
    private ServerRequester serverRequester;
    private ServerSender serverSender;
    private ExecutorService executor;
    private LinearLayout medicineListContainer;
    private TextView test1;
    private TextView test2;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private TextView emailLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //user.sendEmailVerification();

        System.out.println(user.isEmailVerified());

        emailLabel = findViewById(R.id.listEmailLabel);
        emailLabel.setText(user.getEmail());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connection = Login.getConnection();
        executor = Login.getExecutor();
        serverRequester = new ServerRequester(connection);
        serverSender = new ServerSender(connection);

        medicineListContainer = (LinearLayout) findViewById(R.id.medicineListContainer);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = View.inflate(this, R.layout.activity_medicine_tile, null);
        View view = inflater.inflate(R.layout.activity_medicine_tile, null);

        medicineListContainer.addView(view);
        medicineListContainer.addView(view1);

        test1 = view.findViewById(R.id.medicineNameLabel);
        test2 = view1.findViewById(R.id.medicineNameLabel);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MedicineList.this, MedicineDetail.class));
            }
        });

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MedicineList.this, MedicineDetail.class));
            }
        });

        Medication testing = new Medication(0, "Benadryl", 2, 20);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addMedicineButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "testing123";
                serverSender.setData(test);
                executor.execute(serverSender);
                executor.execute(serverRequester);



                test1.setText("Benadryl");
                test2.setText("Tylenol");

                //serverSender.setData("notification");
                //executor.execute(serverSender);
                //executor.execute(serverRequester);

                //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, )


                Snackbar.make(view, (String) serverRequester.getData(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.signOut();
    }
}
