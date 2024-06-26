package com.groupten.bmsproject.Ingredient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class IngredientService {
    
    @Autowired
    private IngredientRepository ingredientRepository;

    public String addNewIngredient(String ingredient, Double price, Double quantity, LocalDate expiryTime, LocalDate dateAdded, String unitType) {
        IngredientEntity newIngredient = new IngredientEntity();
        newIngredient.setIngredient(ingredient);
        newIngredient.setPrice(price);
        newIngredient.setQuantity(quantity);
        newIngredient.setExpiry(expiryTime);
        newIngredient.setDateAdded(dateAdded);
        newIngredient.setUnitType(unitType);
        ingredientRepository.save(newIngredient);
    
        return "Saved";
    }

    public String updateIngredient(Integer id, String ingredient, Double price, Double quantity, LocalDate expiryTime, String unitType) {
        Optional<IngredientEntity> optionalIngredientEntity = ingredientRepository.findById(id);
    
        if (optionalIngredientEntity.isPresent()) {
            IngredientEntity ingredientEntity = optionalIngredientEntity.get();
            ingredientEntity.setIngredient(ingredient);
            ingredientEntity.setPrice(price);
            ingredientEntity.setQuantity(quantity);
            ingredientEntity.setExpiry(expiryTime);
            ingredientEntity.setUnitType(unitType);
            ingredientEntity.setLastUpdateTime(LocalDate.now());
            ingredientRepository.save(ingredientEntity);
        }
        return "Updated";
    }

    public List<IngredientEntity> getAllIngredients() {
        List<IngredientEntity> ingredients = ingredientRepository.findAll();
        System.out.println("Fetched ingredients: " + ingredients); // Debug log
        return ingredients;
    }

    public List<IngredientEntity> getAllProducts() {
        return ingredientRepository.findAll();
    }

    public IngredientEntity findByName(String ingredient) {
        return ingredientRepository.findByIngredient(ingredient);
    }

    public IngredientEntity findByName(IngredientEntity ingredient) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByName'");
    }

    public void saveIngredient(IngredientEntity ingredient) {
        ingredientRepository.save(ingredient);
    }

    // Archive ingredient
    public String archiveIngredient(Integer ingredientId) {
        // Retrieve the ingredient by ID
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId).orElse(null);

        if (ingredient == null) {
            return "Ingredient not found.";
        }

        // Set the ingredient's status to "archived"
        ingredient.setStatus("archived");
        ingredientRepository.save(ingredient);

        return "Ingredient archived successfully.";
    }

    public String removeArchivedIngredient(Integer ingredientId) {
        // Retrieve the ingredient by ID
        IngredientEntity ingredient = ingredientRepository.findById(ingredientId).orElse(null);
    
        if (ingredient == null) {
            return "Ingredient not found.";
        }
    
        // Set the ingredient's status to "active"
        ingredient.setStatus("active");
        ingredientRepository.save(ingredient);
    
        return "Ingredient removed from archived successfully.";
    }



}
