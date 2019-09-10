package com.obaju.model;


import org.springframework.web.multipart.MultipartFile;

public class ProductForm {
    String name;
    double price;
    String desc;
    boolean psale;
    boolean pnew;
    boolean pgift;
    MultipartFile[] images;
    int[] removeProducts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isPsale() {
        return psale;
    }

    public void setPsale(boolean psale) {
        this.psale = psale;
    }

    public boolean isPnew() {
        return pnew;
    }

    public void setPnew(boolean pnew) {
        this.pnew = pnew;
    }

    public boolean isPgift() {
        return pgift;
    }

    public void setPgift(boolean pgift) {
        this.pgift = pgift;
    }

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }

    public int[] getRemoveProducts() {
        return removeProducts;
    }

    public void setRemoveProducts(int[] removeProducts) {
        this.removeProducts = removeProducts;
    }
}
