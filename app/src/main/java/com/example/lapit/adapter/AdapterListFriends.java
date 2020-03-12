package com.example.lapit.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.example.lapit.ui.acitivity.ProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterListFriends extends RecyclerView.Adapter<AdapterListFriends.viewholder> {
    Context context;
    List<User> list;
    DatabaseReference mDataFriends;

    public AdapterListFriends(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_friend, parent, false);
        mDataFriends = FirebaseDatabase.getInstance().getReference("users");
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        User user = list.get(position);
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.mCircleImage);
        holder.mDisplayName.setText(user.getName());
        holder.mStatus.setText(user.getStatus());
        mDataFriends.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("online")) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("online")) {
                        holder.mOnline.setImageResource(R.drawable.online);
                    } else {
                        holder.mOnline.setImageResource(R.drawable.off);
                    }
                }
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
        CircleImageView mCircleImage;
        TextView mDisplayName, mStatus;
        ImageView mOnline;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            mCircleImage = itemView.findViewById(R.id.imUser);
            mDisplayName = itemView.findViewById(R.id.tvDisplayName);
            mStatus = itemView.findViewById(R.id.tvStatus);
            mOnline = itemView.findViewById(R.id.imOnline);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProfileActivity.class);
                    i.putExtra("id", list.get(getLayoutPosition()).getUid());
                    context.startActivity(i);
                }
            });
        }
    }
}
