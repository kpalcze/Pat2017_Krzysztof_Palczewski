package zadania.krzysztofpalczewski;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String BASE_SERVER_URL = //"http://10.0.2.2:8080";
                                                "http://192.168.0.103:8080";
    private ProgressBar progress;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        progress = (ProgressBar) findViewById(R.id.progress_bar);
        new DownloadFileFromURL().execute(BASE_SERVER_URL + "/page_0.json");
        Log.d("url", BASE_SERVER_URL + "/page_0.json");
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
                urlConnection.setDoOutput(true);
                urlConnection.connect();
                File SDCardRoot = Environment.getExternalStorageDirectory();
                File file = new File(SDCardRoot,"page_0.json");
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
            JSONObject json = readFile();
            Gson gson = new Gson();
            Image image = gson.fromJson(json.toString(), Image.class);
            List<ImageDetails> imageDetails = image.getImageDetails();
            ImageDetails img1 = imageDetails.get(0);
            String desc = img1.getDesc();
            Log.d("desc", "" + desc);
        }
    }

    private JSONObject readFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/page_0.json");
        Log.d("essa read", "" + file);
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
            Log.e("Error", "Couldn't convert to JSON");
        } catch (IOException e) {
            Log.e("Error", "Couldn't read file");
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
