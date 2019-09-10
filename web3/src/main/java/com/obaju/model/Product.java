package com.obaju.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;

import javax.persistence.*;
import java.util.List;

@Indexed
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @Field
    private String name;

    @Column(name = "price")
    @Field
    @SortableField
    private double price;

    @Column(name = "description")
    @Field
    private String description;

    @Column(name = "is_sale")
    private int isSale;

    @Column(name = "is_new")
    private int isNew;

    @Column(name = "is_gift")
    private int isGift;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private List<Image> imageList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsSale() {
        return isSale;
    }

    public void setIsSale(int isSale) {
        this.isSale = isSale;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getIsGift() {
        return isGift;
    }

    public void setIsGift(int isGift) {
        this.isGift = isGift;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }
}