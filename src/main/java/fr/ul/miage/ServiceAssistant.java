package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public class ServiceAssistant extends Staff {

    public ServiceAssistant(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(id, login, mdp, nom, prenom);
    }

    @Override
    public void Screen() throws IOException {
        MainTerminal.getConsole().switchWindow(DebarasserTable());
    }

    public BasicWindow DresserTable(Table t) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("La table numéro " + t.getNumero() + " a bien été débarassée"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        new Button("Dresser la table", new Runnable() {
            @Override
            public void run() {
                try {
                    getDbQueries().updateTableLibre(t.getNumero(), t.getEtage());
                    MainTerminal.getConsole().switchWindow(DebarasserTable());
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
        return window;

    }

    public BasicWindow DebarasserTable(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("Menu assistant de service"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        List<Table> table = getDbQueries().getAllTable();
        for(Table e : table){
            panel.addComponent(new Label("Table numéro : " + e.getNumero() + ", état : " + e.getEtat()));
            if(e.getEtat().equals("débarassée")){
                new Button("Débarasser la table numéro " + e.getNumero(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainTerminal.getConsole().switchWindow(DresserTable(e));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }).addTo(panel);
            }
            panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        }
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return window;
    }

}
