package fr.ul.miage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.*;

public class DBQueriesTest {

    DBQueries dbQueries = new DBQueries();

    @Test
    void testConnectionVrai(){
        boolean reponse = dbQueries.userConnection("jpierre", "jpierre");
        assertEquals(reponse, true);
    }

    @Test
    void testConnectionFaux(){
        boolean reponse = dbQueries.userConnection("faux", "faux");
        assertEquals(reponse, false);
    }

    @Test
    void testMdpFaux(){
        boolean reponse = dbQueries.userConnection("jpierre", "jpierres");
        assertEquals(reponse, false);
    }

    @Test
    void testLoginFaux(){
        boolean reponse = dbQueries.userConnection("jpierres", "jpierre");
        assertEquals(reponse, false);
    }

    @Test
    void timeoutConnectionVrai()
    {
        assertTimeout(ofSeconds(5), () -> {
            dbQueries.userConnection("jpierre", "jpierre");
        });
    }

    @Test
    void timeoutConnectionFaux()
    {
        assertTimeout(ofSeconds(5), () -> {
            dbQueries.userConnection("faux", "faux");
        });
    }

    @Test
    void testGetServiceAssistant()
    {
        Staff s = dbQueries.getStaff("aservice");
        assertEquals(s.getClass(), ServiceAssistant.class);
    }

    @Test
    void testGetCook()
    {
        Staff s = dbQueries.getStaff("jpierre");
        assertEquals(s.getClass(), Cook.class);
    }

    @Test
    void testGetButler()
    {
        Staff s = dbQueries.getStaff("mhotel");
        assertEquals(s.getClass(), Butler.class);
    }

    @Test
    void testGetDirector()
    {
        Staff s = dbQueries.getStaff("directeur");
        assertEquals(s.getClass(), Director.class);
    }


    @Test
    void testGetWaiter()
    {
        Staff s = dbQueries.getStaff("val");
        assertEquals(s.getClass(), Waiter.class);
    }

    @Test
    void testGetAllTableNotNull()
    {
        List<Table> tables = dbQueries.getAllTable();
        assertNotNull(tables);
    }

    @Test
    void getServeurAffecteNotNull()
    {
        List<Table> tables = dbQueries.getAllTable();
        List<Waiter> serveurs = dbQueries.getServeurAffecte(tables.get(0));
        assertNotNull(serveurs);
    }

    @Test
    void getServeurNonAffecteNotNull()
    {
        List<Table> tables = dbQueries.getAllTable();
        List<Waiter> serveurs = dbQueries.getServeurNonAffecte(tables.get(0));
        assertNotNull(serveurs);
    }

    @Test
    void getIngredients(){
        List<Ingredient> ingredients = dbQueries.getIngredients();
        assertNotNull(ingredients);
    }

    @Test
    void getCategories(){
        List<Categorie> categories = dbQueries.getCategories();
        assertNotNull(categories);
    }

    @Test
    void getTableId(){
        List<ObjectId> id = new ArrayList<ObjectId>();
        id.add(new ObjectId("60aba63b1a4e41ed8742ed4a"));
        id.add(new ObjectId("60abaff21a4e41ed8742ed4e"));
        List<Table> tables = dbQueries.getTableId(id);
        assertEquals(tables.get(0).get_id(), new ObjectId("60aba63b1a4e41ed8742ed4a"));
        assertEquals(tables.get(1).get_id(), new ObjectId("60abaff21a4e41ed8742ed4e"));
    }

    @Test
    void getPreparationNotNull(){
        List<Preparation> prepa = dbQueries.getPreparationsEnCours();
        assertNotNull(prepa);
    }

    @Test
    void testNewDish(){
        String nomPlat = "test";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("test");
        List<String> categorie = new ArrayList<>();
        categorie.add("test");
        Double prix = 5.5;
        dbQueries.newDish(nomPlat, ingredients, categorie, prix);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Plat");
        List<Cook.Plat> plats = new ArrayList<>();
        Document preparationsDoc = collection.find(eq("nom", nomPlat)).first();
        assertEquals(preparationsDoc.get("Ingredient"), ingredients);
        assertEquals(preparationsDoc.get("Categorie"), categorie);
        assertEquals(preparationsDoc.get("prix"), prix);
    }

    @Test
    void testGetPreparation(){
        List<Preparation> prep = new ArrayList<>();
        prep = dbQueries.getPreparations();
        assertNotNull(prep);
    }

    @Test
    void testGetPlat(){
        ObjectId id = new ObjectId("60abe3eb93ddd7520a179993");
        Cook.Plat a = dbQueries.getPlat(id);
        assertEquals(a._id, id);
    }

    @Test
    void testUpdatePreparation(){
        Preparation p = new Preparation(new ObjectId("60b3828d99a1174c2ae535bf"), "30-05-2021 15:00:00", true, new ObjectId("60abe3eb93ddd7520a179993"), false);
        dbQueries.updatePreparation(p);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Preparation");
        Document preparationsDoc = collection.find(eq("_id", p._id)).first();
        assertTrue((Boolean) preparationsDoc.get("debut"));
    }

    @Test
    void testGetWaiterTableVide(){
        Waiter a = new Waiter("test", "test", "test", "test");
        List<Table> tables = dbQueries.getWaiterTables(a);
        assertEquals(tables, new ArrayList<>());
    }

    @Test
    void testGetWaiterTablePleine(){
        Waiter a = new Waiter("test", "test", "test", "test");
        List<ObjectId> id = new ArrayList<>();
        id.add(new ObjectId("60aba63b1a4e41ed8742ed4a"));
        a.setTable(id);
        List<Table> tables = dbQueries.getWaiterTables(a);
        assertEquals(tables.get(0).get_id(), id.get(0));
    }

    @Test
    void testUpdateTableLibre(){
        dbQueries.updateTableLibre(new ObjectId("60aba63b1a4e41ed8742ed4a"));
        MongoCollection<Document> collection = dbQueries.database.getCollection("Table");
        Document preparationsDoc = collection.find(eq("_id", new ObjectId("60aba63b1a4e41ed8742ed4a"))).first();
        assertEquals(preparationsDoc.get("etat"), "libre");
    }

    @Test
    void testAffecteServeurTable(){
        Table t = new Table(new ObjectId("60aba63b1a4e41ed8742ed4a"), 1, 0, "libre", 2);
        Waiter w = new Waiter("val", "val", "valou", "valentin");
        dbQueries.AffecteServeurTable(t, w);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Personnel");
        List<Table> tables = new ArrayList<>();
        Document preparationsDoc = collection.find(eq("login", w.getLogin())).first();
        tables = (List<Table>) preparationsDoc.get("Table");
        assertNotNull(tables);
    }

    @Test
    void testAddStaff(){
        dbQueries.addStaff("test", "test", "test", "test", "cuisinier", null);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Personnel");
        Document preparationsDoc = collection.find(eq("login", "test")).first();
        assertNotNull(preparationsDoc);
    }

    @Test
    void testGetAllStaff(){
        List<Staff> staff = dbQueries.getAllStaff();
        assertNotNull(staff);
    }

    @Test
    void testUpdateIngredient(){
        ObjectId id = new ObjectId("60abb2932c03d0110c9c8e74");
        dbQueries.updateIngredient(id, 40);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Ingredient");
        Document doc = collection.find(eq("_id", id)).first();
        assertEquals(doc.get("stock"), 40);
    }

    @Test
    void testGetAllPlat(){
        List<Cook.Plat> plats = dbQueries.getAllPlats();
        assertNotNull(plats);
    }

    @Test
    void testModifierPlat(){
        ObjectId id = new ObjectId("60abe3eb93ddd7520a179993");
        Cook.Plat p = dbQueries.getPlat(new ObjectId("60abe3eb93ddd7520a179993"));
        dbQueries.modifierPlat(p, true);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Plat");
        Document doc = collection.find(eq("_id", id)).first();
        assertEquals(doc.get("platDuJour"), true);
    }

    @Test
    void testModificationEmploye(){
        Staff f = dbQueries.getStaff("jpierre");
        dbQueries.modificationEmploye(f.getId(), f.getLogin(), f.getMdp(), "pierrot", f.getPrenom(), "cuisinier", null);
        MongoCollection<Document> collection = dbQueries.database.getCollection("Personnel");
        Document doc = collection.find(eq("login", f.getLogin())).first();
        assertEquals(doc.get("nom"), "pierrot");
    }

    @Test
    void testGetAllCommande(){
        List<Order> commande = dbQueries.getAllCommandes();
        assertNotNull(commande);
    }

    @Test
    void testGetPreparationID(){
        ObjectId id = new ObjectId("60b3827999a1174c2ae535bc");
        List<ObjectId> listeID = new ArrayList<>();
        listeID.add(id);
        List<Preparation> prep = dbQueries.getPreparationID(listeID);
        assertEquals(prep.get(0)._id, id);
    }

















}
