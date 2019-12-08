package com.example.facepen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
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
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;


public class newpost extends AppCompatActivity {
    private static final int GALLERY_PICK = 1;
private Uri imageuri;
     ImageButton pstimage;
     String descriptin;
    EditText description;
    Button addpost;
    StorageReference postimageRef;
    DatabaseReference userRef ;
    DatabaseReference postref;
    private String savecurrentdate,savecurrenttime,postrandomname, downloadurl;
    String Current_user_id;
    FirebaseAuth mAuth ;
    ProgressDialog progress;
    Timer t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
    userRef= FirebaseDatabase.getInstance().getReference().child("users");
      pstimage=findViewById(R.id.pooost_image);
    postref= FirebaseDatabase.getInstance().getReference().child("Posts");
    getSupportActionBar().setTitle("Add new post");
    addpost=findViewById(R.id.newpostbutton);
    mAuth = FirebaseAuth.getInstance();
        progress= new ProgressDialog(newpost.this);
        t = new Timer();
    Current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    postimageRef= FirebaseStorage.getInstance().getReference();
    description=findViewById(R.id.description);
addpost.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
Validatepostinfo();
    }
});
pstimage.setOnClickListener(
        new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        OpenGallery();
    }
});

    }

    private void Validatepostinfo() {
        descriptin=description.getText().toString();
        if (imageuri == null) {

            Toast.makeText(newpost.this,"Please add picture",Toast.LENGTH_LONG);
        } else if (descriptin.isEmpty()) {

            description.setError("We Love To Read Something About This Pic");
        } else  {

storingimagetofirebasestorage();
        }
}

    private void storingimagetofirebasestorage() {
        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat datFormat= new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=datFormat.format(calfordate.getTime());

        Calendar calfortime= Calendar.getInstance();
        SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm");
        savecurrenttime=timeFormat.format(calfordate.getTime());
        postrandomname=savecurrentdate.concat(savecurrenttime);
    final StorageReference filepath=postimageRef.child("Post images").child(imageuri.getLastPathSegment() + postrandomname +".jpg");
    filepath.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

            if(task.isSuccessful()){
                progress.setTitle("Adding Post");
                progress.setMessage("Wait while We Are Adding Your Post...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                t.schedule(new TimerTask() {
                    public void run() {
                        progress.dismiss();
                        Intent mainactivity=new Intent(newpost.this,MainActivity.class);
                        startActivity(mainactivity);
                        // when the task active then close the dialog
                        t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
                    }
                }, 4500);
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadurl=uri.toString();

                        userRef.child(Current_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                    String username = dataSnapshot.child("fullname").getValue().toString();
                                    String userprofileimage = dataSnapshot.child("profileimage").getValue().toString();
                                    HashMap postmap=new HashMap();
                                    postmap.put("uid",Current_user_id);
                                    postmap.put("date",savecurrentdate);
                                    postmap.put("time",savecurrenttime);
                                    postmap.put("descryption",descriptin);

                                    postmap.put("postimage",downloadurl);
                                    postmap.put("profileimage",userprofileimage);
                                    postmap.put("fullname",username);
                                    postref.child(Current_user_id.concat( postrandomname)).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){
                                              // after 2 second (or 2000 miliseconds), the task will be active.
                                            }
                                            else{
                                                Toast.makeText(newpost.this,"updation failed",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                }
                                else{

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }

            else{
                Toast.makeText(newpost.this,"sorry",Toast.LENGTH_SHORT).show();
            }
        }
    });

}

    private void OpenGallery() {
        Intent galleryintent=new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK&&resultCode==RESULT_OK&&data!=null) {
            imageuri = data.getData();
            pstimage.setImageURI(imageuri);
                Picasso.get().load(imageuri).
                        resize(900, 600).into(pstimage);
            pstimage.setImageURI(null);
        }
    }



}
