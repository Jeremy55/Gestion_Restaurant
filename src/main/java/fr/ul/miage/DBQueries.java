package fr.ul.miage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import org.bson.types.ObjectId;

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
                Cook u = gson.fromJson(staff.toJson(), Cook.class);
                u.setId(staff.getObjectId("_id"));
                return u;
            case "assistant de service":
                ServiceAssistant v = gson.fromJson(staff.toJson(), ServiceAssistant.class);
                v.setId(staff.getObjectId("_id"));
                return v;
            case "maitre d'hotel":
                Butler b = gson.fromJson(staff.toJson(), Butler.class);
                b.setId(staff.getObjectId("_id"));
                return b;
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
            Table a = gson.fromJson(doc.toJson(),Table.class);
            a.set_id(doc.getObjectId("_id"));
            tables.add(a);
        }

        return tables;
    }

    public void updateTableLibre(ObjectId id){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Document query = new Document().append("_id", id);
        Document table = collectionTable.find(eq("_id", id)).first();

        Document setData = new Document();
        setData.append("etat", "libre");

        Document update = new Document();
        update.append("$set", setData);

        collectionTable.updateOne(query, update);
    }

    public List<Waiter> getServeurAffecte(Table t){
        MongoCollection<Document> collectionPersonnel = database.getCollection("Personnel");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document> serveur =  collectionPersonnel.find(eq("role", "serveur"));

        List<Waiter> serveurs = new ArrayList<Waiter>();
        for(Document doc : serveur){
            Waiter waiter = gson.fromJson(doc.toJson(),Waiter.class);
            if(doc.get("Table") != null){
                waiter.setTable((List<ObjectId>) doc.get("Table"));
            }
            else{
                waiter.setTable(new ArrayList<ObjectId>());
            }

            serveurs.add(waiter);
        }

        List<Waiter> serveursTable = new ArrayList<Waiter>();
        for(Waiter a : serveurs){
            //System.out.println(a.getTable());
            if(a.getTable().contains(t.get_id())){
                serveursTable.add(a);
            }
        }
        return serveursTable;
    }

    public List<Waiter> getServeurNonAffecte(Table t){
        MongoCollection<Document> collectionPersonnel = database.getCollection("Personnel");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document> serveur =  collectionPersonnel.find(eq("role", "serveur"));

        List<Waiter> serveurs = new ArrayList<Waiter>();
        for(Document doc : serveur){
            Waiter waiter = gson.fromJson(doc.toJson(),Waiter.class);
            if(doc.get("Table") != null){
                waiter.setTable((List<ObjectId>) doc.get("Table"));
            }
            else{
                waiter.setTable(new ArrayList<ObjectId>());
            }
            serveurs.add(waiter);
        }

        List<Waiter> serveursTable = new ArrayList<Waiter>();
        for(Waiter a : serveurs){
            if(!a.getTable().contains(t.get_id())){
                serveursTable.add(a);
            }
        }
        return serveursTable;
    }

    public void AffecteServeurTable(Table table, Waiter serveur){
        MongoCollection<Document> collectionPersonnel = database.getCollection("Personnel");
        Document query = new Document().append("login", serveur.getLogin());
        Document tableServeur = collectionPersonnel.find(eq("login", serveur.getLogin())).first();

        List tableExistante = (List) tableServeur.get("Table");
        if(tableExistante == null){
            tableExistante = new ArrayList();
        }
        tableExistante.add(table.get_id());

        Document setData = new Document();
        setData.append("Table", tableExistante);

        Document update = new Document();
        update.append("$set", setData);

        collectionPersonnel.updateOne(query, update);
    }


}
