package zadania.krzysztofpalczewski;

import android.content.Intent;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

/**
 * Created by K on 2017-01-29.
 */

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    LoginModel loginModel = new LoginModel();

    void onLoginButtonClicked(String emailWritten, String passwordWritten) {
        if (loginModel.isEmailValid(emailWritten)){
            getView().showEmailCorrect();
        } else {
            getView().showWrongEmail();
        }
        if (loginModel.isPasswordValid(passwordWritten)) {
            getView().showPasswordCorrect();
        } else {
            getView().showWrongPassword();
        }
        if (loginModel.isEmailValid(emailWritten) && loginModel.isPasswordValid(passwordWritten)) {
            getView().goToMainActivity();
        }
    }
}
