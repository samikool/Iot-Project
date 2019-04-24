package com.sam.smartpillbottle;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.zip.DataFormatException;

public class MedicineList extends AppCompatActivity {
    private Connection connection;
    private ServerRequester serverRequester;
    private ServerSender serverSender;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        connection = Login.getConnection();
        executor = Login.getExecutor();
        serverRequester = new ServerRequester(connection);
        serverSender = new ServerSender(connection);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addMedicineButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "testing123";
                serverSender.setData(test);
                executor.execute(serverSender);
                executor.execute(serverRequester);
                while(!serverRequester.dataReady()){

                }


                Snackbar.make(view, (String) serverRequester.getData(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
