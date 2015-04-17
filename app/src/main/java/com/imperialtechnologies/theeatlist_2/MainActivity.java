package com.imperialtechnologies.theeatlist_2;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	public final static String ACTIVE_TAB = "com.imperialtechnologies.theeatlist_2.MainActivity.ACTIVE_TAB";
	
	ActionBar actionBar;
	ActionBar.Tab listTab, eatenTab, friendsTab;
	
	ListFragment listTabFragment = new MainActivityFragmentTest();
    ListFragment eatenTabFragment = new MainActivityFragmentTest();
    ListFragment friendsTabFragment = new MainActivityFragmentFriendsTab();

	// The Intent is used to issue an operation should be performed
	public final static int NEW_FOOD_ITEM = 1;
    public final static int EDIT_FOOD_ITEM = 2;

	// The object that allows me to manipulate the database
	DBTools dbTools = new DBTools(this);
	ListAdapter friendAdapter = null;

    //Loader reference
    private final static Integer FULL_FOOD_LIST = 0;
    private final static Integer EATEN_LIST = 1;
    private final static Integer FOOD_DETAILS = 2;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private FoodListLoader foodListLoader;
    SimpleCursorAdapter foodListAdapter;
    SimpleCursorAdapter eatenListAdapter;
    FoodListCursorWrapper eatenCursorWrapper;

    public FoodListLoader onCreateLoader(int id, Bundle args) {

        foodListLoader = new FoodListLoader(getApplicationContext());
        Toast.makeText(getApplicationContext(), "FoodLoader Created", Toast.LENGTH_SHORT).show();

        return foodListLoader;

    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Once data is ready, swap in the new data

        Log.d("Main-LoadManag", "foodListAdapter swapped");
        Log.d("MainActivity", "loaderID: " + Integer.toString(loader.getId()));
        Log.d("Main-LoadManag", "Rows update: " + Integer.toString(data.getCount()));

        foodListAdapter.swapCursor(data);
        eatenCursorWrapper = new FoodListCursorWrapper(data, dbTools.EATEN, dbTools.EATEN_COL);

        Log.d("Main-LoadManag", "eatenListAdapter swapped");
        Log.d("MainActivity", "loaderID: " + Integer.toString(loader.getId()));
        Log.d("Main-LoadManag", "Rows update: " + Integer.toString(eatenCursorWrapper.getCount()));

        eatenListAdapter.swapCursor(eatenCursorWrapper);

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        //Un-map all the information
        foodListAdapter.swapCursor(null);
        eatenListAdapter.swapCursor(null);
    }

	// Called when the Activity is first called
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		actionBar = getActionBar();
		// action bar settings
		actionBar.setDisplayHomeAsUpEnabled(false);
		
		mCallbacks = this;

		//Tab Experiments
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Populate the tabBar
		listTab = actionBar.newTab().setText("List");
		eatenTab = actionBar.newTab().setText("Eaten");
		friendsTab = actionBar.newTab().setText("Friends");
		
		// Set listeners for the tab. The listener class accepts fragments as an argument
		listTab.setTabListener(new TabListener(listTabFragment));
		eatenTab.setTabListener(new TabListener(eatenTabFragment));
		friendsTab.setTabListener(new TabListener(friendsTabFragment));
		
		actionBar.addTab(listTab);
		actionBar.addTab(eatenTab);
		actionBar.addTab(friendsTab);

        Log.d("MainActivity","tabs added");

        //Start the Async Loaders
        getLoaderManager().initLoader(FULL_FOOD_LIST, null,mCallbacks);
        getLoaderManager().initLoader(EATEN_LIST, null,mCallbacks);

        populateListFragment();
		populateEatenListFragment();
		populateFriendListFragment();

	};
	
	public void populateListFragment(){

		foodListAdapter = new SimpleCursorAdapter(this,R.layout.food_item_entry, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        listTabFragment.setListAdapter(foodListAdapter);
			
	}
	
	public void populateEatenListFragment(){

        eatenListAdapter = new SimpleCursorAdapter(this,R.layout.food_item_entry, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        eatenTabFragment.setListAdapter(eatenListAdapter);
	
	}
	
	public void populateFriendListFragment(){
		
		ArrayList<HashMap<String, String>> friendList =  dbTools.getAllFriends();
		friendList.clear();
		
		HashMap<String,String> friendMap = new HashMap<String,String>();
		HashMap<String,String> friendMap2 = new HashMap<String,String>();
		
		friendMap.put("friendId", "1");
		friendMap.put("firstName", "Ella");
		friendMap.put("lastName", "Chan");
		friendMap.put("cityLocation", "Markham");
		
		friendMap2.put("friendId", "2");
		friendMap2.put("firstName", "Satwick");
		friendMap2.put("lastName", "Sharma");
		friendMap2.put("cityLocation", "Windsor");
		friendList.add(friendMap);
		friendList.add(friendMap2);
		
		friendAdapter = new SimpleAdapter( MainActivity.this, friendList, R.layout.friend_item_listview, new String[] { "friendId","firstName", "lastName", "cityLocation"}, new int[] {R.id.friendId, R.id.firstName, R.id.lastName, R.id.cityLocation });
		
		friendsTabFragment.setListAdapter(friendAdapter);
	
	}
	
	public void showAddFoodItem(View view) {
		Intent theIntent = new Intent(getApplicationContext(), NewFoodItem.class);
		// startActivity(theIntent); 
		theIntent.putExtra(ACTIVE_TAB, actionBar.getSelectedNavigationIndex());
		
		Toast.makeText(getApplicationContext(), "Sending Intent to NewFoodItem", Toast.LENGTH_SHORT).show();
		Log.d("Sending to NewFoodItem", "Active_Tab: " + Integer.toString(actionBar.getSelectedNavigationIndex()));
		
		startActivityForResult(theIntent, NEW_FOOD_ITEM);

    }

    public void refreshFoodList(){

        Log.i("MainActivity", "Refreshing Food List");
        Log.d("MainActivity", "Triggering foodListLoader.onContentChanged");
        foodListLoader.onContentChanged();

    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// request code is the code sent out from startActivityForResult
		// result code is the first argument in setResult() from the returning intent

        switch (requestCode) {
            case NEW_FOOD_ITEM:
                Log.d("MainActivity","triggering: foodListLoader.onContentChanged()");
                foodListLoader.onContentChanged();
            case EDIT_FOOD_ITEM:
                Log.d("MainActivity","triggering: foodListLoader.onContentChanged()");
                foodListLoader.onContentChanged();
        }

	}
	
	public void showMainListHelpDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		
		builder.setTitle(R.string.dialogue_main_help);
		
		builder.setPositiveButton("Yes",null);
		
		builder.setMessage(getString(R.string.dialogue_help_main_activity_text));
		
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

        getLoaderManager().destroyLoader(FULL_FOOD_LIST);
		dbTools.close();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    
	        case R.id.action_search:
	        	Toast.makeText(getApplicationContext(), "Searching", Toast.LENGTH_SHORT).show();
	            return true;
	            
	        case R.id.action_new_item:
	        	// how to call add food item
	        	showAddFoodItem(findViewById(R.id.action_new_item));
	        	return true;
	        	
	        case R.id.action_delete_all_items:
	        	confirmDeleteAllFoodItems();
	        	return true;
	        	
	        case R.id.action_main_help:
	        	showMainListHelpDialog();
	        	return true;
	        	
	        case R.id.action_refresh:
	        	Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
                refreshFoodList();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    
	    }
	    
	}
	
	public void confirmDeleteAllFoodItems(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		
		builder.setTitle("Clear List");
		
		builder.setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	
	            // do something when the button is clicked
	            public void onClick(DialogInterface arg0, int arg1) {
	                deleteAllFoodItems();
	            }
        });
		builder.setNegativeButton("Not Yet!",
		    new DialogInterface.OnClickListener() {
			
	            // do something when the button is clicked
	            public void onClick(DialogInterface arg0, int arg1) {
	                doNothing();
	            }
		});
		
		builder.setMessage("This will clear all the food item in your list. Are you sure you want to do that?");
		
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
		
	}

	public void deleteAllFoodItems(){

		Toast.makeText(getApplicationContext(), "Deleted all", Toast.LENGTH_SHORT).show();
		dbTools.clearAllRows();
		
		Intent theIntent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(theIntent); 
		
	}
	
	public void doNothing(){
		
		Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_SHORT).show();
		
	}

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}

//Create a customer adapter - so that you can invalidate old views and then redraw a new one - works for main list but not eaten list

//Add an about/help button in each of the pages - done - update it

//Make list item selector work - when you tap the list item it makes a color change - done
// -- change the color/opacity to be more pleasing

//add a review view function - done?

//add a delete all button - the view is not updating after deleting rows in DB - workaround, restart activity
// -- update delete all to work with the redraw view instead of restarting activity
// -- find a solution to above. Also use startActivityForResult instead of startActivity for Add and Edit

//use share button - done

//add tabs navigation - done

//what is friends tab for?

// swipe away fooditem from listview/individual delete

//Figure out what the Up Button does - lowest