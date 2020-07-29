package com.example.lapit.ui.acitivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.lapit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        FirebaseUser user=null;
        try {
            user=FirebaseAuth.getInstance().getCurrentUser();
        }catch (Exception e){}

        if(user!=null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void clickCreateAccount(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    public void clickAccount(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }
}
