package zadania.krzysztofpalczewski;

import android.graphics.Bitmap;

/**
 * Created by K on 2017-01-27.
 */

public interface AsyncTaskResponse {
    void imageDownloaded(Bitmap output, int imgIndex);
}
