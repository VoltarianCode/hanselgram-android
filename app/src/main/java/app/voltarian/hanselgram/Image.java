package app.voltarian.hanselgram;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Illya on 2017-02-14.
 */

public class Image {

    Date mCreated;
    Bitmap mImage;
    String mLocation;
    String mCaption;



    public Image (){

    }

    public Image (Bitmap image, String location, String caption, Date createdAt){
        this.mCreated = createdAt;
        this.mCaption = caption;
        this.mImage = image;
        this.mLocation = location;
    }


    public Date getDate() {
        return mCreated;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getCaption() {
        return mCaption;
    }

}
