package fr.ul.miage;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println(s.getClass());
        assertEquals(s.getClass(), ServiceAssistant.class);
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










}
