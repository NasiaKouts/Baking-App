package kouts.nasia.aueb.gr.bakingrecipes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import kouts.nasia.aueb.gr.bakingrecipes.Activities.RecipeDetailsActivity;
import kouts.nasia.aueb.gr.bakingrecipes.Models.Recipe;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class RecipesRecyclerViewAdapter extends RecyclerView.Adapter<RecipesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Recipe> recipeList;
    private int numberOfColumns;

    public RecipesRecyclerViewAdapter(
            Context context,
            RecyclerView recyclerView,
            ArrayList<Recipe> movies) {

        this.context = context;
        this.recyclerView = recyclerView;
        this.recipeList = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes_grid_item, parent, false);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRecipeDetails = new Intent(itemView.getContext(), RecipeDetailsActivity.class);
                openRecipeDetails.putExtra(context.getResources().getString(R.string.recipe_key),
                        recipeList.get(recyclerView.getChildLayoutPosition(view)));
                context.startActivity(openRecipeDetails);
            }
        });

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Recipe currenRecipe = recipeList.get(position);

        if(currenRecipe == null) return;

        if (viewHolder.recipeIv.getTag() == null ||
                viewHolder.recipeIv.getDrawable() == null) {

            final String imageUrl = currenRecipe.getImage();
            final ImageView recipeImageView = viewHolder.recipeIv;

            populateRecipeImage(imageUrl, recipeImageView);
        }

        TextView titleTv = viewHolder.recipeTitleTv;
        titleTv.setText("" + currenRecipe.getName().trim());

        TextView servingsNumberTv = viewHolder.recipeServingsNumberTv;
        servingsNumberTv.setText("" + currenRecipe.getServings());
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public void setNumberOfColumns(int columns){
        this.numberOfColumns = columns;
    }

    private void populateRecipeImage(String recipeImageUrl, ImageView recipeImageView) {
        if (recipeImageUrl == null || recipeImageUrl.equals("")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                recipeImageView.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_chef, context.getTheme()));
            } else {
                recipeImageView.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_chef));
            }
            return;
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(context.getResources().getColor(R.color.colorPrimaryDark))
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .build();
        try{
            Picasso.with(context)
                    .load(recipeImageUrl)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_chef)
                    .placeholder(R.drawable.ic_chef)
                    .transform(transformation)
                    .into(recipeImageView);
        } catch (Exception e) {
            // in any case an error has been made
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                recipeImageView.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_chef, context.getTheme()));
            } else {
                recipeImageView.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.ic_chef));
            }
        }

    }

    /*
        Swap adapter's source data and notify that change has occurred
     */
    public void swap(ArrayList<Recipe> newData) {
        if(newData == null || newData.size()==0) return;

        if(recipeList != null && recipeList.size()>0) recipeList.clear();

        if(recipeList == null) recipeList = new ArrayList<>();

        recipeList.addAll(newData);
        notifyDataSetChanged();
    }

    /*
        This is our ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeIv;
        TextView recipeTitleTv;
        TextView recipeServingsNumberTv;

        public ViewHolder(View view) {
            super(view);
            this.recipeIv = view.findViewById(R.id.image_view_recipes_item);
            this.recipeTitleTv = view.findViewById(R.id.text_view_title_recipes_item);
            this.recipeServingsNumberTv = view.findViewById(R.id.text_view_servings_number_recipes_item);
        }
    }
}
