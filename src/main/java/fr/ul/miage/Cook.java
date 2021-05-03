package fr.ul.miage;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import org.bson.types.ObjectId;

public class Cook extends Staff {

    public Cook(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(id, login, mdp, nom, prenom);
    }

    @Override
    public void Screen(){
        System.out.println("je suis cook");
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new Label("Test"));
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        Main.terminal.switchWindow(window);
    }
}
