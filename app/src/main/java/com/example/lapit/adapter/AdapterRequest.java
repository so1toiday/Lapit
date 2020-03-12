package com.example.lapit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.viewholder> {
    Context context;
    private List<User> list;
    private DatabaseReference mRequestFriends;
    private DatabaseReference mFriends;
    private String mCurrenUID;

    public AdapterRequest(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_request, parent, false);
        mRequestFriends = FirebaseDatabase.getInstance().getReference("friend_rq");
        mFriends = FirebaseDatabase.getInstance().getReference("friends");
        mCurrenUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final User user = list.get(position);
        holder.mDisplayname.setText(user.getName());
        Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(holder.mAcceptUser);
        if (user.getUid() != null) {
            holder.mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRequestFriends.child(mCurrenUID).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRequestFriends.child(user.getUid()).child(mCurrenUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    final String CurrentDate= DateFormat.getDateTimeInstance().format(new Date());
                                    mFriends.child(mCurrenUID).child(user.getUid()).setValue(CurrentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriends.child(user.getUid()).child(mCurrenUID).setValue(CurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(context, "Chấp nhận kết bạn", Toast.LENGTH_SHORT).show();

                                                    }else {
                                                        Toast.makeText(context, "Kết bạn thất bại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }

        holder.mDecide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestFriends.child(mCurrenUID).child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRequestFriends.child(user.getUid()).child(mCurrenUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Từ chối kết bạn", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView mDisplayname;
        Button mAccept, mDecide;
        CircleImageView mAcceptUser;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            mDisplayname = itemView.findViewById(R.id.tvDisplayName);
            mAccept = itemView.findViewById(R.id.btnAccept);
            mDecide = itemView.findViewById(R.id.btnDecide);
            mAcceptUser = itemView.findViewById(R.id.imAcceptUser);
        }
    }
}
