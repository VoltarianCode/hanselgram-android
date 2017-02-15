package app.voltarian.hanselgram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Illya on 2017-02-14.
 */

public class ImageArrayAdapter extends ArrayAdapter<Image> {
    HashMap<Image, Integer> mIdMap = new HashMap<Image, Integer>();
    private final Context context;
    private final ArrayList<Image> images;

    public ImageArrayAdapter(Context context,
                             ArrayList<Image> objects) {
        super(context, R.layout.picture_feed, objects);
        this.context = context;
        this.images = objects;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.picture_feed, parent, false);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.listview_image);
        TextView captionView = (TextView) rowView.findViewById(R.id.image_caption);
        TextView dateView = (TextView) rowView.findViewById(R.id.date_uploaded);
        imageView.setImageBitmap(images.get(position).getImage());
        captionView.setText(images.get(position).getCaption());
        Date createdAt = images.get(position).getDate();
        if (createdAt != null){
            String date = images.get(position).getDate().toString();
            dateView.setText(date);

        }


        return rowView;
    }

    @Override
    public long getItemId(int position) {
        Image item = images.get(position);
        mIdMap.put(item, position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
