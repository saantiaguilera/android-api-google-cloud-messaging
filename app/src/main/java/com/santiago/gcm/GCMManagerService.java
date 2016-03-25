package com.santiago.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.List;

/** @notes
 *
 * Build.gradle (app module) must have:

    compile 'com.google.android.gms:play-services-gcm:8.4.0'

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
                <category android:name="[package name]" />
            </intent-filter>
        </receiver>

        <service
            android:name="[path to your GCMListenerService]"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
            </intent-filter>
        </service>

        <service
            android:name="[path to your AmalgamaInstanceIDListenerService]"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.android.gms.iid.InstanceID" />
            </intent-filter>
        </service>
        <service
            android:name="[path to your GCMManagerService]"
            android:exported="false">
            <intent-filter>
                <action android:name="com.santiago.gcm.GCMManagerService.ACTION_UPDATE_TOKEN" />
                <action android:name="com.santiago.gcm.GCMManagerService.ACTION_REGISTER" />
            </intent-filter>
        </service>

 * Usage eg:

    private CustomGCMManager gcmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestRegId();

    }

    public GCMManager getGcmManager() {
        if(gcmManager==null){
            gcmManager = new CustomGCMManager(this);
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

    /**
     * We will only reach here from InstanceIDListenerService, which tells us that the regId changed
     * or from GCMManager.getRegId, that its just for getting the regId
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            //Get the reg id
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            //If its sending the regid to the server, then return false (I hope you do that). Else suscribe to topics as usual and send the success handling callback
            if(sendRegistrationToServer(intent, token)) {
                subscribeTopics(token);
                onHandleIntentCompleted(intent, token);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Suscribe to the topics (if existing)
     * @param token
     * @throws IOException
     */
    private void subscribeTopics(String token) throws IOException {
        List<String> topicList = getTopicList();

        if(topicList!=null && !topicList.isEmpty()) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);

            for (String topic : topicList)
                pubSub.subscribe(token, TOPIC_PREFIX + topic, null);
        }
    }

    /**
     * @return Constant value that represents the GCApp unique Id (usually the thing you get from the google cloud developers or that thing)
     */
    protected abstract String getSenderId();

    /**
     * Tell your REST Server that the regId is different
     *
     * (maybe check before if intent.getAction().equals(GCMManagerService.ACTION_UPDATE_TOKEN) ?? Because if its ACTION_REGISTER_TOKEN
     * you should have already sent it to the ws on the account creation, and since its the same, its useless to do the request again
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

    /**
     * Called when we have finished handling our intent.
     * Send to the broadcastreceiver in {@param GCMManager#GCMManager.RegistrationBroadcastReceiver } the new token
     * @param intent
     * @param token
     */
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