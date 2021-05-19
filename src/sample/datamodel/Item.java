package sample.datamodel;

import java.io.Serializable;
import java.time.LocalDate;

public class Item implements Serializable{
    private String name;
    private double price;
    private String type;
    private LocalDate dateOfPurchase;
    private static final long serialVersionUID = 258L;

    public Item(String name, double price, String type, LocalDate dateOfPurchase) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.dateOfPurchase = dateOfPurchase;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public LocalDate getDateOfPurchase() {
        return dateOfPurchase;
    }

    @Override
    public String toString() {
        return name + "\t" + type + "\n" + dateOfPurchase.toString() + "\t" + String.format("%.2f",price);
    }
}
