package homeservice.com.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static boolean isInternelAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean mobileNumberPatternMatcher(String mobNumber)
    {

        String str = "^[2-9][0-9]{9}$";
        Pattern pttrn = Pattern.compile(str);
        Matcher mtch = pttrn.matcher(mobNumber);
        return mtch.matches();

    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }


}
