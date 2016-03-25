package com.santiago.gcm;

import android.content.Intent;

/**
 * Dont implement this.
 * Used for receiving from google token updates and we catch them in the {@link GCMManagerService#onHandleIntentCompleted(Intent, String)}
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(GCMManagerService.ACTION_UPDATE_TOKEN);
        startService(intent);
    }

}