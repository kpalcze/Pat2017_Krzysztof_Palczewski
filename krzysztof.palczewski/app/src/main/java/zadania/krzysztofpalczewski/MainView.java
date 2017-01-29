package zadania.krzysztofpalczewski;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by K on 2017-01-29.
 */

public interface MainView extends MvpView {
    void showProgress();
    void hideProgress();
    void downloadFile();
}
