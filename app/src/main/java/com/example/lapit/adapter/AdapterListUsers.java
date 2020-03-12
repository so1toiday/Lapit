package com.example.lapit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.example.lapit.ui.acitivity.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterListUsers extends RecyclerView.Adapter<AdapterListUsers.viewholder> {
    Context context;
    List<User> list;

    public AdapterListUsers(Context context, List<User> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        User user=list.get(position);
        holder.mDisplayName.setText(user.getName());
        holder.mStatus.setText(user.getStatus());
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.mUserImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView mStatus,mDisplayName;
        ImageView mUserImage;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            mUserImage=itemView.findViewById(R.id.imUser);
            mDisplayName=itemView.findViewById(R.id.tvDisplayName);
            mStatus=itemView.findViewById(R.id.tvStatus);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent i= new Intent(context, ProfileActivity.class);
                   i.putExtra("id",list.get(getLayoutPosition()).getUid());
                    context.startActivity(i);
                }
            });
        }
    }
}
