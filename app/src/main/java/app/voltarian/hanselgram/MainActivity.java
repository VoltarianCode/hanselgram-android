package app.voltarian.hanselgram;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> users;
    ProgressDialog progressDialog;
    private static final String TAG = "MainActivity";


    protected static boolean authenticated;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add_pic:
                Log.i("Menu Item Selected: ", "Add Picture");
                requestPicture();
                Toast.makeText(MainActivity.this, "Uploading to Timeline", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Log.i("Menu Item Selected: ", "Log Out");
                ParseUser.getCurrentUser().logOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;

        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
        users = new ArrayList<String>();

        progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Resources...");


        if (ParseUser.getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }


        if (ParseUser.getCurrentUser() != null) {
            final ListView userListView = (ListView) findViewById(R.id.user_listview);

            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                    intent.putExtra("username", users.get(position));
                    startActivity(intent);
                }
            });
            ParseQuery<ParseUser> query = ParseUser.getQuery();

            final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);

            //query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            query.addAscendingOrder("username");

            progressDialog.show();

            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null){
                        if (objects.size() > 0){
                            for (ParseUser user: objects){
                                users.add(user.getUsername());
                            }
                        }
                        userListView.setAdapter(arrayAdapter);
                    }
                    progressDialog.dismiss();
                }
            });

        }


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        Log.d(TAG, "Activity Created and Set Up");

    }

    public void requestPicture (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            getPicture();
        }
    }

    public void getPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPicture();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Bitmap mutableBitmap = convertToMutable(bitmap);

                int height = mutableBitmap.getHeight();
                int width = mutableBitmap.getWidth();

                // TODO SCALE IT BASED ON USER'S SCREEN SIZE
                // maybe compress only if the image is large, if its already optimized for web
                // leave it alone, send lossless png
                

                if (width > 3000){
                    mutableBitmap = getResizedBitmap(mutableBitmap, width/3, height/3);
                    //mutableBitmap.setWidth(mutableBitmap.getWidth()/3);
                } else if (width > 2000){
                    mutableBitmap = getResizedBitmap(mutableBitmap, width/2, height/2);
                    //mutableBitmap.setWidth(mutableBitmap.getWidth()/2);
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mutableBitmap.compress(Bitmap.CompressFormat.WEBP, 80, stream);
                byte [] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("image.webp", byteArray);
                ParseObject object = new ParseObject("Image");
                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null ){
                            Toast.makeText(MainActivity.this, "Image Added to Timeline", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "Failed to Add Image, Check Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (IOException e) {

                e.printStackTrace();

            } catch (OutOfMemoryError error){

                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to Add Image, Using Too Much RAM", Toast.LENGTH_LONG).show();

            }
        }


    }
    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        progressDialog.dismiss();
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity Destroyed");

    }
    /**
     * Converts an immutable bitmap to a mutable one. This operation doesn't allocate
     * any additional memory. Should be memory efficient.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a mutable copy of imgIn.
     */
    public Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = File.createTempFile("temp.tmp", null, getApplicationContext().getCacheDir());
            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
