package com.example.facepen;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    TextView profilestatus,username,country,dateofbirth,gender,relationshipstatus,profilename;
    CircleImageView profile_pic;
    StorageReference userprofileimageRef ;
    DatabaseReference profileuserref;
    String Currentuserid;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Profile");
        userprofileimageRef= FirebaseStorage.getInstance().getReference().child("profileimage");
        setContentView(R.layout.activity_profile);
        profile_pic=findViewById(R.id.my_profile_pic);
        Currentuserid=mAuth.getCurrentUser().getUid();

      profilename=findViewById(R.id.my_profile_name);
        profilestatus=findViewById(R.id.my_profile_status);
        username=findViewById(R.id.my_profile_username);
        country=findViewById(R.id.my_profile_country);
        dateofbirth=findViewById(R.id.my_profile_dob);
        gender=findViewById(R.id.my_profile_gender);
        relationshipstatus=findViewById(R.id.my_profile_relationshipstatus);
        profileuserref= FirebaseDatabase.getInstance().getReference().child("users").child(Currentuserid);

        profileuserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String myProfileStatus = dataSnapshot.child("profilestatus").getValue().toString();
                    String myProfileusername = dataSnapshot.child("username").getValue().toString();
                    String myprofilename = dataSnapshot.child("fullname").getValue().toString();
                    String myProfilecountry = dataSnapshot.child("country").getValue().toString();
                    String myProfildateofbirth = dataSnapshot.child("dateofbirth").getValue().toString();
                    String myProfilegender = dataSnapshot.child("gender").getValue().toString();
                    String myProfilerelationship = dataSnapshot.child("Relationshipstatus").getValue().toString();
                   String myprofileimg = dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(myprofileimg).placeholder(R.drawable.profile).into(profile_pic);
                    profilestatus.setText("Status: ".concat(myProfileStatus));
                    username.setText( myProfileusername);
                    country.setText("Country: ".concat(myProfilecountry));
                    dateofbirth.setText("Born: ".concat(myProfildateofbirth));
                    gender.setText("Gender: ".concat(myProfilegender));
                    relationshipstatus.setText("Relationshipstatus: ".concat(myProfilerelationship));
                           profilename.setText(myprofilename);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
