package zadania.krzysztofpalczewski;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by K on 2017-01-29.
 */

public interface LoginView extends MvpView {

    void showWrongPassword();
    void showWrongEmail();
    void showEmailCorrect();
    void showPasswordCorrect();
    void goToMainActivity();
}
