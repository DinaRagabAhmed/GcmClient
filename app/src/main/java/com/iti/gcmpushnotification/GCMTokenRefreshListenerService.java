package com.iti.gcmpushnotification;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by HP on 12/05/2017.
 */

public class GCMTokenRefreshListenerService extends InstanceIDListenerService{

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}
