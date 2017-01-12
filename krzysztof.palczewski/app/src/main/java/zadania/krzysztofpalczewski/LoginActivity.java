package zadania.krzysztofpalczewski;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView wrongEmail, wrongPassword;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);
        sessionManager = new SessionManager(getApplicationContext());

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        wrongEmail = (TextView) findViewById(R.id.wrongEmail);
        wrongPassword = (TextView) findViewById(R.id.wrongPassword);
    }

    public void onLoginButtonClicked(View v) {
        String emailWritten = email.getText().toString();
        String passwordWritten = password.getText().toString();
        if (isEmailValid(emailWritten)){
            wrongEmail.setText("");
        }
        if (isPasswordValid(passwordWritten)) {
            wrongPassword.setText("");
        }
        if (isEmailValid(emailWritten) && isPasswordValid(passwordWritten)) {
            sessionManager.createUserLoginSession("sessionPreferences");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        boolean isEmailValid = pattern.matcher(email).matches();
        if (!isEmailValid)
            wrongEmail.setText(R.string.wrong_email);
        return isEmailValid;
    }

    private boolean isPasswordValid(String password) {
        char[] c_password = password.toCharArray();
        boolean isAtLeast8 = c_password.length >= 8;
        boolean hasDigit = false;
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;

        //searches for digit
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= '0' && c_password[i] <= '9') {
                Log.d("debug", "digit found");
                hasDigit = true;
                break;
            }
        }

        //searches for lowercase letter
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= 97 && c_password[i] <= 122) {
                Log.d("debug", "lowercase letter found");
                hasLowerCase = true;
                break;
            }
        }

        //searches for uppercase letter
        for (int i = 0; i < c_password.length; ++i) {
            if (c_password[i] >= 65 && c_password[i] <= 90) {
                Log.d("debug", "uppercase letter found");
                hasUpperCase = true;
                break;
            }
        }

        if (!hasUpperCase)
            wrongPassword.setText(R.string.wrong_password_uppercase);

        if (!hasLowerCase)
            wrongPassword.setText(R.string.wrong_password_lowercase);

        if (!hasDigit)
            wrongPassword.setText(R.string.wrong_password_digit);

        if (!isAtLeast8)
            wrongPassword.setText(R.string.wrong_password_length);

        if (isAtLeast8 && hasDigit && hasLowerCase && hasUpperCase) {
            return true;
        }
        else {
            return false;
        }
    }
}
