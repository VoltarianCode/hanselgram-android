package app.voltarian.hanselgram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> users;
    ProgressDialog progressDialog;
    private static final String TAG = "MainActivity";
    boolean isConnected;

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

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()){
            isConnected = true;
        } else {
            isConnected = false;
        }


        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (!isConnected) {

            Toast.makeText(MainActivity.this, R.string.connect_to_internet, Toast.LENGTH_LONG).show();
        }

        //fetchUserListview();


        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
                startActivity(intent);
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            TextView textView = (TextView) findViewById(R.id.logged_in_as);
            textView.setText("Welcome Back " + ParseUser.getCurrentUser().getUsername() + "!");
        }


        Log.d(TAG, "Activity Created and Set Up");

    }

    private void fetchUserListview() {

        users.clear();

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
                    if (e == null) {
                        if (objects.size() > 0) {
                            for (ParseUser user : objects) {
                                users.add(user.getUsername());
                            }
                        }
                        userListView.setAdapter(arrayAdapter);
                    }
                    progressDialog.dismiss();
                }
            });

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
        Log.d(TAG, "Main Activity Destroyed");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Main Activity Paused");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected){
            fetchUserListview();
        }
        Log.d(TAG, "Main Activity Resumed");

    }


}
