package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lapit.R;
import com.example.lapit.adapter.AdapterViewpager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mauth;
    private ViewPager vp;
    private TabLayout tab;
    private AdapterViewpager adapter;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mauth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Lapit Chat");
        setSupportActionBar(toolbar);
        Anhxa();
        mRef = FirebaseDatabase.getInstance().getReference("users").child(mauth.getCurrentUser().getUid());
        adapter = new AdapterViewpager(getSupportFragmentManager());
        vp.setOffscreenPageLimit(3);
        vp.setAdapter(adapter);
        vp.setCurrentItem(1);
        tab.setupWithViewPager(vp);
    }

    private void Anhxa() {
        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            mRef.child("online").setValue("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            mRef.child("online").setValue(System.currentTimeMillis());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.alluser:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
