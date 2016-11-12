package app.voltarian.hanselgram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity {

    protected static boolean authenticated;
    protected static SharedPreferences sharedPreferences;

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
            case R.id.settings:
                Log.i("Menu Item Selected: ", "Settings");
                //OPEN SETTINGS ACTIVITY
                return true;
            case R.id.logout:
                Log.i("Menu Item Selected: ", "Log Out");
                sharedPreferences.edit().putBoolean("authenticated", false).apply();
                Intent intent = new Intent(this, MainActivity.class);
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

        sharedPreferences = this.getSharedPreferences("app.voltarian.hanselgram", Context.MODE_PRIVATE);
        authenticated = sharedPreferences.getBoolean("authenticated", false);

        if (!authenticated){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }





        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
