package com.imperialtechnologies.theeatlist_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by kdandang on 4/10/2015.
 */
public class BitmapLoaderBackgroundTask extends AsyncTask<Integer, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Uri fullPhotoUri;
    private Context applicationContext;

    public BitmapLoaderBackgroundTask(Context context, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        fullPhotoUri = Uri.parse(imageView.getTag().toString());
        applicationContext = context;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        //data is the parameters sent in execute(parameters)

        //TODO change picture to something that implies "loading image"

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=8;

        Bitmap selectedPicture = null;

        Log.d("BitmapLoaderTask", "Async file decode starting...");
        selectedPicture = BitmapFactory.decodeFile(fullPhotoUri.toString(), options);

        Log.d("BitmapLoaderTask", "Async file decode complete");
        return selectedPicture;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                resizeBitmapForImageView(imageView, bitmap);//imageView.setImageBitmap(bitmap);
            }
        }
        Toast.makeText(applicationContext,"BitmapLoaderBackgroundTask completed",Toast.LENGTH_SHORT).show();
    }

    private void resizeBitmapForImageView(ImageView imageView, Bitmap thumbnail){


        //TODO - don't pixelate the image
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(thumbnail);
        Log.d("BitmapLoader", "Width: " + imageView.getWidth() + " Height: " + imageView.getHeight());
        Log.i("BitmapLoader", "Thumbnail Bitmap scaled and set for imageView");

    }
}