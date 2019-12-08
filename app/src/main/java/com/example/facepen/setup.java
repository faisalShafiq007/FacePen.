package com.example.facepen;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import static android.app.PendingIntent.getActivity;

@SuppressWarnings("ALL")
public class setup extends AppCompatActivity {
    int GALLERY_PICK=1234;


  StorageReference userprofileimageRef;
    DatabaseReference userRef ;
 final  String Current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final CircleImageView circleImageView=findViewById(R.id.profile_image);
        final EditText profilestat=findViewById(R.id.profile_status);
        final EditText Relationstatus=findViewById(R.id.Relationshipstatus);
       final EditText username=findViewById(R.id.username);
        final EditText fullname=findViewById(R.id.fullname);
      final   EditText country=findViewById(R.id.country);
       final EditText gender=findViewById(R.id.Gender);
      final EditText dateob=findViewById(R.id.setup_Dateofbirth);
        final Timer t = new Timer();
        final ProgressDialog progress = new ProgressDialog(setup.this);
        getSupportActionBar().hide();
userRef= FirebaseDatabase.getInstance().getReference().child("users").child(Current_user_id);
Button save=findViewById(R.id.save);
        userprofileimageRef = FirebaseStorage.getInstance().getReference().child("profileimage");
save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        savesetupinfo();
    }
    private void savesetupinfo() {
        String profilestatus=profilestat.getText().toString();
        String Relationshpstatus=Relationstatus.getText().toString();
         String name=username.getText().toString();
         String f_name=fullname.getText().toString();
         String county=country.getText().toString();
         String gnder=gender.getText().toString();
String dateofbirth=dateob.getText().toString();

    if(name.isEmpty()){
        username.setError("enter username ");
    }
       else if(f_name.isEmpty()){
            fullname.setError("enter fullname ");
        }
    else if(county.isEmpty()){
        country.setError("enter country ");
    }
    else if(gnder.isEmpty()){
        gender.setError("enter gender ");
    }
    else if(profilestatus.isEmpty()){
        profilestat.setError("enter Profile Status");

    }
    else if(Relationshpstatus.isEmpty()){
       Relationstatus.setError("enter Profile Status");

    }

else if(dateofbirth.isEmpty()){
    dateob.setError("please enter date of birth");
        }

    else{
        HashMap usermap=new HashMap();
        usermap.put("username",name);
        usermap.put("fullname",f_name);
        usermap.put("dateofbirth",dateofbirth);
        usermap.put("profilestatus",profilestatus);
        usermap.put("Relationshipstatus",Relationshpstatus);
        usermap.put("country",county);
        usermap.put("gender",gnder);
        userRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    t.schedule(new TimerTask() {
                        public void run() {
                            progress.dismiss();
                            sendusertomain();
                           // when the task active then close the dialog
                            t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                        }
                    }, 2000); // after 2 second (or 2000 miliseconds), the task will be active.

                }
            else{
                    Toast.makeText(setup.this, "Error", Toast.LENGTH_SHORT);
                }



            }

            private void sendusertomain() {
                Intent main=new Intent(setup.this,MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }


    }
});
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintent=new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
              startActivityForResult(galleryintent, GALLERY_PICK);
            }


        });
        //retrieving image from storage
userRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if(dataSnapshot.exists()){
    String image=dataSnapshot.child("profileimage").getValue().toString();
    Picasso.get().load(image).placeholder(R.drawable.profile).into(circleImageView);

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

        if(requestCode==1234 ){

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
                final StorageReference filepath=userprofileimageRef.child(Current_user_id +".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            final ProgressDialog progress = new ProgressDialog(setup.this);
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
                                    userRef.child("profileimage").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                  /*Intent setupintent=new Intent(setup.this,setup.class);
                                  startActivity(setupintent);*/
                                            Toast.makeText(setup.this,"image stored",Toast.LENGTH_LONG).show();


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


    }




