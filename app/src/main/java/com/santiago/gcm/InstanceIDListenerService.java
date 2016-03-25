package com.santiago.gcm;

import android.content.Intent;

/**
 * Must implement this. TODO Should try to find a way to start a child from service implicit
 * Used for receiving from google token updates and we catch them in the {@link GCMManagerService#onHandleIntentCompleted(Intent, String)}
 */
public abstract class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    /**
     * When the Google Cloud changes our reg id, we get a callback here.
     * Start/Send the intent to the service to notify about the update
     */
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this,getGCMManagerServiceClass());
        intent.setAction(GCMManagerService.ACTION_UPDATE_TOKEN);
        startService(intent);
    }

    /**
     * Implement and return your child GCMManager service class
     * @return
     */
    public abstract Class<?> getGCMManagerServiceClass();

}