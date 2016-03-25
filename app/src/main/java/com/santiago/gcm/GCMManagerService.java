package com.santiago.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.List;

/* @notes
:
    * Manifest must have:

    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <uses-permission android:name="[your package].permission.C2D_MESSAGE" />

    <permission android:name="[your package].permission.C2D_MESSAGE"
        android:protectionLevel="signature" />

    * Application must have (inside <application>):

    <receiver
            android:name="com.google.android.gms.gcm.GcmReceiver"
            android:exported="true"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="[your package]" />
            </intent-filter>
        </receiver>

        <service
            android:name="[Path of your GCMListenerService derived class]"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            </intent-filter>
        </service>

        <service
            android:name="com.santiago.gcm.InstanceIDListenerService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.android.gms.iid.InstanceID" />
            </intent-filter>
        </service>
        <service
            android:name="[Path of your GCMManagerService derived class]"
            android:exported="false">
            <intent-filter>
                <action android:name="com.santiago.gcm.GCMManagerService.ACTION_UPDATE_TOKEN" />
                <action android:name="com.santiago.gcm.GCMManagerService.ACTION_REGISTER" />
            </intent-filter>
        </service>

    * If you will need the token in an Activity or Context derived thing. Register a BroadcastReceiver eg:

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = intent.getStringExtra(GCMManagerService.EXTRA_TOKEN);
                // use the token
            }
        };

        if (checkPlayServices()) { // Always first check if the phone supports GPlay Services
            Intent intent = new Intent(GCMManagerService.ACTION_REGISTER);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMManagerService.ACTION_REGISTER_COMPLETED));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.w(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

 */








/*

    * Hay que hacer implementaciones de AmalgamaGCMListenerService con la respuesta a una push y de GCMManagerService para entregar un senderId, topics y actualizar el token al server.

    * hay que hacer una implementacion de AmalgamaInstanceIDListenerService y GCMManager pasandole el class de la implementacion de GCMManagerService (por compatibilidad con lollipop)

    * si en la funcion de actualizar al server se ahce de manera asincronica (una request) hay que llamar a onHandleIntentCompleted con el intent y token dado.

    * hay que agregar en el manifest:

    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <uses-permission android:name="[agregar el package aca].permission.C2D_MESSAGE" />

    <permission android:name="[agregar el package aca].permission.C2D_MESSAGE"
        android:protectionLevel="signature" />

    y en <application>

    <receiver
            android:name="com.google.android.gms.gcm.GcmReceiver"
            android:exported="true"
            android:permission="com.google.android.c2dm.permission.SEND" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                <category android:name="[agregar el package aca]" />
            </intent-filter>
        </receiver>

        <service
            android:name="[agregar la ruta a la implementacion de AmalgamaGCMListenerService aca]"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            </intent-filter>
        </service>

        <service
            android:name="[agregar la ruta a la implementacion de AmalgamaInstanceIDListenerService aca]"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.android.gms.iid.InstanceID" />
            </intent-filter>
        </service>
        <service
            android:name="[agregar la ruta a la implementacion de GCMManagerService aca]"
            android:exported="false">
            <intent-filter>
                <action android:name="com.theamalgama.gcm.GCMManagerService.ACTION_UPDATE_TOKEN" />
                <action android:name="com.theamalgama.gcm.GCMManagerService.ACTION_REGISTER" />
            </intent-filter>
        </service>

    * para inicializar el sistema hay que hacer esto:
    *
    *
    *

    private SoyDeporteGCMManager gcmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestRegId();

    }

    public GCMManager getGcmManager() {
        if(gcmManager==null){
            gcmManager = new SoyDeporteGCMManager(this);
            gcmManager.setListener(new GCMManager.GCMManagerListener() {
                @Override
                public void onRegIDObtained(String string) {
                    Splash.this.onRegIDObtained(string);
                }
            });
        }
        return gcmManager;
    }

    private void requestRegId() {

        getGcmManager().requestRegId();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGcmManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getGcmManager().onPause();
    }




 */





public abstract class GCMManagerService extends IntentService {

    private static final String TOPIC_PREFIX = "/topics/";

    public static final String ACTION_UPDATE_TOKEN = "com.santiago.gcm.GCMManagerService.ACTION_UPDATE_TOKEN";
    public static final String ACTION_UPDATE_TOKEN_COMPLETED = "com.santiago.gcm.GCMManagerService.ACTION_UPDATE_TOKEN_COMPLETED";
    public static final String ACTION_REGISTER = "com.santiago.gcm.GCMManagerService.ACTION_REGISTER";
    public static final String ACTION_REGISTER_COMPLETED = "com.santiago.gcm.GCMManagerService.ACTION_REGISTER_COMPLETED";

    public static final String EXTRA_TOKEN = "com.santiago.gcm.GCMManagerService.EXTRA_TOKEN";

    public GCMManagerService() {
        super(GCMManagerService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            if(sendRegistrationToServer(intent, token) && subscribeTopics(token))
                onHandleIntentCompleted(intent, token);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private boolean subscribeTopics(String token) throws IOException {
        List<String> topicList = getTopicList();

        if(topicList!=null && !topicList.isEmpty()) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);

            for (String topic : topicList)
                pubSub.subscribe(token, TOPIC_PREFIX + topic, null);
        }

        return true;
    }

    /**
     * @return Constant value that represents the GCApp unique Id (usually the thing you get from the google cloud developers or that thing)
     */
    protected abstract String getSenderId();

    /**
     * Tell your REST Server that the regId is different (maybe check before if intent.getAction().equals(GCMManagerService.ACTION_UPDATE_TOKEN) ??
     *
     * @note <strong> If this task will be async (and you will return false ofc because you dont know yet the response),
     * dont forget to call onHandleIntentCompleted (with this intent, token) after the success </strong>
     *
     * @param intent intent that triggered this action
     * @param token new Token
     * @return if it was successful or not the registration in the server
     */
    protected abstract boolean sendRegistrationToServer(Intent intent, String token);

    /**
     * @return topics that the GCM will suscribe to. Null to just listen to everything
     */
    protected abstract List<String> getTopicList();

    protected void onHandleIntentCompleted(Intent intent, String token){
        switch (intent.getAction()){
            case ACTION_REGISTER:
                Intent registrationComplete = new Intent(ACTION_REGISTER_COMPLETED);
                registrationComplete.putExtra(EXTRA_TOKEN,token);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
                break;

            case ACTION_UPDATE_TOKEN:
                Intent updateComplete = new Intent(ACTION_UPDATE_TOKEN_COMPLETED);
                updateComplete.putExtra(EXTRA_TOKEN,token);
                LocalBroadcastManager.getInstance(this).sendBroadcast(updateComplete);
                break;
        }
    }

}