package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.SearchView;

import com.example.lapit.R;
import com.example.lapit.adapter.AdapterListUsers;
import com.example.lapit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
private SearchView mSearchUser;
private RecyclerView mreUser;
private DatabaseReference mUserReferebce;
private List<User> list;
private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("ALL USERS");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressDialog =new ProgressDialog(UsersActivity.this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mProgressDialog.show();
        Anhxa();
        mUserReferebce= FirebaseDatabase.getInstance().getReference().child("users");
        mUserReferebce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list=new ArrayList<>();
                String mCurrenUID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(!snapshot.getKey().equals(mCurrenUID)){
                        User user=snapshot.getValue(User.class);
                        user.setUid(snapshot.getKey());
                        list.add(user);
                    }
                }
                if(list!=null){
                    AdapterListUsers adapter=new AdapterListUsers(UsersActivity.this,list);
                    mreUser.setAdapter(adapter);
                    mreUser.setHasFixedSize(true);
                    mreUser.setLayoutManager(new LinearLayoutManager(UsersActivity.this));
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void Anhxa() {
        mSearchUser=findViewById(R.id.svSearchUser);
        mreUser=findViewById(R.id.reUser);
    }
}
