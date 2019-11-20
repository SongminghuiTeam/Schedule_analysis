package com.cloudea.informmodule;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DaemonIntentService extends IntentService {


    public DaemonIntentService() {
        super("DaemonIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.v("守护线程", "结束");
               break;
            }
           // Log.v("守护线程", "123");
        }

    }
}
