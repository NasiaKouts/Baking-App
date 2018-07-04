package kouts.nasia.aueb.gr.bakingrecipes.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import kouts.nasia.aueb.gr.bakingrecipes.Adapters.StepsRecyclerViewAdapter;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Step;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class MasterRecipeStepsFragment extends Fragment implements StepsRecyclerViewAdapter.StepSelectedAdapterListener{

    @BindView(R.id.card_view_ingredients_steps_fragment)
    CardView ingredientsCardView;

    @BindView(R.id.recycler_view_steps_steps_fragment)
    RecyclerView stepsRecyclerView;

    @BindView(R.id.test_corresponding_open)
    TextView testingOpened;

    private static StepsRecyclerViewAdapter stepsAdapter = null;
    private Recipe recipeSelected = null;
    private ArrayList<Step> steps = null;
    private int stepOpenIndex = -1;

    public MasterRecipeStepsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_recipe_steps, container, false);
        ButterKnife.bind(this, rootView);

        steps = new ArrayList<>();

        if(getActivity() != null) {
            Bundle bundle = getActivity().getIntent().getExtras();
            if(bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
                recipeSelected = (Recipe) bundle.get(getString(R.string.recipe_key));
                if(recipeSelected != null) steps.addAll(Arrays.asList(recipeSelected.getSteps()));
            }
            testingOpened.setText(recipeSelected.getName());
        }
        stepsAdapter = new StepsRecyclerViewAdapter(getContext(), steps, this, stepsRecyclerView);
        stepsRecyclerView.setAdapter(stepsAdapter);
        if(getContext() != null) {
            stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            stepsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        }

        if(getResources().getBoolean(R.bool.isTablet)) {
            if(savedInstanceState != null) {
                stepOpenIndex = savedInstanceState.getInt(getString(R.string.selected_step_bundle_key));
                stepsAdapter.setSelectedItemIndex(stepOpenIndex);
            }
        }

        // Set a click listener on the ingredients card view and trigger the callback onIngredientsCardViewClicked when it is clicked
        ingredientsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientsCardViewCallback.onIngredientsCardViewClicked(view);
            }
        });

        // Return the root view
        return rootView;
    }

    public void updateSelected(int stepOpenIndex) {
        this.stepOpenIndex = stepOpenIndex;
        stepsAdapter.setSelectedItemIndex(stepOpenIndex);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.selected_step_bundle_key), stepOpenIndex);
    }

    /* Establish communication between this fragment and the wrapper activity
         * Handle click on ingredients CardView
         */
    // Define a new interface OnIngredientsCardViewClickListener that triggers a callback in the host activity
    OnIngredientsCardViewClickListener ingredientsCardViewCallback;

    // OnIngredientsCardViewClickListener interface, calls a method in the host activity named onIngredientsCardViewClicked
    public interface OnIngredientsCardViewClickListener {
        void onIngredientsCardViewClicked(View view);
    }
    // --------------------------------------------------------------------


    /* Establish communication between this fragment and the wrapper activity
     * Handle click on step item of RecyclerView
     */
    // Define a new interface OnStepItemClickListener that triggers a callback in the host activity
    OnStepItemClickListener stepsRecyclerViewCallback;

    // OnStepItemClickListener interface, calls a method in the host activity named onStepItemSelected
    public interface OnStepItemClickListener {
        void onStepItemSelected(ArrayList<Step> steps, int index);
    }
    // --------------------------------------------------------------------

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            ingredientsCardViewCallback = (OnIngredientsCardViewClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnIngredientsCardViewClickListener");
        }
        try {
            stepsRecyclerViewCallback = (OnStepItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepItemClickListener");

        }
    }

    @Override
    public void stepSelected(int selectedStepPos) {
        stepOpenIndex = selectedStepPos;
        stepsRecyclerViewCallback.onStepItemSelected(steps, selectedStepPos);
    }

    public static void updateHighlightedSelectedStep(int selectedStep){
        stepsAdapter.setSelectedItemIndex(selectedStep);
    }

}
