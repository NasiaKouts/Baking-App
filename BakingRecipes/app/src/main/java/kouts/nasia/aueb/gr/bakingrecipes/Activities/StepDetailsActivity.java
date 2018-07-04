package kouts.nasia.aueb.gr.bakingrecipes.Activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;

import kouts.nasia.aueb.gr.bakingrecipes.Fragments.StepDetailsFragment;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;
import kouts.nasia.aueb.gr.bakingrecipes.Utils.ConfigurationsInfo;

public class StepDetailsActivity extends AppCompatActivity
        implements StepDetailsFragment.OnVideoInfoUpdateEvent,
            StepDetailsFragment.OnSelectedStepChanged{
    private static int savedOrientation;
    private OrientationEventListener orientationEventListener;
    private boolean isVideoAvailable = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ConfigurationsInfo.onPhoneLandscape(this) ||
                ConfigurationsInfo.onPhonePortrait(this)) {

            if(savedInstanceState != null) {
                savedOrientation = savedInstanceState.getInt(getString(R.string.state_key));
            }
            else {
                savedOrientation = getResources().getConfiguration().orientation;
            }

            // found on https://stackoverflow.com/questions/18078803/way-to-find-device-orientation-change-after-calling-setrequestedorientation
            orientationEventListener = new OrientationEventListener(getApplicationContext()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    boolean isPortrait = isPortrait(orientation);
                    if (!isPortrait && savedOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        savedOrientation = Configuration.ORIENTATION_LANDSCAPE;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                    } else if (isPortrait && savedOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        savedOrientation = Configuration.ORIENTATION_PORTRAIT;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                    }
                }
            };

            orientationEventListener.enable();
        }

        setContentView(R.layout.activity_step_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
            Recipe selectedRecipe = (Recipe)bundle.get(getString(R.string.recipe_key));
            if(selectedRecipe != null) setTitle(selectedRecipe.getName());
        }


    }

    @Override
    public void onBackPressed() {
        if(ConfigurationsInfo.onPhoneLandscape(this)
                && isVideoAvailable) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.state_key), savedOrientation);
    }

    private boolean isPortrait(int orientation) {
        return orientation < 45 || orientation > 315;
    }


    // ignore this in two - pane case
    @Override
    public void onVideoInfoUpdated(boolean isAvailable) {}

    // ignore this in two - pane case
    @Override
    public void onSelectedStepChanged(int step) {}
}
