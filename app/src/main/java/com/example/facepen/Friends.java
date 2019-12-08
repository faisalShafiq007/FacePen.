package com.example.facepen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Friends extends AppCompatActivity {

    RecyclerView myfriendlist;
    DatabaseReference Friendsref,Userref;
    FirebaseAuth mAuth;
    String Online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        getSupportActionBar().setTitle("Friends");
        myfriendlist=findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();
        Online_user_id=mAuth.getCurrentUser().getUid();
        Friendsref= FirebaseDatabase.getInstance().getReference().child("friends").child(Online_user_id);
        Userref=FirebaseDatabase.getInstance().getReference().child("users");
        myfriendlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Friends.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myfriendlist.setLayoutManager(linearLayoutManager);
        DisplayAllfriends();
    }

    private void DisplayAllfriends() {
        FirebaseRecyclerAdapter<friendsclass,friendsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<friendsclass, friendsViewHolder>(
                friendsclass.class,R.layout.all_user_display_layout,friendsViewHolder.class,Friendsref
        ) {
            @Override
            protected void populateViewHolder(final friendsViewHolder viewHolder, final friendsclass model, final int position) {
                viewHolder.setDate(model.getDate());
                 final String userIDS=getRef(position).getKey();
                Userref.child(userIDS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                           final String username=dataSnapshot.child("username").getValue().toString();
                            final String profileimage=dataSnapshot.child("profileimage").getValue().toString();
                            viewHolder.setFullname(username);
                            viewHolder.setProfileimage(getApplicationContext(),profileimage);
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CharSequence options[]=new CharSequence[]{
                                        username +"'s Profile",
                                            ""
                                    };
                                    AlertDialog.Builder builder=new AlertDialog.Builder(Friends.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                         public void onClick(DialogInterface dialog, int which) {
                                            if(which==0){
                                                Intent profileintent=new Intent(Friends.this,personprofile.class);
                                                profileintent.putExtra("visit_user_id",userIDS).toString();
                                                startActivity(profileintent);
                                            }
                                            if(which==1){
                                                Intent Chatintent=new Intent(Friends.this,ChatActivity.class);
                                                String userIDS=getRef(position).getKey();
                                                Chatintent.putExtra("visit_user_id",userIDS);
                                                Chatintent.putExtra("userName",username );
                                                startActivity(Chatintent);

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        myfriendlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class friendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public friendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setFullname(String fullname) {
            TextView myName=mView.findViewById(R.id.all_users_profile_fullname);
            myName.setText(fullname);}

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView myImage=mView.findViewById(R.id.all_users_profle_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }
        public void setDate(String date){
            TextView friendsdate=mView.findViewById(R.id.all_users_status);
            friendsdate.setText(date);
        }
    }
}
