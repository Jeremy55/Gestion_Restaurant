package fr.ul.miage;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;

public class Butler extends Staff {

    public Butler( String login, String mdp, String nom, String prenom) {
        super( login, mdp, nom, prenom);
    }

    @Override
    public void Screen() throws IOException {
        MainTerminal.getConsole().switchWindow(AffichageTable(new Label("")));
    }

    /**
     * Permet d'afficher toutes les tables et de pouvoir en sélectionner une
     * @param lb
     * @return
     */
    public BasicWindow AffichageTable(Label lb){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label(""));
        panel.addComponent(new Label("Menu maître d'hôtel").addStyle(SGR.BOLD).setSize(TerminalSize.ONE));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label(""));
        panel.addComponent(new Label("Liste des tables :"));
        panel.addComponent(new Label(""));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        List<Table> table = getDbQueries().getAllTable();
        for(Table e : table){
                new Button("  - Table n° " + e.getNumero() + " a l'étage " + e.getEtage(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainTerminal.getConsole().switchWindow(AssignerServeur(e, lb));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }).addTo(panel);
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

    /**
     * Permet d'afficher les serveurs affectés et non affectés à une table et permet de pouvoir affecter un serveur à une table
     * @param e
     * @param lb
     * @return
     */
    public BasicWindow AssignerServeur(Table e, Label lb){
        Panel panel = super.deconnection();
        Label lblOutput = lb;
        new Button("Retour en arrière", new Runnable() {
            @Override
            public void run() {
                try {
                    MainTerminal.getConsole().switchWindow(AffichageTable(new Label("")));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }).addTo(panel);
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(lblOutput.setBackgroundColor(TextColor.ANSI.GREEN));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("Liste des serveurs affectés à la table n° " + e.getNumero() + " : " ));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        List<Waiter> serveursAffectes = getDbQueries().getServeurAffecte(e);
        for(Waiter w : serveursAffectes){
            panel.addComponent(new Label ( "\t - " + w.getNom() + " " + w.getPrenom()));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        }
        panel.addComponent(new Label(""));
        panel.addComponent(new Label("Liste des serveurs non affectés à cette table : "));
        List<Waiter> serveursNonAffectes = getDbQueries().getServeurNonAffecte(e);
        for(Waiter w : serveursNonAffectes){
            panel.addComponent(new Label ( "\t - " + w.getNom() + " " + w.getPrenom()));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
            new Button("Affecter le serveur " + w.getNom() + " " + w.getPrenom(), new Runnable() {
                @Override
                public void run() {
                    lblOutput.setText("Le serveur " + w.getNom() + " " + w.getPrenom() + " a bien été affecté à la table n°" + e.getNumero());
                    getDbQueries().AffecteServeurTable(e, w);
                    try {
                        MainTerminal.getConsole().switchWindow(AssignerServeur(e, lblOutput));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }).addTo(panel);
        }
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException z) {
            z.printStackTrace();
        }
        return window;
    }

}
