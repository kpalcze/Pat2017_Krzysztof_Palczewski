package zadania.krzysztofpalczewski;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.regex.Pattern;

public class LoginActivity extends MvpActivity<LoginView, LoginPresenter> implements LoginView {

    private EditText email, password;
    private TextView wrongEmail, wrongPassword;
    SessionManager sessionManager;

    public LoginPresenter createPresenter(){
        return new LoginPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_login);
        sessionManager = new SessionManager(getApplicationContext());
        initViews();
    }

    private void initViews() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        wrongEmail = (TextView) findViewById(R.id.wrongEmail);
        wrongPassword = (TextView) findViewById(R.id.wrongPassword);
    }

    public void onLoginButtonClicked(View v) {
        String emailWritten = email.getText().toString();
        String passwordWritten = password.getText().toString();

        getPresenter().onLoginButtonClicked(emailWritten, passwordWritten);
    }

    public void showWrongPassword(){
        wrongPassword.setText(R.string.wrong_password);
    }

    public void showWrongEmail(){
        wrongEmail.setText(R.string.wrong_email);
    }

    public void showEmailCorrect() {
        wrongEmail.setText("");
    }

    public void showPasswordCorrect() {
        wrongPassword.setText("");
    }

    public void goToMainActivity(){
        sessionManager.createUserLoginSession("sessionPreferences");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
