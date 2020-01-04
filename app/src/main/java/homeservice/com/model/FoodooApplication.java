package homeservice.com.model;

import android.app.Application;
import homeservice.com.pref.Globals;

public class FoodooApplication extends Application {

    private static FoodooApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Globals.init(this);

    }
    public static synchronized FoodooApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}