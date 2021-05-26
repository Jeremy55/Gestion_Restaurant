package fr.ul.miage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.*;

import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
                Cook c = gson.fromJson(staff.toJson(), Cook.class);
                c.setId(staff.getObjectId("_id"));
                return c;
            case "serveur":
                Waiter s = gson.fromJson(staff.toJson(), Waiter.class);
                s.setId(staff.getObjectId("_id"));
                List l = (List) staff.get("Table");
                s.setTable(l);
                return s;
            default:
                return null;
        }
    }

    public List<Table> getAllTable(){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document> table =  collectionTable.find();

        List<Table> tables = new ArrayList<Table>();
        for(Document doc : table){
            tables.add(gson.fromJson(doc.toJson(),Table.class));
        }
        return tables;
    }

    /**
     * Recupère les tables du serveur demandé et parsing Json vers Objet
     * @param waiter
     * @return tables
     */
    public List<Table> getWaiterTables(Waiter waiter){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //On récupère la collection de tables
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        List<Document> listTables = new ArrayList<Document>();

        for (ObjectId t: waiter.getTable()) {
           Document d = collectionTable.find(eq("_id",t)).first();
           listTables.add(d);
        }

        List<Table> tables = new ArrayList<Table>();

        for(Document doc : listTables){
            Table t = gson.fromJson(doc.toJson(),Table.class);
            t.set_id(doc.getObjectId("_id"));
            tables.add(t);
        }

        return tables ;
    }


    public void g(){

    }

}
