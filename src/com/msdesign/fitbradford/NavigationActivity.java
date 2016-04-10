package com.msdesign.fitbradford;

import java.util.ArrayList;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class NavigationActivity extends AppCompatActivity {
	
	private ListView mDrawerList;
	private RelativeLayout mDrawerPane;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private SQLiteHandler db;
	private SessionManager session;

	ArrayList<NavigationItem> mNavItems = new ArrayList<NavigationItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// SQLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// Session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Display User Profile on startup
		setTitle("User Profile");
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, new UserProfileFragment()).commit();

		// Add items into Navigation Drawer
		mNavItems.add(new NavigationItem("Pedometer", R.drawable.ic_action_pedometer));
		mNavItems.add(new NavigationItem("Calorie Monitor", R.drawable.ic_action_scanner));
		mNavItems.add(new NavigationItem("Local Gym", R.drawable.ic_action_location));

		// DrawerLayout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

		// Populate the Navigation Drawer with options
		mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
		mDrawerList = (ListView) findViewById(R.id.navList);
		DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
		mDrawerList.setAdapter(adapter);

		// Drawer Item click listeners
		mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItemFromDrawer(position);
			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

			/**
			 * Called when a drawer has settled in a completely closed state.
			 */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(getTitle());
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(getTitle());
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		switch (item.getItemId()) {
		case R.id.logout:
			logoutUser();
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	private void selectItemFromDrawer(int position) {

		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();

		switch (position) {
		default:
		case 0:
			fragment = new PedometerFragment();
			break;
		case 1:
			fragment = new ScannerFragment();
			break;
		case 2:
			fragment = new LocationFragment();
			break;
		}

		fragmentManager.beginTransaction().replace(R.id.mainContent, fragment).commit();

		mDrawerList.setItemChecked(position, true);
		setTitle(mNavItems.get(position).mTitle);

		// Close the drawer
		mDrawerLayout.closeDrawer(mDrawerPane);
	}

	private void logoutUser() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle("Logout"); //
		alertDialog.setMessage("Are you sure you want to Logout?"); // Message displayed
		
		/* When Yes is clicked */
		alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				session.setLogin(false);
				db.deleteUsers();
				// Launching the login activity
				Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		/* When No button is clicked */
		alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	}
}
