package kouts.nasia.aueb.gr.bakingrecipes;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import kouts.nasia.aueb.gr.bakingrecipes.Activities.IngredientsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Activities.RecipeDetailsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Activities.StepDetailsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.Widget.BakingRecipesIdlingResource;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static kouts.nasia.aueb.gr.bakingrecipes.Utils.Transformations.readJsonFile;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class RecipeDetailsActivityTest {
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private Resources resources = getInstrumentation().getTargetContext().getResources();
    private ArrayList<Recipe> recipes;
    private BakingRecipesIdlingResource idlingResource;

    private final String FIRST_RECIPE = resources.getString(R.string.recipe_opened);

    @Rule
    public IntentsTestRule<RecipeDetailsActivity> detailsActivityActivityTestRule  =
            new  IntentsTestRule<RecipeDetailsActivity>(RecipeDetailsActivity.class)
            {
                @Override
                protected Intent getActivityIntent() {

                    recipes = new ArrayList<>();

                    String jsonContent = readJsonFile(appContext);

                    Gson gson = new GsonBuilder().create();
                    Recipe[] recipesArray = gson.fromJson(jsonContent, Recipe[].class);

                    recipes.addAll(Arrays.asList(recipesArray));
                    InstrumentationRegistry.getTargetContext();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.putExtra(appContext.getResources().getString(R.string.recipe_key),
                            recipes.get(0));
                    return intent;
                }
            };

    @Before
    public void registerIdlingResource() {
        idlingResource = RecipeDetailsActivity.idlingResource;
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents.
        // Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(new Instrumentation
                        .ActivityResult(Activity.RESULT_OK, null));


    }

    @Test
    public void intentOpeningUpRecipeDetails_showCorrespondingInfo() {
        RecipeDetailsActivity detailsActivity = detailsActivityActivityTestRule.getActivity();
        View viewById = detailsActivity.findViewById(R.id.test_corresponding_open);

        assertThat(viewById, notNullValue());
        assertThat(viewById, instanceOf(TextView.class));
        TextView textView = (TextView) viewById;
        assertThat(textView.getText().toString(),is(FIRST_RECIPE));
    }

    @Test
    public void clickOnRecipeIngredients_opensUpIngredientsActivity() {
        onView(withId(R.id.card_view_ingredients_steps_fragment))
                .perform(click());

        intended(hasComponent(
                new ComponentName(getTargetContext(), IngredientsActivity.class)));

        onView(withId(R.id.ingredient_list_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecipeIngredients_opensUpIngredientsActivityWithCorrespondingInfo() {
        onView(withId(R.id.card_view_ingredients_steps_fragment))
                .perform(click());

        intended(hasExtraWithKey(appContext.getResources().getString(R.string.recipe_key)));
    }

    @Test
    public void clickOnRecipeStep_opensUpStepDetailsActivity() {
        onView(withId(R.id.recycler_view_steps_steps_fragment))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        intended(hasComponent(
                new ComponentName(getTargetContext(), StepDetailsActivity.class)));

        onView(withId(R.id.bottom_border_buttons))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecipeStep_opensUpStepDetailsActivityWithCorrespondingInfo() {
        onView(withId(R.id.recycler_view_steps_steps_fragment))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        intended(hasExtraWithKey(appContext.getResources().getString(R.string.recipe_key)));
        intended(hasExtraWithKey(appContext.getResources().getString(R.string.selected_step_bundle_key)));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
