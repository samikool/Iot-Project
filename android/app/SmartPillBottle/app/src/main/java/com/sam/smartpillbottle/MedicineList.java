package com.sam.smartpillbottle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private String token;

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

        test1 = view.findViewById(R.id.listMedicineNameLabel);
        test2 = view1.findViewById(R.id.listMedicineNameLabel);


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }

                token = task.getResult().getToken();


            }
        });



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



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addMedicineButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "testing123";
                serverSender.addData(test);
                executor.execute(serverSender);
                //executor.execute(serverRequester);



                test1.setText("Benadryl");
                test2.setText("Tylenol");

                serverSender.addData("message");
                executor.execute(serverSender);
                executor.execute(serverRequester);
                String response = (String) serverRequester.getData();
                if(response.matches("ready")){
                    serverSender.addData(user.getUid());
                    //System.out.println("added");
                    serverSender.addData(token.substring(0, token.length()/2));
                    //System.out.println("added");
                    serverSender.addData(token.substring(token.length()/2));
                    executor.execute(serverSender);
                }


                //serverSender.setData("notification");
                //executor.execute(serverSender);
                //executor.execute(serverRequester);

                //NotificationCompat.Builder builder = new NotificationCompat.Builder(this, )


                //Snackbar.make(view, (String) serverRequester.getData(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        mAuth.signOut();
    }
}
