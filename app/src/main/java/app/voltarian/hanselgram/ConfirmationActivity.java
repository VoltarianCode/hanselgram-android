package app.voltarian.hanselgram;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ConfirmationActivity extends AppCompatActivity {


    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        setContentView(R.layout.activity_confirmation);



        Button upload = (Button) findViewById(R.id.confirmUploadButton);
        Button cancel = (Button) findViewById(R.id.cancelUploadButton);

        EditText location_from_user = (EditText) findViewById(R.id.location_from_user);
        location_from_user.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    uploadPicture();
                }
                return false;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture();
            }
        });

        TextView confirmUpload = (TextView) findViewById(R.id.confirm_upload);
        confirmUpload.setText("Confirm Upload");

        requestPicture();

    }

    public void requestPicture (){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                getPicture();
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

            selectedImage = data.getData();
            ImageView main = (ImageView) findViewById(R.id.image_for_upload);
            Bitmap bitmap = null;
            Bitmap mutableBitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                 mutableBitmap = convertToMutable(bitmap);

                int height = mutableBitmap.getHeight();
                int width = mutableBitmap.getWidth();

                if (width > 3000){
                    mutableBitmap = getResizedBitmap(mutableBitmap, width/3, height/3);
                    //mutableBitmap.setWidth(mutableBitmap.getWidth()/3);
                } else if (width > 2000){
                    mutableBitmap = getResizedBitmap(mutableBitmap, width/2, height/2);
                    //mutableBitmap.setWidth(mutableBitmap.getWidth()/2);
                } else if (width > 1500 || height > 1500){
                    mutableBitmap = getResizedBitmap(mutableBitmap, 2*width/3, 2*height/3);
                    //mutableBitmap.setWidth(mutableBitmap.getWidth()/2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mutableBitmap != null){
                main.setImageBitmap(mutableBitmap);

            }


        } else {
            backToMainActivity();
        }


    }

    public void uploadPicture (){
        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

            Bitmap mutableBitmap = convertToMutable(bitmap);

            int height = mutableBitmap.getHeight();
            int width = mutableBitmap.getWidth();

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
                        Toast.makeText(getApplicationContext(), "Image Added to Timeline", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to Add Image, Check Internet Connection", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } catch (IOException e) {

            e.printStackTrace();

        } catch (OutOfMemoryError error) {

            error.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to Add Image, Using Too Much RAM", Toast.LENGTH_LONG).show();

        }
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

            //Create a new bitmap to load the bitmap again.
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

    @Override
    public void onResume(){
        super.onResume();

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
}
