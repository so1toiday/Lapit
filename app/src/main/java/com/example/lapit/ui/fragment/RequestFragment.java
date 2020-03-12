package com.example.lapit.ui.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lapit.R;
import com.example.lapit.adapter.AdapterRequest;
import com.example.lapit.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private RecyclerView mListRequest;
    private DatabaseReference mFriendRequest;
    private String mCurrentUID;
    private List<User> list;
    private DatabaseReference mReferenceUser;

    public RequestFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListRequest = view.findViewById(R.id.reRequestFriends);
        mListRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        mListRequest.setHasFixedSize(true);
        mFriendRequest = FirebaseDatabase.getInstance().getReference("friend_rq");
        mReferenceUser=FirebaseDatabase.getInstance().getReference("users");
        mCurrentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendRequest.child(mCurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    list=new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String mrequest_type=snapshot.child("request_type").getValue(String.class);
                        if(mrequest_type.equals("received")){
                            list.add(new User(snapshot.getKey()));

                        }
                    }

                    for(int i=0;i<list.size();i++){
                        final String id=list.get(i).getUid();
                        final int finalI = i;
                        mReferenceUser.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user=dataSnapshot.getValue(User.class);
                                user.setUid(id);
                                list.set(finalI,user);
                                if(finalI ==list.size()-1){
                                    AdapterRequest adapter=new AdapterRequest(getContext(),list);
                                    mListRequest.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(list.size()==0){
                        AdapterRequest adapter=new AdapterRequest(getContext(),list);
                        mListRequest.setAdapter(adapter);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
