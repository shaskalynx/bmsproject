package com.groupten.bmsproject.Inventory;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;

    @PostMapping(path = "/addInventory")
    public String addNewInventory(@RequestParam String ingredient, @RequestParam Double price, @RequestParam Integer quantity, @RequestParam LocalDate expiryTime, @RequestParam LocalDate dateAdded) {
        return inventoryService.addNewInventory(ingredient, price, quantity, expiryTime, dateAdded);
    }
    
}
