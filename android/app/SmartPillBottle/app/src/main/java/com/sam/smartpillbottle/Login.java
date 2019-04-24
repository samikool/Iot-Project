package com.sam.smartpillbottle;

import android.content.Intent;
import android.support.design.widget.Snackbar;
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
    private static Connection connection;
    private static ExecutorService executor;

    public static Connection getConnection(){return connection;}

    public static ExecutorService getExecutor(){return executor;}

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
                    connection = new Connection("68.183.148.234",4044);
                    Snackbar loginStatus = Snackbar.make(v, "Attempting to login", Snackbar.LENGTH_LONG);
                    loginStatus.show();
                    executor.execute(connection);

                    Intent showMedicine = new Intent(Login.this, MedicineList.class);
                    startActivity(showMedicine);
                    //executor.shutdown();
                }catch (IOException e) {
                    System.err.println(e);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( Login.this, MedicineList.class));
                finish();
            }
        });


    }
}
