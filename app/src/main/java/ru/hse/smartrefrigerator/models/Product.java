package ru.hse.smartrefrigerator.models;

/**
 * @author Ilya Trofimov
 */
public class Product {
    public Product(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    // TODO Date
}
