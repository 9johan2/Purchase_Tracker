package sample.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import sample.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Data {

    private static final Data instance = new Data();
    private final ObservableList<Item> johansItems;
    private final ObservableList<Item> janasItems;
    private final FileChooser fileChooser;
    private File file;
    private boolean changed = false;

    private Data() {
        this.johansItems = FXCollections.observableArrayList();
        this.janasItems = FXCollections.observableArrayList();
        this.fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data file", "*.dat"));
        file = null;
    }

    public static Data getInstance() {
        return instance;
    }

    public ObservableList<Item> getJohansItems() {
        return johansItems;
    }

    public ObservableList<Item> getJanasItems() {
        return janasItems;
    }

    public void saveData() {
        if (file != null) {
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                ArrayList<Item> johansList = new ArrayList<>(johansItems);
                ArrayList<Item> janasList = new ArrayList<>(janasItems);

                oos.writeObject(johansList);
                oos.writeObject(janasList);
                changed = false;
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            saveAs();
        }

    }

    public void saveAs() {
        file = fileChooser.showSaveDialog(Main.getStage());
        if (file != null) {
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                ArrayList<Item> johansList = new ArrayList<>(johansItems);
                ArrayList<Item> janasList = new ArrayList<>(janasItems);

                oos.writeObject(johansList);
                oos.writeObject(janasList);
                fileChooser.setInitialDirectory(file.getParentFile());
                changed = false;

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Opens the default file during startup
    public void loadData() {

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Java Programs\\My Programs\\Economy\\data.dat"))) {
            file = new File("C:\\Java Programs\\My Programs\\Economy\\data.dat");
            List<Item> loadJohan = (List<Item>) ois.readObject();
            List<Item> loadJana = (List<Item>) ois.readObject();
            this.johansItems.addAll(loadJohan);
            this.janasItems.addAll(loadJana);
            fileChooser.setInitialDirectory(file.getParentFile());
            changed = false;

        } catch (FileNotFoundException e) {
            newFile();
        }
        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openFile() {
        file = fileChooser.showOpenDialog(Main.getStage());
        if (file != null) {
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                List<Item> loadJohan = (List<Item>) ois.readObject();
                List<Item> loadJana = (List<Item>) ois.readObject();
                this.johansItems.clear();
                this.janasItems.clear();
                this.johansItems.addAll(loadJohan);
                this.janasItems.addAll(loadJana);
                fileChooser.setInitialDirectory(file.getParentFile());
                changed = false;

            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Incompatible data");
                alert.setHeaderText(null);
                alert.setContentText("The file you chose has incompatible data or is corrupted.");
                alert.showAndWait();
            }
        }
    }

    public void newFile() {
        file = null;
        fileChooser.setInitialDirectory(null);
        changed = false;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged() {
        this.changed = true;
    }
}
