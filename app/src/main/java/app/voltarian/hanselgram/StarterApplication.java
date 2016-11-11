/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package app.voltarian.hanselgram;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        // GET YOUR OWN PARSE SERVER CREDENTIALS AND ADD THEM HERE
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(getResources().getString(R.string.app_id))
                .clientKey(getResources().getString(R.string.client_key))
                .server(getResources().getString(R.string.server_url))
                .build()
        );

        /*

        ParseObject object = new ParseObject("Example");

        //will make an object with a column name aString and will
        // add 'hanselgram is coming' to that column in the new object

        object.put("aString", "hanselgram is coming");

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    Log.i("Parse Result", "Successful!");
                } else {
                    Log.i("Parse Result", "Failed" + ex.toString());
                }
            }
        });

        */


        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
