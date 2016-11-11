package app.voltarian.hanselgram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
