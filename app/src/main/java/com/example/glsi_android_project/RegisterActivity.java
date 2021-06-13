package com.example.glsi_android_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
	private Button createAccountButton;
	private EditText userEmail, userPassword;
	private TextView alreadyHaveAccountLink;
	private FirebaseAuth mAuth;
	private DatabaseReference rootRef;
	private ProgressDialog loadingBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mAuth = FirebaseAuth.getInstance();
		rootRef = FirebaseDatabase.getInstance().getReference();
		InitializeFields();
		alreadyHaveAccountLink.setOnClickListener(v -> sendUserToLoginActivity());
		createAccountButton.setOnClickListener(v -> createNewAccount());
	}

	private void createNewAccount() {
		String email = userEmail.getText().toString();
		String password = userPassword.getText().toString();
		if (TextUtils.isEmpty(email)) {
			Toast.makeText(getBaseContext(), "Please enter email", Toast.LENGTH_SHORT).show();
		}

		if (TextUtils.isEmpty(password)) {
			Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_SHORT).show();
		} else {
			loadingBar.setTitle("Creating New account");
			loadingBar.setMessage("Please wait , while we are creating your account");
			loadingBar.setCanceledOnTouchOutside(true);
			loadingBar.show();
			mAuth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							String currentUserId = mAuth.getCurrentUser().getUid();
							rootRef.child("users").child(currentUserId).setValue("");
							sendUserToMainActivity();
							Toast.makeText(getBaseContext(), "Account created", Toast.LENGTH_SHORT).show();
							loadingBar.dismiss();
						} else {
							String errorMessage = task.getException().toString();
							Toast.makeText(getBaseContext(), "error: " + errorMessage, Toast.LENGTH_SHORT).show();
							loadingBar.dismiss();
						}
					});
		}
	}

	private void sendUserToMainActivity() {
		Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(mainIntent);
		finish();
	}

	private void InitializeFields() {
		createAccountButton = (Button) findViewById(R.id.register_button);
		userEmail = (EditText) findViewById(R.id.register_email);
		userPassword = (EditText) findViewById(R.id.register_password);
		alreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);
		loadingBar = new ProgressDialog(this);
	}

	private void sendUserToLoginActivity() {
		Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
		startActivity(loginIntent);
	}
}
