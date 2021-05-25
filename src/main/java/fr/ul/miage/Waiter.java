package fr.ul.miage;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Waiter extends Staff {
    private List<ObjectId> Table;

    public Waiter(String login, String mdp, String nom, String prenom) {
        super(login, mdp, nom, prenom);
        Table = new ArrayList<ObjectId>();
    }

    @Override
    public void Screen(){
        Panel panel = super.deconnection();
        panel.setLayoutManager(new GridLayout(2));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        //panel.addComponent(new Label("Menu serveur").setPosition(new TerminalPosition(1)));

       /* List<Integer> listEtages = getFloors(getDbQueries().getWaiterTables(getLogin()));

        for (Integer etage :listEtages) {
            panel.addComponent(new Label("Etage n°" + etage + " :"));
            for (Table t : getTable()) {
                panel.addComponent(new Label("\t- Table n°" + t.getNumber() + " | Etat : " + t.getEtat()));
            }
        }

        BasicWindow window = new BasicWindow();
        window.setComponent(panel);
        try {
            MainTerminal.getConsole().switchWindow(window);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Récupère une liste d'entier en fonction du nombre d'étages existant.
     * @param lTable
     * @return lEtage
     */
    public List<Integer> getFloors(List<Table> lTable){
        List<Integer> lEtage = new ArrayList<Integer>();
        for (Table tab : lTable) {
           /* if (!lEtage.contains((Integer) tab.getNumber())){
                lEtage.add(tab.getNumber());
            }*/
        }
        return lEtage;
    }

    public List<ObjectId> getTable() {
        return Table;
    }

    public void setTable(List<ObjectId> table) {
        Table = table;
    }
}