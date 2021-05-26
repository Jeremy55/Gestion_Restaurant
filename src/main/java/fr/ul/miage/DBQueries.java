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
import java.util.Date;
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

    public ArrayList<Cook.Preparation> getPreparations(){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  preparationsDoc = collection.find(eq("debut", false));
        ArrayList<Cook.Preparation> preparations = new ArrayList<>();
        for(Document d : preparationsDoc ){
            Cook.Preparation preparation = gson.fromJson(d.toJson(), Cook.Preparation.class);
            preparation.Plat = d.getObjectId("Plat");
            preparation._id = d.getObjectId("_id");
            preparations.add(preparation);
        }
        return preparations;
    }

    public Cook.Plat getPlat(ObjectId _id){
        MongoCollection<Document> collection = database.getCollection("Plat");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Document platDoc = collection.find(eq("_id", _id)).first();
        Cook.Plat plat = gson.fromJson(platDoc.toJson(), Cook.Plat.class);
        plat._id = platDoc.getObjectId("_id");
        return plat;
    }

    public void updatePreparation(Cook.Preparation preparation){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Document query = new Document().append("_id",preparation._id);
        Document setData = new Document();
        if(!(preparation.fin == null)){
            setData.append("fin", preparation.fin);
        }
        setData.append("debut",true);
        Document update = new Document();
        update.append("$set",setData);
        collection.updateOne(query,update);
    }
}
