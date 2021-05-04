package fr.ul.miage;

import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

public class DBQueriesTest {

    @Test
    void testConnectionVrai(){
        boolean reponse = DBQueries.userConnection("jpierre", "jpierre");
        assertEquals(reponse, true);
    }

    @Test
    void testConnectionFaux(){
        boolean reponse = DBQueries.userConnection("faux", "faux");
        assertEquals(reponse, false);
    }

    @Test
    void testMdpFaux(){
        boolean reponse = DBQueries.userConnection("jpierre", "jpierres");
        assertEquals(reponse, false);
    }

    @Test
    void testLoginFaux(){
        boolean reponse = DBQueries.userConnection("jpierres", "jpierre");
        assertEquals(reponse, false);
    }

    @Test
    void timeoutConnectionVrai()
    {
        assertTimeout(ofSeconds(5), () -> {
            DBQueries.userConnection("jpierre", "jpierre");
        });
    }

    @Test
    void timeoutConnectionFaux()
    {
        assertTimeout(ofSeconds(5), () -> {
            DBQueries.userConnection("faux", "faux");
        });
    }}
