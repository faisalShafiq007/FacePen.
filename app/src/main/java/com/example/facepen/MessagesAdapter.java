package com.example.facepen;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private FirebaseAuth mAuth;
    private DatabaseReference usersdatabaseref;
    private List<Messages> usermessagesList;



    public MessagesAdapter (List<Messages>usermessagesList){
        this.usermessagesList=usermessagesList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView reciever_profile_image;
        TextView Sender_message_text,Reciever_message_text;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            Sender_message_text=(TextView) itemView.findViewById(R.id.sender_message_text);
            Reciever_message_text=(TextView)itemView.findViewById(R.id.reciever_message_text);
            reciever_profile_image=(CircleImageView)itemView.findViewById(R.id.message_profile_image);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

View V= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout_of_user,viewGroup,false);
mAuth=FirebaseAuth.getInstance();

return  new MessageViewHolder(V);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String message_sender_id=mAuth.getCurrentUser().getUid();
        Messages messags=usermessagesList.get(position);
        String fromUserid=messags.getFrom().toString();
        String fromMessagetype=messags.getType();
    usersdatabaseref= FirebaseDatabase.getInstance().getReference().child("users").child(fromUserid);
    usersdatabaseref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       if(dataSnapshot.exists()){
           String image=dataSnapshot.child("profileimage").getValue().toString();
           Picasso.get().load(image).into(holder.reciever_profile_image);
       }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    if(fromMessagetype.equals("text")){
        holder.Reciever_message_text.setVisibility(View.INVISIBLE);
        holder.reciever_profile_image.setVisibility(View.INVISIBLE);
        if(fromUserid.equals(message_sender_id)){
            holder.Sender_message_text.setGravity(Gravity.LEFT);
            holder.Sender_message_text.setText(messags.getMessage());

        }
        else{
            holder.Sender_message_text.setVisibility(View.INVISIBLE);
            holder.Reciever_message_text.setVisibility(View.VISIBLE);
            holder.reciever_profile_image.setVisibility(View.VISIBLE);

            holder.Reciever_message_text.setGravity(Gravity.LEFT);

            holder.Reciever_message_text.setText(messags.getMessage());
        }
    }
    }

    @Override
    public int getItemCount() {
        return usermessagesList.size();
    }
}
