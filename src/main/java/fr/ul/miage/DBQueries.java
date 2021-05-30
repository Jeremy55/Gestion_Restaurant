package fr.ul.miage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
            t.setCommande((ObjectId) doc.get("Commande"));
            tables.add(t);
        }

        return tables ;
    }

    /**
     * Récupérer un document commande d'une table
     * @param oid
     * @return commande
     */
    public Order getOrderFromTable(ObjectId oid){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MongoCollection<Document> collectionCommande = database.getCollection("Commande");
        Document doc = collectionCommande.find(eq("_id",oid)).first();
        Order commande = gson.fromJson(doc.toJson(),Order.class);
        commande.setPreparation((List<ObjectId>)doc.get("Preparation"));
        commande.set_id(doc.getObjectId("_id"));
        return commande;
    }

    /**
     * Ajout un nouveau document préparation dans la base de donnée
     * @param preparation
     */
    public void newPreparation(Preparation preparation){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Document prepa = new Document("_id", preparation._id);
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
        Document com = new Document("_id",commmande.get_id());
        com.append("dateDebut", commmande.getDateDebut())
                .append("Preparation", commmande.getPreparation())
                .append("montant", commmande.getMontant());
        collection.insertOne(com);
    }

    /**
     * Actualise une commande
     * @param commande
     */
    public void updateOrder(Order commande){
        MongoCollection<Document> collection = database.getCollection("Commande");
        Document com = new Document().append("_id",commande.get_id());
        Document setData = new Document();
        setData.append("Preparation", commande.getPreparation());
        setData.append("montant", commande.getMontant());
        Document update = new Document();
        update.append("$set",setData);
        collection.updateOne(com,update);
    }


    /**
     * Actualise la date de fin d'une commande
     */
    public void removeOrderFromTable(Table table){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Document query = new Document().append("_id",table.get_id());
        Document tab = collectionTable.find(eq("_id",table.get_id())).first();
        Document update = new Document();
        update.put("$unset", new BasicDBObject("Commande",""));
        collectionTable.updateOne(query,update);
    }

    /**
     * Ajoute la date de fin à une commande
     * @param table
     */
    public void addEndDateToOrder(Table table){
        MongoCollection<Document> collectionCommande = database.getCollection("Commande");
        Document query = new Document().append("_id",table.getCommande());
        Document commande = collectionCommande.find(eq("_idid",table.getCommande())).first();
        Document setData = new Document();
        setData.append("dateFin", DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss").format(LocalDateTime.now()));
        Document update = new Document();
        update.append("$set", setData);
        collectionCommande.updateOne(query,update);
    }


    /**
     * Récupère les catégories contenant au moins un plat avec tous les ingrédients requis > 0
     * @return categories
     */
    public ArrayList<String> getCategoriesWithAtLeastOneDishAvailable(){
        MongoCollection<Document> collectionCat = database.getCollection("Categorie");
        MongoCollection<Document> collectionPlat = database.getCollection("Plat");
        MongoCollection<Document> collectionIngredient = database.getCollection("Ingredient");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  cat = collectionCat.find();
        FindIterable<Document>  plats = collectionPlat.find();
        FindIterable<Document>  ingredients = collectionIngredient.find();
        boolean isOk;
        ArrayList<String> categories = new ArrayList<>();

        //On parcours les plats de la collection
        for (Document plat : plats) {
            isOk = true;
            //On parcours les ingrédients de chaque plat
            for (String ing : (List<String>) plat.get("Ingredient") ) {
                Document d = collectionIngredient.find(eq("nom", ing)).first();
                Integer stock = d.getInteger("stock");
                if (stock.intValue() <= 0) {
                    isOk = false;
                }
            }
            if(isOk){
                //On récupère les catégories du plat si elles ne sont pas déjà présentes dans la liste
                for (String str : (List<String>) plat.get("Categorie")) {
                    if(!categories.contains(str)){
                        categories.add(str);
                    }
                }
            }
        }
        return categories;
    }


    /**
     * Récupère les plats qui ont la catégorie renseigné et dont les ingrédients qui composent ce plat ont un stock > 0
     * @return listPlats
     */
    public ArrayList<Cook.Plat> getDishesAvailable(String categorie){
        MongoCollection<Document> collectionPlat = database.getCollection("Plat");
        MongoCollection<Document> collectionIngredient = database.getCollection("Ingredient");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  plats = collectionPlat.find();
        FindIterable<Document>  ingredients = collectionPlat.find();
        ArrayList<Cook.Plat> listPlats = new ArrayList<>();
        boolean isOk = true;

        //Parcours les plats dans la collection
        for (Document plat :plats) {
            if(((List<String>) plat.get("Categorie")).contains(categorie)){
                //Parcours des ingrédient pour un plat donné
                for (String ing : (List<String>) plat.get("Ingredient")) {
                    Document d = collectionIngredient.find(eq("nom", ing)).first();
                    Integer stock = d.getInteger("stock");
                    if (stock.intValue() <= 0){
                        isOk = false;
                    }
                }
                if(isOk){
                    Cook.Plat platObj = gson.fromJson(plat.toJson(),Cook.Plat.class);
                    platObj._id = plat.getObjectId("_id");
                    listPlats.add(platObj);
                }
            }
        }
        return listPlats;
    }

    /**
     *
     * @param _id
     * @param quantity
     */
    public void updateIngredient(ObjectId _id, int quantity){
        MongoCollection<Document> collection = database.getCollection("Ingredient");
        Document query = new Document().append("_id",_id);
        Document setData = new Document();
        setData.append("stock",quantity);
        Document update = new Document();
        update.append("$set",setData);
        collection.updateOne(query,update);
    }


    /**
     * Récupère un ingrédient donné
     * @param nom
     * @return ingredient
     */
    public Document getIngredient(String nom){
        MongoCollection<Document> collection = database.getCollection("Ingredient");
        Document ingredient = collection.find(eq("nom",nom)).first();
        return ingredient;
    }

    /**
     * Change etat de la table à "débarassée"
     * @param table
     */
    public void updateTableDebarassee(Table table){
        MongoCollection<Document> collection = database.getCollection("Table");
        Document query = new Document().append("_id", table.get_id());
        Document ingredient = collection.find(eq("_id",table.get_id())).first();
        Document setData = new Document();
        setData.append("etat",table.getEtat());
        Document update = new Document();
        update.append("$set",setData);
        collection.updateOne(query,update);
    }


    /**
     * Ajouter une commande à une table
     */
    public void addOrderToTable(Table table, ObjectId order){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Document query = new Document().append("_id",table.get_id());
        Document tab = collectionTable.find(eq("_id",table.get_id())).first();
        Document setData = new Document();
        setData.append("Commande", order);
        Document update = new Document();
        update.append("$set",setData);
        collectionTable.updateOne(query,update);
    }
}
