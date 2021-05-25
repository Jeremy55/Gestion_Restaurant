package fr.ul.miage;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceAssistant extends Staff {

   /* class Rafraichir extends TimerTask {
        public void run() {
            System.out.println("test");
            try {
                MainTerminal.getConsole().switchWindow(DebarasserTable(new Label("")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("test2");

        }
    }*/

    public ServiceAssistant(ObjectId id, String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
    }


    @Override
    public void Screen() throws IOException {
        /*Timer timer = new Timer();
        timer.schedule(new Rafraichir(), 0, 5000);*/
        MainTerminal.getConsole().switchWindow(DebarasserTable(new Label("")));
    }

    public BasicWindow DresserTable(Table t, Label lblOutput) {
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(new Label("La table n° " + t.getNumero() + " a bien été débarassée"));
        panel.addComponent(new Label(""));
        new Button("Dresser la table", new Runnable() {
            @Override
            public void run() {
                try {
                    lblOutput.setText("La table " + t.get_id() + " a bien été dresser");
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

    public BasicWindow DebarasserTable(Label lbl) {
        Panel panel = super.deconnection();
        Label lblOutput = lbl;
        panel.setLayoutManager(new GridLayout(1));
        panel.addComponent(new Label(""));
        panel.addComponent(new Label("Menu assistant de service").addStyle(SGR.BOLD));
        panel.addComponent(lblOutput.setBackgroundColor(TextColor.ANSI.GREEN));
        List<Table> table = getDbQueries().getAllTable();
        for(Table e : table){
            panel.addComponent(new Label(" - Table n° : " + e.getNumero() + ", étage : " + e.getEtage() +", état : " + e.getEtat()));
            if(e.getEtat().equals("débarassée")){
                new Button("Débarasser la table n° " + e.getNumero(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainTerminal.getConsole().switchWindow(DresserTable(e, lblOutput));
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
