package kouts.nasia.aueb.gr.bakingrecipes.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Transformations {
    // This method findS the corresponding number in px, depending the given dp number
    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static String readJsonFile(Context context){
        try {
            String jsonContent = null;

            InputStream inputStream = context.getAssets().open("baking.json");
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) jsonContent = scanner.next();

            inputStream.close();
            return jsonContent;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


     public static void hideSystemUI(@NonNull Activity activity) {
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        if (Build.VERSION.SDK_INT >= 19) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
