package kouts.nasia.aueb.gr.bakingrecipes.Utils;

import kouts.nasia.aueb.gr.bakingrecipes.Widget.BakingRecipesIdlingResource;

public class IdlingResources {

    public static BakingRecipesIdlingResource getIdlingResource(BakingRecipesIdlingResource idlingResource) {
        if (idlingResource == null) {
            idlingResource = new BakingRecipesIdlingResource();
        }
        return idlingResource;
    }
}
