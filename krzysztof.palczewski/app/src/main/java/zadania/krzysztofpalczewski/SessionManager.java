package zadania.krzysztofpalczewski;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by K on 2017-01-10.
 */

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    private static final String IS_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String PREFERENCES_NAME = "sessionPreferences";

    public SessionManager (Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String name){
        editor.putBoolean(IS_USER_LOGGED_IN, true);
        editor.commit();
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGGED_IN, false);
    }

}


