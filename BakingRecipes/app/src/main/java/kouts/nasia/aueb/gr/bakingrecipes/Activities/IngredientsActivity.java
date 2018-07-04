package kouts.nasia.aueb.gr.bakingrecipes.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class IngredientsActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(getString(R.string.recipe_key))) {
            Recipe selectedRecipe = (Recipe)bundle.get(getString(R.string.recipe_key));
            if(selectedRecipe != null) {
                setTitle(selectedRecipe.getName() + getString(R.string.ingredients_activity_title));
            }
        }
    }
}
