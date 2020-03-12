package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.lapit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayname, mUsername, mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mReferen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();
    }

    private void Anhxa() {
        mDisplayname = findViewById(R.id.tip_display_name);
        mUsername = findViewById(R.id.tip_username);
        mPassword = findViewById(R.id.tip_password);
    }

    public void clickCreateAccount(View view) {
        String display_name = mDisplayname.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();
        String username = mUsername.getEditText().getText().toString();
        RegisterUsername(display_name, username, password);
    }

    private void RegisterUsername(final String Displayname, String Username, String Password) {
        final ProgressDialog mdialog = new ProgressDialog(RegisterActivity.this);
        mdialog.setTitle("Đăng Ký Tài Khoản");
        mdialog.show();
        mdialog.setMessage("vui lòng chờ trong giây lát");
        mdialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAuth.createUserWithEmailAndPassword(Username, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    String UserID = mUser.getUid();
                    mReferen = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
                    HashMap<String, String> UserData = new HashMap<>();
                    UserData.put("name", Displayname);
                    UserData.put("status", "Tôi đang sử dụng app chat");
                    mdialog.dismiss();
                    mReferen.setValue(UserData);
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    mdialog.dismiss();
                }
            }
        });
    }
}