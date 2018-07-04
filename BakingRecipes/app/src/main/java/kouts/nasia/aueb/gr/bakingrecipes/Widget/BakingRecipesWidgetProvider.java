package kouts.nasia.aueb.gr.bakingrecipes.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import kouts.nasia.aueb.gr.bakingrecipes.Activities.RecipeDetailsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BakingRecipesWidgetProviderConfigureActivity BakingRecipesWidgetProviderConfigureActivity}
 */
public class BakingRecipesWidgetProvider extends AppWidgetProvider {

    private static final String WidgetClickTag = "WidgetClickTag";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);
        Recipe recipe = BakingRecipesWidgetProviderConfigureActivity.loadRecipeObj(context, appWidgetId);

        if(recipe == null) return;

        String recipeName = recipe.getName().trim();
        String servingsNumber = String.valueOf(recipe.getServings());

        views.setTextViewText(R.id.appwidget_recipe_name,
                (recipeName.equals("")) ? context.getString(R.string.unknownRecipe) : recipeName);

        views.setTextViewText(R.id.appwidget_recipe_servings,
                (servingsNumber.equals("")) ? "-" : servingsNumber);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, BakingRecipesService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //passing app widget id to that RemoteViews Service
        //setting adapter to listview of the widget
        views.setRemoteAdapter(R.id.widget_ingredients_list, svcIntent);

        Intent intent = new Intent(context, BakingRecipesWidgetProvider.class);
        intent.setAction(WidgetClickTag);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_header, pIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null) return;
        if(intent.getAction() == null) return;

        if(intent.getAction().equals(WidgetClickTag)){
            //Onclick event
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);

            Recipe recipe = BakingRecipesWidgetProviderConfigureActivity.loadRecipeObj(context, appWidgetId);

            Intent openDetails = new Intent(context, RecipeDetailsActivity.class);
            openDetails.putExtra(context.getResources().getString(R.string.recipe_key), recipe);
            openDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(openDetails);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BakingRecipesWidgetProviderConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

