package app.voltarian.hanselgram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserFeedActivity extends AppCompatActivity implements View.OnLongClickListener{

    ProgressDialog progressDialog;
    String activeUsername;
    boolean isConnected;
    ArrayList<Image> images = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        setContentView(R.layout.activity_user_feed);


        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            isConnected = true;
        } else {
            isConnected = false;
        }



        Intent intent = getIntent();
        activeUsername = intent.getStringExtra("username");
        getSupportActionBar().setTitle(activeUsername);

        if (isConnected){
            showProgressDialog();
            fetchUserFeed();
        } else {
            Toast.makeText(UserFeedActivity.this, R.string.connect_to_internet, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public boolean onLongClick(View v) {
        return false;
    }



    public void fetchUserFeed(){

        final ListView imageListView = (ListView) findViewById(R.id.feed);
        final ImageArrayAdapter arrayAdapter = new ImageArrayAdapter(this, images);
        imageListView.setAdapter(arrayAdapter);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
        //query.setLimit(5);
        query.whereEqualTo("username", activeUsername);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null ){
                    if (objects.size() > 0){
                        Log.d("Images Found: ", Integer.toString(objects.size()));
                        for (ParseObject object: objects){

                            final ParseFile file = (ParseFile) object.get("image");
                            final Date createdAt = (Date) object.getCreatedAt();
                            final String caption = (String) object.get("caption");
                            final String location = (String) object.get("location");

                            // Using the foreground getData method to maintain the order of the
                            // images in the linear layout. Getting it in background and using callbacks
                            // gave nearly random order each time.



                            Thread thread = new Thread (){
                                @Override
                                public void run(){
                                    try {
                                        byte [] data = new byte[0];
                                        data = file.getData();
                                        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        Image image = new Image(b, location, caption, createdAt);
                                        images.add(image);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                arrayAdapter.notifyDataSetChanged();
                                            }
                                        });


                                    } catch (ParseException e1) {

                                        e1.printStackTrace();

                                    } catch (OutOfMemoryError e2){

                                        e2.printStackTrace();
                                        Toast.makeText(UserFeedActivity.this, "Ran out of RAM", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);

                                    }
                                }
                            };

                            thread.start();
                            /*
                            try {
                                data = file.getData();
                                Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                                Image image = new Image(b, location, caption, createdAt);
                                images.add(image);
                                arrayAdapter.notifyDataSetChanged();

                            } catch (ParseException e1) {

                                e1.printStackTrace();

                            } catch (OutOfMemoryError e2){

                                e2.printStackTrace();
                                Toast.makeText(UserFeedActivity.this, "Ran out of RAM", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            }
                            */

                            /*
                            ParseFile file = (ParseFile) object.get("image");

                            // Using the foreground getData method to maintain the order of the
                            // images in the linear layout. Getting it in background and using callbacks
                            // gave nearly random order each time.

                            byte [] data = new byte[0];
                            try {
                                data = file.getData();
                                Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                                lp.setMargins(Math.round(density*10), Math.round(density*10), Math.round(density*10), Math.round(density*20));
                                imageView.setLayoutParams(lp);

                                imageView.setImageBitmap(b);
                                linearLayout.addView(imageView);

                            } catch (ParseException e1) {

                                e1.printStackTrace();

                            } catch (OutOfMemoryError e2){

                                e2.printStackTrace();
                                Toast.makeText(UserFeedActivity.this, "Ran out of RAM", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            }
                             */


                            /*
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null){
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));
                                        imageView.setImageBitmap(bitmap);
                                        linearLayout.addView(imageView);
                                    }
                                }
                            });
                            */
                        }
                    }
                    //Toast.makeText(UserFeedActivity.this, Integer.toString(objects.size()) + " Images Found", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UserFeedActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        backToMainActivity();
    }

    public void backToMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(UserFeedActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Images...");
        progressDialog.show();
    }


}
