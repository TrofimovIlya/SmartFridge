package ru.hse.smartrefrigerator.models;

import java.util.Date;

/**
 * Created by KingUrgot on 08.07.2015.
 */
public class Product {
    String name;
    Date expirationDate;

    public Product(String name, Date expirationDate) {
        this.name = name;
        this.expirationDate = expirationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expiretionDate) {
        this.expirationDate = expiretionDate;
    }

    @Override
    public String toString() {
        return name + " expires at " + expirationDate;
    }
}
