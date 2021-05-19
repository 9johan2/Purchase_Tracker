package sample.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private static final Data instance = new Data();
    private final ObservableList<Item> johansItems;
    private final ObservableList<Item> janasItems;

    private Data() {
        this.johansItems = FXCollections.observableArrayList();
        this.janasItems = FXCollections.observableArrayList();
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

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Java Programs\\My Programs\\Economy\\data.dat"))) {
            ArrayList<Item> johansList = new ArrayList<>(johansItems);
            ArrayList<Item> janasList = new ArrayList<>(janasItems);

            oos.writeObject(johansList);
            oos.writeObject(janasList);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadData() {

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Java Programs\\My Programs\\Economy\\data.dat"))) {
            List<Item> loadJohan = (List<Item>) ois.readObject();
            List<Item> loadJana = (List<Item>) ois.readObject();
            this.johansItems.addAll(loadJohan);
            this.janasItems.addAll(loadJana);
            System.out.println("Data loaded successfully!");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
