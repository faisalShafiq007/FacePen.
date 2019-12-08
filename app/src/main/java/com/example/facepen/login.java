package com.example.facepen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


@SuppressWarnings("ALL")
public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText email= (EditText) findViewById(R.id.email);
        final EditText password= (EditText) findViewById(R.id.password);
        final TextView register= (TextView) findViewById(R.id.textView3);
        Button buton= (Button) findViewById(R.id.button);
        getSupportActionBar().hide();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login.this,register.class);
                startActivity(intent);
            }
        });
buton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String email1=email.getText().toString();
        String pass=password.getText().toString();
        FirebaseAuth  mAuth=FirebaseAuth.getInstance();
        if(email1.isEmpty()){
            email.setError("enter email");
        }
        else if(pass.isEmpty()){
            password.setError("password field is empty");

        }
        else{
            mAuth.signInWithEmailAndPassword(email1,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Toast.makeText(login.this,"login complete",Toast.LENGTH_SHORT).show();
                    Intent main=new Intent(login.this,MainActivity.class);
                    startActivity(main);
finish();
        }
    }
);
        }
    }}
    );
    }}


