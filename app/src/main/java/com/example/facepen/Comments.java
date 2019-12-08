package com.example.facepen;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class Comments extends AppCompatActivity {
 ImageButton Postcommentbtn;
 EditText Comentinputtext;
 RecyclerView Commentlist;
 String Post_key;
DatabaseReference Usersref,postsref;
String current_user_id;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
mAuth=FirebaseAuth.getInstance();
current_user_id=mAuth.getCurrentUser().getUid();
Post_key=getIntent().getExtras().get("Postkey").toString();
getSupportActionBar().setTitle("Comments");
        postsref= FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_key).child("Comments");
        Commentlist=findViewById(R.id.comments_list);
        Commentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Commentlist.setLayoutManager(linearLayoutManager);
        Comentinputtext=findViewById(R.id.comment_input);
        Postcommentbtn=findViewById(R.id.post_comment_btn);
        Usersref= FirebaseDatabase.getInstance().getReference().child("users");
        Postcommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Usersref.child(current_user_id).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
   if(dataSnapshot.exists()){
       String username=dataSnapshot.child("fullname").getValue().toString();
       validatecomments(username);
       Comentinputtext.setText("");
   }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<comments_class,Commentsviewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<comments_class, Commentsviewholder>(
comments_class.class,
                R.layout.all_comment_layout,
                Commentsviewholder.class
                ,postsref
        ) {
            @Override
            protected void populateViewHolder(Commentsviewholder viewHolder, comments_class model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());

            }
        };
        Commentlist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class Commentsviewholder extends RecyclerView.ViewHolder{
View mView;

        public Commentsviewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTime(String time) {
            TextView mytime=mView.findViewById(R.id.comment_time);
            mytime.setText("  Time:  "+time+"");

        }
        public void setUsername(String username) {
            TextView myusername=mView.findViewById(R.id.comment_user_name);
            myusername.setText("  "+username+"  ");

        }
        public void setDate(String date) {
            TextView mydate=mView.findViewById(R.id.comment_date);
            mydate.setText("  Date:  "+date+"");

        }
        public void setComment(String comment) {
            TextView mycomment=mView.findViewById(R.id.comment_text);
            mycomment.setText(comment);
        }


    }

    private void validatecomments(String username) {
    String commenttext=Comentinputtext.getText().toString();
    if(TextUtils.isEmpty(commenttext)){
        Toast.makeText(this,"please enter comment",Toast.LENGTH_SHORT).show();

    }
    else{
        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat datFormat= new SimpleDateFormat("dd-MMMM-yyyy");
       final String savecurrentdate=datFormat.format(calfordate.getTime());
        Calendar calfortime= Calendar.getInstance();
        SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm");
        final String  savecurrenttime=timeFormat.format(calfordate.getTime());
final String Randomkey=current_user_id+savecurrentdate+savecurrenttime;
        HashMap commentsmap=new HashMap();
        commentsmap.put("uid",current_user_id);
        commentsmap.put("comment",commenttext);
        commentsmap.put("date",savecurrentdate);
        commentsmap.put("time",savecurrenttime);
        commentsmap.put("username",username);
        postsref.child(Randomkey).updateChildren(commentsmap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
           if(task.isSuccessful()){
               Log.e("success","you have entered successfully");

           }
           else{
               Log.e("failed","");
           }
            }
        });
    }
    }


}
