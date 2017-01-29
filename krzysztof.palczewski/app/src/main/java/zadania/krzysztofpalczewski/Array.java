
package zadania.krzysztofpalczewski;


import android.graphics.Bitmap;


public class Array {

    private String title;
    private String desc;
    private String url;
    private Bitmap image;

    public Bitmap getImage() { return image; }

    public void setBitmap(Bitmap image) {
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}