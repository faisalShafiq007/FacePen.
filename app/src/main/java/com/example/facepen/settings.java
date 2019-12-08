package com.example.facepen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class settings extends AppCompatActivity {
    EditText profilestatus,username,profilename,country,dateofbirth,gender,relationshipstatus;
   Button savesettings;
    String myprofileimg;
    StorageReference userprofileimageRef ;

 DatabaseReference settingsuserref;
    String Currentuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
getSupportActionBar().setTitle("Settings");
        userprofileimageRef=FirebaseStorage.getInstance().getReference().child("profileimage");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
          Currentuserid=mAuth.getCurrentUser().getUid();
       profilestatus=findViewById(R.id.settings_status);
       username=findViewById(R.id.settings_username);profilename=findViewById(R.id.settings_profilename);
       country=findViewById(R.id.settings_country);
       dateofbirth=findViewById(R.id.settings_Dateofbirth);
       gender=findViewById(R.id.settings_gender);
       relationshipstatus=findViewById(R.id.settings_relationshipstatus);
       savesettings=findViewById(R.id.settings_button);
       final CircleImageView profileimgae=findViewById(R.id.settings_profile_image);
       settingsuserref=FirebaseDatabase.getInstance().getReference().child("users").child(Currentuserid);
       settingsuserref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   String myProfileStatus=dataSnapshot.child("profilestatus").getValue().toString();
                   String myProfileusername=dataSnapshot.child("username").getValue().toString();
                   String myprofilename=dataSnapshot.child("fullname").getValue().toString();
                   String myProfilecountry=dataSnapshot.child("country").getValue().toString();
                   String myProfildateofbirth=dataSnapshot.child("dateofbirth").getValue().toString();
                   String myProfilegender=dataSnapshot.child("gender").getValue().toString();
                   String myProfilerelationship=dataSnapshot.child("Relationshipstatus").getValue().toString();
                   myprofileimg=dataSnapshot.child("profileimage").getValue().toString();
                   Picasso.get().load(myprofileimg).placeholder(R.drawable.profile).into(profileimgae);
profilestatus.setText(myProfileStatus);
username.setText(myProfileusername);
country.setText(myProfilecountry);
dateofbirth.setText(myProfildateofbirth);
gender.setText(myProfilegender);
relationshipstatus.setText(myProfilerelationship);
profilename.setText(myprofilename);

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
savesettings.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Validateaccountinfo();
    }
});
        profileimgae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintent=new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent, 158);
            }


        });
        //retrieving image from storage
        settingsuserref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String image=dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(profileimgae);

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==158 ){

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
            Uri ImageUri=data.getData();
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resulturi=result.getUri();
                final StorageReference filepath=userprofileimageRef.child( Currentuserid+".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            final ProgressDialog progress = new ProgressDialog(settings.this);
                            final Timer t = new Timer();
                            progress.setTitle("Loading");
                            progress.setMessage("Wait while uploading...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();
                            t.schedule(new TimerTask() {
                                public void run() {
                                    progress.dismiss();
                                    // when the task active then close the dialog
                                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                                }
                            }, 4000);
                            /*final String downloadurl= task.getResult().getStorage().getDownloadUrl().toString();*/
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadurl=uri.toString();
                                    settingsuserref.child("profileimage").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(settings.this,"image stored",Toast.LENGTH_LONG).show();


                                        }
                                    });
                                }
                            });

                        }
                    }


                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                });

            }
        }
    }


    private void Validateaccountinfo() {
        //profilestatus,username,profilename,country,dateofbirth,gender,relationshipstatus;
        String PROFILESATUS=profilestatus.getText().toString();
        String USERNAME=username.getText().toString();
        String PROFILENAME=profilename.getText().toString();
        String COUNTRY=country.getText().toString();
        String DATEOFBIRTH=dateofbirth.getText().toString();
        String GENDER=gender.getText().toString();

        String RELATIONSJIPSTATUS=relationshipstatus.getText().toString();
        if(PROFILESATUS.isEmpty()){
            profilestatus.setError("please enter  profile status");
        }
        else if(USERNAME.isEmpty()){
            username.setError("please enter username");
        }
        else if(PROFILENAME.isEmpty()){
            profilename.setError("please enter profilename");
        }
        else if(COUNTRY.isEmpty()){
            country.setError("please enter country");
        }
        else if(DATEOFBIRTH.isEmpty()){
            dateofbirth.setError("please enter date");
        }
        else if(GENDER.isEmpty()){
            gender.setError("please enter gender");
        }
        else if(RELATIONSJIPSTATUS.isEmpty()){
            relationshipstatus.setError("please enter relationship status");

        }
        else{

            Updateaccountinfo(PROFILESATUS,USERNAME,PROFILENAME,COUNTRY,DATEOFBIRTH,GENDER,RELATIONSJIPSTATUS);
        }

    }

    private void Updateaccountinfo(String profilesatus, String username, String profilename, String country, String dateofbirth, String gender, String relationsjipstatus) {
        HashMap account=new HashMap();
        account.put("username",username);
        account.put("fullname",profilename);
        account.put("dateofbirth",dateofbirth);
        account.put("profilestatus",profilesatus);
        account.put("Relationshipstatus",relationsjipstatus);
        account.put("country",country);
        account.put("gender",gender);
        settingsuserref.updateChildren(account).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
sendusertomainactivity();
                Toast.makeText(settings.this,"Data Updated",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendusertomainactivity() {
        Intent mainac=new Intent(settings.this,MainActivity.class);
        startActivity(mainac);
        finish();
    }


}
