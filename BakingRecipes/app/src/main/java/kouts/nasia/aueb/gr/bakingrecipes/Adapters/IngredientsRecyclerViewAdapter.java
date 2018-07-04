package kouts.nasia.aueb.gr.bakingrecipes.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kouts.nasia.aueb.gr.bakingrecipes.Models.Ingredient;
import kouts.nasia.aueb.gr.bakingrecipes.R;

public class IngredientsRecyclerViewAdapter  extends RecyclerView.Adapter<IngredientsRecyclerViewAdapter.IngredientsHolder>{
    private ArrayList<Ingredient> ingredientsList;

    public class IngredientsHolder extends RecyclerView.ViewHolder{
        public TextView ingredientNameTv;
        public TextView ingredientAmountTv;

        public IngredientsHolder(View view){
            super(view);
            ingredientNameTv = view.findViewById(R.id.text_view_ingredient_name_item);
            ingredientAmountTv = view.findViewById(R.id.text_view_ingredient_amount_item);
        }
    }

    public IngredientsRecyclerViewAdapter(ArrayList<Ingredient> ingredientsList){
        if(ingredientsList == null) this.ingredientsList = new ArrayList<>();
        else this.ingredientsList = ingredientsList;
    }


    @NonNull
    @Override
    public IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredients_item, parent, false);

        return new IngredientsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsHolder holder, int position) {
        Ingredient ingredient = ingredientsList.get(position);
        holder.ingredientNameTv.setText(ingredient.getModifiedIngredientName());
        holder.ingredientAmountTv.setText(ingredient.getQuantity() + " " + ingredient.getMeasure());
    }

    @Override
    public int getItemCount() {
        return ingredientsList == null ? 0 : ingredientsList.size();
    }
}
