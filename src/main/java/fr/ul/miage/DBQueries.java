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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * Récupérer un document commande d'une table
     * @param table
     * @return commande
     */
    public Order getOrderFromTable(Table table){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MongoCollection<Document> collectionCommande = database.getCollection("Commande");
        Document doc = collectionCommande.find(eq("_id",table)).first();
        Order commande = gson.fromJson(doc.toJson(),Order.class);
        commande.set_id(doc.getObjectId("_id"));
        return commande;
    }

    /**
     * Ajout un nouveau document préparation dans la base de donnée
     * @param preparation
     */
    public void newPreparation(Preparation preparation){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Document prepa = new Document("_id",new ObjectId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        prepa.append("heureCommande", dtf.format(localDateTime))
                .append("debut",false)
                .append("Plat", preparation.Plat)
                .append("menuEnfant", preparation.menuEnfant);
        collection.insertOne(prepa);
    }


    /**
     * Ajouter un nouveau document commande dans la base de donnée
     */
    public void newOrder(Order commmande){
        MongoCollection<Document> collection = database.getCollection("Commande");
        Document com = new Document("_id",new ObjectId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        com.append("dateDebut", dtf.format(localDateTime))
                .append("debut",false)
                .append("Preparation", commmande.getPreparation())
                .append("montant", commmande.getMontant());
        collection.insertOne(com);
    }

    /**
     * Récupère la commande d'un document
     */
    public void getOrder(Table table){

    }


    /**
     * Actualise
     * @param commande
     */
    public void updateOrder(Order commande){
        MongoCollection<Document> collection = database.getCollection("Commande");
        Document com = new Document().append("_id",commande.get_id());
        Document setData = new Document();
        if(!(commande.getDateFin() == null)){
            setData.append("dateFin", commande.getDateFin());
        }
        setData.append("Preparation", commande.getPreparation());
        setData.append("montant", commande.getMontant());
        Document update = new Document();
        update.append("$set",setData);
        collection.updateOne(com,update);
    }

/*

    public void updateStock(Cook.Plat plat){
        MongoCollection<Document> collectionPlat = database.getCollection("Plat");
        MongoCollection<Document> collectionIng = database.getCollection("Ingredient");
        Document com = new Document().append("_id",plat._id);
        Document setData = new Document();
        for (String p : plat.Ingredient) {
            setData.append("Ingredient", plat.Ingredient.stock);
        }
        Document update = new Document();
        update.append("$set",setData);
        collectionIng.updateMany(com,update);
    }


    public ArrayList<Categorie> getCategoriesWithOneDishAvailable(){
        MongoCollection<Document> collectionCat = database.getCollection("Categorie");
        MongoCollection<Document> collectionPlat = database.getCollection("Plat");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  cat = collectionCat.find();
        FindIterable<Document>  platCat = collectionPlat.find();

        ArrayList<Categorie> categories = new ArrayList<>();
        for(Document docCat : cat){
            for (Document docPlatCat: platCat) {
                if (docPlatCat.get("Cat")){

                }
            }
            categories.add(gson.fromJson(d.toJson(), Categorie.class));
        }
        return null;
    }

    public ArrayList<Cook.Plat> getDishesAvailable(){
        MongoCollection<Document> collectionPlat = database.getCollection("Plat");
        MongoCollection<Document> collectionIngredient = database.getCollection("Ingredient");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  plats = collectionPlat.find();
        FindIterable<Document>  ingredients = collectionPlat.find();
        ArrayList<Cook.Plat> listPlats = new ArrayList<>();
        for (Document d :plats) {
            for (Document d2:ingredients) {
                if()
            }
        }
        return plats;
    }
*/


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

}
