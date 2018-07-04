package kouts.nasia.aueb.gr.bakingrecipes;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static kouts.nasia.aueb.gr.bakingrecipes.Utils.Transformations.readJsonFile;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;

import kouts.nasia.aueb.gr.bakingrecipes.Activities.RecipeDetailsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Activities.RecipesActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.Widget.BakingRecipesIdlingResource;

@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private ArrayList<Recipe> recipes;
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private BakingRecipesIdlingResource idlingResource;

    @Rule
    public IntentsTestRule<RecipesActivity> activityTestRule
            = new IntentsTestRule<>(RecipesActivity.class);

    @Before
    public void registerIdlingResource() {
        idlingResource = RecipesActivity.idlingResource;
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

    @Before
    public void loadJson() {
        recipes = new ArrayList<>();

        String jsonContent = readJsonFile(appContext);

        Gson gson = new GsonBuilder().create();
        Recipe[] recipesArray = gson.fromJson(jsonContent, Recipe[].class);

        recipes.addAll(Arrays.asList(recipesArray));
    }


    @Test
    public void clickOnRecipeCard_opensUpRecipeDetails() {
        onView(withId(R.id.recycler_view_recipes_main))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        intended(hasComponent(
                new ComponentName(getTargetContext(), RecipeDetailsActivity.class)));

        onView(withId(R.id.card_view_ingredients_steps_fragment))
                .check(matches(isDisplayed()));

        onView(withId(R.id.card_view_steps_steps_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecipeCard_opensUpRecipeDetailsWithCorrespondingInfo() {
        onView(withId(R.id.recycler_view_recipes_main))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        intended(hasExtraWithKey(appContext.getResources().getString(R.string.recipe_key)));
    }

    private static final int ITEM_TO_BE_CHECKED = 3;

    @Test
    public void scrollToItem_checkItsText() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recycler_view_recipes_main))
                .perform(RecyclerViewActions.scrollToPosition(ITEM_TO_BE_CHECKED));

        // Match the text in an item below the fold and check that it's displayed.
        onView(allOf(isDisplayed(), withText(recipes.get(ITEM_TO_BE_CHECKED).getName())))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
