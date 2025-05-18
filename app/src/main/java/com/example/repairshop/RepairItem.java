package com.example.repairshop;

public class RepairItem {
    private String name;
    private String info;
    private String price;

    private String imageResource;

    public RepairItem(String name, String info, String price, String imageResource) {
        this.name = name;
        this.info = info;
        this.price = price;

        this.imageResource = imageResource;
    }

    public RepairItem() {
    }

    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String getPrice() {
        return price;
    }
    public String getImageResource() {
        return imageResource;
    }
}
