package fr.ul.miage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginScreenTests {
    private DBQueries dbQueries = mock(DBQueries.class);

    @Test
    public void testConnexionValide(){
        when(dbQueries.userConnection("jpierre","jpierre")).thenReturn(true);
        LoginScreen a = new LoginScreen();
    }
}
