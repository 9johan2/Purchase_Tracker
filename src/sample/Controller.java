package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.datamodel.Data;
import sample.datamodel.Item;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;


public class Controller {

    @FXML
    private GridPane mainWindow;
    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;
    @FXML
    private ListView<Item> leftList;
    @FXML
    private ListView<Item> rightList;
    @FXML
    private Label leftSum;
    @FXML
    private Label rightSum;


    private final Data data = Data.getInstance();
    private final ObservableList<Item> johansItems = data.getJohansItems();
    private final ObservableList<Item> janasItems = data.getJanasItems();

    private FilteredList<Item> filteredJohan;
    private FilteredList<Item> filteredJana;

    private SortedList<Item> sortedJohan;
    private SortedList<Item> sortedJana;

    private Predicate<Item> wantActivity;
    private Predicate<Item> wantFood;
    private Predicate<Item> wantFurnishing;
    private Predicate<Item> wantPhone;
    private Predicate<Item> wantRent;
    private Predicate<Item> wantTravel;
    private Predicate<Item> wantOther;
    private Predicate<Item> wantAll;

    private Alert unsavedChangesAlert;

    public void initialize() {

        // Create list that can be filtered by categories and sorted by date of purchase
        filteredJohan = new FilteredList<>(johansItems, wantAll);
        filteredJana = new FilteredList<>(janasItems, wantAll);

        Comparator<Item> compareDate = Comparator.comparing(Item::getDateOfPurchase);
        sortedJohan = new SortedList<>(filteredJohan, compareDate);
        sortedJana = new SortedList<>(filteredJana, compareDate);

        // Populate ListViews and Labels
        leftList.setItems(sortedJohan);
        leftLabel.setText("Johan");
        rightList.setItems(sortedJana);
        rightLabel.setText("Jana");
        updateSum();

        unsavedChangesAlert = new Alert(Alert.AlertType.CONFIRMATION);
        unsavedChangesAlert.setTitle("Save changes?");
        unsavedChangesAlert.setHeaderText(null);
        unsavedChangesAlert.setContentText("Would you like to save your changes first?" + "\nAny unsaved changes will be lost.");
        unsavedChangesAlert.getButtonTypes().remove(ButtonType.OK);
        unsavedChangesAlert.getButtonTypes().add(ButtonType.YES);
        unsavedChangesAlert.getButtonTypes().add(ButtonType.NO);

        addContextMenu();
        addPredicates();

    }

    public void addContextMenu() {
        MenuItem addPurchaseJohan = new MenuItem();
        addPurchaseJohan.setText("Add purchase");
        addPurchaseJohan.setOnAction((actionEvent) -> {
            Item item = addPurchase();
            if (item != null) {
                johansItems.add(item);
                updateSum();
                Data.getInstance().setChanged();
            }
        });

        MenuItem addPurchaseJana = new MenuItem();
        addPurchaseJana.setText("Add purchase");
        addPurchaseJana.setOnAction((actionEvent) -> {
            Item item = addPurchase();
            if (item != null) {
                janasItems.add(item);
                updateSum();
                Data.getInstance().setChanged();
            }
        });
            // Creating alert to show in case no item is selected when calling delete
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Please choose an item first");

        MenuItem deleteJohan = new MenuItem();
        deleteJohan.setText("Delete Item");
        deleteJohan.setOnAction((actionEvent) -> {
            Item item = leftList.getSelectionModel().getSelectedItem();
            if (item != null) {
                johansItems.remove(item);
                updateSum();
                Data.getInstance().setChanged();
            } else {
                alert.showAndWait();
            }
        });

        MenuItem deleteJana = new MenuItem();
        deleteJana.setText("Delete Item");
        deleteJana.setOnAction((actionEvent) -> {
            Item item = rightList.getSelectionModel().getSelectedItem();
            if (item != null) {
                janasItems.remove(item);
                updateSum();
                Data.getInstance().setChanged();
            } else {
                alert.showAndWait();
            }
        });

        leftList.setContextMenu(new ContextMenu(addPurchaseJohan, deleteJohan));
        rightList.setContextMenu(new ContextMenu(addPurchaseJana, deleteJana));
    }

    public void addPredicates() {

        wantAll = item -> true;
        wantActivity = item -> item.getType().equalsIgnoreCase("Activities");
        wantFood = item -> item.getType().equalsIgnoreCase("food");
        wantFurnishing = item -> item.getType().equalsIgnoreCase("furnishing");
        wantPhone = item -> item.getType().equalsIgnoreCase("phone");
        wantRent = item -> item.getType().equalsIgnoreCase("rent");
        wantTravel = item -> item.getType().equalsIgnoreCase("travel");
        wantOther = item -> item.getType().equalsIgnoreCase("other");
    }

    private void updateSum() {
        double johanSum = 0;
        for (Item item : sortedJohan) {
            johanSum += item.getPrice();
        }
        leftSum.setText("Sum: " + String.format("%.2f", johanSum));

        double janaSum = 0;
        for (Item item : sortedJana) {
            janaSum += item.getPrice();
        }
        rightSum.setText("Sum: " + String.format("%.2f", janaSum));
    }

    // Will return an Item to add to the lists via the context menu.
    @FXML
    private Item addPurchase() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        dialog.setTitle("New purchase");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("purchaseDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        PurchaseDialogController controller = fxmlLoader.getController();
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            return controller.newItem(); // Check for item == null not required, its handled by PurchaseDialogController.newItem().
        }

        return null;
    }

    @FXML
    public void showAll() {
        filteredJana.setPredicate(wantAll);
        filteredJohan.setPredicate(wantAll);
        updateSum();
    }

    @FXML
    public void filterActivity() {
        filteredJana.setPredicate(wantActivity);
        filteredJohan.setPredicate(wantActivity);
        updateSum();
    }

    @FXML
    public void filterFood() {
        filteredJana.setPredicate(wantFood);
        filteredJohan.setPredicate(wantFood);
        updateSum();
    }

    @FXML
    public void filterFurnish() {
        filteredJana.setPredicate(wantFurnishing);
        filteredJohan.setPredicate(wantFurnishing);
        updateSum();
    }

    @FXML
    public void filterPhone() {
        filteredJana.setPredicate(wantPhone);
        filteredJohan.setPredicate(wantPhone);
        updateSum();
    }

    @FXML
    public void filterRent() {
        filteredJana.setPredicate(wantRent);
        filteredJohan.setPredicate(wantRent);
        updateSum();
    }

    @FXML
    public void filterTravel() {
        filteredJana.setPredicate(wantTravel);
        filteredJohan.setPredicate(wantTravel);
        updateSum();
    }

    @FXML
    public void filterOther() {
        filteredJana.setPredicate(wantOther);
        filteredJohan.setPredicate(wantOther);
        updateSum();
    }

    // Will clear the listViews and set current file == null
    @FXML
    public void newFile() {

        if (Data.getInstance().isChanged()) {

            Optional<ButtonType> result = unsavedChangesAlert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.YES)) {
                save();
                johansItems.clear();
                janasItems.clear();
                Data.getInstance().newFile();
                updateSum();
            } else if (result.isPresent() && result.get().equals(ButtonType.NO)) {
                johansItems.clear();
                janasItems.clear();
                Data.getInstance().newFile();
                updateSum();
            }
        } else {
            johansItems.clear();
            janasItems.clear();
            Data.getInstance().newFile();
            updateSum();
        }
    }


    @FXML
    public void openFile() {
        if (Data.getInstance().isChanged()) {

            Optional<ButtonType> result = unsavedChangesAlert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.YES)) {
                save();
                Data.getInstance().openFile();
                updateSum();

            } else if (result.isPresent() && result.get().equals(ButtonType.NO)) {
                Data.getInstance().openFile();
                updateSum();
            }
        } else {
            Data.getInstance().openFile();
            updateSum();
        }
    }

    @FXML
    public void save() {
        System.out.println("save() accessed");
        Data.getInstance().saveData();
    }

    @FXML
    public void saveAs() {
        Data.getInstance().saveAs();
    }

    @FXML
    public void exit() {
        if (Data.getInstance().isChanged()) {

            Optional<ButtonType> result = unsavedChangesAlert.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.YES)) {
                save();
                Platform.exit();
                System.out.println("Save and exit");
            } else if (result.isPresent() && result.get().equals(ButtonType.NO)) {
                Platform.exit();
                System.out.println("Exit without saving");
            }
        } else {
            Platform.exit();
        }

    }
}
