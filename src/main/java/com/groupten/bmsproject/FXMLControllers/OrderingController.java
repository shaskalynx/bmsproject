package com.groupten.bmsproject.FXMLControllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.Parent;
import javafx.scene.Scene;

import com.groupten.bmsproject.BmsprojectApplication;
import com.groupten.bmsproject.Ingredient.IngredientEntity;
import com.groupten.bmsproject.Order.OrderEntity.DeliveryStatus;
import com.groupten.bmsproject.Order.OrderEntity.PaymentStatus;
import com.groupten.bmsproject.Order.OrderService;
import com.groupten.bmsproject.Product.ProductService;
import com.groupten.bmsproject.ProductionSchedule.ProductionScheduleEntity;
import com.groupten.bmsproject.ProductionSchedule.ProductionScheduleService;
import com.groupten.bmsproject.Product.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class OrderingController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductionScheduleService productionScheduleService;

    @Autowired
    private ProductService productService;

    @FXML
    private TextField CustomerNameTextField;

    @FXML
    private TextField AddressTextField;

    @FXML
    private DatePicker DateOrder;

    @FXML
    private ChoiceBox<String> ProductOrderChoiceBox;

    @FXML
    private TextField remainingProductQuantity;

    @FXML
    private TextField productUnitType;

    @FXML
    private TextField QuantityOrderTextField;

    @FXML
    private ComboBox<PaymentStatus> PaymentStatusComboBox;

    @FXML
    private ComboBox<DeliveryStatus> DeliveryStatusComboBox;

    @FXML
    private Button SaveOrderButton;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @FXML
    private void initialize() {
        PaymentStatusComboBox.getItems().setAll(PaymentStatus.values());
        DeliveryStatusComboBox.getItems().setAll(DeliveryStatus.values());

        // Disable past dates in the DatePicker
        DateOrder.setDayCellFactory(getDateCellFactory());

        // Populate ProductOrderChoiceBox with available products
        populateProductOrderChoiceBox();

        // Add listener to update product details when a product is selected
        ProductOrderChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateProductDetails(newValue);
            }
        });
    }

    private void populateProductOrderChoiceBox() {
        List<ProductionScheduleEntity> availableProducts = productionScheduleService.getAllProducts();
        for (ProductionScheduleEntity product : availableProducts) {
            ProductOrderChoiceBox.getItems().add(product.getID() + " - " + product.getproductName());
        }
    }

    private void updateProductDetails(String productNameWithID) {
        // Extract the product ID from the ComboBox item
        String[] parts = productNameWithID.split(" - ");
        String productID = parts[0];
        String productName = parts[1];

        ProductionScheduleEntity selectedProduct = productionScheduleService.getProductionScheduleById(Integer.parseInt(productID));
        if (selectedProduct != null) {
            remainingProductQuantity.setText(String.valueOf(selectedProduct.getproductschedQuantity()));
            productUnitType.setText(selectedProduct.getlvlofstock());
        }
    }

    private Callback<DatePicker, DateCell> getDateCellFactory() {
        return new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        // Disable all past dates
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #d48200;"); // You can set a style to indicate disabled dates
                        }
                    }
                };
            }
        };
    }

    @FXML
    private void handleSaveButton() {
        String customerName = CustomerNameTextField.getText();
        String address = AddressTextField.getText();
        LocalDate dateOrder = DateOrder.getValue(); // Convert DatePicker value to LocalDate
        String productOrderWithID = ProductOrderChoiceBox.getValue();
        
        // Extract the product ID from the ComboBox item
        String[] parts = productOrderWithID.split(" - ");
        String productID = parts[0];
        String productOrder = parts[1];

        Integer quantityOrder = Integer.parseInt(QuantityOrderTextField.getText());
        PaymentStatus paymentStatus = PaymentStatusComboBox.getValue();
        DeliveryStatus deliveryStatus = DeliveryStatusComboBox.getValue();

        // Get the selected product
        ProductEntity selectedProduct = productService.getProductByID(Integer.parseInt(productID));

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Product Error");
            alert.setHeaderText("Product not found");
            alert.setContentText("The selected product could not be found.");
            alert.showAndWait();
            return;
        }

        // Deduct the ordered quantity from the production schedule
        ProductionScheduleEntity productionSchedule = productionScheduleService.getProductionScheduleById(Integer.parseInt(productID));
        if (productionSchedule != null) {
            Double remainingQuantity = productionSchedule.getproductschedQuantity() - quantityOrder;
            if (remainingQuantity < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Insufficient Quantity");
                alert.setHeaderText("Insufficient quantity of product");
                alert.setContentText("The quantity ordered exceeds the available quantity of the product.");
                alert.showAndWait();
                return;
            }
            productionSchedule.setproductschedQuantity(remainingQuantity);
            productionScheduleService.updateProductionSchedule(productionSchedule);
        }

        // Check for low stock products
        List<ProductionScheduleEntity> lowStockProducts = productionScheduleService.getAllProducts(); // Adjust as necessary to fetch only low stock products
        StringBuilder warningMessage = new StringBuilder();
        for (ProductionScheduleEntity product : lowStockProducts) {
            if (product.getproductschedQuantity() <= LOW_STOCK_THRESHOLD) {
                warningMessage.append(product.getproductName()).append(" (Quantity: ").append(product.getproductschedQuantity()).append(")\n");
            }
        }
        if (warningMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Low Stock Warning");
            alert.setHeaderText("There are low stock products.");
            alert.setContentText(warningMessage.toString());
            alert.showAndWait();
        }

        // Proceed to save the order
        String result = orderService.addNewOrder(customerName, address, dateOrder, productOrder, quantityOrder, paymentStatus, deliveryStatus);
        System.out.println(result);
    }

    @FXML
    private void backtoOrders() throws IOException {
        ConfigurableApplicationContext context = BmsprojectApplication.getApplicationContext(); // Get the application context
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Orders.fxml"));
        loader.setControllerFactory(context::getBean);

        Parent root = loader.load();
        Stage stage = BmsprojectApplication.getPrimaryStage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
