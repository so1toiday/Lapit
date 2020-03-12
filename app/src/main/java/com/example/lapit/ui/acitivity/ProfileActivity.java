package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileSatus, mProfileFriendCount;
    private DatabaseReference mreference;
    private String uid;
    private Context context;
    private Button mbtnSend, mbtnDecide;
    private DatabaseReference mDatabaseFriendRequest;
    private FirebaseUser mCurrentUser;
    private String mCurrentState = "not_friends";
    private DatabaseReference mDataFriends;
    private static final String RECEIVED = "received";
    private static final String NOT_FRIEND = "not_friends";
    private static final String REQ_SENT = "req_sent";
    private static final String REQ_RECEIVED = "req_received";
    private static final String FRIENDS = "friends";
    private ProgressDialog mProgressDialog;
    private String type = "";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Anhxa();
        context = ProfileActivity.this;
        Intent i = getIntent();
        if (i.hasExtra("id")) {
            uid = i.getStringExtra("id");
        }
        getInstance();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                user.setUid(uid);
                Picasso.with(context).load(user.getImage()).placeholder(R.drawable.user).into(mProfileImage);
                mProfileName.setText(user.getName());
                mProfileSatus.setText(user.getStatus());

                //kiểm tra yêu cầu kết bạn
                mDatabaseFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uid)) {
                            type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if (type.equals(RECEIVED)) {
                                mCurrentState = REQ_RECEIVED;
                                mbtnSend.setText("Chấp Nhận");
                                mbtnDecide.setText("Từ Chối");
                                mbtnDecide.setBackgroundColor(getResources().getColor(R.color.orange));
                            } else {
                                mCurrentState = REQ_SENT;
                                mbtnSend.setText("Hủy Yêu Cầu");
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDataFriends.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)) {
                    mbtnSend.setText("BẠN BÈ");
                    mCurrentState = FRIENDS;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDataFriends.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProfileFriendCount.setText(dataSnapshot.getChildrenCount() + " Friends");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getInstance() {
        mreference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabaseFriendRequest = FirebaseDatabase.getInstance().getReference("friend_rq");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDataFriends = FirebaseDatabase.getInstance().getReference("friends");
    }

    private void Anhxa() {
        mProfileImage = findViewById(R.id.imProfileImage);
        mProfileName = findViewById(R.id.tvProfileName);
        mProfileSatus = findViewById(R.id.tvProfileStatus);
        mProfileFriendCount = findViewById(R.id.tvProfileFriendCount);
        mbtnSend = findViewById(R.id.btnSend);
        mbtnDecide = findViewById(R.id.btndecide);
    }

    public void clickSendRequest(View view) {
        // Trạng thái hiện tại không phải bạn bè
        if (mCurrentState.equals(NOT_FRIEND)) {
            mProgressDialog.show();
            mbtnSend.setEnabled(false);
            mDatabaseFriendRequest.child(mCurrentUser.getUid()).child(uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDatabaseFriendRequest.child(uid).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Đã gửi yêu cầu kết bạn thành công", Toast.LENGTH_SHORT).show();
                                    mbtnSend.setEnabled(true);
                                    mbtnSend.setText("Hủy Yêu Cầu");
                                    mCurrentState = REQ_SENT;
                                    mProgressDialog.dismiss();
                                }

                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Thất Bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

//Trạng thái hiện tại gửi yêu cầu kết bạn
        if (mCurrentState.equals(REQ_SENT)) {
            mProgressDialog.show();
            CancelSent();
        }
        //trạng thái đã nhận yêu cầu kết bạn
        if (mCurrentState.equals(REQ_RECEIVED)) {
            mProgressDialog.show();
            final String currentDate = DateFormat.getDateInstance().format(new Date());
            mDataFriends.child(mCurrentUser.getUid()).child(uid).setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mDataFriends.child(uid).child(mCurrentUser.getUid()).setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mbtnSend.setEnabled(false);
                                    mDatabaseFriendRequest.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabaseFriendRequest.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mbtnSend.setEnabled(true);
                                                    mbtnSend.setText("BẠN BÈ");
                                                    mbtnDecide.setText("Nhắn Tin");
                                                    mbtnDecide.setBackgroundColor(getResources().getColor(R.color.blue));
                                                    mCurrentState = FRIENDS;
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(context, "Đã Kết Bạn", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
        // trạng thái là bạn bè
        if (mCurrentState.equals(FRIENDS)) {
            AlertDialog mdialog = new AlertDialog.Builder(context).create();
            mdialog.setMessage("Bạn thực sự muốn hủy bạn bè à");
            mdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mdialog.setButton(DialogInterface.BUTTON_POSITIVE, "Xác Nhận", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mProgressDialog.show();
                    mDataFriends.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDataFriends.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrentState = NOT_FRIEND;
                                    mbtnSend.setText("Kết Bạn");
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Đã hủy kết bạn", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
            mdialog.show();

        }

    }

    private void CancelSent() {
        mbtnSend.setEnabled(false);
        mDatabaseFriendRequest.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseFriendRequest.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mbtnSend.setEnabled(true);
                        mbtnSend.setText("Kết Bạn");
                        mCurrentState = NOT_FRIEND;
                        Toast.makeText(context, "Đã hủy yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            }
        });

    }


    public void clickMessage(View view) {
//        if (mCurrentState.equals(FRIENDS)) {
//        }
        if (mCurrentState.equals(REQ_RECEIVED)) {
            mbtnDecide.setEnabled(false);
            mDatabaseFriendRequest.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabaseFriendRequest.child(uid).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mbtnDecide.setEnabled(true);
                            mbtnSend.setText("Kết Bạn");
                            mCurrentState = NOT_FRIEND;
                            mbtnDecide.setText("Nhắn Tin");
                            mbtnDecide.setBackgroundColor(getResources().getColor(R.color.blue));
                            Toast.makeText(ProfileActivity.this, "Đã từ chối kết bạn", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                }
            });

        }else {
            if(user!=null && user.getUid()!=null){
                Intent i=new Intent(ProfileActivity.this, ChatActivity.class);
                i.putExtra("user_name",user.getName());
                i.putExtra("user_id",user.getUid());
                i.putExtra("image",user.getImage());
                startActivity(i);
            }


        }
    }
}