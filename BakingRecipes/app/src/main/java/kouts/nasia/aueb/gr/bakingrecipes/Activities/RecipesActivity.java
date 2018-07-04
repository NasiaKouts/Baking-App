package kouts.nasia.aueb.gr.bakingrecipes.Activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kouts.nasia.aueb.gr.bakingrecipes.Adapters.RecipesRecyclerViewAdapter;
import kouts.nasia.aueb.gr.bakingrecipes.Widget.BakingRecipesIdlingResource;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;
import kouts.nasia.aueb.gr.bakingrecipes.Utils.RecipeItemDecoration;
import kouts.nasia.aueb.gr.bakingrecipes.Utils.Transformations;

import static kouts.nasia.aueb.gr.bakingrecipes.Utils.IdlingResources.getIdlingResource;

public class RecipesActivity extends AppCompatActivity {

    private static final int DESERIALIZE_JSON = 100;

    private static final int RECYCLERVIEW_COLUMNS_FOR_TABLET = 3;
    private static final int RECYCLERVIEW_COLUMNS_FOR_MOBILE_PORTRAIT = 1;
    private static final int RECYCLERVIEW_COLUMNS_FOR_MOBILE_LANDSCAPE = 2;

    @BindView(R.id.progress_bar_recipes_main)
    ProgressBar pb;

    @BindView(R.id.relative_layout_recipes_main)
    RelativeLayout relativeLayout;

    @BindView(R.id.recycler_view_recipes_main)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.linear_json_error_recipes_main)
    LinearLayout jsonErroLinear;

    private static ArrayList<Recipe> recipeArrayList = null;
    private RecipesRecyclerViewAdapter recipesRecyclerViewAdapter = null;
    private Parcelable recyclerViewPosition = null;
    private static boolean rotated;

    public static BakingRecipesIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_grid);
        ButterKnife.bind(this);

        recipeArrayList = new ArrayList<>();

        // Set different number of columns depending on the mode and device
        RecyclerView.LayoutManager layoutManager;
        int numOfColumns;
        // If it is considered tablet, has screen width larger than 600dp
        if(getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new GridLayoutManager(this, RECYCLERVIEW_COLUMNS_FOR_TABLET);
            numOfColumns = RECYCLERVIEW_COLUMNS_FOR_TABLET;
        }
        else {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                layoutManager = new GridLayoutManager(this, RECYCLERVIEW_COLUMNS_FOR_MOBILE_PORTRAIT);
                numOfColumns = RECYCLERVIEW_COLUMNS_FOR_MOBILE_PORTRAIT;
            }
            else {
                layoutManager = new GridLayoutManager(this, RECYCLERVIEW_COLUMNS_FOR_MOBILE_LANDSCAPE);
                numOfColumns = RECYCLERVIEW_COLUMNS_FOR_MOBILE_LANDSCAPE;
            }
        }
        recipesRecyclerView.setLayoutManager(layoutManager);


        recipesRecyclerViewAdapter = new RecipesRecyclerViewAdapter(this, recipesRecyclerView, recipeArrayList);
        recipesRecyclerViewAdapter.setNumberOfColumns(numOfColumns);
        recipesRecyclerView.setAdapter(recipesRecyclerViewAdapter);
        ((SimpleItemAnimator)recipesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recipesRecyclerView.addItemDecoration(new RecipeItemDecoration(numOfColumns,
                Transformations.dpToPx(this, (int)getResources().getDimension(R.dimen.vertical_margin_small))));

        if(savedInstanceState != null) {
            recyclerViewPosition =  savedInstanceState.getParcelable(getString(R.string.recycler_view_pos));
        }
        else {
            rotated = false;
        }

        idlingResource = getIdlingResource(idlingResource);
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        startLoader();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(getString(R.string.recycler_view_pos),
                recipesRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rotated = true;
    }

    private void startLoader(){
        LoaderManager loaderManager = this.getSupportLoaderManager();
        Loader<String> deserializeLoader = loaderManager.getLoader(DESERIALIZE_JSON);
        if (deserializeLoader == null) {
            loaderManager.initLoader(DESERIALIZE_JSON, null, recipesJsonDeserialize);
        } else {
            loaderManager.restartLoader(DESERIALIZE_JSON, null, recipesJsonDeserialize);
        }
    }

    @OnClick(R.id.image_view_retry_load_json_recipes_main)
    public void reloadJson() {
        jsonErroLinear.setVisibility(View.GONE);
        startLoader();
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Recipe>> recipesJsonDeserialize
            = new LoaderManager.LoaderCallbacks<ArrayList<Recipe>>() {


        @Override
        public Loader<ArrayList<Recipe>> onCreateLoader(int id, @Nullable Bundle args) {
            prepareFetching();
            return new DeserializeJson(getApplicationContext());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
            if (data != null) {
                recipesRecyclerViewAdapter.swap(data);
                if(recyclerViewPosition != null && rotated) {
                    rotated = false;
                    recipesRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewPosition);
                }
                pb.setVisibility(View.GONE);
                recipesRecyclerView.setVisibility(View.VISIBLE);
            } else {
                pb.setVisibility(View.GONE);
                jsonErroLinear.setVisibility(View.VISIBLE);
            }
        }

        // ignore
        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<Recipe>> loader) {
        }
    };

    private void prepareFetching(){
        recipeArrayList.clear();
        pb.setVisibility(View.VISIBLE);
        recipesRecyclerView.setVisibility(View.GONE);
        jsonErroLinear.setVisibility(View.GONE);
    }

    private static String readJsonFile(Context context){
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


    private static class DeserializeJson extends AsyncTaskLoader<ArrayList<Recipe>>{
            ArrayList<Recipe> recipesResult = null;

            public DeserializeJson(Context context){
                super(context);
            }

            @Override
            protected void onStartLoading() {
                if (recipesResult != null) {
                    // Use cached data
                    deliverResult(recipesResult);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public ArrayList<Recipe> loadInBackground() {
                ArrayList<Recipe> recipes = new ArrayList<>();

                String jsonContent = readJsonFile(getContext());

                Gson gson = new GsonBuilder().create();
                Recipe[] recipesArray = gson.fromJson(jsonContent, Recipe[].class);

                recipes.addAll(Arrays.asList(recipesArray));

                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }

                return recipes;
            }

            @Override
            public void deliverResult(@Nullable ArrayList<Recipe> data) {
                recipesResult = data;
                super.deliverResult(data);
            }
    }
}
