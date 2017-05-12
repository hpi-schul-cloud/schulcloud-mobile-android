package org.schulcloud.mobile.util.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.requestBodies.Device;

import javax.inject.Inject;

public class FirebaseIDService extends FirebaseInstanceIdService {
    @Inject
    DataManager mDataManager;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, FirebaseIDService.class);
    }

    private static final String TAG = "FirebaseIDService";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service started");
        SchulCloudApplication.get(this).getComponent().inject(this);
        onTokenRefresh();
    }

    /**
     * Method which is called onTokenRefresh, which will then register the device with third party applications
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        PreferencesHelper helper = new PreferencesHelper(this.getBaseContext());
        if (helper.getMessagingToken().equals("null")) {
            helper.saveMessagingToken(token);
            Log.d(TAG, "sending registration to Server");
            Device device = new Device("firebase", "mobile", "test", helper.getCurrentUserId(), token, "android");

            mDataManager.createDevice(device);
        } else {
            Log.d(TAG, "device already registered");
        }
    }
}
