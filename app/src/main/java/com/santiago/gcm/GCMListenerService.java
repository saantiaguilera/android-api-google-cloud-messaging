package com.santiago.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Override this class to receive the push notifications
 */
public abstract class GCMListenerService extends GcmListenerService {

    /**
     * Callbacks from push notifications are received here
     * @param from
     * @param data
     */
    @Override
    public abstract void onMessageReceived(String from, Bundle data);

}