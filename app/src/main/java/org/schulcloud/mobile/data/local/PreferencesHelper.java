package org.schulcloud.mobile.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.injection.ApplicationContext;

@Singleton
public class PreferencesHelper {

    public static final String PREF_FILE_NAME = "schulcloud_pref_file";

    private final SharedPreferences mPref;

    @Inject
    public PreferencesHelper(@ApplicationContext Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public String saveAccessToken(AccessToken accessToken) {
        SharedPreferences.Editor editor = mPref.edit();
        // todo: save pref-keys in strings.xml or constant
        editor.putString("jwt", accessToken.accessToken);
        if (editor.commit()) {
            return accessToken.getAccessToken();
        }

        return null;
    }

    public String getAccessToken() {
        return mPref.getString("jwt", "null");
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

}
