package kouts.nasia.aueb.gr.bakingrecipes.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;

import kouts.nasia.aueb.gr.bakingrecipes.R;

public class ConfigurationsInfo {
    // at place 0 -> width, at place 1 -> height
    public static int[] findScreenDimens(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        int[] screenDimens = new int[2];
        screenDimens[0] = outMetrics.widthPixels;
        screenDimens[1] = outMetrics.heightPixels;
        return screenDimens;
    }

    public static boolean onPhoneLandscape(Context context){
        if(!context.getResources().getBoolean(R.bool.isTablet)) {
            if (context.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                return true;
            }
        }
        return false;
    }

    public static boolean onPhonePortrait(Context context){
        if(!context.getResources().getBoolean(R.bool.isTablet)) {
            if (context.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_PORTRAIT ){
                return true;
            }
        }
        return false;
    }
}
