package com.example.facepen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
RecyclerView usermessageslst;
ImageButton Sendmessagebtn,SendImagefilebtn;
List<Messages> messageslist=new ArrayList<>();
 LinearLayoutManager linearLayoutManager;
 MessagesAdapter messagesAdapter;
EditText userMesageInput;
String messageRecieverid,messagerecievername,messageSenderID,savecurrentdate,savecurrenttime;
DatabaseReference RootReference;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        messageRecieverid=getIntent().getExtras().get("visit_user_id").toString();
        messagerecievername=getIntent().getExtras().get("userName").toString();
            intialization();
        getSupportActionBar().setTitle(messagerecievername);
        Sendmessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
Fetchmessages();
    }

    private void intialization() {
        RootReference= FirebaseDatabase.getInstance().getReference();
        Sendmessagebtn=findViewById(R.id.send_message_button);
        SendImagefilebtn=findViewById(R.id.send_image_file_button);
        userMesageInput=findViewById(R.id.input_message);
        usermessageslst=findViewById(R.id.messages_list_users);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        messagesAdapter=new MessagesAdapter(messageslist);
        linearLayoutManager=new LinearLayoutManager(this);
        usermessageslst.setHasFixedSize(true);
        usermessageslst.setLayoutManager(linearLayoutManager);
        usermessageslst.setAdapter(messagesAdapter);

    }

    private void Fetchmessages() {
        RootReference.child("Messages").child(messageSenderID).child(messageRecieverid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messageslist.add(messages);
                    messagesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendMessage() {
    String Messagetext=userMesageInput.getText().toString();
    if(TextUtils.isEmpty(Messagetext)){
        userMesageInput.setError("Please type a message");
    }
    else{
        String message_sender_ref="Messages/"+messageSenderID+"/"+messageRecieverid;
        String message_reciever_ref="Messages/"+messageRecieverid +"/"+messageSenderID;
        DatabaseReference user_message_key=RootReference.child("Messages").child(messageSenderID).child(messageRecieverid).push();
        String message_push_id=user_message_key.getKey();
        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat datFormat= new SimpleDateFormat("dd-MMMM-yyyy");
        savecurrentdate=datFormat.format(calfordate.getTime());

        Calendar calfortime= Calendar.getInstance();
        SimpleDateFormat timeFormat= new SimpleDateFormat("HH:mm:ss");
        savecurrenttime=timeFormat.format(calfordate.getTime());
        Map messageTextBody=new HashMap();
        messageTextBody.put("message",Messagetext);
        messageTextBody.put("time",savecurrenttime);
        messageTextBody.put("date",savecurrentdate);
        messageTextBody.put("type","text");
        messageTextBody.put("from",messageSenderID);
        Map messagebodydetails=new HashMap();
        messagebodydetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
        messagebodydetails.put(message_reciever_ref+"/"+message_push_id,messageTextBody);
        RootReference.updateChildren(messagebodydetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
           if(task.isSuccessful()){
               Toast.makeText(ChatActivity.this,"message send successfully",Toast.LENGTH_SHORT).show();
               userMesageInput.setText("");
           }
           else{
               Toast.makeText(ChatActivity.this,"message send failed",Toast.LENGTH_SHORT).show();
               userMesageInput.setText("");
           }

            }
        });


    }
    }


}
