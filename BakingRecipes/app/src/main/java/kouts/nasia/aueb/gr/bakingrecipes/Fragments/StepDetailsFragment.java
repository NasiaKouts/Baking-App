package kouts.nasia.aueb.gr.bakingrecipes.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.OnClick;
import jp.shts.android.library.TriangleLabelView;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Step;
import kouts.nasia.aueb.gr.bakingrecipes.R;
import kouts.nasia.aueb.gr.bakingrecipes.Utils.ConfigurationsInfo;
import kouts.nasia.aueb.gr.bakingrecipes.Utils.ExoPlayerUtils;

public class StepDetailsFragment extends Fragment {

    // Views
    @BindView(R.id.media_layout_step_details)
    FrameLayout mediaLayout;

    @BindView(R.id.play_media_step_details)
    PlayerView exoPlayerView;

    @BindView(R.id.text_view_description_step_details)
    TextView stepDescriptionTextView;

    @BindView(R.id.progress_bar_load_media_step_details)
    ProgressBar mediaLoadingProgressBar;

    @BindView(R.id.media_connectivity_error)
    LinearLayout connectivityErrorLayout;

    @BindView(R.id.image_view_refresh_media_step_details)
    ImageView refreshMediaIv;

    @BindView(R.id.corner_label)
    TriangleLabelView cornerLabel;

    @BindView(R.id.previous_step_details_linear)
    LinearLayout goToPreviousLy;

    @BindView(R.id.next_step_details_linear)
    LinearLayout goToNextLy;

    @BindView(R.id.video_thumbnail_details)
    ImageView thumbnail;

    // ----------------------------------

    private View rootView;

    private ArrayList<Step> steps = null;
    private int selectedStepIndex = 0;
    private long playerPosition = 0;

    private ExoPlayerUtils exoPlayerUtils;
    private Handler handler;

    public StepDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_steps_details, container, false);
        ButterKnife.bind(this, rootView);

        this.rootView = rootView;

        // Width / Height = 16:9
        if(!ConfigurationsInfo.onPhoneLandscape(getContext())
                || getResources().getBoolean(R.bool.isTablet)){
            int height;
            if(getResources().getBoolean(R.bool.isTablet)) {
                height = (int) ((getResources().getDisplayMetrics().widthPixels - 550) * (9f / 16));
            }
            else {
                height = (int) (getResources().getDisplayMetrics().widthPixels * (9f / 16));
            }
            ViewGroup.LayoutParams params = exoPlayerView.getLayoutParams();
            params.height = height;
            exoPlayerView.setLayoutParams(params);

            thumbnail.setLayoutParams(params);
        }

        steps = new ArrayList<>();

        // Get steps from selected recipe, and index of specific selected step
        if (getActivity() != null && getActivity().getIntent() != null) {
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
                Recipe selectedRecipe = (Recipe) bundle.get(getString(R.string.recipe_key));
                if(selectedRecipe != null) steps.addAll(Arrays.asList(selectedRecipe.getSteps()));
            }
            if (bundle != null && bundle.containsKey(getString(R.string.selected_step_bundle_key))) {
                selectedStepIndex = bundle.getInt(getString(R.string.selected_step_bundle_key));
            }
        }

        if(getResources().getBoolean(R.bool.isTablet)){
            if(getArguments() != null) {
                if(getArguments().containsKey(getResources()
                        .getString(R.string.selected_step_bundle_key))) {
                    selectedStepIndex = getArguments().getInt(getResources()
                            .getString(R.string.selected_step_bundle_key));
                }
            }
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(getString(R.string.playback_pos))) {
                playerPosition = (long)savedInstanceState.get(getString(R.string.playback_pos));
            }
            if(savedInstanceState.containsKey(getString(R.string.step_index))) {
                selectedStepIndex = (int)savedInstanceState.get(getString(R.string.step_index));
            }
        }
        // Return the root view
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(handler!=null) handler.removeCallbacks(null);

        checkMediaPlayerAndLandscapeModification();

        populateUi();
    }

    private void checkMediaPlayerAndLandscapeModification() {
        if(steps.get(selectedStepIndex).getVideoURL() != null &&
                !steps.get(selectedStepIndex).getVideoURL().trim().equals("")) {
            videoInfoUpdateCallback.onVideoInfoUpdated(true);
            if(getActivity() != null && exoPlayerUtils == null) {
                exoPlayerUtils = new ExoPlayerUtils(getActivity(),
                        exoPlayerView,
                        playerPosition,
                        mediaLoadingProgressBar,
                        thumbnail,
                        Uri.parse(steps.get(selectedStepIndex).getThumbnailURL()),
                        connectivityErrorLayout,
                        refreshMediaIv);
            }
            if(ConfigurationsInfo.onPhoneLandscape(getContext())){
                if(getActivity() != null) ((AppCompatActivity) getActivity())
                        .getSupportActionBar().hide();
            }
        }
        else if(ConfigurationsInfo.onPhoneLandscape(getContext())){
            videoInfoUpdateCallback.onVideoInfoUpdated(false);
        }
    }

    // Delete any previous info and populate ui from the start
    @OnClick(R.id.previous_step_details_linear)
    public void goToPreviousStep() {
        if(exoPlayerUtils != null) exoPlayerUtils.abandon();
        exoPlayerUtils = null;
        stepDescriptionTextView.setText("");
        cornerLabel.setPrimaryText("");
        selectedStepIndex--;
        playerPosition = 0;
        onSelectedStepChangedCallback.onSelectedStepChanged(selectedStepIndex);
        populateUi();
    }

    // Delete any previous info and populate ui from the start
    @OnClick(R.id.next_step_details_linear)
    public void goToNextStep() {
        if(exoPlayerUtils != null) exoPlayerUtils.abandon();
        exoPlayerUtils = null;
        stepDescriptionTextView.setText("");
        cornerLabel.setPrimaryText("");
        selectedStepIndex++;
        playerPosition = 0;
        onSelectedStepChangedCallback.onSelectedStepChanged(selectedStepIndex);
        populateUi();
    }

    private void populateUi() {
        updateNavigationThroughStepsButtons();

        Step selectedStep = steps.get(selectedStepIndex);

        setTextAndEditDescriptionTv(selectedStep);

        String mediaUrl = selectedStep.getVideoURL().trim();
        if(mediaUrl.trim().equals("")) {
            mediaLayout.setVisibility(View.GONE);
        }
        else {
            // this will be null if we got here from previous or next step button
            // from a step that didn't provide any video
            if(exoPlayerUtils == null) {
                checkMediaPlayerAndLandscapeModification();
            }
            mediaLayout.setVisibility(View.VISIBLE);
            exoPlayerUtils.initializeMediaSession();
            exoPlayerUtils.initializePlayer(Uri.parse(mediaUrl));
        }
    }

    private void updateNavigationThroughStepsButtons() {
        // disable and make invisible the "go to previous button" if its the first step
        if(selectedStepIndex == 0) {
            goToPreviousLy.setVisibility(View.INVISIBLE);
            goToPreviousLy.setClickable(false);
        }
        // disable and make invisible the "go to next button" if its the last step
        else if(selectedStepIndex == steps.size() - 1) {
            goToNextLy.setVisibility(View.INVISIBLE);
            goToNextLy.setClickable(false);
        }
        // otherwise set both clickable and visible
        else {
            goToPreviousLy.setVisibility(View.VISIBLE);
            goToPreviousLy.setClickable(true);
            goToNextLy.setVisibility(View.VISIBLE);
            goToNextLy.setClickable(true);
        }
    }

    private void setTextAndEditDescriptionTv(Step selectedStep) {
        String mediaUrl = selectedStep.getVideoURL().trim();
        if(ConfigurationsInfo.onPhoneLandscape(getContext()) && !mediaUrl.equals("")) {
            cornerLabel.setVisibility(View.GONE);
            stepDescriptionTextView.setVisibility(View.GONE);
            return;
        }

        stepDescriptionTextView.setVisibility(View.VISIBLE);
        String[] sentences = selectedStep.getDescription().trim().split("\\.");
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;
        for(String sentence : sentences) {
            if(first) {
                first = false;
                // making sure the first cell contains simple the "numberOfStep." part, otherwise add it to the builder
                if(sentence.length() > 3) {
                    stringBuilder.append(getString(R.string.bullet));
                    stringBuilder.append(sentences[0].trim());
                    stringBuilder.append("\n\n");
                }
                continue;
            }
            stringBuilder.append(getString(R.string.bullet));
            stringBuilder.append(sentence.trim());
            stringBuilder.append("\n\n");
        }

        stepDescriptionTextView.setText(stringBuilder.toString());
        cornerLabel.setPrimaryText(getString(R.string.step_details_activity_title) + selectedStepIndex);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(getString(R.string.playback_pos), playerPosition);
        outState.putInt(getString(R.string.step_index), selectedStepIndex);

        if(exoPlayerUtils != null) exoPlayerUtils.abandon();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(exoPlayerUtils != null) {
            long currentPos = exoPlayerUtils.getContentPosition();
            if(currentPos != -1) {
                playerPosition = currentPos;
            }
            if(Util.SDK_INT <= 23) {
                exoPlayerUtils.abandon();
                exoPlayerUtils = null;
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(exoPlayerUtils != null) {
            if(Util.SDK_INT > 23) {
                exoPlayerUtils.abandon();
                exoPlayerUtils = null;
            }
        }
    }

    /* Establish communication between this fragment and the wrapper activity
     * Inform about availability of video on current step
     */
    OnVideoInfoUpdateEvent videoInfoUpdateCallback;

    public interface OnVideoInfoUpdateEvent {
        void onVideoInfoUpdated(boolean isAvailable);
    }
    // --------------------------------------------------------------------


    /* Establish communication between this fragment and the wrapper activity
     * In case of two pane inform the master to update the selected item
     */
    OnSelectedStepChanged onSelectedStepChangedCallback;

    public interface OnSelectedStepChanged {
        void onSelectedStepChanged(int step);
    }
    // --------------------------------------------------------------------

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            videoInfoUpdateCallback = (OnVideoInfoUpdateEvent) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoInfoUpdateEvent");
        }

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onSelectedStepChangedCallback = (OnSelectedStepChanged) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnSelectedStepChanged");
        }
    }
}