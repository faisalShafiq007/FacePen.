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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/*import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;*/


public class register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        final EditText email2= (EditText) findViewById(R.id.email);
        final EditText password= (EditText) findViewById(R.id.password);
        final EditText confirmpasword= (EditText) findViewById(R.id.confirm_password);
        final Button register= (Button) findViewById(R.id.register);
final TextView signin= (TextView) findViewById(R.id.textView3);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(register.this);

signin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent signIn=new Intent(register.this,login.class);
        startActivity(signIn);
    }
});

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email1=email2.getText().toString();
                String pass=password.getText().toString();
                String conpas=confirmpasword.getText().toString();
                if(email1.isEmpty()|| !email1.matches(emailPattern)){

                     email2.setError("enter complete email i.e ali@gmail.com");
                }
                else if(pass.isEmpty()|| pass.length()<6){
                    password.setError("enter password with minimum 6 length");

                }

                else if(conpas.isEmpty()|| !conpas.matches(pass)){
                    confirmpasword.setError("please confirm password");

                }


                else{
                    mAuth=FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email1, pass)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                         Toast.makeText(register.this, "Register complete", Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(register.this,setup.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                    // ...
                                }
                            });

                }

            }



        });


    }

}

