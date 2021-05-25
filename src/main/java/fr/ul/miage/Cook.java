package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;

public class Cook extends Staff {

    public Cook( String login, String mdp, String nom, String prenom) {
        super( login, mdp, nom, prenom);
    }

    @Override
    public void Screen(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("Menu cuisinier"));
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
