package kouts.nasia.aueb.gr.bakingrecipes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import java.util.ArrayList;

import kouts.nasia.aueb.gr.bakingrecipes.Widget.BakingRecipesIdlingResource;
import kouts.nasia.aueb.gr.bakingrecipes.Fragments.IngredientsFragment;
import kouts.nasia.aueb.gr.bakingrecipes.Fragments.MasterRecipeStepsFragment;
import kouts.nasia.aueb.gr.bakingrecipes.Fragments.StepDetailsFragment;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Step;
import kouts.nasia.aueb.gr.bakingrecipes.R;

import static kouts.nasia.aueb.gr.bakingrecipes.Fragments.MasterRecipeStepsFragment.updateHighlightedSelectedStep;
import static kouts.nasia.aueb.gr.bakingrecipes.Utils.IdlingResources.getIdlingResource;

public class RecipeDetailsActivity extends AppCompatActivity
        implements MasterRecipeStepsFragment.OnIngredientsCardViewClickListener,
        MasterRecipeStepsFragment.OnStepItemClickListener,
        StepDetailsFragment.OnVideoInfoUpdateEvent,
        StepDetailsFragment.OnSelectedStepChanged{

    private boolean isTwoPane = false;
    private Recipe selectedRecipe;
    private int openedStep = -1;
    private boolean isVideoAvailable = false;

    private MasterRecipeStepsFragment masterFragment;
    public static BakingRecipesIdlingResource idlingResource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        idlingResource = getIdlingResource(idlingResource);
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        masterFragment = (MasterRecipeStepsFragment)
                getSupportFragmentManager().findFragmentById(R.id.master_list_fragment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
            selectedRecipe = (Recipe)bundle.get(getString(R.string.recipe_key));
            if(selectedRecipe != null) setTitle(selectedRecipe.getName());
            if (idlingResource != null) {
                idlingResource.setIdleState(true);
            }
        }

        if(findViewById(R.id.two_pane_linear_layout) != null){
            isTwoPane = true;
        }

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(getString(R.string.isAFragmentOpen))){
                openFragment = savedInstanceState
                        .getInt(getString(R.string.isAFragmentOpen));
            }
            if(savedInstanceState.containsKey(getString(R.string.openedStepIndex))){
                openedStep = savedInstanceState
                        .getInt(getString(R.string.openedStepIndex));

                if(openFragment == INGREDIENTS_FRAGMENT_OPEN) {
                    CardView cv = findViewById(R.id.card_view_ingredients_steps_fragment);
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentGreatOpacity));
                }
                else {
                    CardView cv = findViewById(R.id.card_view_ingredients_steps_fragment);
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons));
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.isAFragmentOpen), openFragment);
        outState.putInt(getString(R.string.openedStepIndex), openedStep);
        super.onSaveInstanceState(outState);
    }

    private CardView cv = null;

    private final static int NO_FRAGMENT_OPEN = 0;
    private final static int INGREDIENTS_FRAGMENT_OPEN = 1;
    private final static int STEP_FRAGMENT_OPEN = 2;
    private int openFragment = NO_FRAGMENT_OPEN;

    @Override
    public void onIngredientsCardViewClicked(View view) {
        if(isTwoPane) {
            cv = (CardView) view;

            switch (openFragment) {
                case NO_FRAGMENT_OPEN:
                    openFragment = INGREDIENTS_FRAGMENT_OPEN;
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentGreatOpacity));
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.step_details_container, new IngredientsFragment(), getString(R.string.ingredients_tag))
                            .commit();
                    break;

                case INGREDIENTS_FRAGMENT_OPEN:
                    openFragment = NO_FRAGMENT_OPEN;
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons));
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.ingredients_tag));
                    if(fragment != null)
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    break;

                case STEP_FRAGMENT_OPEN:
                    openedStep = -1;
                    masterFragment.updateSelected(openedStep);
                    openFragment = INGREDIENTS_FRAGMENT_OPEN;
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentGreatOpacity));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_details_container, new IngredientsFragment(), getString(R.string.ingredients_tag))
                            .commit();
                    break;

                default:
                    break;
            }
        }
        else {
            Intent openRecipeDetails = new Intent(this, IngredientsActivity.class);
            openRecipeDetails.putExtra(getResources().getString(R.string.recipe_key),
                    selectedRecipe);
            startActivity(openRecipeDetails);
        }
    }

    private Bundle createBundleToBeSentToFragment(int selectedStepIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt(getResources()
                .getString(R.string.selected_step_bundle_key), selectedStepIndex);
        return bundle;
    }

    @Override
    public void onStepItemSelected(ArrayList<Step> steps, int index) {
        if(isTwoPane) {
            StepDetailsFragment newFragment;
            switch (openFragment) {
                case NO_FRAGMENT_OPEN:
                    openedStep = index;
                    openFragment = STEP_FRAGMENT_OPEN;
                    newFragment = new StepDetailsFragment();
                    newFragment.setArguments(createBundleToBeSentToFragment(index));
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.step_details_container, newFragment, getString(R.string.step_details_tag))
                            .commit();
                    break;

                case INGREDIENTS_FRAGMENT_OPEN:
                    CardView cv = findViewById(R.id.card_view_ingredients_steps_fragment);
                    cv.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorIcons));
                    openedStep = index;
                    openFragment = STEP_FRAGMENT_OPEN;
                    newFragment = new StepDetailsFragment();
                    newFragment.setArguments(createBundleToBeSentToFragment(index));
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_details_container, newFragment, getString(R.string.step_details_tag))
                        .commit();
                    break;

                case STEP_FRAGMENT_OPEN:
                    if(index == openedStep) {
                        openFragment = NO_FRAGMENT_OPEN;
                        openedStep = -1;
                        masterFragment.updateSelected(openedStep);
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.step_details_tag));
                        if(fragment != null)
                            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        break;
                    }
                    else {
                        openedStep = index;
                        openFragment = STEP_FRAGMENT_OPEN;
                        newFragment = new StepDetailsFragment();
                        newFragment.setArguments(createBundleToBeSentToFragment(index));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_details_container, newFragment, getString(R.string.step_details_tag))
                                .commit();
                    }

                    break;

                default:
                    break;
            }
        }
        else {
            Intent openStepDetails = new Intent(this, StepDetailsActivity.class);
            openStepDetails.putExtra(getResources().getString(R.string.recipe_key),
                    selectedRecipe);
            openStepDetails.putExtra(getResources()
                    .getString(R.string.selected_step_bundle_key), index);
            startActivity(openStepDetails);
        }
    }

    @Override
    public void onVideoInfoUpdated(boolean isAvailable) {
        isVideoAvailable = isAvailable;
    }

    @Override
    public void onSelectedStepChanged(int step) {
        openedStep = step;
        updateHighlightedSelectedStep(step);
    }
}
