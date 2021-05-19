package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sample.datamodel.Item;

import java.time.LocalDate;

public class PurchaseDialogController {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;

    public Item newItem() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            name = " ";
        }

        String category = categoryBox.getValue();
        if (category == null) {
            category = "Other";
        }

        double price = 0.00;
        if (!priceField.getText().isEmpty()) {
            try {
                double temp = Double.parseDouble(priceField.getText());
                if (temp >= 0) {
                    price = temp;
                }
            } catch(NumberFormatException e) {
                price = 0.00;
            }
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            date = LocalDate.now();
        }

        return new Item(name, price, category, date);
    }
}
