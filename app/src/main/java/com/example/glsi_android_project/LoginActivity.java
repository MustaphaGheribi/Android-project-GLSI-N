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

public class LoginActivity extends AppCompatActivity {

	private Button loginBution, phoneLoginButton;
	private EditText userEmail, userPassword;
	private TextView needNewAccountLink, forgetPasswordLink;
	private FirebaseAuth mAuth;
	private ProgressDialog loadingBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mAuth = FirebaseAuth.getInstance();

		InitializeFields();

		needNewAccountLink.setOnClickListener(v -> sendUserToRegisterActivity());

		loginBution.setOnClickListener(v -> loginUser());
	}

	private void loginUser() {
		String email = userEmail.getText().toString();
		String password = userPassword.getText().toString();
		if (TextUtils.isEmpty(email)) {
			Toast.makeText(getBaseContext(), "Please enter email", Toast.LENGTH_SHORT).show();
		}

		if (TextUtils.isEmpty(password)) {
			Toast.makeText(getBaseContext(), "Please enter password", Toast.LENGTH_SHORT).show();
		} else {
			mAuth.signInWithEmailAndPassword(email, password)
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							sendUserToMainActivity();
							Toast.makeText(getBaseContext(), "Login succesful", Toast.LENGTH_SHORT).show();
							loadingBar.dismiss();
						} else {
							loadingBar.setTitle("Logging in");
							loadingBar.setMessage("Please wait , while we log into  your account");
							loadingBar.setCanceledOnTouchOutside(true);
							loadingBar.show();
							String errorMessage = task.getException().toString();
							Toast.makeText(getBaseContext(), "error: " + errorMessage, Toast.LENGTH_SHORT).show();
							loadingBar.dismiss();
						}
					});
		}
	}

	private void InitializeFields() {
		loginBution = (Button) findViewById(R.id.login_button);
		phoneLoginButton = (Button) findViewById(R.id.phone_login_button);
		userEmail = (EditText) findViewById(R.id.login_email);
		userPassword = (EditText) findViewById(R.id.login_password);
		needNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
		forgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
		loadingBar = new ProgressDialog(this);
	}

	private void sendUserToRegisterActivity() {
		Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(registerIntent);
	}

	private void sendUserToMainActivity() {
		Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
		mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(mainIntent);
		finish();
	}
}
