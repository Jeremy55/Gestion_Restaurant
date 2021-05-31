package fr.ul.miage;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.googlecode.lanterna.gui2.*;
import java.util.ArrayList;
import java.util.List;

public class WaiterTest {
    Waiter w = new Waiter("test","test","dupont","jean");
    @Test
    public void  getFloorsTestTrue(){
        List<Table> lTable = new ArrayList<>();
        lTable.add(new Table(2,"occupé",3));
        lTable.add(new Table(1,"libre",6));
        lTable.add(new Table(2,"occupé",4));
        List<Integer> lEtage = w.getFloors(lTable);
        Assertions.assertEquals(lEtage.size(),2);
    }
    //Ne contient que des valeurs distinctes ? voir avec jerem et chloé
    @Test
    public void  getFloorsTestFalse(){
        List<Table> lTable = new ArrayList<>();
        lTable.add(new Table(2,"occupé",3));
        lTable.add(new Table(1,"libre",6));
        lTable.add(new Table(2,"occupé",4));
        List<Integer> lEtage = w.getFloors(lTable);
        Assertions.assertNotEquals(lEtage.size(),3);
    }

    @Test
    public void  setColorStatesTest(){
        Table t = new Table(2,"occupé",3);
        Label lbl = new Label("test");
        w.setColorStates(t,lbl);
        Assertions.assertEquals(lbl.getBackgroundColor(), TextColor.ANSI.YELLOW_BRIGHT);
    }

}
