package com.imperialtechnologies.theeatlist_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NewFoodItem extends Activity {

	// Widget globals
	EditText foodItemNameEditText;
	EditText foodItemLocationEditText;
	RatingBar foodItemRatingBar;
	EditText foodItemReviewEditText;
	ImageView foodItemImageView;
	CheckBox foodItemEatenCheckBox;
	Spinner foodTypeSpinner;
	
	// Spinner+FoodType relevant globals
	ArrayList<String> foodTypeItemList = new ArrayList<String>();
	String [] foodTypeItemArray;
	
	DBTools dbTools = new DBTools(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_item_add);
		
		// action bar settings
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));    
				
		
		foodItemNameEditText = (EditText) findViewById(R.id.foodItemNameEditText);
		foodItemLocationEditText = (EditText) findViewById(R.id.foodItemLocationEditText);
		foodItemRatingBar = (RatingBar) findViewById(R.id.foodItemRatingBar);
		foodItemReviewEditText = (EditText) findViewById(R.id.foodItemReviewEditText);
		foodItemImageView = (ImageView) findViewById(R.id.foodItemImageView);
		foodItemEatenCheckBox = (CheckBox) findViewById(R.id.foodItemEatenCheckBox);
		foodTypeSpinner = (Spinner) findViewById(R.id.foodTypeSpinner);
		
		//Initialize the Food Type Spinner
		initializeFoodTypeSpinner();

        //Hide Keyboard Action Listener
        TextView.OnEditorActionListener closeSoftKeyboard = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getApplicationContext(),"Input Hidden!",Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(foodItemLocationEditText.getWindowToken(),0);
                    return true;
                } else {
                    return false;
                }
            }
        };

        //Set listeners to the EditText views
        foodItemNameEditText.setOnEditorActionListener(closeSoftKeyboard);

        foodItemLocationEditText.setOnEditorActionListener(closeSoftKeyboard);

        foodItemReviewEditText.setOnEditorActionListener(closeSoftKeyboard);

	}
	
	//Image retrieval section
	static final int REQUEST_IMAGE_GET = 1;

	public void selectImage(View view) {
		
		selectImage();
		
	}
	
	public void selectImage() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("image/*");
	    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(intent, REQUEST_IMAGE_GET);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
	        //Bitmap thumbnail = data.getParcelable("data");
	        Uri fullPhotoUri = data.getData();
	        // Do work with photo saved at fullPhotoUri
	        foodItemImageView.setImageURI(fullPhotoUri);
	        foodItemImageView.setTag(fullPhotoUri.toString());

	    }
	}
	
	public void initializeFoodTypeSpinner() {
		
		// Area where I would populate the foodTypeItemList ArrayList with DB values
		foodTypeItemList.clear();
		
		foodTypeItemList.add("Asian");
		foodTypeItemList.add("American");
		foodTypeItemList.add("Indian");
		foodTypeItemList.add("French");
		foodTypeItemList.add("Italian");
		foodTypeItemList.add("Ramen");
		foodTypeItemList.add("Sushi");
		
		foodTypeItemList.remove(EditFoodItem.DEFAULT_SPINNER_ENTRY);
		
		Collections.sort(foodTypeItemList);
		foodTypeItemList.add(0,EditFoodItem.DEFAULT_SPINNER_ENTRY);
		
		foodTypeItemArray = new String[foodTypeItemList.size()];
		foodTypeItemArray = foodTypeItemList.toArray(foodTypeItemArray);
		// The is the end of the array population
				
		ArrayAdapter<String> foodTypeSpinnerAdapter = new ArrayAdapter<String>( NewFoodItem.this, R.layout.food_type_spinner_item, R.id.foodTypeSpinnerItemTextView, foodTypeItemArray);
		foodTypeSpinner.setAdapter(foodTypeSpinnerAdapter);
		
		
	}
	
	private String eatenString(boolean eaten){
		
		if (eaten) 
			{ return "eaten"; }
		else return "";
		
	}

	public void saveFoodItem(View view) {
		
		// Will hold the HashMap of values 
		
		HashMap<String, String> queryValuesMap =  new  HashMap<String, String>();

		// Get the values from the EditText boxes
		
		queryValuesMap.put("foodItemName", foodItemNameEditText.getText().toString());
		queryValuesMap.put("foodItemLocation", foodItemLocationEditText.getText().toString());
		queryValuesMap.put("foodItemRating", "0");
		//queryValuesMap.put("foodItemRating", String.valueOf(foodItemRatingBar.getNumStars()));
		queryValuesMap.put("foodItemReview", foodItemReviewEditText.getText().toString());
		queryValuesMap.put("foodItemPicture", foodItemImageView.getTag().toString());
		queryValuesMap.put("foodItemEaten", eatenString(foodItemEatenCheckBox.isChecked()));
		queryValuesMap.put("foodType", foodTypeSelectionCheck(foodTypeSpinner.getSelectedItem().toString()));
		
		Toast.makeText(getApplication(), EditFoodItem.DEFAULT_SPINNER_ENTRY, Toast.LENGTH_SHORT).show();
		//Toast.makeText(getApplication(), eatenString(foodItemEatenCheckBox.isChecked()), Toast.LENGTH_SHORT).show();
		
		// Call for the HashMap to be added to the database
		
		dbTools.insertFoodItem(queryValuesMap);
		
		// Call for MainActivity to execute
		
		this.callMainActivity(view);
	}
	
	public String foodTypeSelectionCheck(String foodType){	
		Toast.makeText(getApplicationContext(), foodType, Toast.LENGTH_SHORT).show();
		
		if (foodType.equals(EditFoodItem.DEFAULT_SPINNER_ENTRY)) {
			return "";
		} else {
			return foodType;
		}
	}
		
	public void callMainActivity(View view) {

		Intent incomingIntent = getIntent();
		int activeTab = incomingIntent.getIntExtra(MainActivity.ACTIVE_TAB, 0);
		
		Toast.makeText(getApplicationContext(), "Returning Intent from NewFoodItem", Toast.LENGTH_SHORT).show();
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra(MainActivity.ACTIVE_TAB,activeTab);
		setResult(RESULT_OK,returnIntent);
		finish();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.new_food_item_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_save:
	        	saveFoodItem(findViewById(R.id.action_save));
	            return true;
	        case R.id.action_settings:
	        	Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.action_help:
	        	showAddFoodHelpDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void showAddFoodHelpDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(NewFoodItem.this);
		
		builder.setTitle(R.string.dialogue_add_help);
		
		builder.setPositiveButton("Yes",null);
		
		builder.setMessage(getString(R.string.dialogue_help_add_activity_text));
		
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
		
	}
	
}
