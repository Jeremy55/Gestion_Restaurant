package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;

public class Cook extends Staff {

    public Cook(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(id, login, mdp, nom, prenom);
    }

    @Override
    public void Screen(){
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new Label("Test"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        new Button("Déconnection", new Runnable() {
            @Override
            public void run() {
                LoginScreen loginScreen = new LoginScreen();
                try {
                    MainTerminal.getConsole().switchWindow(loginScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addTo(panel);
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
