package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lapit.R;
import com.example.lapit.adapter.AdapterMessage;
import com.example.lapit.function.getTimeAgo;
import com.example.lapit.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView mDisplayName, mLastSeen;
    private CircleImageView mImageUserReceive, mImageUserSent;
    private EditText mMessage;
    private RecyclerView mListMessage;
    private String ReceivedUID = "";
    private String SentUID = "";
    private DatabaseReference mUserReference;
    private DatabaseReference mMessageReferece;
    private FirebaseUser mCurrentUser;
    private List<Message> listMessage;
    private AdapterMessage adapterMessage;
    private  String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        listMessage=new ArrayList<>();
        Anhxa();
        getDataIntent();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mListMessage.setLayoutManager(linearLayoutManager);
        mUserReference = FirebaseDatabase.getInstance().getReference("users");
        mMessageReferece = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        SentUID = mCurrentUser.getUid();
        mMessageReferece.child("chat").child(SentUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(ReceivedUID)){
                    Map map=new HashMap();
                    map.put("message","No Message");
                    map.put("time",ServerValue.TIMESTAMP);

                    Map map2=new HashMap();
                    map2.put("chat/"+SentUID+"/"+ReceivedUID,map);
                    map2.put("chat/"+ReceivedUID+"/"+SentUID,map);
                    mMessageReferece.updateChildren(map2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUserReference.child(SentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image") && dataSnapshot.exists()) {
                    String image = dataSnapshot.child("image").getValue(String.class);
                    Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.user).into(mImageUserSent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mUserReference.child(SentUID).keepSynced(true);
        mUserReference.child(ReceivedUID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot mDataOnline = dataSnapshot.child("online");
                if (mDataOnline.exists()) {
                    String online = mDataOnline.getValue().toString();
                    if (online.equals("online")) {
                        mLastSeen.setText("online");
                    } else {
                        try {
                            long time = Long.parseLong(online);
                            String TimeAgo = getTimeAgo.getTime(time);
                            mLastSeen.setText(TimeAgo);

                        } catch (Exception e) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getMessage();

    }

    private void getDataIntent() {
        Intent i = getIntent();
        if (i.hasExtra("user_name")) {
            String UserName = i.getStringExtra("user_name");
            mDisplayName.setText(UserName);
        }
        if (i.hasExtra("user_id")) {
            ReceivedUID = i.getStringExtra("user_id");
        }
        if (i.hasExtra("image")) {
             url = i.getStringExtra("image");
            Picasso.with(ChatActivity.this).load(url).placeholder(R.drawable.user).into(mImageUserReceive);
        }
    }

    private void Anhxa() {
        mDisplayName = findViewById(R.id.tvDisplayName);
        mImageUserReceive = findViewById(R.id.imReceive);
        mImageUserSent = findViewById(R.id.imUserSent);
        mMessage = findViewById(R.id.etMessage);
        mListMessage = findViewById(R.id.reMessage);
        mLastSeen = findViewById(R.id.tvLastSeen);

    }

    public void sendMessage(View view) {
        sendMessage();
    }

    private void sendMessage() {
        // sent messager
        final String Message = mMessage.getText().toString();
        if (!TextUtils.isEmpty(Message)) {
            DatabaseReference senMessagekey = mMessageReferece.child("message").child(SentUID).child(ReceivedUID).push();
            String key = senMessagekey.getKey();
            final Map message = new HashMap();
            message.put("type", "text");
            message.put("text", Message);
            message.put("seen", false);
            message.put("time", ServerValue.TIMESTAMP);
            message.put("rq_type","sent");

            Map message2 = new HashMap();
            message2.put("type", "text");
            message2.put("text", Message);
            message2.put("seen", false);
            message2.put("time", ServerValue.TIMESTAMP);
            message2.put("rq_type","received");

            Map messageUser = new HashMap();
            messageUser.put("message/" + SentUID + "/" + ReceivedUID + "/" + key, message);
            messageUser.put("message/" + ReceivedUID + "/" + SentUID + "/" + key, message2);

            mMessageReferece.updateChildren(messageUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                 mMessage.setText("");
                }
            });

            Map lastMessage1=new HashMap();
            lastMessage1.put("time",ServerValue.TIMESTAMP);
            lastMessage1.put("message",Message);

            Map lastmassageUser=new HashMap();
            lastmassageUser.put("chat/"+SentUID + "/" + ReceivedUID ,lastMessage1);
            lastmassageUser.put("chat/"+ReceivedUID + "/" + SentUID ,lastMessage1);

            mMessageReferece.updateChildren(lastmassageUser);

        }
    }
    private  void getMessage(){
        mMessageReferece.child("message").child(SentUID).child(ReceivedUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listMessage=new ArrayList<>();
                for(DataSnapshot dt: dataSnapshot.getChildren()){
                    Message message=dt.getValue(Message.class);
                    listMessage.add(message);
                }
                adapterMessage=new AdapterMessage(ChatActivity.this,listMessage,url);
                mListMessage.setAdapter(adapterMessage);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
