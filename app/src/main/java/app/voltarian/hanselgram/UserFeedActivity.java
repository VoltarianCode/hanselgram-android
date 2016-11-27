package app.voltarian.hanselgram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity implements View.OnLongClickListener{

    ProgressDialog progressDialog;
    LinearLayout linearLayout;
    String activeUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        showProgressDialog();


        Intent intent = getIntent();
        activeUsername = intent.getStringExtra("username");

        if (activeUsername.toCharArray()[activeUsername.length() - 1] == 's'){

            getSupportActionBar().setTitle(activeUsername + "' Feed");

        } else {

            getSupportActionBar().setTitle(activeUsername + "s' Feed");

        }

        fetchUserFeed();




        //imageView.setImageDrawable();
    }



    @Override
    public boolean onLongClick(View v) {

        return false;
    }



    public void fetchUserFeed(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

        query.whereEqualTo("username", activeUsername);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null ){
                    if (objects.size() > 0){
                        Log.d("Images Found: ", Integer.toString(objects.size()));
                        for (ParseObject object: objects){
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
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                float density = getApplicationContext().getResources().getDisplayMetrics().density;
                                lp.setMargins(Math.round(density*10), Math.round(density*10), Math.round(density*10), Math.round(density*10));
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
                    Toast.makeText(UserFeedActivity.this, Integer.toString(objects.size()) + " Images Found", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UserFeedActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }



    public void showProgressDialog(){
        progressDialog = new ProgressDialog(UserFeedActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Images...");
        progressDialog.show();

        linearLayout = (LinearLayout) findViewById(R.id.user_feed_linear_layout);
    }


}
