package zadania.krzysztofpalczewski;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncTaskResponse {

    public final static String BASE_SERVER_URL =  "http://10.0.2.2:8080";
                                                    //"http://192.168.0.106:8080";


    private ProgressBar progress;
    private boolean isConnectedToInternet = false;
    private boolean loading = true;
    private boolean tabletSize;
    private int currentPage = 1;
    private int imgIndex = 0;
    private List<Array> imgsDetails;
    private List<Array> allImgsDetails = new ArrayList<Array>();
    private String fileName = "page_0.json";
    private String[] fileNames = {"page_0.json", "page_1.json", "page_2.json"};
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private LruCache<String, Bitmap> mMemoryCache;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());

        initViews();
        checkInternetConnection();
        if (isConnectedToInternet) {
           downloadFile(fileName);
        }
        //TODO add swipe refresh
        initRecyclerView();
    }

    private void initViews() {
        progress = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tabletSize = getResources().getBoolean(R.bool.isTablet);
    }

    private void initRecyclerView() {
        tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            mLayoutManager = new GridLayoutManager(this, 4);
        } else {
            mLayoutManager = new GridLayoutManager(this, 2);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ImageAdapter(this, allImgsDetails);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0)
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    //when scrolled to the end of list, load new file
                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            int lastPage = fileNames.length;
                            if (currentPage == lastPage) {
                                return;
                            }
                            fileName = fileNames[currentPage];
                            currentPage++;
                            downloadFile(fileName);
                        }
                    }
                }
            }
        });
    }

    private void checkInternetConnection(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInf = connMgr.getActiveNetworkInfo();
        if (netInf != null && netInf.isConnected()){
            isConnectedToInternet= true;
        } else{
            isConnectedToInternet = false;
            Context context = getApplicationContext();
            CharSequence text = getResources().getString(R.string.no_internet_connection);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    void downloadFile(String fileEnding){
        // first check for permissions
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                new DownloadFileFromURL().execute(BASE_SERVER_URL + "/" + fileEnding);
            }
        } else {
            new DownloadFileFromURL().execute(BASE_SERVER_URL + "/" + fileEnding);
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(fileName);
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.connect();

                File SDCardRoot = Environment.getExternalStorageDirectory();
                File file = new File(SDCardRoot, fileName);
                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    fileOutput.write(buffer, 0, bufferLength);
                }

                fileOutput.flush();
                fileOutput.getFD().sync();
                fileOutput.close();

            } catch (final MalformedURLException e) {
                Log.e("MalformedURLException ", "");
                e.printStackTrace();
            } catch (final IOException e) {
                Log.e("IOException", "");
                e.printStackTrace();
            }
            catch (final Exception e) {
                Log.e("Exception", "");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            progress.setVisibility(View.INVISIBLE);
            loading = true;
            populateListFromFile();

            /*
              when displaying on tablet 10 (or even 20) cards is not enough to scroll the recycler view, so download next pages automatically
             */
            if (tabletSize && ( currentPage == 1 || currentPage == 2)) {
                fileName = fileNames[currentPage];
                downloadFile(fileName);
                currentPage++;
            }
        }
    }

    private void populateListFromFile() {
        JSONObject json = readFile(fileName);
        String sJson = json.toString();
        Gson gson = new Gson();
        if (json != null) {
            Image image = gson.fromJson(json.toString(), Image.class);
            imgsDetails = image.getArray();
            for (int i = 0; i < imgsDetails.size(); i++) {
                allImgsDetails.add(imgsDetails.get(i));
            }
            adapter.notifyDataSetChanged();
            loadImages();
            adapter.notifyDataSetChanged();
        }
    }

    private void loadImages() {
        for (int i = 0; i < imgsDetails.size(); i++) {
            ImageLoader imgLoader = new ImageLoader(imgsDetails.get(i).getUrl(), adapter, i, MainActivity.this, imgIndex);
            imgLoader.loadImage();
            imgIndex++;
        }
    }

    @Override
    public void imageDownloaded(Bitmap output, int imgIndex){
        if (allImgsDetails != null) {
            allImgsDetails.get(imgIndex).setBitmap(output);
        }

    }

    private JSONObject readFile(String fileEnding) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/" + fileEnding);
        StringBuilder builder = new StringBuilder();
        JSONObject json;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            br.close();
            json = new JSONObject(builder.toString());
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sessionManager.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
