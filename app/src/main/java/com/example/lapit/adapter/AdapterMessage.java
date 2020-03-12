package com.example.lapit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapit.R;
import com.example.lapit.model.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.viewholder> {
    Context context;
    List<Message> list;
    String image;

    public AdapterMessage(Context context, List<Message> list, String image) {
        this.context = context;
        this.list = list;
        this.image = image;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Message ms = list.get(position);

        if (ms.getRq_type().equals("received")) {
            Picasso.with(context).load(image).placeholder(R.drawable.user).into(holder.mImageMessage);
            holder.mMessageleft.setText(ms.getText());
            holder.right.setVisibility(View.GONE);
            holder.left.setVisibility(View.VISIBLE);
        } else if (ms.getRq_type().equals("sent")) {
            holder.mMessageright.setText(ms.getText());
            holder.left.setVisibility(View.GONE);
            holder.right.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        LinearLayout left,right;
        TextView mMessageleft,mMessageright;
        CircleImageView mImageMessage;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            left = itemView.findViewById(R.id.left);
            right = itemView.findViewById(R.id.right);
            mMessageleft = itemView.findViewById(R.id.tvMessageleft);
            mMessageright = itemView.findViewById(R.id.tvMessageright);
            mImageMessage = itemView.findViewById(R.id.imUserMessage);
        }
    }


}
