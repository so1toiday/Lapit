package com.example.lapit.ui.acitivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapit.R;
import com.example.lapit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {
    private TextView mDisplayName, mStatus;
    private ImageView mImage;
    private DatabaseReference mReference;
    private static final int GALLERY_CODE = 999;
    private StorageReference mImageStore;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        final FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        UserID = User.getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserID);
        mReference.keepSynced(true);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mDisplayName.setText(user.getName());
                mStatus.setText(user.getStatus());
                if (user.getImage() != null) {
                    Picasso.with(SettingActivity.this).load(user.getImage()).into(mImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingActivity.this, "Lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });
        Anhxa();

    }

    private void Anhxa() {
        mDisplayName = findViewById(R.id.tvDisplayName);
        mStatus = findViewById(R.id.tvStatus);
        mImage = findViewById(R.id.imUser);
    }

    public void ChangImage(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_CODE);
    }

    public void ChangeStatus(View view) {
        final AlertDialog alertDialog=new AlertDialog.Builder(SettingActivity.this).create();

        View layoutview =LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_changstatus, null);
        alertDialog.setView(layoutview);
        final EditText mStatus=layoutview.findViewById(R.id.etStatus);
        Button mButton=layoutview.findViewById(R.id.button);
        alertDialog.show();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                final ProgressDialog mProcessDialog=new ProgressDialog(SettingActivity.this);
                mProcessDialog.setTitle("LOADING...");
                mProcessDialog.show();
                String status =mStatus.getText().toString();
                mReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProcessDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            final Uri uri = data.getData();
            final ProgressDialog mdialog = new ProgressDialog(SettingActivity.this);
            mdialog.setTitle("UPLOADING...");
            mdialog.show();
            mImageStore = FirebaseStorage.getInstance().getReference().child("image_profile").child("image" + UserID);
            mImageStore.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mImageStore.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                mReference.child("image").setValue(task.getResult().toString());
                                mImage.setImageURI(uri);
                                Toast.makeText(SettingActivity.this, "UPLOAD THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mdialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(SettingActivity.this, "UPLOAD THẤT BẠI", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
