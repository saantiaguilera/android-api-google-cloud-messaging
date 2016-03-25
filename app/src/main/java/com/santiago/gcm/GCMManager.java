package com.santiago.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public abstract class GCMManager {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Context context;
    private RegistrationBroadcastReceiver registrationBroadcastReceiver;
    private GCMManagerListener listener;

    public GCMManager(Context context) {
        this.context = context;

        //Create our broadcastreceiver and register it
        registrationBroadcastReceiver = new RegistrationBroadcastReceiver();
        onResume();
    }

    public Context getContext() {
        return context;
    }

    public GCMManagerListener getListener() {
        return listener;
    }

    public void setListener(GCMManagerListener listener) {
        this.listener = listener;
    }

    /**
     * Method for requesting our regId
     * Starts the service (if not started sends the register intent to handle)
     *
     * @note: must set listener to receive a callback of the regId Obtained
     */
    public void requestRegId(){
        if (checkPlayServices()) {
            Intent intent = new Intent(getContext(),getGCMManagerServiceClass());
            intent.setAction(GCMManagerService.ACTION_REGISTER);
            getContext().startService(intent);
        }
    }

    /**
     * Call each time the app goes to foreground
     * This registers our Broadcast Receiver to receive when the regId was obtained
     */
    public void onResume(){
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(GCMManagerService.ACTION_REGISTER_COMPLETED);
            filter.addAction(GCMManagerService.ACTION_UPDATE_TOKEN_COMPLETED);

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(registrationBroadcastReceiver, filter);
        } catch (IllegalArgumentException e) {
            //Already registered
        }
    }

    /**
     * Call each time the app goes to background
     * This unregisters our broadcast
     */
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(registrationBroadcastReceiver);
    }

    /**
     * Checks if the mobile supports Play Services
     * @return flag of the support of GPs
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode) && getContext() instanceof Activity)
                apiAvailability.getErrorDialog((Activity) getContext(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else android.os.Process.killProcess(android.os.Process.myPid());

            return false;
        }

        return true;
    }


    /**
     * Implement in a child class
     * TODO find a way to start the child service implicitly
     * @return GCMManager child service .class
     */
    public abstract Class<?> getGCMManagerServiceClass();

    /**
     * Our broadcast receiver to get notified when a new token has come
     */
    public class RegistrationBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(GCMManagerService.EXTRA_TOKEN) && getListener()!=null)
                getListener().onRegIDObtained(intent.getStringExtra(GCMManagerService.EXTRA_TOKEN));
        }

    }

    /**
     * Listener interface for regId obtention
     */
    public interface GCMManagerListener {
        void onRegIDObtained(String string);
    }

}
