package fr.ul.miage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

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
            case "assistant de service":
                return gson.fromJson(staff.toJson(), ServiceAssistant.class);
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

    public void updateTableLibre(int numero, int etage){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Document query = new Document().append("numero", numero).append("etage", etage);
        Document table = collectionTable.find(and(eq("numero", numero), eq("etage", etage))).first();


        Document setData = new Document();
        setData.append("etat", "libre");

        Document update = new Document();
        update.append("$set", setData);

        collectionTable.updateOne(query, update);


    }


}
