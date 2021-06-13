package com.example.glsi_android_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
	Button updateAccountSettings;
	EditText username, userStatus;
	CircleImageView userProfileImage;
	private String currentUserId;
	private FirebaseAuth mAuth;
	private DatabaseReference rootRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		mAuth = FirebaseAuth.getInstance();
		currentUserId = mAuth.getCurrentUser().getUid();
		rootRef = FirebaseDatabase.getInstance().getReference();
		initializeFields();
		updateAccountSettings.setOnClickListener(v -> updateSettings());

		retrieveUserInfo();
	}

	private void retrieveUserInfo() {
		rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange( DataSnapshot snapshot) {
				if((snapshot.exists()) &&  (snapshot.hasChild("name")) && (snapshot.hasChild("image"))){
					String retrieveUsername = snapshot.child("name").getValue().toString();
					String retrievestatus = snapshot.child("status").getValue().toString();
					String retrieveImage = snapshot.child("image").getValue().toString();
					username.setText(retrieveUsername);
					userStatus.setText(retrievestatus);
				}
				else if((snapshot.exists()) &&  (snapshot.hasChild("name"))){
					String retrieveUsername = snapshot.child("name").getValue().toString();
					String retrievestatus = snapshot.child("status").getValue().toString();
					username.setText(retrieveUsername);
					userStatus.setText(retrievestatus);
				}
				else {
					Toast.makeText(SettingsActivity.this, "Please set your profile info", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled( DatabaseError error) {

			}
		});
	}


	private void initializeFields() {
		updateAccountSettings = findViewById(R.id.update_settings_button);
		username = findViewById(R.id.set_user_name);
		userStatus = findViewById(R.id.set_profile_status);
		userProfileImage = findViewById(R.id.set_profile_image);

	}

	private void updateSettings() {
		String setUserName = username.getText().toString();
		String setUserStatus = userStatus.getText().toString();

		if (TextUtils.isEmpty(setUserName)) {
			Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
		}

		if (TextUtils.isEmpty(setUserName)) {
			Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
		} else {
			HashMap<String, String> profileMap = new HashMap<>();
			profileMap.put("uid", currentUserId);
			profileMap.put("name", setUserName);
			profileMap.put("status", setUserStatus);
			rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(task -> {
				if(task.isSuccessful()) {
					Toast.makeText(this, "Profile updated succesfuly", Toast.LENGTH_SHORT).show();
					sendUserToMainActivity();
				} else {
					String errorMessage  = task.getException().toString();
					Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

				}
			});
		}

	}

	private void sendUserToMainActivity() {
		Intent settingsIntent = new Intent(SettingsActivity.this, MainActivity.class);
		settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(settingsIntent);
		finish();
	}
}
