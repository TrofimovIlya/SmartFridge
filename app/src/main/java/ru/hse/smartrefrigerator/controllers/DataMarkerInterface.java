package ru.hse.smartrefrigerator.controllers;

/**
 * @author Ilya Trofimov
 */
public interface DataMarkerInterface {
    public ProductDataProvider getDataProvider();

    public void onItemClicked(int position);

    public void onItemRemoved(int position);
}
