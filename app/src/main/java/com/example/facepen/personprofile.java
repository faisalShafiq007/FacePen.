package com.example.facepen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class personprofile extends AppCompatActivity {
    TextView profilestatus,username,country,dateofbirth,gender,relationshipstatus,profilename;
    CircleImageView profile_pic;
    Button sendfriendrequestbtn,declinefriendrequestbtn;
    DatabaseReference friendrequestref,userref,friendsref;
    String Senderuserid,Recieveruserid,currentstate,savecurrentdate;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personprofile);
        profile_pic=findViewById(R.id.person_profile_pic);
        profilename=findViewById(R.id.person_full_name);
        profilestatus=findViewById(R.id.person_status);
        username=findViewById(R.id.person_username);
        country=findViewById(R.id.person_country);
        dateofbirth=findViewById(R.id.person_dob);
        friendsref=FirebaseDatabase.getInstance().getReference().child("friends");
        friendrequestref=FirebaseDatabase.getInstance().getReference().child("friendrequests");
        gender=findViewById(R.id.person_gender);
        Senderuserid=mAuth.getCurrentUser().getUid();
        currentstate="not_friends";
        relationshipstatus=findViewById(R.id.person_relationshipstatus);
        sendfriendrequestbtn=findViewById(R.id.person_sendfriendrequestbutton);
        declinefriendrequestbtn=findViewById(R.id.person_declinefriendrequestbutton);
Recieveruserid= getIntent().getExtras().get("visit_user_id").toString();
userref= FirebaseDatabase.getInstance().getReference().child("users");
        userref.child(Recieveruserid).addValueEventListener(new ValueEventListener() {
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
                    MaintainanceofButton();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        declinefriendrequestbtn.setVisibility(View.INVISIBLE);
        declinefriendrequestbtn.setEnabled(true);
        if(!Senderuserid.equals(Recieveruserid)){

           sendfriendrequestbtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

                   sendfriendrequestbtn.setEnabled(false);
                   if(currentstate.equals("not_friends")){
                       SendFriendRequesttoaperson();

                   }
                   if(currentstate.equals("request_sent")){
                       CancelFriendRequest();
                   }
                   if(currentstate.equals("request_recieved")){
AcceptFriendRequest();
                   }
                   if(currentstate.equals("friends")){
                       Unfriendsanexistingfriend();
                   }

               }
           });
        }
        else{
            declinefriendrequestbtn.setVisibility(View.INVISIBLE);
            sendfriendrequestbtn.setVisibility(View.INVISIBLE);
        }
    }

    private void Unfriendsanexistingfriend() {
        friendsref.child(Senderuserid).child(Recieveruserid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friendsref.child(Recieveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendfriendrequestbtn.setEnabled(true);
                                currentstate="not_friends";
                                sendfriendrequestbtn.setText("Sent Friend Request");
                                declinefriendrequestbtn.setVisibility(View.INVISIBLE);
                                declinefriendrequestbtn.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });

    }

    private void AcceptFriendRequest() {
        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat datFormat= new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=datFormat.format(calfordate.getTime());
        friendsref.child(Senderuserid).child(Recieveruserid).child("date").setValue(savecurrentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friendsref.child(Recieveruserid).child(Senderuserid).child("date").setValue(savecurrentdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendrequestref.child(Senderuserid).child(Recieveruserid)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            friendrequestref.child(Recieveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        sendfriendrequestbtn.setEnabled(true);
                                                        currentstate="friends";
                                                        sendfriendrequestbtn.setText("Unfriend");
                                                        declinefriendrequestbtn.setVisibility(View.INVISIBLE);
                                                        declinefriendrequestbtn.setEnabled(false);

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });

                            }

                        }
                    });
                }

            }
        });
    }

    private void CancelFriendRequest() {
        friendrequestref.child(Senderuserid).child(Recieveruserid)
              .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friendrequestref.child(Recieveruserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendfriendrequestbtn.setEnabled(true);
                                currentstate="not_friends";
                                sendfriendrequestbtn.setText("Sent Friend Request");
                                declinefriendrequestbtn.setVisibility(View.INVISIBLE);
                                declinefriendrequestbtn.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });
    }

    private void MaintainanceofButton() {
        friendrequestref.child(Senderuserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(Recieveruserid)){
                    String Request_type=dataSnapshot.child(Recieveruserid).child("request_type").getValue().toString();
                    if(Request_type.equals("sent")){
                        currentstate="request_sent";
                        sendfriendrequestbtn.setText("Cancel Friend Request");
                        declinefriendrequestbtn.setVisibility(View.INVISIBLE);
                        declinefriendrequestbtn.setEnabled(false);

                    }
                    else if(Request_type.equals("recieved")){
                        currentstate="request_recieved";
                        sendfriendrequestbtn.setText("Accept Friend Request");
                        declinefriendrequestbtn.setVisibility(View.VISIBLE);
                        declinefriendrequestbtn.setEnabled(true);
                        declinefriendrequestbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest();
                            }
                        });

                    }
                }
                else{
                    friendsref.child(Senderuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(Recieveruserid)){
                                currentstate="friends";
                                 sendfriendrequestbtn.setText("Unfriend");
                                 declinefriendrequestbtn.setVisibility(View.INVISIBLE);
                                 declinefriendrequestbtn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendFriendRequesttoaperson() {
friendrequestref.child(Senderuserid).child(Recieveruserid)
        .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
   if(task.isSuccessful()){
       friendrequestref.child(Recieveruserid).child(Senderuserid).child("request_type").setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
          if(task.isSuccessful()){
              sendfriendrequestbtn.setEnabled(true);
              currentstate="request_sent";
              sendfriendrequestbtn.setText("Cancel Friend Request");
              declinefriendrequestbtn.setVisibility(View.INVISIBLE);
              declinefriendrequestbtn.setEnabled(false);

          }
           }
       });
   }
    }
});
    }
}
