package com.example.facepen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class clickpost extends AppCompatActivity {
    ImageView postimage;
    TextView postdescription;
    Button editpostbutton;
    Button deletepostbutton;
    String postkey;
    private DatabaseReference clickpostref;
    private FirebaseAuth mAuth;
    String Current_user_id;
    String Databaseuserid;
    String decription;
    String clickpostimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickpost);
        postimage=findViewById(R.id.clickpost_imageView);
        postdescription=findViewById(R.id.post_description);
       editpostbutton=findViewById(R.id.clickpost_edit);
      //to  only edit/delete his own posts only
       mAuth=FirebaseAuth.getInstance();
      Current_user_id=mAuth.getCurrentUser().getUid();
       deletepostbutton=findViewById(R.id.clickpost_delete);

       deletepostbutton.setVisibility(View.INVISIBLE);
       editpostbutton.setVisibility(View.INVISIBLE);
       postkey= getIntent().getExtras().get("postkey").toString();
       clickpostref= FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);

       clickpostref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.exists()){
                 Databaseuserid=dataSnapshot.child("uid").getValue().toString();

                 decription = dataSnapshot.child("descryption").getValue().toString();
                 clickpostimage=dataSnapshot.child("postimage").getValue().toString();
                 postdescription.setText(decription);
                 Picasso.get().load(clickpostimage).into(postimage);
                 if(Current_user_id.equals(Databaseuserid)){
                     deletepostbutton.setVisibility(View.VISIBLE);
                     editpostbutton.setVisibility(View.VISIBLE);

                 }

                 editpostbutton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         EditCurrentuser(decription);
                     }
                 });
             }
           }




           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
       deletepostbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DeleteCurrentpost();
           }
       });

    }

    private void deleteimagefromstorage(String clickpostimage) {
   FirebaseStorage mStorage=FirebaseStorage.getInstance();
        StorageReference photoRef = mStorage.getReferenceFromUrl(clickpostimage);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(clickpost.this,"This Post is deleted Succesfully",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!

            }
        });
    }

    private void EditCurrentuser(String decription) {
        AlertDialog.Builder builder=new AlertDialog.Builder(clickpost.this);
        builder.setTitle("Edit Post");
        final EditText inputfield=new EditText(clickpost.this);
inputfield.setText(decription);
builder.setView(inputfield);
builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
    clickpostref.child("descryption").setValue(inputfield.getText().toString());
        Toast.makeText(clickpost.this,"This Post is updated Succesfully",Toast.LENGTH_LONG).show();
senusertomainactivity();
    }
});
builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
dialog.cancel();
    }
});
        Dialog dialog=builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
    }

    private void DeleteCurrentpost() {
        clickpostref.removeValue();
        deleteimagefromstorage(clickpostimage);
        senusertomainactivity();
        Intent main=new Intent(clickpost.this,MainActivity.class);
        startActivity(main);
        finish();

    }

    private void senusertomainactivity() {
        Intent main=new Intent(clickpost.this,MainActivity.class);
        startActivity(main);
        finish();
    }
}
