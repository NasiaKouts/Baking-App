package kouts.nasia.aueb.gr.bakingrecipes.Models;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private double quantity;
    private String measure;
    private String ingredient;

    public Ingredient(double quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getModifiedIngredientName() {
        String modifiedName = ingredient.replace("(", "\n(");
        modifiedName = modifiedName.substring(0, 1).toUpperCase() + modifiedName.substring(1).toLowerCase();
        return modifiedName;
    }
}
