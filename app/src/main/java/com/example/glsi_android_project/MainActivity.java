package com.example.glsi_android_project;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {
	private Toolbar mToolbar;
	private ViewPager myViewPager;
	private TabLayout myTabLayout;
	private TabsAccessorAdapter myTabsAccessorAdapter;
	private FirebaseUser currentUser;
	private FirebaseAuth mAuth;
	private DatabaseReference rootRef;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();
		rootRef = FirebaseDatabase.getInstance().getReference();
		mToolbar = findViewById(R.id.main_page_toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setTitle("chat");

		myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
		myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
		myViewPager.setAdapter(myTabsAccessorAdapter);
		myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
		myTabLayout.setupWithViewPager(myViewPager);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (currentUser == null) {
			sendUserToLoginActivity();
		} else {
			verifyUserExistance();
		}
	}

	private void verifyUserExistance() {
		String currentUserId = mAuth.getCurrentUser().getUid();
		rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				if ((snapshot.child("name").exists())) {
					Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
				} else {
					sendUserToSettingsActivity();
				}
			}

			@Override
			public void onCancelled(DatabaseError error) {

			}
		});
	}

	private void sendUserToLoginActivity() {
		Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
		loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
		finish();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.main_logout_option) {
			mAuth.signOut();
			sendUserToLoginActivity();
		}
		if (item.getItemId() == R.id.main_settings_option) {
			sendUserToSettingsActivity();
		}
		return true;
	}

	private void sendUserToSettingsActivity() {
		Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
		settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(settingsIntent);
		finish();
	}

}
