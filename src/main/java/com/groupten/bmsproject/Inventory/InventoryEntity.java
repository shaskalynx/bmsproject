package com.groupten.bmsproject.Inventory;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Entity
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_seq")
    @SequenceGenerator (name = "inv_seq", sequenceName = "inventory_sequence", allocationSize = 1)

    private Integer id;
    private String ingredient;
    private Double price;
    private Integer quantity;
    private LocalDateTime expiryTime;

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id=id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExpiry() {
        return expiryTime;
    }

    public void setExpiry(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
 // Property methods for JavaFX TableView binding
     public IntegerProperty idProperty() {
        return new SimpleIntegerProperty(id);
    }

    public StringProperty IngredientProperty() {
        return new SimpleStringProperty(ingredient);
    }

     public DoubleProperty priceProperty() {
        return new SimpleDoubleProperty(price);
    }

    public IntegerProperty quantityIngredientProperty() {
        return new SimpleIntegerProperty(quantity);
    }

    public ObjectProperty<LocalDateTime> expiryTimeProperty() {
        return new SimpleObjectProperty<>(expiryTime);
    }
}
