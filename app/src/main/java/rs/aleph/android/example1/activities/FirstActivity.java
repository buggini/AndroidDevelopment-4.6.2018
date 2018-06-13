package rs.aleph.android.example1.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;

import rs.aleph.android.example1.R;
import rs.aleph.android.example1.adapters.DrawerAdapter;
import rs.aleph.android.example1.async.SimpleSyncTask;
import rs.aleph.android.example1.dialogs.AboutDialog;
import rs.aleph.android.example1.fragments.DetailFragment;
import rs.aleph.android.example1.fragments.ListFragment;
import rs.aleph.android.example1.model.NavigationItem;

// Each activity extends Activity class
public class FirstActivity extends AppCompatActivity implements ListFragment.OnItemSelectedListener {
	//13.06.2018
	// rad na casu servise za netvork mrezu
	private boolean landscape = false;


	// Position of the item to be displayed in the detail fragment
	private int position = 0;

	private ListFragment listFragment = null;

	private DetailFragment detailFragment = null;


	static final int PICK_CONTACT_REQUEST = 0;  // The request code

	// The activity's state
	private int a;
	private float b;
	private String c;
	private int[] d = {0, 1, 2, 3, 4};

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItemFromDrawer(position);
		}
	}

	// Attributes used by NavigationDrawer
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private RelativeLayout drawerPane;
	private CharSequence drawerTitle;
	private ArrayList<NavigationItem> drawerItems = new ArrayList<NavigationItem>();

	// Attributes used by Dialog
	private AlertDialog dialog;

	// Attributes representing the activity's state
	private boolean landscapeMode = false; // Is the device in the landscape mode?
	private boolean masterShown = false; // Is the MasterFragment fragment shown?
	private boolean detailShown = false; // Is the DetailFragment fragment shown?

	private int itemId = 0; // Selected item ID

	// onCreate method is a lifecycle method called when he activity is starting
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Each lifecycle method should call the method it overrides
		super.onCreate(savedInstanceState);

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onCreate()", Toast.LENGTH_SHORT).show();

		// Draws layout
		setContentView(R.layout.activity_first);

		// Load instance state from bundle
		if (savedInstanceState != null) {
			this.a = savedInstanceState.getInt("a");
			this.b = savedInstanceState.getFloat("b");
			this.c = savedInstanceState.getString("c");
			this.d = savedInstanceState.getIntArray("d");
			this.position = savedInstanceState.getInt("position");
		}
		// Enables ActionBar app icon to behave as action to toggle NavigationDrawer
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
			actionBar.setHomeButtonEnabled(true);
			actionBar.show();
		}
		// Manages NavigationDrawer

		// Populates a list of NavigationDrawer items
		drawerItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
		drawerItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_settings_long), R.drawable.ic_action_settings));
		drawerItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));
		drawerItems.add(new NavigationItem(getString(R.string.drawer_food), getString(R.string.drawer_Food_long), R.drawable.ic_room_service));

		drawerTitle = getTitle();
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerList = (ListView) findViewById(R.id.navList);

		// Populates NavigtionDrawer with options
		drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
		DrawerAdapter adapter = new DrawerAdapter(this, drawerItems);

		// Sets a custom shadow that overlays the main content when NavigationDrawer opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
		drawerList.setAdapter(adapter);

		drawerToggle = new ActionBarDrawerToggle(
				this,                           /* host Activity */
				drawerLayout,                   /* DrawerLayout object */
				toolbar,                        /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,           /* "open drawer" description for accessibility */
				R.string.drawer_close           /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
			}
		};
		// If the activity is started for the first time create master fragment
		if (savedInstanceState == null) {
			// FragmentTransaction is a set of changes (e.g. adding, removing and replacing fragments) that you want to perform at the same time.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ListFragment listFragment = new ListFragment();
			ft.add(R.id.list_view, listFragment, "List_Fragment_1");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}

		// If the device is in the landscape mode and the detail fragment is null create detail fragment
		if (findViewById(R.id.detail_view) != null) {
			landscapeMode = true;
			getFragmentManager().popBackStack();

			DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.detail_view);
			if (detailFragment == null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				detailFragment = new DetailFragment();
				ft.replace(R.id.detail_view, detailFragment, "Detail_Fragment_1");
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
				detailShown = true;
			}
		}

		masterShown = true;
		detailShown = false;
		itemId = 0;
	}


	// onStart method is a lifecycle method called after onCreate (or after onRestart when the
	// activity had been stopped, but is now again being displayed to the user)
	@Override
	protected void onStart() {

		super.onStart();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onStart()", Toast.LENGTH_SHORT).show();
	}

	// onRestart method is a lifecycle method called after onStop when the current activity is
	// being re-displayed to the user
	@Override
	protected void onRestart() {

		super.onRestart();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onRestart()", Toast.LENGTH_SHORT).show();
	}

	// onResume method is a lifecycle method called after onRestoreInstanceState, onRestart, or
	// onPause, for your activity to start interacting with the user
	@Override
	protected void onResume() {

		super.onResume();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onResume()", Toast.LENGTH_SHORT).show();
	}

	// onPause method is a lifecycle method called when an activity is going into the background,
	// but has not (yet) been killed
	@Override
	protected void onPause() {

		super.onPause();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onPause()", Toast.LENGTH_SHORT).show();
	}

	// onStop method is a lifecycle method called when the activity are no longer visible to the user
	@Override
	protected void onStop() {

		super.onStop();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onStop()", Toast.LENGTH_SHORT).show();
	}

	// onDestroy method is a lifecycle method that perform any final cleanup before an activity is destroyed
	@Override
	protected void onDestroy() {

		super.onDestroy();

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onDestroy()", Toast.LENGTH_SHORT).show();
	}

	public void btnSelectContactClicked(View view) {
		// This is an implicit intent
		Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
		// Show user only contacts w/ phone numbers
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
		// startActivity method starts an activity
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request it is that we're responding to
		if (requestCode == PICK_CONTACT_REQUEST) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// Get the URI that points to the selected contact
				Uri contactUri = data.getData();

				// Shows contact URI
				Toast.makeText(this, "Contact URI: " + contactUri, Toast.LENGTH_SHORT).show();
			}
		}
	}

	// onSaveInstanceState method is a life-cycle method that is called to ask the fragment to save its current dynamic state, so it can later be reconstructed in a new instance of its process is restarted.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		super.onSaveInstanceState(savedInstanceState);

		// Shows a toast message (a pop-up message)
		Toast.makeText(this, "FirstActivity.onSaveInstanceState()", Toast.LENGTH_SHORT).show();

		savedInstanceState.putInt("a", a);
		savedInstanceState.putFloat("b", b);
		savedInstanceState.putString("c", c);
		savedInstanceState.putIntArray("d", d);
		savedInstanceState.putInt("position", position);
	}

	@Override
	public void onItemSelected(int position) {

		// Set position
		this.position = position;


		// Shows a toast message (a pop-up message)
		//Toast.makeText(getBaseContext(), "FirstActivity.onItemSelected()", Toast.LENGTH_SHORT).show();

		if (landscapeMode) {
			// If the device is in the landscape mode updates detail fragment's content.
			DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.detail_view);
			detailFragment.updateContent(position);
		} else {
			// If the device is in the portrait mode sets detail fragment's content and replaces master fragment with detail fragment in a fragment transaction.
			DetailFragment detailFragment = new DetailFragment();
			detailFragment.setContent(position);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.list_view, detailFragment, "Detail_Fragment_2");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(null);
			ft.commit();

			masterShown = false;
			detailShown = true;
		}

		// Shows a toast message (a pop-up message)
		Toast.makeText(getBaseContext(), "FirstActivity.onItemSelected()", Toast.LENGTH_SHORT).show();

//		if (landscape) {
//			// If the device is in the landscape mode updates detail fragment's content.
//			detailFragment.updateContent(position);
//		} else {
//			// If the device is in the portrait mode sets detail fragment's content and replaces master fragment with detail fragment in a fragment transaction.
//			detailFragment.setContent(position);
//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//			ft.replace(R.id.list_view, detailFragment, "Detail_Fragment_1");
//			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//			ft.addToBackStack(null);
//			ft.commit();
//		}
	}

	// onBackPressed method is called when the activity has detected the user's press of the back key (it has to be overrided because fragment's behavior has to be implemented manualy).
	@Override
	public void onBackPressed() {

		if (landscapeMode) {
			finish();
		} else if (masterShown == true) {
			finish();
		} else if (detailShown == true) {
			getFragmentManager().popBackStack();
			ListFragment masterFragment = new ListFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.list_view, masterFragment, "Master_Fragment");
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			masterShown = true;
			detailShown = false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// onOptionsItemSelected method is called whenever an item in the Toolbar is selected.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_create:
				Toast.makeText(this, "Action " + getString(R.string.fragment_master_action_create) + " executed.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_update:
				Toast.makeText(this, "Action " + getString(R.string.fragment_detal_action_update) + " executed.", Toast.LENGTH_SHORT).show();
				new SimpleSyncTask(getApplicationContext()).execute();
				break;
			case R.id.action_delete:
				Toast.makeText(this, "Action " + getString(R.string.fragment_detal_action_delete) + " executed.", Toast.LENGTH_SHORT).show();
				Snackbar.make(findViewById(R.id.list_view), "test", Snackbar.LENGTH_SHORT).show();
				break;
		}
		// ovde zeki treb da dodas novo dugme u tool baru za sihronizaciju
		return super.onOptionsItemSelected(item);
	}

	// Overrides setTitle method to support older api levels
	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}


	// Method(s) that manage NavigationDrawer.

	// onPostCreate method is called ofthen onRestoreInstanceState to synchronize toggle state
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	// onConfigurationChanged method is called when the device configuration changes to pass configuration change to the drawer toggle
	@Override
	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);

		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(configuration);
	}

	public void btnOpenCameraClicked(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivity(takePictureIntent);
	}
	private void selectItemFromDrawer(int position){

		if (position == 0) {
			// TODO
			// FirstActivity is already shown
		} else if (position == 1) {
			Intent settings = new Intent(FirstActivity.this, SettingsActivity.class);
			startActivity(settings);
		} else if (position == 2) {
			if (dialog == null) {
				dialog = new AboutDialog(FirstActivity.this).prepareDialog();
			} else {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
			// TODO: SImplify
			// To not create dialog class
			// call it from here.

			dialog.show();
		}

		drawerList.setItemChecked(position, true);
		setTitle(drawerItems.get(position).getTitle());
		drawerLayout.closeDrawer(drawerPane);
	}



}


