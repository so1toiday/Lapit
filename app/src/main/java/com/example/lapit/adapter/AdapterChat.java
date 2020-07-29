package com.example.lapit.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.example.lapit.ui.acitivity.ChatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.viewholder> {
List<String> list;
Context context;
DatabaseReference mref;
String UID;
    public AdapterChat(Context context,List<String> list,String UID){
        this.context=context;
        this.list=list;
        mref= FirebaseDatabase.getInstance().getReference();
        this.UID=UID;
    }
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chat, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        mref.child("chat").child(UID).child(list.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("time")){
                    Long timestamps= dataSnapshot.child("time").getValue(Long.class);
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
                   String date= dateFormat.format(timestamps);
                   holder.mTime.setText(date);
                }

                if(dataSnapshot.hasChild("message")){
                    String Message= dataSnapshot.child("message").getValue(String.class);
                    holder.mChatStatus.setText(Message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mref.child("users").child(list.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final User user=dataSnapshot.getValue(User.class);
                Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.mImage);
                holder.mChatName.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
      CircleImageView mImage;
      TextView mChatName;
      TextView mChatStatus;
      TextView mTime;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            mImage=itemView.findViewById(R.id.imChatImage);
            mChatName=itemView.findViewById(R.id.tvChatName);
            mChatStatus=itemView.findViewById(R.id.tvChatStatus);
            mTime=itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mref.child("users").child(list.get(getLayoutPosition())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            final User user=dataSnapshot.getValue(User.class);
                            Intent i=new Intent(context, ChatActivity.class);
                            i.putExtra("user_name",user.getName());
                            i.putExtra("user_id",dataSnapshot.getKey());
                            i.putExtra("image",user.getImage());
                            context.startActivity(i);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            });
        }
    }
}
