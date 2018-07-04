package kouts.nasia.aueb.gr.bakingrecipes.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import kouts.nasia.aueb.gr.bakingrecipes.Adapters.IngredientsRecyclerViewAdapter;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Ingredient;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class IngredientsFragment extends Fragment {

    @BindView(R.id.recycler_view_ingredients_fragment)
    RecyclerView ingredientsRecyclerView;

    private IngredientsRecyclerViewAdapter ingredientsAdapter = null;
    private ArrayList<Ingredient> ingredients = null;

    public IngredientsFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_ingredients_details, container, false);
        ButterKnife.bind(this, rootView);

        ingredients = new ArrayList<>();

        if(getActivity() != null && getActivity().getIntent() != null) {
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
                Recipe selectedRecipe = (Recipe) bundle.get(getString(R.string.recipe_key));
                if(selectedRecipe != null){
                    ingredients.addAll(Arrays.asList(selectedRecipe.getIngredients()));
                }
            }
        }

        ingredientsAdapter = new IngredientsRecyclerViewAdapter(ingredients);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        if(getContext() != null) {
            ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ingredientsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        }
        // Return the root view
        return rootView;
    }
}
