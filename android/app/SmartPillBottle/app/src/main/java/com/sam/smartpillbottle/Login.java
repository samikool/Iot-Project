package com.sam.smartpillbottle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {
    private Button loginButton;
    private Button registerButton;
    private EditText emailBox;
    private EditText passwordBox;
    private static Connection connection;
    private static ExecutorService executor;
    private ServerRequester serverRequester;
    private ServerSender serverSender;

    public static Connection getConnection(){return connection;}

    public static ExecutorService getExecutor(){return executor;}

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);

        executor = Executors.newCachedThreadPool();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //establish connection (may not be needed with Firebase)
        try {
            connection = new Connection("192.168.0.20", 4044);
            executor.execute(connection);
            serverRequester = new ServerRequester(connection);
            serverSender = new ServerSender(connection);

        } catch (IOException e) {
            System.err.println(e);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Attempting to login", Snackbar.LENGTH_LONG).show();
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Attempting to Register account", Snackbar.LENGTH_LONG).show();
                register();
            }
        });

        if(firebaseAuth.getCurrentUser() != null){
            showMedicineList();
        }
    }

    private void showMedicineList(){
        startActivity(new Intent(Login.this, MedicineList.class));
        finish();
    }

    private void register(){
        firebaseAuth.createUserWithEmailAndPassword(emailBox.getText().toString(), passwordBox.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Snackbar.make(findViewById(R.id.loginLayout),"Successfully registered account!", Snackbar.LENGTH_LONG).show();
                        login();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        firebaseDatabase.getReference(user.getUid() + "/email").setValue(user.getEmail());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(findViewById(R.id.loginLayout),"Unable to register account", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void login(){
        firebaseAuth.signInWithEmailAndPassword(emailBox.getText().toString(), passwordBox.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        showMedicineList();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.loginLayout),"Failed to login. Please try again.", Snackbar.LENGTH_LONG).show();
                    }
        });

    }
}