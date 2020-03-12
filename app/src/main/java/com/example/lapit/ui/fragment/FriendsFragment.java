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

import com.example.lapit.R;
import com.example.lapit.adapter.AdapterListFriends;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
RecyclerView mListFriends;
DatabaseReference mDataFriends;
DatabaseReference mUserFriends;
FirebaseUser mCurrentUser;
List<User> ListUserFriends;
List<String> listid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListFriends=view.findViewById(R.id.reListFriends);
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mUserFriends=FirebaseDatabase.getInstance().getReference("users");
        mDataFriends=FirebaseDatabase.getInstance().getReference("friends").child(mCurrentUser.getUid());;
        mListFriends.setHasFixedSize(true);
        mListFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        dataFriendsChanged();

    }

    private void dataFriendsChanged() {
        mDataFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listid=new ArrayList<>();
                ListUserFriends=new ArrayList<>();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String id=snapshot.getKey();
                    listid.add(id);
                }

                for(int i=0;i<listid.size();i++){
                    final int finalI = i;
                    mUserFriends.child(listid.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user=dataSnapshot.getValue(User.class);
                            user.setUid(dataSnapshot.getKey());
                            ListUserFriends.add(user);
                            if(finalI ==listid.size()-1){
                                AdapterListFriends adapterListFriends=new AdapterListFriends(getContext(),ListUserFriends);
                                mListFriends.setAdapter(adapterListFriends);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
