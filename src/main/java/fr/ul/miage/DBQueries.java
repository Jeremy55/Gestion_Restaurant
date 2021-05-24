package fr.ul.miage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBQueries {

    private final MongoDatabase database;

    public DBQueries(){
        MongoClient mongoClient = MongoClients.create("mongodb+srv://admin:qFoOXXTZeYMRcihb@cluster0.vfnf9.mongodb.net/GestionRestaurant?retryWrites=true&w=majority");
        this.database = mongoClient.getDatabase("GestionRestaurant");
    }

    public boolean userConnection(String login, String mdp){
        MongoCollection<Document> collection = database.getCollection("Personnel");
        long nb = collection.countDocuments(and(eq("login", login), eq("mdp", mdp)));
        return nb==1;
    }

    public Staff getStaff(String login){
        MongoCollection<Document> collection = database.getCollection("Personnel");
        Document staff = collection.find(eq("login", login)).first();
        Gson gson = new GsonBuilder().create();
        String role = staff.get("role").toString();
        switch (role){
            case "cuisinier":
                return gson.fromJson(staff.toJson(), Cook.class);
            default:
                return null;
        }
    }

    public ArrayList<Ingredient> getIngredients(){
       MongoCollection<Document> collection = database.getCollection("Ingredient");
       Gson gson = new GsonBuilder().setPrettyPrinting().create();
       FindIterable<Document>  ingredientsDoc = collection.find();
       ArrayList<Ingredient> ingredients = new ArrayList<>();
       for(Document d : ingredientsDoc){
           ingredients.add(gson.fromJson(d.toJson(), Ingredient.class));
       }
       return ingredients;
    }

    public ArrayList<Categorie> getCategories(){
        MongoCollection<Document> collection = database.getCollection("Categorie");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  ingredientsDoc = collection.find();
        ArrayList<Categorie> categories = new ArrayList<>();
        for(Document d : ingredientsDoc){
            categories.add(gson.fromJson(d.toJson(), Categorie.class));
        }
        return categories;
    }

    public void newDish(String nomPlat,List<String> ingredients,List<String> categorie,Double prix){
        MongoCollection<Document> collection = database.getCollection("Plat");
        Document plat = new Document("_id",new ObjectId());
        plat.append("nom",nomPlat)
        .append("Ingredient",ingredients)
        .append("Categorie",categorie)
        .append("prix",prix)
        .append("platDuJour",false);
        collection.insertOne(plat);
    }

}
