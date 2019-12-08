package com.example.facepen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.design.widget.NavigationView.OnClickListener;
import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
   String currentuserid;
    private CircleImageView navprofileimage;
    private DatabaseReference userRef, Postsref,likeref;
    private TextView navprofileusername1;
    private RecyclerView postList;
    Boolean Likechecker=false;


    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        postList = findViewById(R.id.all_user_post_list);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        scoresRef.keepSynced(true);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);

        linearLayoutManager.setReverseLayout(true);

        linearLayoutManager.setStackFromEnd(true);

        postList.setLayoutManager(linearLayoutManager);
        final NavigationView navigationview = findViewById(R.id.nav_view);

        View headerView = navigationview.inflateHeaderView(R.layout.nav_header_main);
        Postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        likeref=FirebaseDatabase.getInstance().getReference("Likes");
        mAuth = FirebaseAuth.getInstance();


        Postsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DisplayAllUsersPosts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (mAuth.getCurrentUser() != null) {


            String Current_user_id = mAuth.getCurrentUser().getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users");

            navprofileimage = headerView.findViewById(R.id.nav_profile_image);
            navprofileusername1 = headerView.findViewById(R.id.nav_usermame);

            userRef.child(Current_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild("fullname")) {
                            String fullname = dataSnapshot.child("fullname").getValue().toString();
                            navprofileusername1.setText(fullname);
                        }
                        if (dataSnapshot.hasChild("profileimage")) {

                            String image = dataSnapshot.child("profileimage").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile).into(navprofileimage);
                        } else {
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DisplayAllUsersPosts();
        } else {
            Toast.makeText(MainActivity.this, "sorry to connect", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer;
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

DisplayAllUsersPosts();
    }
    private void DisplayAllUsersPosts() {
FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(
        Posts.class,R.layout.all_post_layout,PostsViewHolder.class,Postsref) {
    @Override
    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {
        final String post_key=getRef(position).getKey();

        viewHolder.setFullname(model.getFullname());
        viewHolder.setTime(model.getTime());
        viewHolder.setDate(model.getDate());
        viewHolder.setDescription(model.getDescryption());
        viewHolder.setLikeButtonStatus(post_key);
viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());
viewHolder.setPostimage(getApplicationContext(),model.getPostimage());
    viewHolder.mView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent clickpostintent=new Intent(MainActivity.this,clickpost.class);
            clickpostintent.putExtra("postkey",post_key);
            startActivity(clickpostintent);
        }
    });

viewHolder.commentpostbutton.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent commentintent=new Intent(MainActivity.this,Comments.class);
       commentintent.putExtra("Postkey",post_key);
        startActivity(commentintent);
    }
});

    viewHolder.LikepostButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
Likechecker=true;
likeref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        currentuserid=mAuth.getCurrentUser().getUid();
  if(Likechecker.equals(true)){
      if(dataSnapshot.child(post_key).hasChild(currentuserid)){

          likeref.child(post_key).child(currentuserid).removeValue();
          Likechecker=false;
      }
      else{
          likeref.child(post_key).child(currentuserid).setValue(true);
          Likechecker=false;
      }
  }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
        }
    });
    }
};
postList.setAdapter(firebaseRecyclerAdapter);


    }

        public static class PostsViewHolder extends RecyclerView.ViewHolder {
            View mView;
            ImageButton LikepostButton,commentpostbutton;
            TextView displaynumberoflike;
            int countlikes;
            String cureentuserId;
            DatabaseReference likeref;


            public PostsViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                LikepostButton=(ImageButton)mView.findViewById(R.id.like_button);
                commentpostbutton=(ImageButton) mView.findViewById(R.id.comment_button);
                displaynumberoflike=(TextView) mView.findViewById(R.id.display_number_of_likes);
likeref=FirebaseDatabase.getInstance().getReference().child("Likes");
            cureentuserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

            }
            public void setLikeButtonStatus(final String postkey){
                likeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.child(postkey).hasChild(cureentuserId)){
countlikes=(int) dataSnapshot.child(postkey).getChildrenCount();
                   LikepostButton.setImageResource(R.drawable.like);
                   displaynumberoflike.setText((Integer.toString(countlikes)+(" likes")));
                   }
                   else{
                       countlikes=(int) dataSnapshot.child(postkey).getChildrenCount();
                       LikepostButton.setImageResource(R.drawable.dislike);
                       displaynumberoflike.setText((Integer.toString(countlikes)+(" likes")));

                   }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            public void setFullname(String fullname) {
                TextView username = (TextView) mView.findViewById(R.id.post_user_name);
                username.setText(fullname);
            }

            public void setProfileimage(Context ctx, String profileimage) {
                CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
                Picasso.get().load(profileimage).into(image);
            }

            public void setTime(String time) {
                TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
                PostTime.setText("    " + time);
            }

            public void setDate(String date) {
                TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
                PostDate.setText("    " + date);
            }

            public void setDescription(String description) {
                TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
                PostDescription.setText(description);
            }

            public void setPostimage(Context ctx1, String postimage) {
                ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
                Picasso.get().load(postimage).into(PostImage);
            }
        }


        @Override
        public void onBackPressed () {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                DisplayAllUsersPosts();

            } else if (id == R.id.action_post) {
                sendusertopostactivity();
            }

            return super.onOptionsItemSelected(item);
        }

        private void sendusertopostactivity () {
            Intent postintent = new Intent(MainActivity.this, newpost.class);
            startActivity(postintent);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.home) {
                // Handle the camera action
            } else if (id == R.id.login) {
                Intent signin = new Intent(MainActivity.this, login.class);
                startActivity(signin);
            } else if (id == R.id.newpst) {
                Intent pstactvity = new Intent(MainActivity.this, newpost.class);
                startActivity(pstactvity);
            } else if (id == R.id.signup) {
                Intent sighup = new Intent(MainActivity.this, register.class);
                startActivity(sighup);
            } else if (id == R.id.Logout) {
                mAuth.signOut();
                sudusertologin();
            }
            else  if(id==R.id.settings){
                Intent settingsintent=new Intent(MainActivity.this,settings.class);
                startActivity(settingsintent);

            }
            else  if(id==R.id.message_aa){
                Intent settingsinte=new Intent(MainActivity.this,ChatActivity.class);
                startActivity(settingsinte);

            }
            else  if(id==R.id.find_friends){
                Intent f_friends=new Intent(MainActivity.this, find.class);
                startActivity(f_friends);

            }
            else if(id==R.id.profilenew){
                Intent profile=new Intent(MainActivity.this, com.example.facepen.profile.class);
                startActivity(profile);

            }
            else if(id==R.id.friends){
                Intent frienddd=new Intent(MainActivity.this, Friends.class);
                startActivity(frienddd);
            }



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        @Override
        protected void onStart () {
            super.onStart();
            FirebaseUser curntuser = mAuth.getCurrentUser();
            if (curntuser == null) {
                sudusertologin();
            } else {

            }
        }



        private void sudusertologin () {
            Intent sendusertologinactivity = new Intent(MainActivity.this, login.class);
            sendusertologinactivity.addFlags(sendusertologinactivity.FLAG_ACTIVITY_NEW_TASK | sendusertologinactivity.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(sendusertologinactivity);
            finish();
        }

    }

