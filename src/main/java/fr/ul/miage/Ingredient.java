package fr.ul.miage;

import org.bson.types.ObjectId;

public class Ingredient {

    public ObjectId _id;
    public String nom;
    public int stock;

    public Ingredient(String nom, int stock){
        this.nom = nom;
        this.stock = stock;
    }


}
