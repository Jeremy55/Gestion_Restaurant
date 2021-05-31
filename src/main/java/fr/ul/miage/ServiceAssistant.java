package fr.ul.miage;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceAssistant extends Staff {

    public ServiceAssistant(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
    }


    @Override
    public void screen() throws IOException {
        MainTerminal.getConsole().switchWindow(DebarasserTable(new Label("")));
    }

    /**
     * Permet d'afficher la fenêtre pour dresser une table
     * @param t
     * @param lblOutput
     * @return
     */
    public BasicWindow DresserTable(Table t, Label lblOutput) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("La table n°" + t.getNumero()+ " a bien été débarassée"));
        panel.addComponent(new Label(""));
        new Button("Dresser la table", new Runnable() {
            @Override
            public void run() {
                try {
                    lblOutput.setText("La table n°" + t.getNumero() + " a bien été dresser");
                    getDbQueries().updateTableLibre(t.get_id());
                    MainTerminal.getConsole().switchWindow(DebarasserTable(lblOutput));
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

    /**
     * Permet d'afficher une fenêtre pour afficher toutes les tables, leurs états et de pouvoir les débarasser
     * @param lbl
     * @return
     */
    public BasicWindow DebarasserTable(Label lbl) {
        Panel panel = new Panel();
        Label lblOutput = lbl;
        Timer timer = new Timer();

        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                panel.removeAllComponents();
                new Button("Déconnexion", new Runnable() {
                    @Override
                    public void run() {
                        try {
                            timer.cancel();
                            LoginScreen loginScreen = new LoginScreen();
                            MainTerminal.getConsole().switchWindow(loginScreen);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addTo(panel);
                panel.addComponent(new Label("Menu assistant de service").addStyle(SGR.BOLD));
                panel.addComponent(lblOutput.setBackgroundColor(TextColor.ANSI.GREEN));
                List<Table> table = getDbQueries().getAllTable();
                for (Table e : table) {
                    panel.addComponent(new Label(" - Table n°" + e.getNumero() + ", étage : " + e.getEtage() + ", état : " + e.getEtat()));
                    if (e.getEtat().equals("débarassée")) {
                        new Button("Débarasser la table n° " + e.getNumero(), new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    timer.cancel();
                                    MainTerminal.getConsole().switchWindow(DresserTable(e, lblOutput));
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        }).addTo(panel);
                    }
                    panel.addComponent(new EmptySpace());
                }
            }
        }, 0, 5000);

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
