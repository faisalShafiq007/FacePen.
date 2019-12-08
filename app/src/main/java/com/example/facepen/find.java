package com.example.facepen;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
public class find extends AppCompatActivity {
    private ImageButton SearchButton=null;
     private EditText SearchInputText=null;
    private RecyclerView SearchResultList=null;
    private DatabaseReference allUsersdatabaseref;
    Query searchpeopleandfriendsquery=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        getSupportActionBar().setTitle("Find");
        SearchInputText=findViewById(R.id.search_box_input);

        allUsersdatabaseref= FirebaseDatabase.getInstance().getReference().child("users");

        SearchResultList=findViewById(R.id.search_result_list);
SearchResultList.setHasFixedSize(true);
SearchResultList.setLayoutManager(new LinearLayoutManager(this));

SearchButton= findViewById(R.id.search_people_friends_button);

 SearchButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         String searchBoxInput=SearchInputText.getText().toString();


         SearchPeopleAndFriends(searchBoxInput);
     }
 });

    }

 public void SearchPeopleAndFriends(String searchBoxInput) {
     searchpeopleandfriendsquery= allUsersdatabaseref.orderByChild("fullname").startAt(searchBoxInput)
             .endAt(searchBoxInput.concat("\uf8ff"));
        Toast.makeText(this,"Searching",Toast.LENGTH_SHORT).show();
     FirebaseRecyclerAdapter<findfriends,FindFriendsViewholder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<findfriends, FindFriendsViewholder>(
                findfriends.class, R.layout.all_user_display_layout,FindFriendsViewholder.class,searchpeopleandfriendsquery) {
            @Override
            protected void populateViewHolder(FindFriendsViewholder viewHolder, findfriends model, final int position) {


                viewHolder.setFullname(model.getFullname());
                viewHolder.setProfilestatus(model.getProfilestatus());
                viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String visit_user_id= getRef(position).getKey();
                    Intent profileintent=new Intent(find.this,personprofile.class);
                    profileintent.putExtra("visit_user_id",visit_user_id);
                    startActivity(profileintent);
                }
            });
            }


        };
SearchResultList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FindFriendsViewholder extends RecyclerView.ViewHolder{

        View mView;

        public FindFriendsViewholder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setFullname(String fullname) {
            TextView myName=mView.findViewById(R.id.all_users_profile_fullname);
            myName.setText(fullname);}

        public void setProfilestatus(String profilestatus) {
            TextView myStatus=mView.findViewById(R.id.all_users_status);
            myStatus.setText(profilestatus);
        }
        public void setProfileimage(Context ctx,String profileimage) {
            CircleImageView myImage=mView.findViewById(R.id.all_users_profle_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }
    }
}