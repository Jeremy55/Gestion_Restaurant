package fr.ul.miage;

import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

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
    }}