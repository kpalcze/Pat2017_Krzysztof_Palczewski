package zadania.krzysztofpalczewski;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 5000;
    private boolean backPressed = false;
    private boolean onStopCalled = false;
    private Handler handler;
    SessionManager sessionManager;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!backPressed){
                if (sessionManager.isUserLoggedIn()) {
                    Intent main = new Intent (SplashActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                } else {
                    Intent login = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        handler = new Handler();
        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
    }

    //if onStart() is called again (e.g. user opens app that was in background state) create handler again
    @Override
    protected void onStart() {
        super.onStart();
        if (onStopCalled) {
            handler = new Handler();
            handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH / 2);
        }
    }

    //if onStop() is called (e.g. user clicks HOME BUTTON) stop handler
    @Override
    protected void onStop() {
        super.onStop();
        onStopCalled = true;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        finish();
    }
}