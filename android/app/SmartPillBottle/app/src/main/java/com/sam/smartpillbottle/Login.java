package com.sam.smartpillbottle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;
    private EditText usernameBox;
    private EditText passwordBox;
    private Client client;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);

        executor = Executors.newCachedThreadPool();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    client = new Client("68.183.148.234",4044);
                    //client.connect();
                    //client.initializeStreams();
                    //client.stayConnected();
                    executor.execute(client);
                    executor.shutdown();
                }catch (IOException e) {
                    System.err.println(e);
                }
            }
        });


    }
}
