package zadania.krzysztofpalczewski;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.name;

/**
 * Created by K on 2017-01-27.
 */

public class ImageLoader {

    private static int NEW_WIDTH = 512, NEW_HEIGHT = 512;
    private String url;
    private Bitmap image;
    private ImageAdapter imgAdapter;
    private AsyncTaskResponse asyncTaskResponse;
    private int imgIndex;

    public ImageLoader(String imgUrl, ImageAdapter imgAdapter, int position,
                       AsyncTaskResponse activityContext, int imgIndex) {
        this.url = imgUrl;
        this.imgAdapter = imgAdapter;
        this.image = null;
        this.asyncTaskResponse = activityContext;
        this.imgIndex = imgIndex;
    }

    public Bitmap getImage() {
        return image;
    }

    public void loadImage() {
        if (url != null && !url.equals("")) {
            new ImageLoadTask().execute(url);
        }
    }

    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {


        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }

        protected Bitmap doInBackground(String... urls) {
             {
                return getScaledBitmapUsingMatrix(urls[0], NEW_WIDTH, NEW_HEIGHT );
                //return getScaledBitmapFromUrl(urls[0], 512, 512);
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.i("ImageLoadTask", "Successfully loaded " + name + " image");
                if (imgAdapter != null) {
                    asyncTaskResponse.imageDownloaded(bitmap, imgIndex);
                    imgAdapter.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load " + name + " image");
            }
        }
    }

    private Bitmap getScaledBitmapUsingMatrix(String urls, int reqWidth, int reqHeight) {
        try {
            URL url = new URL(urls);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap = getResizedBitmap(myBitmap, reqWidth, reqHeight);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }
}

