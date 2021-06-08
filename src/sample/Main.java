package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import sample.datamodel.Data;

import java.util.Optional;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Purchase tracker");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setResizable(false);
        stage = primaryStage;
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            if (Data.getInstance().isChanged()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save changes?");
                alert.setHeaderText(null);
                alert.setContentText("Would you like to save your changes before quitting?" + "\nAny unsaved changes will be lost.");
                alert.getButtonTypes().remove(ButtonType.OK);
                alert.getButtonTypes().add(ButtonType.YES);
                alert.getButtonTypes().add(ButtonType.NO);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get().equals(ButtonType.YES)) {
                    Data.getInstance().saveData();
                    Platform.exit();
                    System.out.println("Save and exit");
                } else if (result.isPresent() && result.get().equals(ButtonType.NO)) {
                    Platform.exit();
                    System.out.println("Exit without saving");
                } else {
                    windowEvent.consume();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        Data.getInstance().loadData();
    }

    public static Stage getStage() {
        return stage;
    }
}
