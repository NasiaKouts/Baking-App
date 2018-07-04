package kouts.nasia.aueb.gr.bakingrecipes.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class BakingRecipesService extends RemoteViewsService{

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }
}
