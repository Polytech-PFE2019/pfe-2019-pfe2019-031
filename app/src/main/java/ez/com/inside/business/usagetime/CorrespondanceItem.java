package ez.com.inside.business.usagetime;

import android.graphics.drawable.Drawable;

public class CorrespondanceItem {
    private Drawable image;
    private long time;
    private String text;

    public CorrespondanceItem(Drawable image, long time, String text){
        this.image = image;
        this.time = time;
        this.text = text;
    }


    public Drawable getImage() {
        return image;
    }

    public long getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
