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

import java.lang.reflect.Type;
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
            case "directeur":
                Director d = gson.fromJson(staff.toJson(), Director.class);
                d.setId(staff.getObjectId("_id"));
                return d;
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

    public ArrayList<Preparation> getPreparations(){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  preparationsDoc = collection.find(eq("debut", false));
        ArrayList<Preparation> preparations = new ArrayList<>();
        for(Document d : preparationsDoc ){
            Preparation preparation = gson.fromJson(d.toJson(), Preparation.class);
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

    public void updatePreparation(Preparation preparation){
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
    /**
     * Cette méthode permet de récupérer toutes les tables de la base de données
     * @return une liste de Table
     */
    public List<Table> getAllTable(){
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document> table =  collectionTable.find(); //On récupère toutes les tables

        List<Table> tables = new ArrayList<Table>();
        for(Document doc : table){
            Table a = gson.fromJson(doc.toJson(),Table.class); //On transforme les documents en table
            a.set_id(doc.getObjectId("_id"));
            tables.add(a);
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
     * Permet de mettre l'état d'une table de débarassée à libre
     * @param
     */
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

    /**
     * Permet de récupérer la liste des serveurs qui sont affectés à une table
     * @param t
     * @return liste de serveurs affectés
     */
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

    /**
     * Permet de récupérer la liste des serveurs qui ne sont pas affectés à une table
     * @param t
     * @return liste de serveurs non affectés
     */
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

    /**
     * Permet d'affecter un serveur à une table
     * @param table
     * @param serveur
     */
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

    /**
     * Ajouter un nouvel employé dans la BDD
     * @param login
     * @param mdp
     * @param nom
     * @param prenom
     * @param role
     * @param tables
     */
    public void addStaff(String login, String mdp, String nom, String prenom, String role, List<Table> tables){
        MongoCollection<Document> collectionPersonnel = database.getCollection("Personnel");
        Document personnel = new Document("_id",new ObjectId());
        personnel.append("login",login)
                .append("mdp",mdp)
                .append("role",role)
                .append("nom",nom)
                .append("prenom",prenom);
        List tableID = new ArrayList();
        if(role.equals("serveur")){
            for(Table i : tables){
                tableID.add(i.get_id());
            }
            personnel.append("Table", tableID);
        }
        collectionPersonnel.insertOne(personnel);
    }

    /**
     * Cette méthode permet de récupérer tout les employés
     * @return une liste d'employés
     */
    public List<Staff> getAllStaff(){
        MongoCollection<Document> collectionTable = database.getCollection("Personnel");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document> res =  collectionTable.find(); //On récupère tout les employés

        List<Staff> staff = new ArrayList<Staff>();
        for(Document doc : res){
            switch(doc.get("role").toString()){ //En fonction du role de l'employé on crée l'objet correspondant
                case "serveur":
                    Waiter a = gson.fromJson(doc.toJson(), Waiter.class);
                    a.setId(doc.getObjectId("_id"));
                    List l = (List) doc.get("Table");
                    a.setTable(l);
                    Staff p = a;
                    staff.add(p);
                    break;
                case "cuisinier":
                    Staff b = gson.fromJson(doc.toJson(),Cook.class);
                    b.setId(doc.getObjectId("_id"));
                    staff.add(b);
                    break;
                case "maitre d'hotel":
                    Staff c = gson.fromJson(doc.toJson(), Butler.class);
                    c.setId(doc.getObjectId("_id"));
                    staff.add(c);
                    break;
                case "assistant de service":
                    Staff d = gson.fromJson(doc.toJson(), ServiceAssistant.class);
                    d.setId(doc.getObjectId("_id"));
                    staff.add(d);
                    break;
                case "directeur":
                    Staff e = gson.fromJson(doc.toJson(), Director.class);
                    e.setId(doc.getObjectId("_id"));
                    staff.add(e);
                    break;
            }

        }
        return staff;
    }

    /**
     * Permet de récupérer les informations de plusieurs tables à partir de leur id
     * @param id
     * @return
     */
    public List<Table> getTableId(List<ObjectId> id) {
        MongoCollection<Document> collectionTable = database.getCollection("Table");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Table> tables = new ArrayList<>();
        for(ObjectId i : id){
            Document table = collectionTable.find(eq("_id", i)).first();
            Table t = gson.fromJson(table.toJson(),Table.class);
            t.set_id(table.getObjectId("_id"));
            tables.add(t);
        }
        return tables;
    }

    /**
     * Permet de récupérer les préparations qui sont en cours.
     * @return
     */
    public ArrayList<Preparation> getPreparationsEnCours(){
        MongoCollection<Document> collection = database.getCollection("Preparation");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FindIterable<Document>  preparationsDoc = collection.find(eq("debut", true));
        ArrayList<Preparation> preparations = new ArrayList<>();
        for(Document d : preparationsDoc ){
            Preparation preparation = gson.fromJson(d.toJson(), Preparation.class);
            preparation.Plat = d.getObjectId("Plat");
            preparation._id = d.getObjectId("_id");
            preparations.add(preparation);
        }
        return preparations;
    }

}
