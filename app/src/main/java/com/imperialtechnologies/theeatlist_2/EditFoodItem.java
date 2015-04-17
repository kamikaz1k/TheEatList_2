package com.imperialtechnologies.theeatlist_2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

public class EditFoodItem extends Activity implements LoaderManager.LoaderCallbacks<HashMap<String, String>>{

	public static String DEFAULT_SPINNER_ENTRY = "What type of food is it?";
    public static String THUMBNAIL_DIR = "thumbnails";
    public static Integer LOAD_URI = 1;
    public static Integer LOAD_PATH = 2;

    //Intent constants
    public static final int UNCHANGED = 0;
    public static final int EDITED = 1;
	
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
	
	//Share relevant globals
	ShareActionProvider menuShareActionProvider;
	String shareFoodText;
	Intent shareIntent = new Intent(Intent.ACTION_SEND);
    Uri foodThumbnailUri = Uri.parse("");

    //Loader relevant globals
    public static final int FOOD_ITEM_LOADER = 0;
    String foodId;
    private FoodItemLoader foodItemLoader;
    private LoaderManager.LoaderCallbacks<HashMap> mCallbacks;
	DBTools dbTools = new DBTools(this);	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.food_item_details);
		
		//Initialize the Food Type Spinner
		foodTypeSpinner = (Spinner) findViewById(R.id.foodTypeSpinner);
		populateFoodItemArrayList();
		
		ArrayAdapter<String> foodTypeSpinnerAdapter = new ArrayAdapter<String>( EditFoodItem.this, R.layout.food_type_spinner_item, R.id.foodTypeSpinnerItemTextView, foodTypeItemArray);
		foodTypeSpinner.setAdapter(foodTypeSpinnerAdapter);
				
		// action bar settings
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		foodItemNameEditText = (EditText) findViewById(R.id.foodItemNameEditText);
		foodItemLocationEditText = (EditText) findViewById(R.id.foodItemLocationEditText);
		foodItemRatingBar = (RatingBar) findViewById(R.id.foodItemRatingBar);
		foodItemReviewEditText = (EditText) findViewById(R.id.foodItemReviewEditText);
		foodItemImageView = (ImageView) findViewById(R.id.foodItemImageView);
		foodItemEatenCheckBox = (CheckBox) findViewById(R.id.foodItemEatenCheckBox);
		//Spinner is initialized beforehand

        Log.d("tag", "got to here");

		Intent theIntent = getIntent();
		
		foodId = theIntent.getStringExtra("foodId");

        //Moved this section to populateFoodItemDetails()
        //TODO make argument optional?
        populateFoodItemDetails(null);

        //dunno why this doesn't work
        //mCallbacks = this;

        //start loader - use activity instance as callback
        //getLoaderManager().initLoader(FOOD_ITEM_LOADER,null,this);

        //Hide Keyboard Action Listener
        TextView.OnEditorActionListener closeSoftKeyboard = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Toast.makeText(getApplicationContext(),"Input Hidden!",Toast.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(foodItemLocationEditText.getWindowToken(),0);
                    setShareIntent(shareIntent);
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

    public void populateFoodItemDetails(HashMap<String,String> foodItemList ){
        if (foodItemList == null) {
            Log.i("EditFoodItem", "Food Hashmap was empty");
            foodItemList = dbTools.getFoodItemDetails(foodId);

            //TODO-threading for fetching food item details
            /*foodItemNameEditText.setText("Food Name");
            foodItemLocationEditText.setText("Restaurant Location");
            foodItemRatingBar.setNumStars(5);
            foodItemReviewEditText.setText("Review Text");
            foodItemImageView.setTag("");
            foodItemEatenCheckBox.setChecked(true);
            foodTypeSpinner.setSelection(0);*/
        }

        if(foodItemList.size() != 0){

            foodItemNameEditText.setText(foodItemList.get("foodItemName"));
            foodItemLocationEditText.setText(foodItemList.get("foodItemLocation"));
            foodItemRatingBar.setNumStars(Integer.parseInt(foodItemList.get("foodItemRating")));
            foodItemReviewEditText.setText(foodItemList.get("foodItemReview"));
            foodItemImageView.setTag(foodItemList.get("foodItemPicture"));
            foodItemEatenCheckBox.setChecked(eaten(foodItemList.get("foodItemEaten")));
            foodTypeSpinner.setSelection(foodTypeItemList.indexOf(foodItemList.get("foodType")));

            Log.d("EditFoodItem","Img tag: "+foodItemImageView.getTag().toString());

            setImageOnLoad(foodItemImageView.getTag().toString());
        }

    }
	
	public void populateFoodItemArrayList(){
		
		foodTypeItemList.clear();
		
		foodTypeItemList.add("Asian");
		foodTypeItemList.add("American");
		foodTypeItemList.add("Indian");
		foodTypeItemList.add("French");
		foodTypeItemList.add("Italian");
		foodTypeItemList.add("Ramen");
		foodTypeItemList.add("Sushi");
		
		foodTypeItemList.remove(DEFAULT_SPINNER_ENTRY);
		
		Collections.sort(foodTypeItemList);
		foodTypeItemList.add(0,DEFAULT_SPINNER_ENTRY);
		
		foodTypeItemArray = new String[foodTypeItemList.size()];
		foodTypeItemArray = foodTypeItemList.toArray(foodTypeItemArray);
		
	}

	//Image retrieval section
	static final int REQUEST_IMAGE_GET = 1;

	public void selectImage(View view) {
		
		selectImage();
		
	}
	
	public void setImageOnLoad(String imageLocation) {
		Log.d("EditFoodItem", "starting setImageOnLoad");

		if (!imageLocation.equals("")) {
            Log.d("EditFoodItem", "setting Img URI using Img tag");
            //foodItemImageView.setImageURI(Uri.parse(imageLocation));
            loadBitmap(LOAD_PATH,foodItemImageView);
            Log.d("EditFoodItem", "Img URI set successfully");

        }
		
		else {
			Toast.makeText(getApplicationContext(), "There is no saved picture...", Toast.LENGTH_SHORT).show();
            Log.d("EditFoodItem", "Img tag was empty");
            // doesn't load the picture...
		}
		
		Toast.makeText(getApplicationContext(), "Uri was: " + imageLocation, Toast.LENGTH_SHORT).show();

    }
	
	public void selectImage() {
        Log.d("EditFoodItem", "starting selectImage");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("image/*");
	    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
	    if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d("EditFoodItem", "starting ActivityForResult REQUEST_IMAGE_GET");
            startActivityForResult(intent, REQUEST_IMAGE_GET);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//retrieve selected Image URI, and turn it into something useable
	    if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
	    	Log.i("EditFoodItem", "Image selected from Gallery.");
            Log.d("EditFoodItem", "Starting URI data parsing");

            // take original image, turn it into bitmap, and convert to a compressed png thumbnail
	        Uri fullPhotoUri = data.getData();
            Log.d("EditFoodItem", "Gallery Selected photo URI: " + fullPhotoUri.toString());

            try {
                //make bitmap out of original URI path
	    		BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=8;
                InputStream bitmapStream = getContentResolver().openInputStream(fullPhotoUri);
                Bitmap selectedPicture = BitmapFactory.decodeStream(bitmapStream, null, options);
                Log.d("EditFoodItem", "BitmapFactory decodeFile successful");
                //make bitmap out of original Uri path
                //Bitmap selectedPicture = MediaStore.Images.Media.getBitmap(getContentResolver(), fullPhotoUri);

                Log.d("EditFoodItem", "Thumbnail bitmap created");

                String pathToThumbnail = saveToAppThumbnailFolder(selectedPicture);
                Log.d("EditFoodItem", "Thumbnail file saved to thumbnail folder");

                foodItemImageView.setTag(pathToThumbnail);
                //foodItemImageView.setImageURI(Uri.parse(pathToThumbnail));
                loadBitmap(LOAD_PATH,foodItemImageView);
                Log.d("EditFoodItem", "Img URI set successfully");

                setShareIntent(shareIntent);
		        
		        Log.i("EditFoodItem", "Bitmap parsing and creation successful");

            } catch (Exception e) {
                Log.w("EditFoodItem", "There should be no error...");
                e.printStackTrace();

            }

	    }
	}

    /** Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /** Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("EditFoodItem", "Thumbnail directory not created");
        }
        return file;
    }

	private String saveToAppThumbnailFolder(Bitmap bitmapImage){
		Log.i("EditFoodText", "started saveToAppThumbnailFolder");
        Log.d("EditFoodText", "Starting bitmap parsing...");

        Log.d("EditFoodText", "Image height: " + Integer.toString(bitmapImage.getHeight()));
        Log.d("EditFoodText", "Image width: " + Integer.toString(bitmapImage.getWidth()));


        if ((bitmapImage.getHeight() > 1279) || (bitmapImage.getWidth() > 1919)){

            float scaleValue;
            Integer originalHeight = bitmapImage.getHeight();
            Integer originalWidth = bitmapImage.getWidth();

            if (originalHeight > originalWidth) {
                scaleValue = (float) originalHeight/1280;
                Log.d("EditFoodItem", "Scale inside if: " + Float.toString(scaleValue));
            } else {
                scaleValue = (float) originalWidth/1920;
                Log.d("EditFoodItem", "Scale inside else: " + Float.toString(scaleValue));
            }

            float scaledWidth = originalWidth/scaleValue, scaledHeight = originalHeight/scaleValue;

            Log.d("EditFoodItem", "OriginalHeight: " + Integer.toString(originalHeight));
            Log.d("EditFoodItem", "OriginalWidth: " + Integer.toString(originalWidth));

            Log.d("EditFoodItem", "Scale: " + Float.toString(scaleValue));
            Log.d("EditFoodItem", "ScaledHeight: " + Float.toString(scaledHeight));
            Log.d("EditFoodItem", "ScaledWidth: " + Float.toString(scaledWidth));

            //Create the Matrix used for resizing the bitmap
            Matrix matrix = new Matrix();
            matrix.postScale(scaleValue, scaleValue);

            Log.d("EditFoodItem", "Int Conversion W: " + Integer.toString(Math.round(scaledWidth)));
            Log.d("EditFoodItem", "Int Conversion H: " + Integer.toString(Math.round(scaledHeight)));

            Bitmap resizedBitmap = Bitmap.createBitmap(bitmapImage, 0, 0,
                    Math.round(scaledWidth), Math.round(scaledHeight),
                    matrix, false);

            Log.d("EditFoodItem", "resizedBitmap created");
            bitmapImage.recycle();
            bitmapImage = resizedBitmap;
            Log.i("EditFoodItem", "Bitmap reassigned");
            Log.i("EditFoodItem", "Bitmap resize completed");

        }

		Calendar calendar = Calendar.getInstance();
		
		// TIMESTAMPs are not correct....why?
		String year = String.format("%04d", calendar.get(Calendar.YEAR));
		Log.i("TIMESTAMP", "calendar.YEAR: " + Integer.toString(Calendar.YEAR));
		
		String month = String.format("%02d", calendar.get(Calendar.MONTH));
		String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
		String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
		String second = String.format("%02d", calendar.get(Calendar.SECOND));
		String timeStamp = year+month+day+hour+minute+second;
		
		Log.d("EditFoodText", "Timestamp string: " + timeStamp);

        if (!isExternalStorageWritable()) {
           return "Storage was busy...";
        }

        // path to /sdcard/Android/data/
        File directory = getAlbumStorageDir(getApplicationContext(),THUMBNAIL_DIR);
        if (directory==null){
            return "Directory wasn't created...";
            }

        File myPath = new File(directory, "IMG_" + timeStamp + ".png");
        Log.d("EditFoodText", "Directory and file name created");
        Log.d("EditFoodText", "filepath: " + myPath.getAbsolutePath().toString());

        FileOutputStream fos = null;
		
		try {
			
			fos = new FileOutputStream(myPath);
            Log.d("EditFoodText", "FileOutputStream set");
            //Compressed bitmap to an image file
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			Log.i("EditFoodText", "PNG created from Bitmap");
			
            fos.close();
			
		} catch (Exception e) {
            Log.e("EditFoodText", "FileOutputStream/Compression failed");
            e.printStackTrace();
		}
		
		return myPath.getAbsolutePath();
		
	}
	
	private boolean eaten(String eaten){
		if (eaten.equals("eaten")){			
			return true;			
		} else {			
			return false;	
		}		
	}
	
	private String eatenValue(boolean eaten){
		
		if (eaten == true){
			return "eaten";		
		} else {
			return "";
		}
	}
	
	public void saveFoodItem(View view){
		
		HashMap<String, String> queryValuesMap = new HashMap<String, String>();
		
		foodItemNameEditText = (EditText) findViewById(R.id.foodItemNameEditText);
		foodItemLocationEditText = (EditText) findViewById(R.id.foodItemLocationEditText);
		foodItemRatingBar = (RatingBar) findViewById(R.id.foodItemRatingBar);
		foodItemReviewEditText = (EditText) findViewById(R.id.foodItemReviewEditText);
		foodItemImageView = (ImageView) findViewById(R.id.foodItemImageView);
		foodItemEatenCheckBox = (CheckBox) findViewById(R.id.foodItemEatenCheckBox);
		foodTypeSpinner = (Spinner) findViewById(R.id.foodTypeSpinner);
		
		Intent theIntent = getIntent();
		String contactId = theIntent.getStringExtra("foodId");
		
		queryValuesMap.put("_id", contactId);
		queryValuesMap.put("foodItemName", foodItemNameEditText.getText().toString());
		queryValuesMap.put("foodItemLocation", foodItemLocationEditText.getText().toString());
		queryValuesMap.put("foodItemRating", String.valueOf(foodItemRatingBar.getNumStars()));
		queryValuesMap.put("foodItemReview", foodItemReviewEditText.getText().toString());
		queryValuesMap.put("foodItemPicture", foodItemImageView.getTag().toString());
		queryValuesMap.put("foodItemEaten", eatenValue(foodItemEatenCheckBox.isChecked()));
		queryValuesMap.put("foodType", foodTypeSelectionCheck(foodTypeSpinner.getSelectedItem().toString()));

		dbTools.updateFoodItem(queryValuesMap);
		this.callMainActivity(view);

	}
	
	public String foodTypeSelectionCheck(String foodType){	
		Toast.makeText(getApplicationContext(), foodType, Toast.LENGTH_SHORT).show();
		
		if (foodType.equals(DEFAULT_SPINNER_ENTRY)) {
			return "";
		} else {
			return foodType;
		}
		
	}
	
	public void removeFoodItem(View view){
		
		Intent theIntent = getIntent();
		String foodId = theIntent.getStringExtra("foodId");
		dbTools.deleteFoodItem(foodId);

    	Toast.makeText(getApplicationContext(), "Food Item Deleted", Toast.LENGTH_SHORT).show();
    	
		this.callMainActivity(view);
		
	}
	
	public void callMainActivity(View view){

        Intent incomingIntent = getIntent();
        int activeTab = incomingIntent.getIntExtra(MainActivity.ACTIVE_TAB, 0);

        Toast.makeText(getApplicationContext(), "Returning Intent from EditFoodItem", Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.ACTIVE_TAB,activeTab);
        setResult(RESULT_OK,returnIntent);
        finish();
		
	}
	
	public void showLocationOnGoogleMaps(View view){
		
		Toast.makeText(getApplicationContext(), "starting showLocationOnGoogleMaps", Toast.LENGTH_LONG).show();
		
		String foodLocation = foodItemLocationEditText.getText().toString();
		
		if (!foodLocation.equals("")) {
			// if location is not empty...
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					Uri.parse("http://maps.google.com/maps?q=" + foodLocation + "+" + currentLocation() ));
				
			Log.v("showLocation URI: ", "http://maps.google.com/maps?q=" + foodLocation + "+" + currentLocation());

			startActivity(intent);
			
		} else {
			
			Toast.makeText(getApplicationContext(), "No location entered...", Toast.LENGTH_LONG).show();
		}
		
	}
	
	public String currentLocation() {
		// refer to https://developer.android.com/training/location/retrieve-current.html
		// to include actual city location
		Toast.makeText(getApplicationContext(), "Currently using Toronto as default city", Toast.LENGTH_LONG).show();
		
		return "Toronto";
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_food_item_actions, menu);
	    
	    MenuItem item = menu.findItem(R.id.action_share);


        menuShareActionProvider = (ShareActionProvider) item.getActionProvider();
        setShareIntent(shareIntent);
        
	    return super.onCreateOptionsMenu(menu);
	}

    private void setShareIntent(Intent shareIntent){
        if (menuShareActionProvider != null){
            Log.i("EditFoodItem", "Setting share Intent");

            //assign values to share intent
            shareFoodText = "Let's go eat " +
                    foodItemNameEditText.getText().toString() +
                    " @ " + foodItemLocationEditText.getText().toString();

            foodThumbnailUri = Uri.parse("file://" + foodItemImageView.getTag().toString());
            Log.d("EditFoodItem", "Sharing URI: " + foodThumbnailUri.toString());

            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareFoodText);
            shareIntent.putExtra(Intent.EXTRA_STREAM, foodThumbnailUri);

            menuShareActionProvider.setShareIntent(shareIntent);
            Log.i("EditFoodItem", "Share Intent set");

        }


    }


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    
	        case R.id.action_save:
	        	saveFoodItem(findViewById(R.id.action_save));
	            return true;
	            
	        case R.id.action_delete:
	        	removeFoodItem(findViewById(R.id.action_save));
	        	return true;

            case R.id.action_help:
	        	showEditListHelpDialog();
	        	return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	            
	    }
	    
	}
	
	public void showEditListHelpDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EditFoodItem.this);
		
		builder.setTitle(R.string.dialogue_edit_help);
		
		builder.setPositiveButton("Yes",null);
		
		builder.setMessage(getString(R.string.dialogue_help_edit_activity_text));
		
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
		
	}

    /** loads bitmap using an AsyncTask. ImageView tag must have URI in string form */
    public void loadBitmap(int resId, ImageView imageView) {
        BitmapLoaderBackgroundTask task = new BitmapLoaderBackgroundTask(getApplicationContext(),imageView);
        task.execute(resId);
    }

    public void shareOnFb(View v){
        Log.d("EditFoodItem", "shareOnFb started");
        Uri photoUri = Uri.parse("file://" + foodItemImageView.getTag().toString());

        CallbackManager callbackManager;
        ShareDialog shareDialog;

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        try {
            Log.i("EditFoodItem", "Create Bitmap");
            Log.v("EditFoodItem", "URI: " + photoUri.toString());
            Bitmap selectedPicture = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            Log.d("EditFoodItem", "Bitmap Created");
            Toast.makeText(getApplicationContext(),"Made a Bitmap, about to share", Toast.LENGTH_SHORT).show();

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(selectedPicture)
                    .build();
            Log.d("EditFoodItem", "SharePhoto created");
            Toast.makeText(getApplicationContext(),"SharePhoto ok", Toast.LENGTH_SHORT).show();

            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            Log.d("EditFoodItem", "SharePhotoContent created");
            Toast.makeText(getApplicationContext(),"SharePhotoContent ok", Toast.LENGTH_SHORT).show();

            Log.i("EditFoodItem", "Showing FB ShareDialog");
            shareDialog.show(content);
            Log.d("EditFoodItem", "ShareDialog shown");

        } catch (IOException e) {
            Log.e("EditFoodItem", "IOException");
            Log.v("EditFoodItem", e.toString());
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Bitmap error", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public Loader<HashMap<String, String>> onCreateLoader(int id, Bundle args) {
        FoodItemLoader foodItemLoader = new FoodItemLoader(getApplicationContext(),foodId);
        Toast.makeText(getApplicationContext(), "FoodItemLoader Created", Toast.LENGTH_SHORT).show();

        return foodItemLoader;
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, String>> loader, HashMap<String, String> data) {
        //Since there is one loader, I don't need to check the ID
        //populate layout with food details
        populateFoodItemDetails(data);
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, String>> loader) {
        Log.d("EditFoodItem", "FoodItemLoaderReset!");
        populateFoodItemDetails(null);
    }
}

// add a rating update method
// add a picture update method